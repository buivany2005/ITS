// Booking/Cart page handler
(function () {
  // State
  let cart = [];
  let rentalDates = { from: null, to: null };

  // DOM Elements (will be set after DOM loads)
  let btnCheckout, btnContinue, cartTableBody;
  let summaryCount, summarySubtotal, summaryInsurance, summaryTax, summaryTotal;

  // Initialize
  function init() {
    console.log("Initializing cart page...");

    // Get DOM elements
    btnCheckout = document.getElementById("btn-checkout");
    btnContinue = document.getElementById("btn-continue");
    cartTableBody = document.querySelector("tbody");
    summaryCount = document.getElementById("summary-count");
    summarySubtotal = document.getElementById("summary-subtotal");
    summaryInsurance = document.getElementById("summary-insurance");
    summaryTax = document.getElementById("summary-tax");
    summaryTotal = document.getElementById("summary-total");

    // Load cart from storage
    loadCart();

    // Check for new vehicle from URL
    checkForNewVehicle();

    // Render cart items
    renderCart();

    // Attach event listeners
    attachEventListeners();

    // Update summary
    updateSummary();
  }

  // Load cart from session storage
  function loadCart() {
    try {
      const stored = sessionStorage.getItem("cart");
      if (stored) {
        cart = JSON.parse(stored);
        console.log("Loaded cart:", cart);
      }
    } catch (e) {
      console.error("Error loading cart:", e);
      cart = [];
    }
  }

  // Save cart to session storage
  function saveCart() {
    try {
      sessionStorage.setItem("cart", JSON.stringify(cart));
    } catch (e) {
      console.error("Cannot save cart:", e);
    }
  }

  // Check if a new vehicle is being added from URL
  async function checkForNewVehicle() {
    const params = new URLSearchParams(window.location.search);
    const vehicleId = params.get("vehicleId");

    if (vehicleId) {
      try {
        const vehicle = await window.api.getVehicle(vehicleId);
        addToCart(vehicle);
        // Remove vehicleId from URL without reload
        window.history.replaceState(
          {},
          document.title,
          window.location.pathname,
        );
      } catch (err) {
        console.error("Error loading vehicle:", err);
      }
    }
  }

  // Add vehicle to cart
  function addToCart(vehicleData) {
    const existing = cart.find((item) => item.id === vehicleData.id);
    if (!existing) {
      cart.push({
        id: vehicleData.id,
        name: vehicleData.name,
        pricePerDay: vehicleData.pricePerDay,
        imageUrl:
          vehicleData.imageUrl || getDefaultImage(vehicleData.vehicleType),
        vehicleType: vehicleData.vehicleType,
        year: vehicleData.year,
        transmission: vehicleData.transmission,
      });
      saveCart();
      renderCart();
      updateSummary();
    }
  }

  // Get default image based on type
  function getDefaultImage(type) {
    const images = {
      OTO: "/img/oto/Toyota_Granvia_2021.png",
      XEMAY: "/img/oto/xe_may/Vision.png",
      XEDAP: "/img/oto/xe_dap/Road_Fascino.png",
    };
    return images[type] || images.OTO;
  }

  // Render cart items to table
  function renderCart() {
    if (!cartTableBody) return;

    if (cart.length === 0) {
      cartTableBody.innerHTML = `
        <tr>
          <td colspan="4" class="px-6 py-12 text-center text-gray-500">
            <span class="material-symbols-outlined text-5xl mb-4 block">shopping_cart</span>
            <p class="text-lg font-medium">Giỏ hàng trống</p>
            <p class="text-sm mt-2">Hãy chọn phương tiện để thuê</p>
          </td>
        </tr>
      `;
      return;
    }

    const daysText = rentalDates.days ? `${rentalDates.days} ngày` : "0 ngày";
    const dateRangeText =
      rentalDates.from && rentalDates.to
        ? `${formatDate(rentalDates.from)} - ${formatDate(rentalDates.to)}`
        : "Chưa chọn ngày";

    cartTableBody.innerHTML = cart
      .map((item, index) => {
        const itemTotal = rentalDates.days
          ? item.pricePerDay * rentalDates.days
          : 0;
        const vehicleTypeText = getVehicleTypeText(item.vehicleType);

        return `
        <tr class="hover:bg-gray-50/50 dark:hover:bg-gray-800/20 transition-colors" data-item-id="${item.id}">
          <td class="px-6 py-6">
            <div class="flex items-center gap-4">
              <div
                class="size-16 rounded-lg bg-cover bg-center shrink-0 border border-gray-100 dark:border-gray-700"
                style="background-image: url('${item.imageUrl}');"
              ></div>
              <div>
                <p class="font-bold text-[#111418] dark:text-white">${item.name}</p>
                <p class="text-xs text-gray-500 mt-1 uppercase">
                  ${vehicleTypeText} • ${item.transmission || "Tự động"}
                </p>
                <button
                  class="btn-delete text-xs text-red-500 font-bold mt-2 flex items-center gap-1 hover:underline"
                  data-id="${item.id}"
                >
                  <span class="material-symbols-outlined text-sm">delete</span>
                  Xóa
                </button>
              </div>
            </div>
          </td>
          <td class="px-6 py-6 text-sm text-gray-600 dark:text-gray-400">
            <div class="space-y-1">
              <p class="item-dates font-medium text-[#111418] dark:text-gray-200">
                ${dateRangeText}
              </p>
              <p class="item-days text-xs">Tổng cộng: ${daysText}</p>
            </div>
          </td>
          <td class="px-6 py-6 text-sm">
            <p class="item-price font-medium text-gray-900 dark:text-gray-100">
              ${formatPrice(item.pricePerDay)}
            </p>
            <p class="text-xs text-gray-500">/ ngày</p>
          </td>
          <td class="px-6 py-6 text-right">
            <p class="item-total font-bold text-primary text-lg">${formatPrice(itemTotal)}</p>
          </td>
        </tr>
      `;
      })
      .join("");

    // Re-attach delete button listeners
    document.querySelectorAll(".btn-delete").forEach((btn) => {
      btn.addEventListener("click", handleRemoveItem);
    });
  }

  // Get vehicle type text in Vietnamese
  function getVehicleTypeText(type) {
    const types = {
      OTO: "Ô tô",
      XEMAY: "Xe máy",
      XEDAP: "Xe đạp",
    };
    return types[type] || "Phương tiện";
  }

  // Attach event listeners
  function attachEventListeners() {
    // Checkout button - show modal
    if (btnCheckout) {
      btnCheckout.addEventListener("click", showBookingModal);
    }

    // Continue button - go back to vehicle list
    if (btnContinue) {
      btnContinue.addEventListener("click", () => {
        window.location.href = "danh_sach_xe.html";
      });
    }

    // Delete buttons
    document.querySelectorAll(".btn-delete").forEach((btn) => {
      btn.addEventListener("click", handleRemoveItem);
    });
  }

  // Handle remove item from cart
  function handleRemoveItem(e) {
    const btn = e.currentTarget;
    const itemId = btn.dataset.id;

    if (confirm("Bạn có chắc chắn muốn xóa phương tiện này khỏi giỏ hàng?")) {
      cart = cart.filter((item) => String(item.id) !== String(itemId));
      saveCart();
      renderCart();
      updateSummary();
    }
  }

  // Show booking modal for date selection
  function showBookingModal() {
    if (cart.length === 0) {
      alert("Giỏ hàng trống! Vui lòng chọn phương tiện để thuê.");
      return;
    }

    // Get tomorrow as min date
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const minDate = tomorrow.toISOString().slice(0, 16);

    const modalHTML = `
      <div id="bookingModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm">
        <div class="bg-white dark:bg-[#1a2632] w-full max-w-md p-8 rounded-2xl shadow-2xl animate-scale-in">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-2xl font-bold">Thời gian thuê xe</h3>
            <button id="btn-close-modal" class="material-symbols-outlined text-gray-400 hover:text-red-500 cursor-pointer">close</button>
          </div>
          
          <form id="rentalForm" class="space-y-6">
            <div>
              <label class="block text-sm font-semibold mb-2">Ngày nhận xe</label>
              <input 
                type="datetime-local" 
                id="rental-date-from"
                min="${minDate}"
                required 
                class="w-full p-3 rounded-lg border border-gray-300 dark:bg-gray-800 dark:border-gray-700 focus:ring-2 focus:ring-primary outline-none"
              >
            </div>
            
            <div>
              <label class="block text-sm font-semibold mb-2">Ngày trả xe</label>
              <input 
                type="datetime-local" 
                id="rental-date-to"
                min="${minDate}"
                required 
                class="w-full p-3 rounded-lg border border-gray-300 dark:bg-gray-800 dark:border-gray-700 focus:ring-2 focus:ring-primary outline-none"
              >
            </div>

            <div id="rental-preview" class="bg-gray-50 dark:bg-gray-800 p-4 rounded-lg hidden">
              <p class="text-sm text-gray-600 dark:text-gray-400">Thời gian thuê: <span id="preview-days" class="font-bold text-primary">0</span> ngày</p>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-1">Tổng tiền dự kiến: <span id="preview-total" class="font-bold text-primary">0đ</span></p>
            </div>

            <div class="pt-4">
              <button type="submit" class="w-full bg-primary text-white font-bold py-4 rounded-xl hover:bg-blue-600 transition-colors">
                Xác nhận đặt xe
              </button>
            </div>
          </form>
        </div>
      </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    // Add animation style
    const style = document.createElement("style");
    style.textContent = `
      @keyframes scale-in {
        from { transform: scale(0.9); opacity: 0; }
        to { transform: scale(1); opacity: 1; }
      }
      .animate-scale-in { animation: scale-in 0.2s ease-out; }
    `;
    document.head.appendChild(style);

    // Get modal elements
    const modal = document.getElementById("bookingModal");
    const dateFromInput = document.getElementById("rental-date-from");
    const dateToInput = document.getElementById("rental-date-to");
    const previewSection = document.getElementById("rental-preview");
    const previewDays = document.getElementById("preview-days");
    const previewTotal = document.getElementById("preview-total");
    const form = document.getElementById("rentalForm");
    const btnClose = document.getElementById("btn-close-modal");

    // Close modal on button click
    btnClose.addEventListener("click", closeModal);

    // Close modal on backdrop click
    modal.addEventListener("click", (e) => {
      if (e.target === modal) closeModal();
    });

    // Update preview when dates change
    function updatePreview() {
      const from = new Date(dateFromInput.value);
      const to = new Date(dateToInput.value);

      if (from && to && !isNaN(from.getTime()) && !isNaN(to.getTime())) {
        if (to <= from) {
          previewSection.classList.add("hidden");
          return;
        }

        const days = Math.ceil((to - from) / (1000 * 60 * 60 * 24));
        const subtotal = cart.reduce(
          (sum, item) => sum + item.pricePerDay * days,
          0,
        );
        const insurance = Math.round(subtotal * 0.05);
        const tax = Math.round((subtotal + insurance) * 0.1);
        const total = subtotal + insurance + tax;

        previewDays.textContent = days;
        previewTotal.textContent = formatPrice(total);
        previewSection.classList.remove("hidden");
      } else {
        previewSection.classList.add("hidden");
      }
    }

    dateFromInput.addEventListener("change", () => {
      // Set min date for dateTo
      if (dateFromInput.value) {
        dateToInput.min = dateFromInput.value;
      }
      updatePreview();
    });
    dateToInput.addEventListener("change", updatePreview);

    // Handle form submission
    form.addEventListener("submit", (e) => {
      e.preventDefault();

      const from = new Date(dateFromInput.value);
      const to = new Date(dateToInput.value);

      if (to <= from) {
        alert("Ngày trả xe phải sau ngày nhận xe!");
        return;
      }

      // Save rental dates
      rentalDates = {
        from: from,
        to: to,
        days: Math.ceil((to - from) / (1000 * 60 * 60 * 24)),
      };

      // Close modal
      closeModal();

      // Update cart display
      renderCart();
      updateSummary();

      // Show success message
      showNotification("Đã cập nhật thời gian thuê xe!");
    });
  }

  // Close modal
  function closeModal() {
    const modal = document.getElementById("bookingModal");
    if (modal) modal.remove();
  }

  // Update summary sidebar
  function updateSummary() {
    const count = cart.length;
    const days = rentalDates.days || 0;

    const subtotal = cart.reduce(
      (sum, item) => sum + item.pricePerDay * days,
      0,
    );
    const insurance = Math.round(subtotal * 0.05); // 5% insurance
    const tax = Math.round((subtotal + insurance) * 0.1); // 10% VAT
    const total = subtotal + insurance + tax;

    if (summaryCount) summaryCount.textContent = count;
    if (summarySubtotal) summarySubtotal.textContent = formatPrice(subtotal);
    if (summaryInsurance) summaryInsurance.textContent = formatPrice(insurance);
    if (summaryTax) summaryTax.textContent = formatPrice(tax);
    if (summaryTotal) summaryTotal.textContent = formatPrice(total);

    // Update checkout button state
    if (btnCheckout) {
      if (count === 0) {
        btnCheckout.disabled = true;
        btnCheckout.classList.add("opacity-50", "cursor-not-allowed");
      } else {
        btnCheckout.disabled = false;
        btnCheckout.classList.remove("opacity-50", "cursor-not-allowed");
      }
    }
  }

  // Show notification toast
  function showNotification(message) {
    const toast = document.createElement("div");
    toast.className =
      "fixed bottom-6 right-6 bg-green-500 text-white px-6 py-3 rounded-lg shadow-lg z-50 animate-slide-up";
    toast.innerHTML = `
      <div class="flex items-center gap-2">
        <span class="material-symbols-outlined">check_circle</span>
        <span>${message}</span>
      </div>
    `;

    // Add animation style
    const style = document.createElement("style");
    style.textContent = `
      @keyframes slide-up {
        from { transform: translateY(100%); opacity: 0; }
        to { transform: translateY(0); opacity: 1; }
      }
      .animate-slide-up { animation: slide-up 0.3s ease-out; }
    `;
    document.head.appendChild(style);

    document.body.appendChild(toast);

    setTimeout(() => {
      toast.remove();
    }, 3000);
  }

  // Format price
  function formatPrice(price) {
    if (!price || isNaN(price)) return "0đ";
    return new Intl.NumberFormat("vi-VN").format(price) + "đ";
  }

  // Format date
  function formatDate(date) {
    if (!date) return "";
    return new Intl.DateTimeFormat("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    }).format(date);
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
