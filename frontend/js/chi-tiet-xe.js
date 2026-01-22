// Vehicle detail page handler
(function () {
  // Get vehicle ID from URL
  function getVehicleId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
  }

  // Initialize
  async function init() {
    const vehicleId = getVehicleId();
    if (!vehicleId) {
      showError("Không tìm thấy ID phương tiện");
      return;
    }
    await loadVehicleDetail(vehicleId);
    attachEventListeners();
  }

  // Fetch vehicle detail from API using window.api
  async function loadVehicleDetail(id) {
    try {
      console.log("Loading vehicle with ID:", id);
      const vehicle = await window.api.getVehicle(id);
      console.log("Vehicle data from API:", vehicle);
      renderVehicleDetail(vehicle);
    } catch (err) {
      console.error("Error loading vehicle:", err);
      showError("Không thể tải thông tin phương tiện");
    }
  }

  // Render vehicle detail to page
  function renderVehicleDetail(vehicle) {
    console.log(
      "Rendering vehicle:",
      vehicle.name,
      "Image URL:",
      vehicle.imageUrl,
    );

    // Update title by ID
    const titleElement = document.getElementById("vehicle-name");
    if (titleElement) {
      titleElement.textContent = vehicle.name || "Phương tiện";
    }

    // Update page title
    document.title = `${vehicle.name} | MotoRent VN`;

    // Get image URL - use vehicle's image or fallback to default based on type
    const imageUrl = getImageUrl(vehicle);
    console.log("Final image URL:", imageUrl);

    // Update main image by ID
    const mainImage = document.getElementById("main-image");
    if (mainImage) {
      mainImage.style.backgroundImage = `url("${imageUrl}")`;
    }

    // Update gallery with vehicle image
    updateGallery(vehicle, imageUrl);

    // Update location by ID
    const locationElement = document.getElementById("vehicle-location");
    if (locationElement) {
      locationElement.innerHTML = `<span class="material-symbols-outlined text-red-500 text-lg">location_on</span> ${vehicle.location || "Việt Nam"}`;
    }

    // Update price by ID
    const priceElement = document.getElementById("vehicle-price");
    if (priceElement) {
      priceElement.textContent = formatPrice(vehicle.pricePerDay);
    }

    // Update status
    const statusElement = document.getElementById("vehicle-status");
    if (statusElement) {
      const isAvailable =
        vehicle.available !== false &&
        vehicle.status !== "RENTED" &&
        vehicle.status !== "MAINTENANCE";
      if (isAvailable) {
        statusElement.textContent = "Còn xe";
        statusElement.className =
          "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider";
      } else {
        statusElement.textContent = "Hết xe";
        statusElement.className =
          "bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400 px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider";
      }
    }

    // Update specifications
    updateSpecifications(vehicle);

    // Store vehicle data for booking
    window.currentVehicle = vehicle;
  }

  // Get valid image URL for vehicle
  function getImageUrl(vehicle) {
    // Check if vehicle has a valid local image URL
    if (vehicle.imageUrl && vehicle.imageUrl.startsWith("/img/")) {
      return vehicle.imageUrl;
    }
    // If URL is external or invalid, use default based on type
    return getDefaultImage(vehicle.vehicleType);
  }

  // Update gallery with vehicle images
  function updateGallery(vehicle, mainImageUrl) {
    const galleryContainer = document.getElementById("gallery-images");
    if (!galleryContainer) return;

    // Get related images based on vehicle type
    const relatedImages = getRelatedImages(vehicle.vehicleType);

    // Build gallery HTML
    galleryContainer.innerHTML =
      relatedImages
        .slice(0, 3)
        .map(
          (img) => `
      <div class="aspect-square rounded-lg bg-cover bg-center cursor-pointer hover:ring-2 hover:ring-primary transition-all shadow-sm"
           style="background-image: url('${img}');"></div>
    `,
        )
        .join("") +
      `
      <div class="relative aspect-square rounded-lg bg-cover bg-center cursor-pointer overflow-hidden group shadow-sm"
           style="background-image: url('${mainImageUrl}');">
        <div class="absolute inset-0 bg-black/50 flex items-center justify-center text-white font-bold group-hover:bg-black/60 transition-all">
          +${relatedImages.length} ảnh
        </div>
      </div>
    `;
  }

  // Get related images based on vehicle type
  function getRelatedImages(type) {
    const imagesByType = {
      OTO: [
        "/img/oto/Toyota_Granvia_2021.png",
        "/img/oto/Toyota_Avanza_Premio_2022.png",
        "/img/oto/Suzuki_Ertiga_2022.png",
        "/img/oto/KIA_Seltos.png",
        "/img/oto/Ford_Ranger_2022%20.png",
      ],
      XEMAY: [
        "/img/oto/xe_may/Vision.png",
        "/img/oto/xe_may/Exciter_150.png",
        "/img/oto/xe_may/Exciter_135.png",
        "/img/oto/xe_may/SH_move.png",
        "/img/oto/xe_may/Wave.png",
      ],
      XEDAP: [
        "/img/oto/xe_dap/Road_Fascino.png",
        "/img/oto/xe_dap/Touring_Merida_Explorer.png",
        "/img/oto/xe_dap/Touring_Mocos.png",
      ],
    };
    return imagesByType[type] || imagesByType.OTO;
  }

  // Update specifications section
  function updateSpecifications(vehicle) {
    const specItems = document.querySelectorAll(".grid .flex.flex-col");
    specItems.forEach((item) => {
      const label = item.querySelector(".text-xs");
      const value = item.querySelector(".text-sm.font-bold");
      if (label && value) {
        const labelText = label.textContent.toLowerCase();
        if (labelText.includes("nhiên liệu")) {
          value.textContent = vehicle.fuelType || "Xăng";
        } else if (labelText.includes("hộp số")) {
          value.textContent = vehicle.transmission || "Tự động";
        } else if (labelText.includes("chỗ")) {
          value.textContent = vehicle.seats || 5;
        } else if (labelText.includes("năm")) {
          value.textContent = vehicle.year || 2023;
        }
      }
    });
  }

  // Attach event listeners
  function attachEventListeners() {
    const bookButton = document.getElementById("btn-book-detail");
    const startDateInput = document.getElementById("start-date-input");
    const endDateInput = document.getElementById("end-date-input");

    // Set initial date values
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const nowStr = formatDateTimeLocal(now);
    const tomorrowStr = formatDateTimeLocal(tomorrow);

    startDateInput.min = nowStr;
    endDateInput.min = tomorrowStr;

    startDateInput.value = nowStr;
    endDateInput.value = tomorrowStr;

    // Add event listeners for date changes
    startDateInput.addEventListener("change", calculateTotal);
    endDateInput.addEventListener("change", calculateTotal);
    bookButton.addEventListener("click", handleBooking);

    // Initial calculation
    calculateTotal();
  }

  // Calculate total cost based on selected dates
  function calculateTotal() {
    const startDateInput = document.getElementById("start-date-input");
    const endDateInput = document.getElementById("end-date-input");
    const summaryDays = document.getElementById("summary-days");
    const summaryServiceFee = document.getElementById("summary-service-fee");
    const summaryTotal = document.getElementById("summary-total");
    const summaryPricePerDay = document.getElementById("summary-price-per-day");

    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    // Validate dates
    if (!startDateInput.value || !endDateInput.value) {
      resetSummary();
      return;
    }

    if (endDate <= startDate) {
      resetSummary();
      return;
    }

    // Get price from vehicle data
    const vehicle = window.currentVehicle;
    const pricePerDay = vehicle.pricePerDay || 0;

    // Calculate days
    const diffTime = endDate - startDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    // Calculate costs
    const subtotal = pricePerDay * diffDays;
    const serviceFee = Math.round(subtotal * 0.05); // 5% service fee
    const total = subtotal + serviceFee;

    // Update summary
    if (summaryDays) summaryDays.textContent = diffDays + " ngày";
    if (summaryServiceFee)
      summaryServiceFee.textContent = formatPrice(serviceFee);
    if (summaryTotal) summaryTotal.textContent = formatPrice(total);
    if (summaryPricePerDay)
      summaryPricePerDay.textContent = formatPrice(pricePerDay) + " / ngày";

    console.log({
      days: diffDays,
      pricePerDay,
      subtotal,
      serviceFee,
      total,
    });
  }

  // Reset summary
  function resetSummary() {
    const summaryDays = document.getElementById("summary-days");
    const summaryServiceFee = document.getElementById("summary-service-fee");
    const summaryTotal = document.getElementById("summary-total");

    if (summaryDays) summaryDays.textContent = "0 ngày";
    if (summaryServiceFee) summaryServiceFee.textContent = "0đ";
    if (summaryTotal) summaryTotal.textContent = "0đ";
  }

  // Handle booking
  async function handleBooking() {
    const startDateInput = document.getElementById("start-date-input");
    const endDateInput = document.getElementById("end-date-input");
    const btnBook = document.getElementById("btn-book-detail");
    const vehicle = window.currentVehicle;

    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    // Validate
    if (!startDateInput.value || !endDateInput.value) {
      alert("Vui lòng chọn ngày bắt đầu và kết thúc!");
      return;
    }

    if (endDate <= startDate) {
      alert("Ngày kết thúc phải sau ngày bắt đầu!");
      return;
    }

    // Calculate final amounts
    const diffTime = endDate - startDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    const pricePerDay = vehicle.pricePerDay || 0;
    const subtotal = pricePerDay * diffDays;
    const serviceFee = Math.round(subtotal * 0.05);
    const total = subtotal + serviceFee;

    // Create booking order
    const orderData = {
      vehicleId: vehicle.id,
      // Backend expects LocalDate (yyyy-MM-dd)
      dateFrom: startDate.toISOString().split("T")[0],
      dateTo: endDate.toISOString().split("T")[0],
      pickupLocation: vehicle.location || "", // optional fields
      returnLocation: vehicle.location || "",
      customerName: undefined,
      customerPhone: undefined,
      customerEmail: undefined,
      notes: undefined,
    };

    console.log("Creating order:", orderData);

    // Debug: Log all localStorage keys
    console.log("All localStorage keys:", Object.keys(localStorage));
    console.log("localStorage content:", {
      user: localStorage.getItem("user"),
      token: localStorage.getItem("token"),
      auth: localStorage.getItem("auth"),
    });

    try {
      // Check if user is logged in
      const user = localStorage.getItem("user");
      console.log("User from localStorage:", user);

      if (!user) {
        console.warn("No user found in localStorage");
        alert("Vui lòng đăng nhập để đặt xe!");
        window.location.href =
          "../login/login.html?redirect=" +
          encodeURIComponent(window.location.href);
        return;
      }

      btnBook.disabled = true;
      btnBook.textContent = "Đang xử lý...";

      // Create order via API
      const response = await window.api.createOrder(orderData);
      console.log("Order created:", response);

      alert("Đặt xe thành công! Chủ xe sẽ xác nhận trong thời gian sớm nhất.");
      window.location.href = "./quan_ly_xe.html";
    } catch (error) {
      console.error("Error creating order:", error);
      alert("Có lỗi xảy ra khi đặt xe. Vui lòng thử lại!");
      btnBook.disabled = false;
      btnBook.textContent = "Đặt thuê ngay";
    }
  }

  // Format datetime-local value
  function formatDateTimeLocal(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    return `${year}-${month}-${day}T${hours}:${minutes}`;
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
    const titleElement = document.querySelector("h1");
    if (titleElement) {
      titleElement.textContent = message;
    }
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
