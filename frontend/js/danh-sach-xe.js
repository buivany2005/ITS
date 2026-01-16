// Vehicle listing page with filters
(function () {
  // DOM Elements
  const filterOto = document.getElementById("filter-type-oto");
  const filterXeMay = document.getElementById("filter-type-xemay");
  const filterXeDap = document.getElementById("filter-type-xedap");
  const btnResetFilters = document.getElementById("btn-reset-filters");
  const vehicleGrid = document.querySelector(
    ".grid.grid-cols-1.md\\:grid-cols-2.xl\\:grid-cols-3"
  );
  const resultCount = document.querySelector("h1.text-2xl");

  // State
  let allVehicles = [];
  let filters = {
    types: [], // ["oto", "xemay", "xedap"]
    priceMin: 100000,
    priceMax: 1500000,
    location: "all",
    available: true,
  };

  // Initialize
  async function init() {
    await loadVehicles();
    attachEventListeners();
  }

  // Fetch vehicles from API
  async function loadVehicles() {
    try {
      const data = await window.api.searchVehicles({});
      allVehicles = data || [];
      renderVehicles(allVehicles);
    } catch (err) {
      console.error("Error loading vehicles:", err);
      showError("Không thể tải danh sách phương tiện");
    }
  }

  // Attach event listeners to filters
  function attachEventListeners() {
    if (filterOto) {
      filterOto.addEventListener("change", handleFilterChange);
    }
    if (filterXeMay) {
      filterXeMay.addEventListener("change", handleFilterChange);
    }
    if (filterXeDap) {
      filterXeDap.addEventListener("change", handleFilterChange);
    }
    if (btnResetFilters) {
      btnResetFilters.addEventListener("click", resetFilters);
    }
  }

  // Handle filter change
  function handleFilterChange() {
    filters.types = [];

    if (filterOto && filterOto.checked) {
      filters.types.push("OTO");
    }
    if (filterXeMay && filterXeMay.checked) {
      filters.types.push("XEMAY");
    }
    if (filterXeDap && filterXeDap.checked) {
      filters.types.push("XEDAP");
    }

    applyFilters();
  }

  // Apply filters and render
  function applyFilters() {
    let filtered = allVehicles;

    // Filter by type
    if (filters.types.length > 0) {
      filtered = filtered.filter((v) => filters.types.includes(v.vehicleType));
    }

    // Filter by price (can be extended)
    // filtered = filtered.filter(v =>
    //   v.pricePerDay >= filters.priceMin &&
    //   v.pricePerDay <= filters.priceMax
    // );

    renderVehicles(filtered);
  }

  // Reset all filters
  function resetFilters() {
    if (filterOto) filterOto.checked = false;
    if (filterXeMay) filterXeMay.checked = false;
    if (filterXeDap) filterXeDap.checked = false;

    filters.types = [];
    applyFilters();
  }

  // Render vehicles to grid
  function renderVehicles(vehicles) {
    if (!vehicleGrid) return;

    // Update count
    if (resultCount) {
      resultCount.textContent = `${vehicles.length} phương tiện tìm thấy`;
    }

    if (vehicles.length === 0) {
      vehicleGrid.innerHTML = `
        <div class="col-span-full text-center py-12">
          <p class="text-[#617589] text-lg">Không tìm thấy phương tiện phù hợp</p>
        </div>
      `;
      return;
    }

    vehicleGrid.innerHTML = vehicles
      .map((vehicle) => createVehicleCard(vehicle))
      .join("");
  }

  // Create vehicle card HTML
  function createVehicleCard(vehicle) {
    const typeLabels = {
      OTO: "Ô tô",
      XEMAY: "Xe máy",
      XEDAP: "Xe đạp",
    };

    const typeLabel = typeLabels[vehicle.vehicleType] || vehicle.vehicleType;
    const imageUrl = vehicle.imageUrl || getDefaultImage(vehicle.vehicleType);
    const rating = vehicle.rating || 4.5;
    const price = formatPrice(vehicle.pricePerDay);

    return `
      <div class="group bg-white dark:bg-slate-900 rounded-xl border border-[#f0f2f4] dark:border-slate-800 overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300">
        <div class="relative h-48 w-full overflow-hidden">
          <img
            alt="${vehicle.name}"
            class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
            src="${imageUrl}"
            onerror="this.src='https://via.placeholder.com/400x300?text=No+Image'"
          />
          <div class="absolute top-3 left-3 bg-white/90 dark:bg-slate-900/90 backdrop-blur-sm px-2 py-1 rounded text-[10px] font-bold uppercase text-primary">
            ${typeLabel} • ${vehicle.brand || ""}
          </div>
        </div>
        <div class="p-5">
          <div class="flex justify-between items-start mb-2">
            <h3 class="text-lg font-bold group-hover:text-primary transition-colors">
              ${vehicle.name}
            </h3>
            <div class="flex items-center gap-1 text-orange-500">
              <span class="material-symbols-outlined text-sm fill-current">star</span>
              <span class="text-xs font-bold text-[#111418] dark:text-white">${rating}</span>
            </div>
          </div>
          <div class="flex items-center gap-1 text-[#617589] text-sm mb-4">
            <span class="material-symbols-outlined text-sm">location_on</span>
            <span>${vehicle.location || "Việt Nam"}</span>
          </div>
          <div class="flex items-end justify-between border-t border-[#f0f2f4] dark:border-slate-800 pt-4 mt-2">
            <div>
              <p class="text-xs text-[#617589]">Giá thuê</p>
              <p class="text-lg font-bold text-primary">
                ${price}<span class="text-xs text-[#617589] font-normal">/ngày</span>
              </p>
            </div>
            <button
              onclick="window.location.href='chi_tiet_xe.html?id=${vehicle.id}'"
              class="bg-primary/10 hover:bg-primary text-primary hover:text-white px-4 py-2 rounded-lg text-sm font-bold transition-all duration-200"
            >
              Xem chi tiết
            </button>
          </div>
        </div>
      </div>
    `;
  }

  // Get default image based on type
  function getDefaultImage(type) {
    const images = {
      OTO: "https://lh3.googleusercontent.com/aida-public/AB6AXuBWSymljZ8TVOZYjVADw1AdXOuk032_3qOc0YquXHzGRrY9zv18dzCQjgLRqY45rx1nsazNlkVpEf7Mm5UgQdnTVtpW8QlxvelqDfycpwEV2erJ3AJ1jS1JuMM0RxkCk6NfmN7-LJr3H2eRHWhE6Sb3XSyMDX07hbsmcqb9lhkboYqOyeCZk1NPb0Vz01y5pSYXPtIBcsQ6QzDLqcMKGG22SWvAvR1mLUJE6g51DNbL0fRBTa2ziTzVRFgwIlKJA0cuRmyLiTKWp7aJ",
      XEMAY:
        "https://lh3.googleusercontent.com/aida-public/AB6AXuAnk96OdOLrKbhDYa1FZtVeZpFrIR2_E9dkxZDI6oAlLDCG1GlM6nne9IMosEkP6Xj8e-x1n8fFSnVJ5MPV4-nnK5w_mwFWuwZfBbTBgMi54-nXESggrsAbi5AuTTwrE8nkU4Vx0zjZRTBXFF_vt844D5JsAvbZM-McVIXTYX70WmJUHbzY8kTGs1tvdmpd0VQhgdRJV9nYjjBWZPYEiOd-4TJ9IvDnZhd4iljkDvgxGam54nRXojlKbq6E5e00O5jPaezWhaaSPqHH",
      XEDAP:
        "https://lh3.googleusercontent.com/aida-public/AB6AXuC7VKrJaczKrbY2RtCROCbOE-mwk8lQzy_eDnZu_fJQUNLasQYL0VlcGg1L5t5Up7PK96tlEwxf7Fbaj_qasoDEPxeW7fquxVDGnSPoJW9G0b2N5aZouVW10IcmPdQFF_2EEj8W4VNJBsCsosN2GPixA3iR7WnwEFfqqsNcUbQjUrf71oYw2M7F08-nuOcKcpsPKjSYkq6pYKcnogCiUpYC36ZL8rPRHg-Di7pXyahb-7BzN-N0DDrzT_upOwxmPIfKy5wkrUSXuAtR",
    };
    return images[type] || "https://via.placeholder.com/400x300?text=Vehicle";
  }

  // Format price
  function formatPrice(price) {
    return (
      new Intl.NumberFormat("vi-VN", {
        style: "decimal",
      }).format(price) + "đ"
    );
  }

  // Show error message
  function showError(message) {
    if (vehicleGrid) {
      vehicleGrid.innerHTML = `
        <div class="col-span-full text-center py-12">
          <p class="text-red-500 text-lg">${message}</p>
        </div>
      `;
    }
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
