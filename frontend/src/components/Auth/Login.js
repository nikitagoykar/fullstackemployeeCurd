import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const Login = () => {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("http://localhost:8080/auth/login", credentials);
      const { accessToken, role } = response.data;

      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("userRole", role); // Store user role

      if (role === "admin") {
        navigate("/user"); // Redirect Admins to Admin Dashboard
      } else {
        navigate("/employee-dashboard"); // Redirect Users to Employee List
      }
    } catch (error) {
      console.error("Login Failed:", error);
      alert("Invalid credentials");
    }
  };

  return (
    <div className="container">
      <h2>Login</h2>
      <form onSubmit={handleLogin}>
        <div className="mb-3">
          <label className="form-label">Username</label>
          <input
            type="text"
            className="form-control"
            value={credentials.username}
            onChange={(e) => setCredentials({ ...credentials, username: e.target.value })}
          />
        </div>
        <div className="mb-3">
          <label className="form-label">Password</label>
          <input
            type="password"
            className="form-control"
            value={credentials.password}
            onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
          />
        </div>
        <button type="submit" className="btn btn-primary">Login</button>
      </form>
    </div>
  );
};

export default Login;
