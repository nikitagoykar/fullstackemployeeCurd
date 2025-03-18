import "./App.css";
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import AddUser from "./users/AddUser";
import EditUser from "./users/EditUser";
import ViewUser from "./users/ViewUser";

function App() {
  const userRole = localStorage.getItem("userRole"); // Get user role from localStorage
  const userId = localStorage.getItem("userId"); // Get logged-in user's ID

  return (
    <div className="App">
      <Router>
        <Navbar />
        <Routes>
          <Route exact path="/" element={<Home />} />

          {/* Admin Routes */}
          {userRole === "admin" && (
            <>
              <Route exact path="/adduser" element={<AddUser />} />
              <Route exact path="/edituser/:id" element={<EditUser />} />
              <Route exact path="/viewuser/:id" element={<ViewUser />} />
            </>
          )}

          {/* User Route - Can Only Edit Their Own Account */}
          {userRole === "user" && (
            <Route
              exact
              path={`/edituser/${userId}`} // Restrict users to edit only their profile
              element={<EditUser />}
            />
          )}

          {/* Redirect unauthorized users */}
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
