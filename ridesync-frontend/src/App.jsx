// src/App.jsx
import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import ViewRecords from "./pages/ViewRecords";
import UploadRecord from "./pages/UploadRecord";

function App() {
  return (
    <div className="container mt-4">
      <h2 className="text-center mb-4 fw-bold">🏍️ RideSync Dashboard</h2>

      {/* Navigation */}
      <nav className="d-flex justify-content-center mb-4">
        <Link to="/view" className="btn btn-outline-primary mx-2 px-4">
          View Records
        </Link>
        <Link to="/upload" className="btn btn-outline-success mx-2 px-4">
          Add Record
        </Link>
      </nav>

      {/* Routes */}
      <Routes>
        <Route path="/" element={<ViewRecords />} />
        <Route path="/view" element={<ViewRecords />} />
        <Route path="/upload" element={<UploadRecord />} />
      </Routes>
    </div>
  );
}

export default App;
