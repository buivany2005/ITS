// Admin Vehicles page interactions: search, filter, add, edit, delete, sidebar toggle
(function () {
  const qs = (sel, root = document) => root.querySelector(sel);
  const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  // Elements
  const inputSearch = qs("#vehicle-search");
  const categoryBtns = qsa(".category-btn");
  const vehicleTypeSelect = qs("#vehicle-type-filter");
  const btnAdd = qs("#btn-add-vehicle");
  const btnToggleSidebar = qs("#btn-toggle-sidebar");
  const tableBody = qs("#vehicles-table-body");

  // State
  let vehicles = [];
  let currentPage = 1;
  const pageSize = 12;
  let filters = { q: "", category: "all" };

  function fetchVehicles() {
    const params = new URLSearchParams();
    if (filters.q) params.set("q", filters.q);
    // If category is an enum string (e.g. OTO/XEMAY/XEDAP) set vehicleType
    if (filters.category && filters.category !== "all") {
      params.set("vehicleType", filters.category);
    }
    if (filters.status) params.set("status", filters.status);
    fetch("/api/admin/vehicles?" + params.toString())
      .then((r) => r.json())
      .then((data) => {
        // apply local filtering as a fallback (or to combine filters client-side)
        let items = data || [];
        items = applyLocalFilters(items);
        vehicles = items;
        renderTable();
      })
      .catch((err) => console.error("fetchVehicles error", err));
  }

  // Convert File to data URL (base64) for inline storage
  function fileToDataUrl(file) {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result);
      reader.onerror = () =>
        reject(reader.error || new Error("Không đọc được file"));
      reader.readAsDataURL(file);
    });
  }

  function applyLocalFilters(items) {
    const q = (filters.q || "").toLowerCase();
    const category = filters.category || "all";
    const statusFilter = filters.status || null;
    return (items || []).filter((v) => {
      // category filter: if not 'all', compare with vehicle.category (enum or string)
      if (category !== "all") {
        if (!v.category) return false;
        if (
          v.category.toString().toUpperCase() !==
          category.toString().toUpperCase()
        )
          return false;
      }

      // status filter: if set, ensure vehicle.status matches
      if (statusFilter) {
        if (!v.status) return false;
        if (
          v.status.toString().toUpperCase() !==
          statusFilter.toString().toUpperCase()
        )
          return false;
      }

      if (!q) return true;

      const haystack = [v.name, v.model, v.licensePlate, v.brand, v.description]
        .filter(Boolean)
        .map((s) => s.toString().toLowerCase())
        .join(" ");

      return haystack.includes(q);
    });
  }

  function renderTable() {
    if (!tableBody) return;
    const total = vehicles.length;
    const totalPages = Math.max(1, Math.ceil(total / pageSize));
    if (currentPage > totalPages) currentPage = totalPages;
    const start = (currentPage - 1) * pageSize;
    const end = start + pageSize;
    const pageVehicles = vehicles.slice(start, end);

    tableBody.innerHTML = pageVehicles
      .map((v) => {
        const statusBadge = getStatusBadge(v.status);
        return `
        <tr data-vehicle-id="${v.id}">
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="h-20 w-28 rounded overflow-hidden border bg-white">
              <img src="${
                v.thumbnail || "/img/oto/default.png"
              }" alt="" class="h-full w-full object-cover" />
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div>
              <div class="text-sm font-medium text-gray-900">${escapeHtml(
                v.name,
              )}</div>
              <div class="text-xs text-gray-500">${escapeHtml(
                v.model || "",
              )}</div>
              <div class="text-xs text-gray-500">BS: ${escapeHtml(
                v.licensePlate || "N/A",
              )}</div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">${escapeHtml(v.category)}</td>
          <td class="px-6 py-4 whitespace-nowrap">
            <select class="status-select text-xs rounded px-2 py-1 ${statusBadge}" data-vehicle-id="${
              v.id
            }">
              <option value="AVAILABLE" ${v.status === "AVAILABLE" ? "selected" : ""}>Sẵn sàng</option>
              <option value="RENTED" ${v.status === "RENTED" ? "selected" : ""}>Đang thuê</option>
              <option value="MAINTENANCE" ${v.status === "MAINTENANCE" ? "selected" : ""}>Bảo trì</option>
              <option value="UNAVAILABLE" ${v.status === "UNAVAILABLE" ? "selected" : ""}>Không khả dụng</option>
            </select>
          </td>
          <td class="px-6 py-4 whitespace-nowrap text-sm font-semibold">
            ${formatPrice(v.pricePerDay)}đ/ngày
          </td>
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center gap-2">
              <button data-id="${
                v.id
              }" class="btn-edit p-2 text-[#617589] hover:text-primary transition-colors">
                <span class="material-symbols-outlined text-xl">edit_note</span>
              </button>
              <button data-id="${
                v.id
              }" class="btn-delete p-2 text-[#617589] hover:text-red-500 transition-colors">
                <span class="material-symbols-outlined text-xl">delete</span>
              </button>
            </div>
          </td>
        </tr>
      `;
      })
      .join("\n");

    // attach action handlers
    qsa(".btn-edit").forEach((btn) => btn.addEventListener("click", onEdit));
    qsa(".btn-delete").forEach((btn) =>
      btn.addEventListener("click", onDelete),
    );
    qsa(".status-select").forEach((sel) =>
      sel.addEventListener("change", onStatusChange),
    );

    renderPagination(total, totalPages);
  }

  function renderPagination(totalItems, totalPages) {
    const info = qs("#vehicles-pagination-info");
    const container = qs("#vehicles-pagination");
    if (!container || !info) return;

    const start = (currentPage - 1) * pageSize + 1;
    const end = Math.min(currentPage * pageSize, totalItems);
    info.textContent = `Hiển thị ${start} - ${end} trong tổng số ${totalItems} xe`;

    // build buttons
    container.innerHTML = "";

    const makeBtn = (text, cls = "", disabled = false, page = null) => {
      const btn = document.createElement("button");
      btn.className = `px-3 py-1.5 rounded-lg text-sm font-medium ${cls}`;
      btn.textContent = text;
      if (disabled) btn.disabled = true;
      if (page) btn.dataset.page = page;
      return btn;
    };

    // Prev
    const prev = makeBtn(
      "Trước",
      "border border-[#dbe0e6] bg-white hover:bg-gray-50",
      currentPage === 1,
      currentPage - 1,
    );
    container.appendChild(prev);

    // page numbers (show up to 7 pages with truncation)
    const maxButtons = 7;
    let startPage = Math.max(1, currentPage - Math.floor(maxButtons / 2));
    let endPage = Math.min(totalPages, startPage + maxButtons - 1);
    if (endPage - startPage + 1 < maxButtons) {
      startPage = Math.max(1, endPage - maxButtons + 1);
    }

    for (let p = startPage; p <= endPage; p++) {
      const cls =
        p === currentPage
          ? "bg-primary text-white"
          : "border border-[#dbe0e6] bg-white hover:bg-gray-50";
      const btn = makeBtn(p, cls, false, p);
      container.appendChild(btn);
    }

    // Next
    const next = makeBtn(
      "Tiếp",
      "border border-[#dbe0e6] bg-white hover:bg-gray-50",
      currentPage === totalPages,
      currentPage + 1,
    );
    container.appendChild(next);

    // attach handlers
    Array.from(container.querySelectorAll("button")).forEach((b) => {
      b.addEventListener("click", (e) => {
        const page = parseInt(e.currentTarget.dataset.page, 10);
        if (!isNaN(page) && page >= 1 && page <= totalPages) {
          currentPage = page;
          renderTable();
          // scroll to top of table
          const tableTop = document.querySelector("#vehicles-table-body");
          if (tableTop)
            tableTop.scrollIntoView({ behavior: "smooth", block: "start" });
        }
      });
    });
  }

  function getStatusBadge(status) {
    const badges = {
      AVAILABLE:
        "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
      RENTED:
        "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
      MAINTENANCE:
        "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",
      UNAVAILABLE:
        "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300",
    };
    return badges[status] || badges.AVAILABLE;
  }

  function formatPrice(price) {
    if (!price) return "0";
    return new Intl.NumberFormat("vi-VN").format(price);
  }

  async function onStatusChange(e) {
    const vehicleId = e.currentTarget.dataset.vehicleId;
    const newStatus = e.currentTarget.value;

    try {
      const res = await fetch(`/api/admin/vehicles/${vehicleId}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status: newStatus }),
      });

      if (!res.ok) throw new Error("Failed to update status");

      alert("Cập nhật trạng thái thành công!");
      fetchVehicles(); // Reload
    } catch (err) {
      alert("Không thể cập nhật trạng thái: " + err.message);
      fetchVehicles(); // Revert
    }
  }

  function onEdit(e) {
    const id = e.currentTarget.dataset.id;
    // Open edit modal with vehicle data
    showVehicleModal(id);
  }

  async function showVehicleModal(vehicleId = null) {
    let vehicle = null;

    // If editing, fetch vehicle data
    if (vehicleId) {
      try {
        const res = await fetch(`/api/admin/vehicles/${vehicleId}`);
        if (!res.ok) throw new Error("Failed to fetch vehicle");
        vehicle = await res.json();
      } catch (err) {
        alert("Không thể tải thông tin xe: " + err.message);
        return;
      }
    }

    // Create modal HTML
    const modalHtml = `
      <div id="vehicle-modal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
        <div class="bg-white dark:bg-gray-800 rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
          <div class="p-6 border-b border-gray-200 dark:border-gray-700">
            <h3 class="text-xl font-bold">${vehicle ? "Chỉnh sửa phương tiện" : "Thêm phương tiện mới"}</h3>
          </div>
          <form id="vehicle-form" class="p-6 space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium mb-2">Tên xe *</label>
                <input type="text" name="name" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.name) : ""}"
                  placeholder="Honda Vision 2023">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Loại xe *</label>
                <select name="type" required class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
                  <option value="XEMAY" ${vehicle && vehicle.category === "XEMAY" ? "selected" : ""}>Xe máy</option>
                  <option value="OTO" ${vehicle && vehicle.category === "OTO" ? "selected" : ""}>Ô tô</option>
                  <option value="XEDAP" ${vehicle && vehicle.category === "XEDAP" ? "selected" : ""}>Xe đạp</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Hãng xe *</label>
                <input type="text" name="brand" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.brand) : ""}"
                  placeholder="Honda">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Model *</label>
                <input type="text" name="model" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.model) : ""}"
                  placeholder="Vision">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Năm sản xuất *</label>
                <input type="number" name="year" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? vehicle.year : new Date().getFullYear()}"
                  min="2000" max="2030">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Màu sắc *</label>
                <input type="text" name="color" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.color) : ""}"
                  placeholder="Đen">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Biển số xe *</label>
                <input type="text" name="licensePlate" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.licensePlate) : ""}"
                  placeholder="29-G1 123.45">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Giá thuê/ngày (VNĐ) *</label>
                <input type="number" name="pricePerDay" required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? vehicle.pricePerDay : ""}"
                  min="0" placeholder="150000">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Số chỗ ngồi</label>
                <input type="number" name="seats"
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle && vehicle.seats ? vehicle.seats : ""}"
                  min="1" max="50" placeholder="2">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Loại nhiên liệu</label>
                <input type="text" name="fuelType"
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.fuelType || "") : ""}"
                  placeholder="Xăng">
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Truyền động</label>
                <select name="transmission" class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
                  <option value="Automatic" ${vehicle && vehicle.transmission === "Automatic" ? "selected" : ""}>Tự động</option>
                  <option value="Manual" ${vehicle && vehicle.transmission === "Manual" ? "selected" : ""}>Số sàn</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">Hình ảnh</label>
                <input type="file" name="imageFile" accept="image/*"
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
                <p class="text-xs text-gray-500 mt-1">Hoặc nhập URL bên dưới</p>
              </div>
              <div>
                <label class="block text-sm font-medium mb-2">URL hình ảnh</label>
                <input type="text" name="imageUrl"
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                  value="${vehicle ? escapeHtml(vehicle.thumbnail || "") : ""}"
                  placeholder="/img/oto/xe_may/Vision.png">
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium mb-2">Mô tả</label>
              <textarea name="description" rows="3"
                class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary"
                placeholder="Mô tả chi tiết về xe...">${vehicle ? escapeHtml(vehicle.description || "") : ""}</textarea>
            </div>
            <div class="flex gap-3 pt-4">
              <button type="submit" class="flex-1 bg-primary text-white px-6 py-3 rounded-xl font-bold hover:bg-primary/90">
                ${vehicle ? "Cập nhật" : "Thêm mới"}
              </button>
              <button type="button" id="modal-cancel" class="flex-1 bg-gray-200 text-gray-800 px-6 py-3 rounded-xl font-bold hover:bg-gray-300">
                Hủy
              </button>
            </div>
          </form>
        </div>
      </div>
    `;

    // Insert modal
    document.body.insertAdjacentHTML("beforeend", modalHtml);

    // Attach handlers
    const modal = qs("#vehicle-modal");
    const form = qs("#vehicle-form");
    const cancelBtn = qs("#modal-cancel");

    cancelBtn.addEventListener("click", () => modal.remove());

    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      const formData = new FormData(form);
      const imageFile = formData.get("imageFile");
      const imageUrl = (formData.get("imageUrl") || "").trim();

      // Validate required numeric fields
      const yearVal = parseInt(formData.get("year"), 10);
      const priceVal = parseFloat(formData.get("pricePerDay"));
      if (!yearVal || Number.isNaN(yearVal)) {
        alert("Năm sản xuất không hợp lệ");
        return;
      }
      if (!priceVal || Number.isNaN(priceVal)) {
        alert("Giá thuê/ngày không hợp lệ");
        return;
      }

      const data = {
        name: formData.get("name"),
        type: formData.get("type"),
        vehicleType: formData.get("type"), // backend accepts vehicleType as well
        status: "AVAILABLE",
        brand: formData.get("brand"),
        model: formData.get("model"),
        year: yearVal,
        color: formData.get("color"),
        licensePlate: formData.get("licensePlate"),
        pricePerDay: priceVal,
        description: formData.get("description"),
        seats: formData.get("seats")
          ? parseInt(formData.get("seats"), 10)
          : null,
        fuelType: formData.get("fuelType"),
        transmission: formData.get("transmission"),
      };

      // Handle image: prefer URL if provided; otherwise encode small file to base64
      if (imageUrl) {
        data.imageUrl = imageUrl;
      } else if (imageFile && imageFile.size > 0) {
        const maxSizeBytes = 800 * 1024; // ~800KB to avoid server limits
        if (imageFile.size > maxSizeBytes) {
          const mb = (imageFile.size / 1024 / 1024).toFixed(2);
          alert(
            "File ảnh quá lớn: " +
              mb +
              "MB (giới hạn ~0.8MB). Vui lòng nhập URL hoặc chọn file nhỏ hơn.",
          );
          return;
        }
        try {
          data.imageUrl = await fileToDataUrl(imageFile);
        } catch (err) {
          alert("Không đọc được file ảnh: " + err.message);
          return;
        }
      }

      try {
        const url = vehicle
          ? `/api/admin/vehicles/${vehicle.id}`
          : "/api/admin/vehicles";
        const method = vehicle ? "PUT" : "POST";

        const res = await fetch(url, {
          method,
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
        });

        if (!res.ok) {
          let errText = "Failed to save vehicle";
          try {
            const errJson = await res.json();
            errText = errJson.error || errText;
          } catch (parseErr) {
            const txt = await res.text();
            errText = txt || res.status + " " + res.statusText;
          }
          throw new Error(errText);
        }

        alert(vehicle ? "Cập nhật xe thành công!" : "Thêm xe mới thành công!");
        modal.remove();
        fetchVehicles(); // Reload
      } catch (err) {
        alert("Lỗi: " + err.message);
      }
    });
  }

  function onDelete(e) {
    const id = e.currentTarget.dataset.id;
    if (!confirm("Xác nhận xoá phương tiện này?")) return;
    fetch(`/api/admin/vehicles/${encodeURIComponent(id)}`, { method: "DELETE" })
      .then((r) => {
        if (!r.ok) throw new Error("delete failed");
        // remove from DOM
        const tr = document.querySelector(`tr[data-vehicle-id="${id}"]`);
        if (tr) tr.remove();
        alert("Xoá thành công!");
      })
      .catch((err) => alert("Xoá thất bại"));
  }

  function onCategoryClick(e) {
    const cat = e.currentTarget.dataset.category;
    // Map UI category identifiers to backend enum values
    const mapping = {
      motorbike: "XEMAY",
      car: "OTO",
      bicycle: "XEDAP",
      all: "all",
    };

    // If user clicked the 'status' button (opens status filter), don't change category
    if (cat === "status") {
      return;
    }

    // Normal behavior: set filter and fetch. 'all' means no vehicleType filter.
    if (cat === "all") {
      filters.category = "all";
    } else {
      filters.category = mapping[cat] || cat || "all";
    }
    categoryBtns.forEach((b) => b.classList.remove("bg-primary", "text-white"));
    e.currentTarget.classList.add("bg-primary", "text-white");
    currentPage = 1;
    fetchVehicles();
  }

  // (Removed large chooser handlers — chooser UI was deleted)

  // Vehicle type select handler (replaces category buttons)
  if (vehicleTypeSelect) {
    vehicleTypeSelect.addEventListener("change", (e) => {
      const val = e.target.value || "all";
      filters.category = val;
      fetchVehicles();
    });
  }

  // Status filter handler
  const statusSelect = qs("#vehicle-status-filter");
  if (statusSelect) {
    statusSelect.addEventListener("change", (e) => {
      filters.status = e.target.value || null;
      fetchVehicles();
    });
  }

  function onSearchInput(e) {
    filters.q = e.target.value.trim();
    // debounce
    clearTimeout(onSearchInput._t);
    currentPage = 1;
    onSearchInput._t = setTimeout(fetchVehicles, 350);
  }

  function onAddClick() {
    showVehicleModal();
  }

  function onToggleSidebar() {
    const aside = document.querySelector("aside");
    if (aside) aside.classList.toggle("hidden");
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

  function attachExistingHandlers() {
    qsa(".btn-edit").forEach((btn) => btn.addEventListener("click", onEdit));
    qsa(".btn-delete").forEach((btn) =>
      btn.addEventListener("click", onDelete),
    );
  }

  // init
  if (inputSearch) inputSearch.addEventListener("input", onSearchInput);
  categoryBtns.forEach((b) => b.addEventListener("click", onCategoryClick));
  if (btnAdd) btnAdd.addEventListener("click", onAddClick);
  if (btnToggleSidebar)
    btnToggleSidebar.addEventListener("click", onToggleSidebar);

  // attach handlers to existing static table rows so buttons work before fetch completes
  attachExistingHandlers();

  // initial load
  fetchVehicles();
})();
