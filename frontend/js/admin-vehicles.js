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
    fetch("/api/vehicles?" + params.toString())
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
        return `
        <tr data-vehicle-id="${v.id}">
          <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center gap-3">
              <div class="h-20 w-28 rounded overflow-hidden border bg-white">
                <img src="${
                  v.thumbnail || "/img/oto/default.png"
                }" alt="" class="h-full w-full object-cover" />
              </div>
              <div>
                <div class="text-sm font-medium text-gray-900">${escapeHtml(
                  v.name
                )}</div>
                <div class="text-xs text-gray-500">${escapeHtml(
                  v.model || ""
                )}</div>
              </div>
            </div>
          </td>
          <td class="px-6 py-4 whitespace-nowrap">${escapeHtml(v.category)}</td>
          <td class="px-6 py-4 whitespace-nowrap">${escapeHtml(
            v.licensePlate || ""
          )}</td>
          <td class="px-6 py-4 whitespace-nowrap">${
            v.status || "available"
          }</td>
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
      btn.addEventListener("click", onDelete)
    );
  }

  function onEdit(e) {
    const id = e.currentTarget.dataset.id;
    // Navigate to edit page (server should handle)
    window.location.href = `/admin/vehicles/edit.html?id=${encodeURIComponent(
      id
    )}`;
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
    window.location.href = "/admin/vehicles/new.html";
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
      btn.addEventListener("click", onDelete)
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
