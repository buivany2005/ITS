// Admin Vehicles page interactions: search, filter, add, edit, delete, sidebar toggle
(function () {
  const qs = (sel, root = document) => root.querySelector(sel);
  const qsa = (sel, root = document) => Array.from(root.querySelectorAll(sel));

  // Elements
  const inputSearch = qs("#vehicle-search");
  const categoryBtns = qsa(".category-btn");
  const btnAdd = qs("#btn-add-vehicle");
  const btnToggleSidebar = qs("#btn-toggle-sidebar");
  const tableBody = qs("#vehicles-table-body");

  // State
  let vehicles = [];
  let filters = { q: "", category: "all" };

  function fetchVehicles() {
    const params = new URLSearchParams();
    if (filters.q) params.set("q", filters.q);
    if (filters.category && filters.category !== "all")
      params.set("category", filters.category);
    fetch("/api/admin/vehicles?" + params.toString())
      .then((r) => r.json())
      .then((data) => {
        vehicles = data || [];
        renderTable();
      })
      .catch((err) => console.error("fetchVehicles error", err));
  }

  function renderTable() {
    if (!tableBody) return;
    tableBody.innerHTML = vehicles
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
  }

  function getStatusBadge(status) {
    const badges = {
      AVAILABLE: "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400",
      RENTED: "bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-400",
      MAINTENANCE: "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400",
      UNAVAILABLE: "bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300",
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
    // Remove any existing modal first
    const existingModal = document.querySelector("#vehicle-modal");
    if (existingModal) existingModal.remove();

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
      const data = {
        name: formData.get("name"),
        type: formData.get("type"),
        brand: formData.get("brand"),
        model: formData.get("model"),
        year: parseInt(formData.get("year")),
        color: formData.get("color"),
        licensePlate: formData.get("licensePlate"),
        pricePerDay: parseFloat(formData.get("pricePerDay")),
        description: formData.get("description"),
        imageUrl: formData.get("imageUrl"),
        seats: formData.get("seats") ? parseInt(formData.get("seats")) : null,
        fuelType: formData.get("fuelType"),
        transmission: formData.get("transmission"),
      };

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
          const err = await res.json();
          throw new Error(err.error || "Failed to save vehicle");
        }

        alert(vehicle ? "Cập nhật xe thành công!" : "Thêm xe mới thành công!");
        modal.remove();
        fetchVehicles(); // Reload
      } catch (err) {
        alert("Lỗi: " + err.message);
        modal.remove();
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
    filters.category = cat || "all";
    categoryBtns.forEach((b) => b.classList.remove("bg-primary", "text-white"));
    e.currentTarget.classList.add("bg-primary", "text-white");
    fetchVehicles();
  }

  function onSearchInput(e) {
    filters.q = e.target.value.trim();
    // debounce
    clearTimeout(onSearchInput._t);
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
