import React from "react";
import { Routes, Route } from "react-router-dom";
import ViewRecords from "./pages/ViewRecords";
import UploadRecord from "./pages/UploadRecord";
import AboutMe from "./pages/AboutMe";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";

function App() {
  return (
    <>
      <Navbar />

      <div className="container mt-4 mb-5">
        <Routes>
          <Route path="/" element={<ViewRecords />} />
          <Route path="/view" element={<ViewRecords />} />
          <Route path="/upload" element={<UploadRecord />} />
          <Route path="/about" element={<AboutMe />} /> {/* ✅ Added */}
        </Routes>
      </div>

      <Footer />
    </>
  );
}

export default App;
