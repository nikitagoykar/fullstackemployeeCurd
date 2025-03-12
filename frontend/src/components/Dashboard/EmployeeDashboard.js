import React from 'react';
import { useNavigate } from 'react-router-dom';

const EmployeeDashboard = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    navigate('/login');
  };

  return (
    <div>
      <h1>Employee Dashboard</h1>
      <p>Welcome, Employee!</p>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default EmployeeDashboard;
