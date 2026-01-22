(function () {
  const loginBtn = document.getElementById("btn-login");
  const registerBtn = document.getElementById("btn-register");
  const searchBtn = document.getElementById("btn-search");
  const newsletterBtn = document.getElementById("btn-newsletter");
  const newsletterEmail = document.getElementById("newsletter-email");

  // Kiểm tra trạng thái đăng nhập
  function checkLoginStatus() {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user && user.fullName) {
          // Đã đăng nhập - ẩn nút đăng nhập/đăng ký, hiển thị thông tin user
          if (loginBtn) {
            loginBtn.textContent = user.fullName;
            loginBtn.classList.remove("bg-[#f0f2f4]", "text-[#111418]");
            loginBtn.classList.add("bg-primary", "text-white");
            // Đổi sự kiện click thành dropdown menu
            loginBtn.removeEventListener("click", goToLogin);
            loginBtn.addEventListener("click", showUserMenu);
          }
          if (registerBtn) {
            registerBtn.textContent = "Đăng xuất";
            registerBtn.classList.remove("bg-primary");
            registerBtn.classList.add("bg-red-500", "hover:bg-red-600");
            registerBtn.removeEventListener("click", goToRegister);
            registerBtn.addEventListener("click", logout);
          }
        }
      } catch (e) {
        console.error("Error parsing user data:", e);
      }
    }
  }

  function goToLogin() {
    window.location.href = "../login/login.html";
  }

  function goToRegister() {
    window.location.href = "../login/login.html#signup";
  }

  function showUserMenu() {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      const user = JSON.parse(userStr);
      // Có thể thêm dropdown menu ở đây, tạm thời chuyển đến trang quản lý
      if (user.role === "ADMIN") {
        window.location.href = "../admin/quan_ly_phuong_tien.html";
      } else {
        window.location.href = "../use/quan_ly_xe.html";
      }
    }
  }

  function logout() {
    localStorage.removeItem("user");
    alert("Đăng xuất thành công!");
    window.location.reload();
  }

  if (loginBtn) loginBtn.addEventListener("click", goToLogin);
  if (registerBtn) registerBtn.addEventListener("click", goToRegister);

  // Kiểm tra đăng nhập khi trang load
  checkLoginStatus();

  if (searchBtn) {
    searchBtn.addEventListener("click", () => {
      const selectType = document.getElementById("select-type");
      const selectLocation = document.getElementById("select-location");

      const type = selectType ? selectType.value : "";
      const location = selectLocation ? selectLocation.value : "";

      // Build query params, only include non-empty values
      const params = new URLSearchParams();
      if (type) params.set("type", type);
      if (location) params.set("location", location);

      const qs = params.toString();
      window.location.href = "../use/danh_sach_xe.html" + (qs ? "?" + qs : "");
    });
  }

  document.querySelectorAll('[data-add="promo"]').forEach((btn) => {
    btn.addEventListener("click", async () => {
      const item = { type: "promo", id: btn.getAttribute("data-id") || null };
      btn.disabled = true;
      try {
        await window.api.addToCart(item);
        btn.classList.add("bg-primary", "text-white");
      } catch (err) {
        btn.disabled = false;
        alert("Không thể thêm vào giỏ: " + (err.message || err));
      }
    });
  });

  if (newsletterBtn) {
    newsletterBtn.addEventListener("click", async (e) => {
      e.preventDefault();
      const email = newsletterEmail ? newsletterEmail.value.trim() : "";
      if (!email || !/^\S+@\S+\.\S+$/.test(email)) {
        alert("Vui lòng nhập email hợp lệ.");
        return;
      }
      try {
        await window.api.subscribeNewsletter(email);
        alert("Cảm ơn! Chúng tôi đã nhận email: " + email);
        newsletterEmail.value = "";
      } catch (err) {
        alert("Đăng ký thất bại: " + (err.message || err));
      }
    });
  }
})();
