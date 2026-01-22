// Booking page - Vehicle rental booking
(function () {
  // DOM Elements
  const vehicleImage = document.getElementById("vehicle-image");
  const vehicleName = document.getElementById("vehicle-name");
  const vehicleType = document.getElementById("vehicle-type");
  const vehiclePrice = document.getElementById("vehicle-price");
  const startDateInput = document.getElementById("start-date");
  const endDateInput = document.getElementById("end-date");
  const summaryPricePerDay = document.getElementById("summary-price-per-day");
  const summaryDays = document.getElementById("summary-days");
  const summaryServiceFee = document.getElementById("summary-service-fee");
  const summaryTotal = document.getElementById("summary-total");
  const btnBook = document.getElementById("btn-book");

  // State
  let vehicle = null;
  let pricePerDay = 0;

  // Initialize
  async function init() {
    console.log("Initializing booking page...");

    // Get vehicle ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const vehicleId = urlParams.get("id");

    if (!vehicleId) {
      alert("Không tìm thấy thông tin xe!");
      window.location.href = "danh_sach_xe.html";
      return;
    }

    // Load vehicle data
    await loadVehicle(vehicleId);

    // Set minimum dates (today)
    const now = new Date();
    const tomorrow = new Date(now);
    tomorrow.setDate(tomorrow.getDate() + 1);

    const nowStr = formatDateTimeLocal(now);
    const tomorrowStr = formatDateTimeLocal(tomorrow);

    startDateInput.min = nowStr;
    endDateInput.min = tomorrowStr;

    // Set default values
    startDateInput.value = nowStr;
    endDateInput.value = tomorrowStr;

    // Attach event listeners
    startDateInput.addEventListener("change", calculateTotal);
    endDateInput.addEventListener("change", calculateTotal);
    btnBook.addEventListener("click", handleBooking);

    // Initial calculation
    calculateTotal();
  }

  // Load vehicle data
  async function loadVehicle(vehicleId) {
    try {
      const data = await window.api.getVehicle(vehicleId);
      vehicle = data;
      pricePerDay = vehicle.pricePerDay || 0;

      // Update UI
      if (vehicleName) vehicleName.textContent = vehicle.name || "Xe";
      if (vehicleType) {
        const typeLabels = {
          OTO: "Ô tô",
          XEMAY: "Xe máy",
          XEDAP: "Xe đạp",
        };
        vehicleType.textContent = `${typeLabels[vehicle.vehicleType] || vehicle.vehicleType} • ${vehicle.brand || ""}`;
      }
      if (vehiclePrice) vehiclePrice.textContent = formatPrice(pricePerDay);
      if (summaryPricePerDay)
        summaryPricePerDay.textContent = formatPrice(pricePerDay);

      if (vehicleImage) {
        vehicleImage.src =
          vehicle.imageUrl || getDefaultImage(vehicle.vehicleType);
        vehicleImage.alt = vehicle.name;
        vehicleImage.onerror = function () {
          this.src = "/img/oto/Toyota_Granvia_2021.png";
        };
      }
    } catch (error) {
      console.error("Error loading vehicle:", error);
      alert("Không thể tải thông tin xe!");
      window.location.href = "danh_sach_xe.html";
    }
  }

  // Calculate total cost
  function calculateTotal() {
    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    // Validate dates
    if (!startDateInput.value || !endDateInput.value) {
      resetSummary();
      return;
    }

    if (endDate <= startDate) {
      alert("Ngày kết thúc phải sau ngày bắt đầu!");
      resetSummary();
      return;
    }

    // Calculate days
    const diffTime = endDate - startDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    // Calculate costs
    const subtotal = pricePerDay * diffDays;
    const serviceFee = Math.round(subtotal * 0.05); // 5% service fee
    const total = subtotal + serviceFee;

    // Update summary
    if (summaryDays) summaryDays.textContent = diffDays;
    if (summaryServiceFee)
      summaryServiceFee.textContent = formatPrice(serviceFee);
    if (summaryTotal) summaryTotal.textContent = formatPrice(total);

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
    if (summaryDays) summaryDays.textContent = "0";
    if (summaryServiceFee) summaryServiceFee.textContent = "0";
    if (summaryTotal) summaryTotal.textContent = "0";
  }

  // Handle booking
  async function handleBooking() {
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
    const subtotal = pricePerDay * diffDays;
    const serviceFee = Math.round(subtotal * 0.05);
    const total = subtotal + serviceFee;

    // Create booking order
    const orderData = {
      vehicleId: vehicle.id,
      // Backend OrderRequest expects LocalDate (yyyy-MM-dd)
      dateFrom: startDate.toISOString().split("T")[0],
      dateTo: endDate.toISOString().split("T")[0],
      pickupLocation: vehicle.location || "",
      returnLocation: vehicle.location || "",
      customerName: undefined,
      customerPhone: undefined,
      customerEmail: undefined,
      notes: undefined,
    };

    console.log("Creating order:", orderData);

    try {
      // Check if user is logged in
      const user = localStorage.getItem("user");
      if (!user) {
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

  // Format price
  function formatPrice(price) {
    return new Intl.NumberFormat("vi-VN").format(price);
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

  // Get default image
  function getDefaultImage(type) {
    const images = {
      OTO: "/img/oto/Toyota_Granvia_2021.png",
      XEMAY: "/img/oto/xe_may/Exciter_150.png",
      XEDAP: "/img/oto/xe_dap/Road_Fascino.png",
    };
    return images[type] || "/img/oto/Toyota_Granvia_2021.png";
  }

  // Start when DOM is ready
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
