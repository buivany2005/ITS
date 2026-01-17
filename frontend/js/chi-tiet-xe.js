// Vehicle detail page handler
(function () {
  // Get vehicle ID from URL
  function getVehicleId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
  }

  // DOM Elements
  const vehicleTitle = document.querySelector("h1");
  const vehicleImage = document.querySelector("img[alt]");
  const priceElement = document.querySelector(
    ".text-primary.text-2xl, .text-xl.font-bold"
  );
  const descriptionElement = document.querySelector("p.text-\\[\\#617589\\]");
  const bookButton = document.querySelector('button[class*="bg-primary"]');

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

  // Fetch vehicle detail from API
  async function loadVehicleDetail(id) {
    try {
      const response = await fetch(`http://localhost:8081/api/vehicles/${id}`);
      if (!response.ok) throw new Error("Vehicle not found");
      const vehicle = await response.json();
      renderVehicleDetail(vehicle);
    } catch (err) {
      console.error("Error loading vehicle:", err);
      showError("Không thể tải thông tin phương tiện");
    }
  }

  // Render vehicle detail
  function renderVehicleDetail(vehicle) {
    if (vehicleTitle) {
      vehicleTitle.textContent = vehicle.name || "Phương tiện";
    }
    if (vehicleImage) {
      vehicleImage.src =
        vehicle.imageUrl || getDefaultImage(vehicle.vehicleType);
      vehicleImage.alt = vehicle.name;
    }
    if (priceElement) {
      priceElement.textContent = formatPrice(vehicle.pricePerDay) + "/ngày";
    }
    if (descriptionElement) {
      descriptionElement.textContent =
        vehicle.description || "Mô tả phương tiện";
    }
  }

  // Attach event listeners
  function attachEventListeners() {
    if (bookButton) {
      bookButton.addEventListener("click", () => {
        const vehicleId = getVehicleId();
        window.location.href = `dat_xe.html?vehicleId=${vehicleId}`;
      });
    }
  }

  // Get default image based on type
  function getDefaultImage(type) {
    const images = {
      OTO: "https://via.placeholder.com/600x400?text=Car",
      XEMAY: "https://via.placeholder.com/600x400?text=Motorcycle",
      XEDAP: "https://via.placeholder.com/600x400?text=Bicycle",
    };
    return images[type] || "https://via.placeholder.com/600x400?text=Vehicle";
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
    if (vehicleTitle) {
      vehicleTitle.textContent = message;
    }
  }

  // Start on page load
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", init);
  } else {
    init();
  }
})();
