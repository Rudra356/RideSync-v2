
import React from "react";
import {
  Routes,
  Route,
  Navigate,
  Outlet,
} from "react-router-dom";

import { AuthProvider, useAuth } from "./context/AuthContext";

import ViewRecords from "./pages/ViewRecords";
import UploadRecord from "./pages/UploadRecord";
import AboutMe from "./pages/AboutMe";
import Login from "./pages/Login";
import Register from "./pages/Register";

import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

function ProtectedRoute() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div className="rs-loading">
        <div className="rs-loading-line" />
        <p>Preparing your RideSync experience...</p>
      </div>
    );
  }

  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />;
}

function AppRoutes() {
  return (
    <div className="rs-app-shell">
      <Navbar />

      <main className="rs-main">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/about" element={<AboutMe />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<ViewRecords />} />
            <Route path="/view" element={<ViewRecords />} />
            <Route path="/upload" element={<UploadRecord />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>

      <Footer />
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
