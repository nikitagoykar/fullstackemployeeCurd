import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // Function to check user role after login
  const fetchUserRole = async () => {
    try {
      const response = await fetch('http://localhost:8080/users/me', {
        method: 'GET',
        credentials: 'include', // Ensures JWT cookie is included
      });

      if (response.ok) {
        const data = await response.json();
        if (data.role === 'ROLE_ADMIN') {
          navigate('/admin-dashboard');
        } else if (data.role === 'ROLE_USER') {
          navigate('/employee-dashboard');
        }
      } else {
        throw new Error('Failed to fetch user role');
      }
    } catch (error) {
      console.error('Failed to fetch user role:', error);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const response = await fetch('http://localhost:8080/users/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include', // Important for JWT authentication
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.error || 'Login failed');
      }

      // After login is successful, check the user's role
      fetchUserRole();
    } catch (err) {
      setError(err.message); // Handle any errors from login or fetchUserRole
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-4">
          <div className="card p-4 shadow-lg">
            <h2 className="text-center mb-4">Login</h2>
            {error && <div className="alert alert-danger">{error}</div>}
            <form onSubmit={handleLogin}>
              <div className="mb-3">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="Enter username"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  required
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Password</label>
                <input
                  type="password"
                  className="form-control"
                  placeholder="Enter password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
              <button type="submit" className="btn btn-primary w-100">
                Login
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
