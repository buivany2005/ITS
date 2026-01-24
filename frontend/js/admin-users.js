// Admin Users/Customers page
(function () {
  const qs = (sel, root = document) => root.querySelector(sel);
  const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  // Elements
  const searchInput = qs("#user-search");
  const tableBody = qs("#users-table-body");
  const paginationInfo = qs("#pagination-info");
  const btnExport = qs("#btn-export-users");

  // State
  let users = [];
  let filteredUsers = [];

  function fetchUsers() {
    fetch("/api/admin/users")
      .then((r) => {
        if (!r.ok) throw new Error("Failed to fetch users");
        return r.json();
      })
      .then((data) => {
        users = data || [];
        filteredUsers = users;
        renderTable();
        updatePagination();
      })
      .catch((err) => {
        console.error("fetchUsers error", err);
        tableBody.innerHTML = `
          <tr>
            <td colspan="6" class="px-6 py-8 text-center text-[#617589]">
              Không thể tải danh sách khách hàng. Vui lòng thử lại.
            </td>
          </tr>
        `;
      });
  }

  function renderTable() {
    if (!tableBody) return;
    
    if (filteredUsers.length === 0) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="6" class="px-6 py-8 text-center text-[#617589]">
            Không tìm thấy khách hàng nào.
          </td>
        </tr>
      `;
      return;
    }

    tableBody.innerHTML = filteredUsers
      .map((user) => {
        const roleLabel = getRoleLabel(user.role);
        const roleBadge = getRoleBadge(user.role);
        
        return `
        <tr data-user-id="${user.id}" class="hover:bg-gray-50 dark:hover:bg-gray-800/30 transition-colors">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
            ${user.id}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center gap-3">
              <div class="h-10 w-10 rounded-full bg-primary/20 flex items-center justify-center text-primary font-bold text-sm">
                ${getInitials(user.fullName || user.email)}
              </div>
              <div>
                <div class="text-sm font-medium text-gray-900 dark:text-white">${escapeHtml(user.fullName || "N/A")}</div>
              </div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
            ${escapeHtml(user.email)}
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
            ${escapeHtml(user.phone || "N/A")}
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <select class="role-select text-sm rounded-lg px-3 py-1.5 ${roleBadge}" data-user-id="${user.id}">
              <option value="USER" ${user.role === "USER" ? "selected" : ""}>Khách hàng</option>
              <option value="ADMIN" ${user.role === "ADMIN" ? "selected" : ""}>Quản trị viên</option>
            </select>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-right">
            <div class="flex justify-end gap-2">
              <button data-id="${user.id}" class="btn-delete p-2 text-[#617589] hover:text-red-500 transition-colors">
                <span class="material-symbols-outlined text-xl">delete</span>
              </button>
            </div>
          </td>
        </tr>
      `;
      })
      .join("\n");

    // Attach event listeners
    qsa(".btn-delete").forEach((btn) => btn.addEventListener("click", onDelete));
    qsa(".role-select").forEach((sel) => sel.addEventListener("change", onRoleChange));
  }

  function getRoleLabel(role) {
    const labels = {
      ADMIN: "Quản trị viên",
      USER: "Khách hàng",
    };
    return labels[role] || role;
  }

  function getRoleBadge(role) {
    if (role === "ADMIN") {
      return "bg-purple-100 text-purple-700 dark:bg-purple-900/30 dark:text-purple-400";
    }
    return "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400";
  }

  function getInitials(name) {
    if (!name) return "U";
    const parts = name.trim().split(" ");
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }

  function escapeHtml(s) {
    if (!s) return "";
    return String(s).replace(/[&"'<>]/g, function (c) {
      return {
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;",
      }[c];
    });
  }

  async function onRoleChange(e) {
    const userId = e.currentTarget.dataset.userId;
    const newRole = e.currentTarget.value;
    
    if (!confirm(`Xác nhận thay đổi vai trò người dùng #${userId} thành ${getRoleLabel(newRole)}?`)) {
      // Revert selection
      fetchUsers();
      return;
    }

    try {
      const res = await fetch(`/api/admin/users/${userId}/role`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ role: newRole }),
      });
      
      if (!res.ok) throw new Error("Failed to update role");
      
      alert("Cập nhật vai trò thành công!");
      fetchUsers(); // Reload to reflect changes
    } catch (err) {
      alert("Không thể cập nhật vai trò: " + err.message);
      fetchUsers(); // Reload to revert
    }
  }

  async function onDelete(e) {
    const id = e.currentTarget.dataset.id;
    
    if (!confirm("Xác nhận xoá người dùng này? Hành động này không thể hoàn tác!")) return;
    
    try {
      const res = await fetch(`/api/admin/users/${encodeURIComponent(id)}`, {
        method: "DELETE",
      });
      
      if (!res.ok) throw new Error("Delete failed");
      
      // Remove from DOM
      const tr = document.querySelector(`tr[data-user-id="${id}"]`);
      if (tr) tr.remove();
      
      // Update local state
      users = users.filter((u) => u.id != id);
      filteredUsers = filteredUsers.filter((u) => u.id != id);
      updatePagination();
      
      alert("Xoá người dùng thành công!");
    } catch (err) {
      alert("Xoá thất bại: " + err.message);
    }
  }

  function onSearchInput(e) {
    const query = e.target.value.trim().toLowerCase();
    
    if (!query) {
      filteredUsers = users;
    } else {
      filteredUsers = users.filter((user) => {
        const name = (user.fullName || "").toLowerCase();
        const email = (user.email || "").toLowerCase();
        const phone = (user.phone || "").toLowerCase();
        return name.includes(query) || email.includes(query) || phone.includes(query);
      });
    }
    
    renderTable();
    updatePagination();
  }

  function updatePagination() {
    if (paginationInfo) {
      paginationInfo.textContent = `Hiển thị ${filteredUsers.length} trong tổng số ${users.length} khách hàng`;
    }
  }

  function exportUsersCSV() {
    const headers = ["ID", "Họ tên", "Email", "Số điện thoại", "Vai trò"];
    const rows = [headers];
    
    filteredUsers.forEach((user) => {
      rows.push([
        user.id || "",
        escapeHtml(user.fullName || "N/A"),
        escapeHtml(user.email || ""),
        escapeHtml(user.phone || "N/A"),
        getRoleLabel(user.role),
      ]);
    });
    
    // Proper CSV format with quoted fields
    const csv = rows.map((row) => 
      row.map((cell) => {
        // Escape quotes and wrap in quotes
        const escaped = String(cell).replace(/"/g, '""');
        return `"${escaped}"`;
      }).join(",")
    ).join("\n");
    
    const blob = new Blob(["\ufeff" + csv], { type: "text/csv;charset=utf-8;" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `khach_hang_${new Date().toISOString().split("T")[0]}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  }

  // Event listeners
  if (searchInput) {
    searchInput.addEventListener("input", onSearchInput);
  }
  
  if (btnExport) {
    btnExport.addEventListener("click", exportUsersCSV);
  }

  // Initial load
  fetchUsers();
})();
