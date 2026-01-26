(function () {
  const exportBtn = document.getElementById("btn-export");
  const searchInput = document.getElementById("order-search");
  const dateFrom = document.getElementById("date-from");
  const dateTo = document.getElementById("date-to");
  const statusTabs = Array.from(document.querySelectorAll(".status-tab"));
  const orderRows = Array.from(
    document.querySelectorAll("tbody tr[data-order-id]"),
  );
  const statusSelects = Array.from(
    document.querySelectorAll(".order-status-select"),
  );
  const viewButtons = Array.from(document.querySelectorAll(".btn-view"));

  let orders = [];

  // Utility functions
  function formatDate(dateString) {
    if (!dateString) return "N/A";
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString("vi-VN");
    } catch (e) {
      return dateString;
    }
  }

  function formatCurrency(amount) {
    if (amount === null || amount === undefined) return "0 VND";
    try {
      return new Intl.NumberFormat("vi-VN").format(amount) + " VND";
    } catch (e) {
      return amount + " VND";
    }
  }

  function fetchOrders(status = "all") {
    const params = new URLSearchParams();
    if (status && status !== "all") {
      // Map frontend status to backend enum
      const statusMap = {
        pending: "PENDING",
        active: "IN_PROGRESS",
        completed: "COMPLETED",
        cancelled: "CANCELLED",
      };
      const apiStatus = statusMap[status] || status.toUpperCase();
      params.set("status", apiStatus);
    }
    fetch("/api/admin/orders?" + params.toString())
      .then((r) => r.json())
      .then((data) => {
        orders = data || [];
        renderTable();
      })
      .catch((err) => console.error("fetchOrders error", err));
  }

  function renderTable() {
    const tbody = document.querySelector("tbody");
    if (!tbody) return;

    // If no orders, show empty state
    if (orders.length === 0) {
      tbody.innerHTML = `
        <tr>
          <td colspan="7" class="px-6 py-12 text-center text-sm text-gray-500">
            <div class="flex flex-col items-center">
              <span class="material-symbols-outlined text-4xl text-gray-300 mb-2">receipt_long</span>
              <p>Không có đơn hàng nào</p>
            </div>
          </td>
        </tr>
      `;
    } else {
      tbody.innerHTML = orders
        .map(
          (order) => `
        <tr data-order-id="${order.id}">
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#${order.id}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.userName || "N/A"}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.vehicleName || "N/A"}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatDate(order.dateFrom)} - ${formatDate(order.dateTo)}</td>
          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${formatCurrency(order.totalPrice)}</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <select class="order-status-select" data-order-id="${order.id}">
              <option value="PENDING" ${order.status === "PENDING" ? "selected" : ""}>Chờ duyệt</option>
              <option value="CONFIRMED" ${order.status === "CONFIRMED" ? "selected" : ""}>Đã duyệt</option>
              <option value="IN_PROGRESS" ${order.status === "IN_PROGRESS" ? "selected" : ""}>Đang thuê</option>
              <option value="COMPLETED" ${order.status === "COMPLETED" ? "selected" : ""}>Hoàn thành</option>
              <option value="CANCELLED" ${order.status === "CANCELLED" ? "selected" : ""}>Đã hủy</option>
            </select>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
            <button class="btn-view text-indigo-600 hover:text-indigo-900" data-order-id="${order.id}">Xem</button>
          </td>
        </tr>
      `,
        )
        .join("");
    }

    // Re-attach handlers
    attachHandlers();
  }

  function attachHandlers() {
    const newStatusSelects = Array.from(
      document.querySelectorAll(".order-status-select"),
    );
    const newViewButtons = Array.from(document.querySelectorAll(".btn-view"));
    newStatusSelects.forEach((sel) => {
      sel.addEventListener("change", async (e) => {
        const orderId = sel.getAttribute("data-order-id");
        const newStatus = sel.value;
        try {
          const res = await fetch(
            "/api/admin/orders/" + encodeURIComponent(orderId) + "/status",
            {
              method: "PATCH",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ status: newStatus }),
            },
          );
          if (!res.ok) throw new Error("Server error");
          // Update local order status
          const order = orders.find((o) => o.id == orderId);
          if (order) order.status = newStatus;
        } catch (err) {
          alert("Không thể cập nhật trạng thái: " + err.message);
        }
      });
    });
    newViewButtons.forEach((btn) => {
      btn.addEventListener("click", () => {
        const id = btn.getAttribute("data-order-id");
        // navigate to detail page (placeholder)
        window.location.href =
          "../use/chi_tiet_xe.html?orderId=" + encodeURIComponent(id);
      });
    });
  }

  function filterRows({ query = "", status = "all", from, to } = {}) {
    const q = query.trim().toLowerCase();
    orderRows.forEach((row) => {
      const text = row.innerText.toLowerCase();
      let visible = true;
      if (q && !text.includes(q)) visible = false;
      if (status && status !== "all") {
        const sel = row.querySelector(".order-status-select");
        if (sel && sel.value !== status) visible = false;
      }
      // date filtering skipped for simplicity
      row.style.display = visible ? "" : "none";
    });
  }

  if (searchInput) {
    searchInput.addEventListener("input", (e) => {
      const q = e.target.value || "";
      filterRows({ query: q });
    });
  }

  statusTabs.forEach((tab) => {
    tab.addEventListener("click", (e) => {
      e.preventDefault();
      const status = tab.getAttribute("data-status");
      // highlight active
      statusTabs.forEach((t) =>
        t.classList.remove("text-primary", "border-primary"),
      );
      tab.classList.add("text-primary", "border-primary");
      fetchOrders(status);
    });
  });

  if (exportBtn) {
    exportBtn.addEventListener("click", async () => {
      try {
        const res = await fetch("/api/admin/orders/export");
        if (!res.ok) throw new Error("Export failed");
        const blob = await res.blob();
        const url = URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = "orders.xlsx";
        document.body.appendChild(a);
        a.click();
        a.remove();
        URL.revokeObjectURL(url);
      } catch (err) {
        alert("Không thể xuất báo cáo: " + (err.message || err));
      }
    });
  }

  // Initial load
  fetchOrders();
})();
