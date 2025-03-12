import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ component: Component }) => {
  const token = localStorage.getItem('token');  // Get the JWT token from localStorage

  // If no token, redirect to login page
  if (!token) {
    return <Navigate to="/login" />;
  }

  return <Component />;  // If token exists, render the protected component
};

export default ProtectedRoute;
