import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';  // Updated imports
import Login from './components/Auth/Login';
import AdminDashboard from './components/Dashboard/AdminDashboard';
import EmployeeDashboard from './components/Dashboard/EmployeeDashboard';
import ProtectedRoute from './components/ProtectedRoute';
// Import Login component

const App = () => {
  return (
    <Router>
      <Routes> {/* Use Routes instead of Switch */}
        <Route path="/login" element={<Login />} />  {/* Route for Login */}
        <Route path="/admin-dashboard" element={<ProtectedRoute role="ROLE_ADMIN"><AdminDashboard /></ProtectedRoute>} />
                <Route path="/employee-dashboard" element={<ProtectedRoute role="ROLE_USER"><EmployeeDashboard /></ProtectedRoute>} />
        {/* Add other routes as needed */}
      </Routes>
    </Router>
  );
};

export default App;
