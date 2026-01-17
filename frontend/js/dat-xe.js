// Booking/Cart page handler
(function () {
  // Get vehicle ID from URL
  function getVehicleId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("vehicleId");
  }

  // DOM Elements
  const form = document.querySelector("form");
  const dateFromInput =
    document.getElementById("date-from") ||
    document.querySelector('input[type="date"]');
  const dateToInput =
    document.getElementById("date-to") ||
    document.querySelectorAll('input[type="date"]')[1];
  const totalPriceElement = document.querySelector(
    ".total-price, .text-2xl.font-bold"
  );
  const btnConfirm = document.querySelector('button[class*="bg-primary"]');
  const btnRemove = document.querySelectorAll(
    '.btn-remove, button[class*="text-red"]'
  );

  // State
  let vehicle = null;
  let cart = [];

  // Initialize
  async function init() {
    loadCart();
    const vehicleId = getVehicleId();
    if (vehicleId) {
      await loadVehicle(vehicleId);
    }
    attachEventListeners();
    updateTotalPrice();
  }

  // Load cart from session storage
  function loadCart() {
    try {
      const stored = sessionStorage.getItem("cart");
      if (stored) {
        cart = JSON.parse(stored);
      }
    } catch (e) {
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

  // Add vehicle to cart
  function addToCart(vehicleData) {
    const existing = cart.find((item) => item.id === vehicleData.id);
    if (!existing) {
      cart.push({
        id: vehicleData.id,
        name: vehicleData.name,
        pricePerDay: vehicleData.pricePerDay,
        imageUrl: vehicleData.imageUrl,
        dateFrom: null,
        dateTo: null,
      });
      saveCart();
    }
  }

  // Fetch vehicle from API
  async function loadVehicle(id) {
    try {
      const response = await fetch(`http://localhost:8081/api/vehicles/${id}`);
      if (!response.ok) throw new Error("Vehicle not found");
      vehicle = await response.json();
      addToCart(vehicle);
    } catch (err) {
      console.error("Error loading vehicle:", err);
    }
  }

  // Attach event listeners
  function attachEventListeners() {
    if (dateFromInput) {
      dateFromInput.addEventListener("change", updateTotalPrice);
    }
    if (dateToInput) {
      dateToInput.addEventListener("change", updateTotalPrice);
    }
    if (btnConfirm) {
      btnConfirm.addEventListener("click", handleConfirmBooking);
    }
    btnRemove.forEach((btn) => {
      btn.addEventListener("click", handleRemoveItem);
    });
  }

  // Calculate total price
  function updateTotalPrice() {
    if (!dateFromInput || !dateToInput) return;

    const from = new Date(dateFromInput.value);
    const to = new Date(dateToInput.value);

    if (isNaN(from.getTime()) || isNaN(to.getTime())) return;

    const days = Math.ceil((to - from) / (1000 * 60 * 60 * 24));
    if (days <= 0) return;

    let total = 0;
    cart.forEach((item) => {
      total += (item.pricePerDay || 0) * days;
    });

    if (totalPriceElement) {
      totalPriceElement.textContent = formatPrice(total);
    }
  }

  // Handle confirm booking
  async function handleConfirmBooking(e) {
    e.preventDefault();

    if (cart.length === 0) {
      alert("Giỏ hàng trống!");
      return;
    }

    const dateFrom = dateFromInput?.value;
    const dateTo = dateToInput?.value;

    if (!dateFrom || !dateTo) {
      alert("Vui lòng chọn ngày thuê và ngày trả");
      return;
    }

    // Navigate to payment page
    const params = new URLSearchParams({
      dateFrom,
      dateTo,
      vehicleIds: cart.map((v) => v.id).join(","),
    });
    window.location.href = `../pay/pay.html?${params.toString()}`;
  }

  // Handle remove item from cart
  function handleRemoveItem(e) {
    const btn = e.currentTarget;
    const itemId = btn.dataset.id;
    if (itemId) {
      cart = cart.filter((item) => String(item.id) !== String(itemId));
      saveCart();
      // Remove from DOM
      const row = btn.closest("tr, .cart-item, div[data-item-id]");
      if (row) row.remove();
      updateTotalPrice();
    }
  }

  // Format price
  function formatPrice(price) {
    return (
      new Intl.NumberFormat("vi-VN", {
        style: "decimal",
      }).format(price) + "đ"
    );
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
