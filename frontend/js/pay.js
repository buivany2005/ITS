// Payment page handler
(function () {
  // Get params from URL
  function getParams() {
    const params = new URLSearchParams(window.location.search);
    return {
      dateFrom: params.get("dateFrom"),
      dateTo: params.get("dateTo"),
      vehicleIds: params.get("vehicleIds")?.split(",") || [],
    };
  }

  // DOM Elements
  const paymentForm = document.querySelector("form");
  const cardNumberInput = document.getElementById("card-number");
  const cardNameInput = document.getElementById("card-name");
  const expiryInput = document.getElementById("card-expiry");
  const cvvInput = document.getElementById("card-cvv");
  const totalPriceElement = document.querySelector(
    ".total-price, .text-2xl.font-bold, .text-xl.font-bold"
  );
  const btnPay = document.querySelector('button[class*="bg-primary"]');
  const orderSummary = document.querySelector(".order-summary, .cart-summary");

  // State
  let orderDetails = null;

  // Initialize
  async function init() {
    const params = getParams();
    if (params.vehicleIds.length > 0) {
      await loadOrderSummary(params);
    }
    attachEventListeners();
    formatCardInputs();
  }

  // Load order summary
  async function loadOrderSummary(params) {
    try {
      const vehicles = [];
      for (const id of params.vehicleIds) {
        const response = await fetch(
          `http://localhost:8081/api/vehicles/${id}`
        );
        if (response.ok) {
          vehicles.push(await response.json());
        }
      }

      const from = new Date(params.dateFrom);
      const to = new Date(params.dateTo);
      const days = Math.ceil((to - from) / (1000 * 60 * 60 * 24));

      let total = 0;
      vehicles.forEach((v) => {
        total += (v.pricePerDay || 0) * days;
      });

      orderDetails = {
        vehicles,
        dateFrom: params.dateFrom,
        dateTo: params.dateTo,
        days,
        total,
      };

      renderOrderSummary();
    } catch (err) {
      console.error("Error loading order:", err);
    }
  }

  // Render order summary
  function renderOrderSummary() {
    if (!orderDetails) return;

    if (totalPriceElement) {
      totalPriceElement.textContent = formatPrice(orderDetails.total);
    }
  }

  // Attach event listeners
  function attachEventListeners() {
    if (btnPay) {
      btnPay.addEventListener("click", handlePayment);
    }
  }

  // Format card inputs
  function formatCardInputs() {
    if (cardNumberInput) {
      cardNumberInput.addEventListener("input", (e) => {
        let value = e.target.value.replace(/\D/g, "");
        value = value.replace(/(\d{4})(?=\d)/g, "$1 ");
        e.target.value = value.substring(0, 19);
      });
    }

    if (expiryInput) {
      expiryInput.addEventListener("input", (e) => {
        let value = e.target.value.replace(/\D/g, "");
        if (value.length >= 2) {
          value = value.substring(0, 2) + "/" + value.substring(2, 4);
        }
        e.target.value = value;
      });
    }

    if (cvvInput) {
      cvvInput.addEventListener("input", (e) => {
        e.target.value = e.target.value.replace(/\D/g, "").substring(0, 4);
      });
    }
  }

  // Handle payment
  async function handlePayment(e) {
    e.preventDefault();

    // Validate inputs
    const cardNumber = cardNumberInput?.value.replace(/\s/g, "");
    const cardName = cardNameInput?.value.trim();
    const expiry = expiryInput?.value;
    const cvv = cvvInput?.value;

    if (!cardNumber || cardNumber.length < 16) {
      alert("Vui lòng nhập số thẻ hợp lệ");
      return;
    }

    if (!cardName) {
      alert("Vui lòng nhập tên chủ thẻ");
      return;
    }

    if (!expiry || expiry.length < 5) {
      alert("Vui lòng nhập ngày hết hạn hợp lệ");
      return;
    }

    if (!cvv || cvv.length < 3) {
      alert("Vui lòng nhập mã CVV hợp lệ");
      return;
    }

    // Disable button
    btnPay.disabled = true;
    btnPay.textContent = "Đang xử lý...";

    try {
      // Simulate payment processing
      await new Promise((resolve) => setTimeout(resolve, 2000));

      // Create order (call API if available)
      // const response = await fetch('http://localhost:8081/api/orders', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify({
      //     vehicleIds: orderDetails.vehicles.map(v => v.id),
      //     dateFrom: orderDetails.dateFrom,
      //     dateTo: orderDetails.dateTo,
      //     total: orderDetails.total
      //   })
      // });

      // Clear cart
      sessionStorage.removeItem("cart");

      // Show success
      alert("Thanh toán thành công! Cảm ơn bạn đã sử dụng dịch vụ.");
      window.location.href = "../home/index.html";
    } catch (err) {
      alert("Thanh toán thất bại: " + (err.message || err));
      btnPay.disabled = false;
      btnPay.textContent = "Thanh toán";
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
