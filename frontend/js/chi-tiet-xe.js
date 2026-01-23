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
    const specContainer = document.querySelector(
      ".grid.grid-cols-2.sm\\:grid-cols-4",
    );
    if (!specContainer) return;

    let specs = [];

    if (vehicle.vehicleType === "OTO") {
      specs = [
        {
          icon: "ev_station",
          label: "Nhiên liệu",
          value: vehicle.fuelType || "Xăng",
        },
        {
          icon: "settings_input_component",
          label: "Hộp số",
          value: vehicle.transmission || "Tự động",
        },
        {
          icon: "airline_seat_recline_extra",
          label: "Số ghế",
          value: `${vehicle.seats || 5} Chỗ`,
        },
        {
          icon: "speed",
          label: "Động cơ",
          value: vehicle.model || "1.5L",
        },
      ];
    } else if (vehicle.vehicleType === "XEMAY") {
      specs = [
        {
          icon: "ev_station",
          label: "Nhiên liệu",
          value: vehicle.fuelType || "Xăng",
        },
        {
          icon: "speed",
          label: "Công suất",
          value: vehicle.model || "150cc",
        },
        {
          icon: "airline_seat_recline_extra",
          label: "Số ghế",
          value: `${vehicle.seats || 2} Chỗ`,
        },
        {
          icon: "local_shipping",
          label: "Tải trọng",
          value: "150kg",
        },
      ];
    } else if (vehicle.vehicleType === "XEDAP") {
      specs = [
        {
          icon: "pedal_bike",
          label: "Loại xe",
          value: vehicle.fuelType === "Điện" ? "Xe đạp điện" : "Xe đạp cơ",
        },
        {
          icon: "battery_full",
          label: "Pin",
          value: vehicle.fuelType === "Điện" ? "36V 10Ah" : "Không áp dụng",
        },
        {
          icon: "airline_seat_recline_extra",
          label: "Số ghế",
          value: `${vehicle.seats || 1} Chỗ`,
        },
        {
          icon: "local_shipping",
          label: "Tải trọng",
          value: "120kg",
        },
      ];
    }

    specContainer.innerHTML = specs
      .map(
        (spec) => `
      <div class="flex flex-col items-center gap-2 p-4 bg-background-light dark:bg-gray-800 rounded-xl">
        <span class="material-symbols-outlined text-primary text-3xl">${spec.icon}</span>
        <span class="text-xs text-[#617589] dark:text-gray-400 font-medium uppercase">${spec.label}</span>
        <span class="text-sm font-bold">${spec.value}</span>
      </div>
    `,
      )
      .join("");

    // Update rental conditions, insurance, and regulations based on vehicle type
    updateRentalConditions(vehicle);
    updateInsurance(vehicle);
    updateRegulations(vehicle);
  }

  function updateRentalConditions(vehicle) {
    const dieuKienTab = document.getElementById("dieu-kien");
    if (!dieuKienTab) return;

    let conditions = [];

    if (vehicle.vehicleType === "OTO") {
      conditions = [
        {
          icon: "id_card",
          title: "Giấy tờ thuê xe",
          description:
            "Yêu cầu CCCD gắn chip (đối chiếu) và Bằng lái xe hạng B1 trở lên.",
        },
        {
          icon: "account_balance_wallet",
          title: "Tài sản thế chấp",
          description:
            "15 triệu đồng (tiền mặt/chuyển khoản) hoặc Xe máy có giá trị tương đương (kèm cavet gốc).",
        },
        {
          icon: "priority_high",
          title: "Lưu ý",
          description:
            "Khách hàng phải trên 21 tuổi và có kinh nghiệm lái xe tối thiểu 1 năm.",
        },
      ];
    } else if (vehicle.vehicleType === "XEMAY") {
      conditions = [
        {
          icon: "id_card",
          title: "Giấy tờ thuê xe",
          description:
            "Yêu cầu CCCD gắn chip (đối chiếu) và Bằng lái xe hạng A1 trở lên.",
        },
        {
          icon: "account_balance_wallet",
          title: "Tài sản thế chấp",
          description:
            "5 triệu đồng (tiền mặt/chuyển khoản) hoặc Xe đạp điện có giá trị tương đương.",
        },
        {
          icon: "priority_high",
          title: "Lưu ý",
          description:
            "Khách hàng phải trên 18 tuổi và có kinh nghiệm lái xe máy tối thiểu 6 tháng.",
        },
      ];
    } else if (vehicle.vehicleType === "XEDAP") {
      conditions = [
        {
          icon: "id_card",
          title: "Giấy tờ thuê xe",
          description: "Yêu cầu CCCD gắn chip (đối chiếu).",
        },
        {
          icon: "account_balance_wallet",
          title: "Tài sản thế chấp",
          description:
            "1 triệu đồng (tiền mặt/chuyển khoản) hoặc tiền cọc bằng thẻ tín dụng.",
        },
        {
          icon: "priority_high",
          title: "Lưu ý",
          description:
            "Khách hàng phải trên 16 tuổi. Không yêu cầu bằng lái xe.",
        },
      ];
    }

    dieuKienTab.innerHTML = conditions
      .map(
        (condition) => `
      <div class="flex gap-4">
        <span class="material-symbols-outlined text-primary">${condition.icon}</span>
        <div>
          <p class="font-bold">${condition.title}</p>
          <p class="text-sm text-[#617589] dark:text-gray-400">${condition.description}</p>
        </div>
      </div>
    `,
      )
      .join("");
  }

  function updateInsurance(vehicle) {
    const baoHiemTab = document.getElementById("bao-hiem");
    if (!baoHiemTab) return;

    let insurance = [];

    if (vehicle.vehicleType === "OTO") {
      insurance = [
        {
          icon: "shield_with_house",
          title: "Bảo hiểm Trách nhiệm Dân sự",
          description:
            "Bắt buộc. Bảo vệ thiệt hại gây ra cho bên thứ ba. Bảo hiểm tối thiểu 1 tỷ đồng.",
        },
        {
          icon: "verified",
          title: "Bảo hiểm Hạn chế Phạm vi",
          description:
            "Tùy chọn. Bảo phí: 2% tổng giá trị thuê xe. Được bảo hiểm từ 70% giá trị phương tiện.",
        },
        {
          icon: "info",
          title: "Chi tiết",
          description:
            "Không bảo hiểm cho hư hỏng do chế độ bảo dưỡng không đúng hay va chạm do tắc đường.",
        },
      ];
    } else if (vehicle.vehicleType === "XEMAY") {
      insurance = [
        {
          icon: "shield_with_house",
          title: "Bảo hiểm Trách nhiệm Dân sự",
          description:
            "Bắt buộc. Bảo vệ thiệt hại gây ra cho bên thứ ba. Bảo hiểm tối thiểu 50 triệu đồng.",
        },
        {
          icon: "verified",
          title: "Bảo hiểm Vật chất",
          description:
            "Tùy chọn. Bảo phí: 1.5% tổng giá trị thuê xe. Được bảo hiểm từ 80% giá trị phương tiện.",
        },
        {
          icon: "info",
          title: "Chi tiết",
          description:
            "Không bảo hiểm cho hư hỏng do lốp xe, phụ tùng thay thế định kỳ.",
        },
      ];
    } else if (vehicle.vehicleType === "XEDAP") {
      insurance = [
        {
          icon: "shield_with_house",
          title: "Bảo hiểm Trách nhiệm Dân sự",
          description:
            "Bắt buộc. Bảo vệ thiệt hại gây ra cho bên thứ ba. Bảo hiểm tối thiểu 10 triệu đồng.",
        },
        {
          icon: "verified",
          title: "Bảo hiểm Vật chất",
          description:
            "Tùy chọn. Bảo phí: 1% tổng giá trị thuê xe. Được bảo hiểm từ 90% giá trị phương tiện.",
        },
        {
          icon: "info",
          title: "Chi tiết",
          description:
            "Không bảo hiểm cho hư hỏng do sử dụng sai mục đích hoặc vận chuyển hàng hóa nặng.",
        },
      ];
    }

    baoHiemTab.innerHTML = insurance
      .map(
        (item) => `
      <div class="flex gap-4">
        <span class="material-symbols-outlined text-primary">${item.icon}</span>
        <div>
          <p class="font-bold">${item.title}</p>
          <p class="text-sm text-[#617589] dark:text-gray-400">${item.description}</p>
        </div>
      </div>
    `,
      )
      .join("");
  }

  function updateRegulations(vehicle) {
    const quyDinhTab = document.getElementById("quy-dinh");
    if (!quyDinhTab) return;

    let regulations = [];

    if (vehicle.vehicleType === "OTO") {
      regulations = [
        {
          icon: "schedule",
          title: "Thời gian thuê xe",
          description:
            "Tối thiểu 1 ngày. Nhận xe lúc 8:00 sáng, trả xe lúc 18:00 chiều. Trễ giờ tính thêm phí 100.000đ/giờ.",
        },
        {
          icon: "local_gas_station",
          title: "Xăng dầu",
          description:
            "Xe được giao với đầy xăng. Khách hàng phải trả về đầy xăng. Thiếu xăng tính theo giá thị trường.",
        },
        {
          icon: "warning",
          title: "Lỗi vi phạm giao thông",
          description:
            "Khách hàng chịu trách nhiệm thanh toán mọi khoản phạt giao thông phát sinh trong thời gian thuê.",
        },
      ];
    } else if (vehicle.vehicleType === "XEMAY") {
      regulations = [
        {
          icon: "schedule",
          title: "Thời gian thuê xe",
          description:
            "Tối thiểu 1 ngày. Nhận xe lúc 8:00 sáng, trả xe lúc 18:00 chiều. Trễ giờ tính thêm phí 50.000đ/giờ.",
        },
        {
          icon: "local_gas_station",
          title: "Xăng dầu",
          description:
            "Xe được giao với đầy xăng. Khách hàng phải trả về đầy xăng. Thiếu xăng tính theo giá thị trường.",
        },
        {
          icon: "warning",
          title: "Lỗi vi phạm giao thông",
          description:
            "Khách hàng chịu trách nhiệm thanh toán mọi khoản phạt giao thông phát sinh trong thời gian thuê.",
        },
      ];
    } else if (vehicle.vehicleType === "XEDAP") {
      regulations = [
        {
          icon: "schedule",
          title: "Thời gian thuê xe",
          description:
            "Tối thiểu 4 giờ. Nhận xe từ 8:00 sáng đến 18:00 chiều. Trễ giờ tính thêm phí 20.000đ/giờ.",
        },
        {
          icon: "battery_full",
          title: "Pin và sạc",
          description:
            vehicle.fuelType === "Điện"
              ? "Xe được giao với pin đầy. Khách hàng phải trả về pin đầy. Có thể thuê kèm bộ sạc."
              : "Không áp dụng.",
        },
        {
          icon: "warning",
          title: "Lỗi vi phạm",
          description:
            "Khách hàng chịu trách nhiệm về mất mát hoặc hư hỏng xe trong thời gian thuê.",
        },
      ];
    }

    quyDinhTab.innerHTML = regulations
      .map(
        (regulation) => `
      <div class="flex gap-4">
        <span class="material-symbols-outlined text-primary">${regulation.icon}</span>
        <div>
          <p class="font-bold">${regulation.title}</p>
          <p class="text-sm text-[#617589] dark:text-gray-400">${regulation.description}</p>
        </div>
      </div>
    `,
      )
      .join("");
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

    // Tab switching functionality
    const tabButtons = document.querySelectorAll(".tab-btn");
    const tabContents = document.querySelectorAll(".tab-content");

    tabButtons.forEach((button) => {
      button.addEventListener("click", () => {
        const targetTab = button.getAttribute("data-tab");

        // Remove active state from all buttons
        tabButtons.forEach((btn) => {
          btn.classList.remove("text-primary", "border-b-2", "border-primary");
          btn.classList.add("text-[#617589]", "dark:text-gray-400");
        });

        // Add active state to clicked button
        button.classList.remove("text-[#617589]", "dark:text-gray-400");
        button.classList.add("text-primary", "border-b-2", "border-primary");

        // Hide all tab contents
        tabContents.forEach((content) => {
          content.classList.add("hidden");
        });

        // Show target tab content
        const targetContent = document.getElementById(targetTab);
        if (targetContent) {
          targetContent.classList.remove("hidden");
        }
      });
    });
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
