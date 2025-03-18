import React from "react";
import { Link } from "react-router-dom";

const Navbar = () => {
  const isLoggedIn = localStorage.getItem("accessToken");
  const userRole = localStorage.getItem("userRole"); // Check user role

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userId");
    window.location.reload();
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">
          Employee Management System
        </Link>
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link active" to="/">
                Home
              </Link>
            </li>
            {isLoggedIn && userRole === "admin" && (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/admin-dashboard">
                    Admin Dashboard
                  </Link>
                </li>
                <li className="nav-item">
                  <Link className="nav-link" to="/adduser">
                    Add User
                  </Link>
                </li>
              </>
            )}
            {isLoggedIn && userRole === "user" && (
              <li className="nav-item">
                <Link className="nav-link" to="/employee-dashboard">
                  Employee Dashboard
                </Link>
              </li>
            )}
          </ul>

          <ul className="navbar-nav">
            {isLoggedIn ? (
              <li className="nav-item">
                <button className="btn btn-outline-light" onClick={handleLogout}>
                  Logout
                </button>
              </li>
            ) : (
              <li className="nav-item">
                <Link className="btn btn-outline-light" to="/login">
                  Login
                </Link>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
