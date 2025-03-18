import React from "react";
import { Link } from "react-router-dom";

const Navbar = () => {
  const accessToken = localStorage.getItem("accessToken");
  const userRole = localStorage.getItem("userRole"); // Get the user role from localStorage

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userRole");
    window.location.reload();
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">Employee Management System</Link>
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
              <Link className="nav-link" to="/">Home</Link>
            </li>

            {accessToken ? (
              <>
                {userRole === "admin" && (
                  <>
                    <li className="nav-item">
                      <Link className="nav-link" to="/user">Admin Dashboard</Link>
                    </li>
                    <li className="nav-item">
                      <Link className="nav-link" to="/employee-dashboard">Employee Dashboard</Link>
                    </li>
                  </>
                )}

                {userRole === "user" && (
                  <li className="nav-item">
                    <Link className="nav-link" to="/employee-dashboard">Employee List</Link>
                  </li>
                )}

                <li className="nav-item">
                  <button className="btn btn-outline-danger" onClick={handleLogout}>Logout</button>
                </li>
              </>
            ) : (
              <li className="nav-item">
                <Link className="nav-link" to="/login">Login</Link>
              </li>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
