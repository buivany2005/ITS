// auth-check.js - Kiểm tra trạng thái đăng nhập cho tất cả các trang
(function () {
  // Tìm các nút đăng nhập/đăng ký trong header
  const loginBtn =
    document.getElementById("btn-login") ||
    document.querySelector('[data-action="login"]');
  const registerBtn =
    document.getElementById("btn-register") ||
    document.querySelector('[data-action="register"]');

  // Kiểm tra trạng thái đăng nhập
  function checkLoginStatus() {
    const userStr = localStorage.getItem("user");
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        if (user && user.fullName) {
          updateUIForLoggedInUser(user);
        }
      } catch (e) {
        console.error("Error parsing user data:", e);
        localStorage.removeItem("user");
        setupLoginButtons();
      }
    } else {
      // Chưa đăng nhập - gắn sự kiện cho các nút
      setupLoginButtons();
    }
  }

  function setupLoginButtons() {
    if (loginBtn) {
      loginBtn.onclick = function () {
        window.location.href = getBasePath() + "login/login.html";
      };
    }
    if (registerBtn) {
      registerBtn.onclick = function () {
        window.location.href = getBasePath() + "login/login.html#register";
      };
    }
  }

  function updateUIForLoggedInUser(user) {
    if (loginBtn) {
      loginBtn.textContent = user.fullName;
      loginBtn.classList.remove("bg-[#f0f2f4]", "text-[#111418]");
      loginBtn.classList.add("bg-primary", "text-white");
      loginBtn.onclick = function () {
        // Chuyển đến trang phù hợp với role
        if (user.role === "ADMIN") {
          window.location.href =
            getBasePath() + "admin/quan_ly_phuong_tien.html";
        } else {
          window.location.href = getBasePath() + "use/quan_ly_xe.html";
        }
      };
    }

    if (registerBtn) {
      registerBtn.textContent = "Đăng xuất";
      registerBtn.classList.remove("bg-primary");
      registerBtn.classList.add("bg-red-500", "hover:bg-red-600");
      registerBtn.onclick = function () {
        logout();
      };
    }
  }

  function logout() {
    localStorage.removeItem("user");
    alert("Đăng xuất thành công!");
    window.location.href = getBasePath() + "home/index.html";
  }

  function getBasePath() {
    // Tính toán đường dẫn base dựa trên vị trí hiện tại
    const path = window.location.pathname;
    if (
      path.includes("/home/") ||
      path.includes("/login/") ||
      path.includes("/use/") ||
      path.includes("/pay/") ||
      path.includes("/admin/")
    ) {
      return "../";
    }
    return "./";
  }

  // Kiểm tra khi DOM loaded
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", checkLoginStatus);
  } else {
    checkLoginStatus();
  }

  // Export để có thể sử dụng từ bên ngoài
  window.authCheck = {
    checkLoginStatus,
    logout,
    getUser: function () {
      const userStr = localStorage.getItem("user");
      return userStr ? JSON.parse(userStr) : null;
    },
    isLoggedIn: function () {
      return !!localStorage.getItem("user");
    },
  };
})();
