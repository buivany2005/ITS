// Vehicle listing page with filters
(function () {
  // DOM Elements
  const filterOto = document.getElementById("filter-type-oto");
  const filterXeMay = document.getElementById("filter-type-xemay");
  const filterXeDap = document.getElementById("filter-type-xedap");
  const filterLocation = document.getElementById("filter-location");
  const filterPriceMin = document.getElementById("filter-price-min");
  const filterPriceMax = document.getElementById("filter-price-max");
  const priceRangeTrack = document.getElementById("price-range-track");
  const priceMinLabel = document.getElementById("price-min-label");
  const priceMaxLabel = document.getElementById("price-max-label");
  const btnResetFilters = document.getElementById("btn-reset-filters");
  const sortSelect = document.getElementById("sort-select");
  const searchInput = document.getElementById("search-input");
  const searchButton = document.getElementById("search-button");
  const paginationContainer = document.getElementById("pagination-container");
  const vehicleGrid = document.querySelector(
    ".grid.grid-cols-1.md\\:grid-cols-2.xl\\:grid-cols-3",
  );
  const resultCount = document.querySelector("h1.text-2xl");

  // State
  let allVehicles = [];
  let filters = {
    types: [], // ["oto", "xemay", "xedap"]
    priceMin: 0,
    priceMax: 10000000,
    location: "all",
    available: true,
  };
  let currentSort = "popular"; // Default sort option
  let searchQuery = ""; // Search query
  let currentPage = 1; // Current page
  const itemsPerPage = 12; // Items per page

  // Initialize
  async function init() {
    console.log("Init called, URL:", window.location.href);
    parseQueryParams(); // Parse first (don't apply yet, no data)
    await loadVehicles();
    attachEventListeners();
    // After loading, apply filters (will render filtered or all vehicles)
    applyFilters();
  }

  // Parse query params and set filters (without applying)
  function parseQueryParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const typeParam = urlParams.get("type");
    const locationParam = urlParams.get("location");
    const priceMinParam = urlParams.get("priceMin");
    const priceMaxParam = urlParams.get("priceMax");

    console.log(
      "URL Params - type:",
      typeParam,
      "location:",
      locationParam,
      "priceMin:",
      priceMinParam,
      "priceMax:",
      priceMaxParam,
    );

    let hasFilters = false;

    // Áp dụng bộ lọc loại xe
    if (typeParam) {
      const typeUpper = typeParam.toUpperCase();
      filters.types = [typeUpper];
      hasFilters = true;

      // Check the corresponding checkbox
      if (typeUpper === "OTO" && filterOto) {
        filterOto.checked = true;
        console.log("Checked OTO checkbox");
      }
      if (typeUpper === "XEMAY" && filterXeMay) {
        filterXeMay.checked = true;
        console.log("Checked XEMAY checkbox");
      }
      if (typeUpper === "XEDAP" && filterXeDap) {
        filterXeDap.checked = true;
        console.log("Checked XEDAP checkbox");
      }
    }

    // Áp dụng bộ lọc khu vực
    if (locationParam) {
      filters.location = locationParam;
      hasFilters = true;

      // Set the location select value
      if (filterLocation) {
        // Find matching option
        const options = filterLocation.options;
        for (let i = 0; i < options.length; i++) {
          if (
            options[i].value
              .toLowerCase()
              .includes(locationParam.toLowerCase()) ||
            locationParam.toLowerCase().includes(options[i].value.toLowerCase())
          ) {
            filterLocation.value = options[i].value;
            console.log("Set location to:", options[i].value);
            break;
          }
        }
      }
    }

    // Áp dụng bộ lọc giá từ URL
    if (priceMinParam) {
      const minVal = parseInt(priceMinParam);
      if (!isNaN(minVal)) {
        filters.priceMin = minVal;
        if (filterPriceMin) filterPriceMin.value = minVal;
        hasFilters = true;
      }
    }
    if (priceMaxParam) {
      const maxVal = parseInt(priceMaxParam);
      if (!isNaN(maxVal)) {
        filters.priceMax = maxVal;
        if (filterPriceMax) filterPriceMax.value = maxVal;
        hasFilters = true;
      }
    }

    // Update price slider UI if we have price params
    if (priceMinParam || priceMaxParam) {
      updatePriceRangeUI();
    }

    // Nếu có bộ lọc từ URL, chỉ log (không apply vì chưa có data)
    if (hasFilters) {
      console.log("Filters set from URL:", filters);
    }
  }

  // Fetch vehicles from API
  async function loadVehicles() {
    try {
      console.log("Loading vehicles from API...");
      const data = await window.api.searchVehicles({});
      allVehicles = data || [];
      console.log("Loaded vehicles:", allVehicles.length);
      // Don't render here - let init() handle it after applying filters
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
    if (filterLocation) {
      filterLocation.addEventListener("change", handleLocationChange);
    }
    if (filterPriceMin) {
      filterPriceMin.addEventListener("input", handlePriceSlider);
    }
    if (filterPriceMax) {
      filterPriceMax.addEventListener("input", handlePriceSlider);
    }
    if (btnResetFilters) {
      btnResetFilters.addEventListener("click", resetFilters);
    }
    if (sortSelect) {
      sortSelect.addEventListener("change", handleSortChange);
    }
    if (searchInput) {
      searchInput.addEventListener("input", handleSearch);
      // Support Enter key
      searchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
          handleSearch();
        }
      });
    }
    if (searchButton) {
      searchButton.addEventListener("click", handleSearch);
    }
  }

  // Format price to Vietnamese format
  function formatPriceLabel(value) {
    return new Intl.NumberFormat("vi-VN").format(value) + "đ";
  }

  // Update price range slider track and labels
  function updatePriceRangeUI() {
    const min = parseInt(filterPriceMin.value);
    const max = parseInt(filterPriceMax.value);
    const rangeMin = parseInt(filterPriceMin.min);
    const rangeMax = parseInt(filterPriceMin.max);

    // Calculate percentages for track position
    const leftPercent = ((min - rangeMin) / (rangeMax - rangeMin)) * 100;
    const rightPercent = 100 - ((max - rangeMin) / (rangeMax - rangeMin)) * 100;

    if (priceRangeTrack) {
      priceRangeTrack.style.left = leftPercent + "%";
      priceRangeTrack.style.right = rightPercent + "%";
    }

    // Update labels
    if (priceMinLabel) priceMinLabel.textContent = formatPriceLabel(min);
    if (priceMaxLabel) priceMaxLabel.textContent = formatPriceLabel(max);
  }

  // Handle price slider change
  function handlePriceSlider() {
    let minVal = parseInt(filterPriceMin.value);
    let maxVal = parseInt(filterPriceMax.value);

    // Prevent crossing
    if (minVal > maxVal - 100000) {
      if (this === filterPriceMin) {
        filterPriceMin.value = maxVal - 100000;
        minVal = maxVal - 100000;
      } else {
        filterPriceMax.value = minVal + 100000;
        maxVal = minVal + 100000;
      }
    }

    filters.priceMin = minVal;
    filters.priceMax = maxVal;

    updatePriceRangeUI();
    applyFilters();
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

  // Handle location change
  function handleLocationChange() {
    filters.location = filterLocation.value;
    applyFilters();
  }

  // Handle sort change
  function handleSortChange() {
    currentSort = sortSelect.value;
    console.log("Sort changed to:", currentSort);
    applyFilters();
  }

  // Handle search input
  function handleSearch() {
    searchQuery = searchInput.value.trim().toLowerCase();
    console.log("Search query:", searchQuery);
    applyFilters();
  }

  // Sort vehicles based on current sort option
  function sortVehicles(vehicles) {
    const sorted = [...vehicles]; // Create a copy to avoid mutating original
    console.log(
      "Sorting vehicles with:",
      currentSort,
      "Total:",
      vehicles.length,
    );

    switch (currentSort) {
      case "price-asc":
        const ascSorted = sorted.sort(
          (a, b) => (a.pricePerDay || 0) - (b.pricePerDay || 0),
        );
        console.log(
          "Price ASC - First 3:",
          ascSorted
            .slice(0, 3)
            .map((v) => ({ name: v.name, price: v.pricePerDay })),
        );
        return ascSorted;
      case "price-desc":
        const descSorted = sorted.sort(
          (a, b) => (b.pricePerDay || 0) - (a.pricePerDay || 0),
        );
        console.log(
          "Price DESC - First 3:",
          descSorted
            .slice(0, 3)
            .map((v) => ({ name: v.name, price: v.pricePerDay })),
        );
        return descSorted;
      case "rating":
        return sorted.sort((a, b) => (b.rating || 0) - (a.rating || 0));
      case "popular":
      default:
        // Popular sort: higher rating and lower price get priority
        return sorted.sort((a, b) => {
          const scoreA = (a.rating || 4.0) * 10 - (a.pricePerDay || 0) / 100000;
          const scoreB = (b.rating || 4.0) * 10 - (b.pricePerDay || 0) / 100000;
          return scoreB - scoreA;
        });
    }
  }

  // Apply filters and render
  function applyFilters() {
    let filtered = allVehicles;
    console.log("applyFilters called, allVehicles count:", allVehicles.length);
    console.log("Current filters:", JSON.stringify(filters));
    console.log("Search query:", searchQuery);

    // Filter by search query
    if (searchQuery) {
      filtered = filtered.filter((v) => {
        const name = (v.name || "").toLowerCase();
        const brand = (v.brand || "").toLowerCase();
        const location = (v.location || "").toLowerCase();
        const description = (v.description || "").toLowerCase();

        return (
          name.includes(searchQuery) ||
          brand.includes(searchQuery) ||
          location.includes(searchQuery) ||
          description.includes(searchQuery)
        );
      });
      console.log("After search filter:", filtered.length);
    }

    // Filter by type
    if (filters.types.length > 0) {
      filtered = filtered.filter((v) => filters.types.includes(v.vehicleType));
      console.log("After type filter:", filtered.length);
    }

    // Filter by location
    if (filters.location && filters.location !== "all") {
      filtered = filtered.filter((v) => {
        if (!v.location) return false;
        // Kiểm tra location có chứa khu vực được chọn không
        return v.location
          .toLowerCase()
          .includes(filters.location.toLowerCase());
      });
      console.log("After location filter:", filtered.length);
    }

    // Filter by price
    if (filters.priceMin > 0 || filters.priceMax < 10000000) {
      filtered = filtered.filter((v) => {
        const price = v.pricePerDay || 0;
        return price >= filters.priceMin && price <= filters.priceMax;
      });
      console.log("After price filter:", filtered.length);
    }

    console.log("Final filtered count:", filtered.length);

    // Apply sorting
    const sorted = sortVehicles(filtered);
    renderVehicles(sorted);
  }

  // Reset all filters
  function resetFilters() {
    if (filterOto) filterOto.checked = false;
    if (filterXeMay) filterXeMay.checked = false;
    if (filterXeDap) filterXeDap.checked = false;
    if (filterLocation) filterLocation.value = "all";
    if (filterPriceMin) filterPriceMin.value = 0;
    if (filterPriceMax) filterPriceMax.value = 10000000;
    if (searchInput) searchInput.value = "";

    filters.types = [];
    filters.location = "all";
    filters.priceMin = 0;
    filters.priceMax = 10000000;
    searchQuery = "";
    currentPage = 1; // Reset to page 1

    updatePriceRangeUI();
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
      if (paginationContainer) paginationContainer.innerHTML = "";
      return;
    }

    // Calculate pagination
    const totalPages = Math.ceil(vehicles.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedVehicles = vehicles.slice(startIndex, endIndex);

    // Render only current page vehicles
    vehicleGrid.innerHTML = paginatedVehicles
      .map((vehicle) => createVehicleCard(vehicle))
      .join("");

    // Render pagination controls
    renderPagination(totalPages);
  }

  // Render pagination controls
  function renderPagination(totalPages) {
    if (!paginationContainer || totalPages <= 1) {
      if (paginationContainer) paginationContainer.innerHTML = "";
      return;
    }

    let paginationHTML = "";

    // Previous button
    paginationHTML += `
      <button
        onclick="window.vehicleListPage.goToPage(${currentPage - 1})"
        class="size-10 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-800 bg-white dark:bg-slate-900 hover:text-primary transition-colors ${currentPage === 1 ? "opacity-50 cursor-not-allowed" : ""}"
        ${currentPage === 1 ? "disabled" : ""}
      >
        <span class="material-symbols-outlined">chevron_left</span>
      </button>
    `;

    // Page numbers
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage < maxVisiblePages - 1) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    // First page
    if (startPage > 1) {
      paginationHTML += `
        <button
          onclick="window.vehicleListPage.goToPage(1)"
          class="size-10 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-800 bg-white dark:bg-slate-900 hover:text-primary font-bold transition-colors"
        >1</button>
      `;
      if (startPage > 2) {
        paginationHTML += '<span class="px-2 text-[#617589]">...</span>';
      }
    }

    // Page buttons
    for (let i = startPage; i <= endPage; i++) {
      paginationHTML += `
        <button
          onclick="window.vehicleListPage.goToPage(${i})"
          class="size-10 flex items-center justify-center rounded-lg font-bold transition-colors ${
            i === currentPage
              ? "bg-primary text-white"
              : "border border-[#dbe0e6] dark:border-slate-800 bg-white dark:bg-slate-900 hover:text-primary"
          }"
        >${i}</button>
      `;
    }

    // Last page
    if (endPage < totalPages) {
      if (endPage < totalPages - 1) {
        paginationHTML += '<span class="px-2 text-[#617589]">...</span>';
      }
      paginationHTML += `
        <button
          onclick="window.vehicleListPage.goToPage(${totalPages})"
          class="size-10 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-800 bg-white dark:bg-slate-900 hover:text-primary font-bold transition-colors"
        >${totalPages}</button>
      `;
    }

    // Next button
    paginationHTML += `
      <button
        onclick="window.vehicleListPage.goToPage(${currentPage + 1})"
        class="size-10 flex items-center justify-center rounded-lg border border-[#dbe0e6] dark:border-slate-800 bg-white dark:bg-slate-900 hover:text-primary transition-colors ${currentPage === totalPages ? "opacity-50 cursor-not-allowed" : ""}"
        ${currentPage === totalPages ? "disabled" : ""}
      >
        <span class="material-symbols-outlined">chevron_right</span>
      </button>
    `;

    paginationContainer.innerHTML = paginationHTML;
  }

  // Go to specific page
  function goToPage(page) {
    const totalItems =
      document.querySelectorAll(
        ".grid.grid-cols-1.md\\:grid-cols-2.xl\\:grid-cols-3",
      ).length > 0
        ? allVehicles.length
        : 0;
    const totalPages = Math.ceil(totalItems / itemsPerPage);

    if (page < 1 || page > totalPages) return;

    currentPage = page;
    // Scroll to top
    window.scrollTo({ top: 0, behavior: "smooth" });
    applyFilters();
  }

  // Expose goToPage globally for onclick handlers
  window.vehicleListPage = {
    goToPage: goToPage,
  };

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

    // Determine availability status
    const isAvailable = vehicle.available !== false; // Default to available if not specified
    const availabilityBadge = isAvailable
      ? '<div class="absolute top-3 right-3 bg-green-500 text-white px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1"><span class="material-symbols-outlined text-sm">check_circle</span>Còn trống</div>'
      : '<div class="absolute top-3 right-3 bg-red-500 text-white px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1"><span class="material-symbols-outlined text-sm">cancel</span>Đã đặt</div>';

    return `
      <div class="group bg-white dark:bg-slate-900 rounded-xl border border-[#f0f2f4] dark:border-slate-800 overflow-hidden shadow-sm hover:shadow-xl transition-all duration-300 ${!isAvailable ? "opacity-75" : ""}">
        <div class="relative h-48 w-full overflow-hidden">
          <img
            alt="${vehicle.name}"
            class="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
            src="${imageUrl}"
            onerror="this.src='/img/oto/Toyota_Granvia_2021.png'"
          />
          <div class="absolute top-3 left-3 bg-white/90 dark:bg-slate-900/90 backdrop-blur-sm px-2 py-1 rounded text-[10px] font-bold uppercase text-primary">
            ${typeLabel} • ${vehicle.brand || ""}
          </div>
          ${availabilityBadge}
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
              class="${isAvailable ? "bg-primary/10 hover:bg-primary text-primary hover:text-white" : "bg-gray-300 text-gray-500 cursor-not-allowed"} px-4 py-2 rounded-lg text-sm font-bold transition-all duration-200"
              ${!isAvailable ? "disabled" : ""}
            >
              ${isAvailable ? "Xem chi tiết" : "Đã đặt"}
            </button>
          </div>
        </div>
      </div>
    `;
  }

  // Get default image based on type
  function getDefaultImage(type) {
    const images = {
      OTO: "/img/oto/Toyota_Granvia_2021.png",
      XEMAY: "/img/oto/xe_may/Exciter_150.png",
      XEDAP: "/img/oto/xe_dap/Road_Fascino.png",
    };
    return images[type] || "/img/oto/Toyota_Granvia_2021.png";
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
