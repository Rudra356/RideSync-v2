import React, { useState } from "react";
import axios from "axios";
import "../styles/custom.css";

function UploadRecord() {
  const [form, setForm] = useState({
    spareName: "",
    RC: "",
    issue: "",
    brandModel: "",
    price: "",
    currentKM: "",
    upcomingCheckUpKM: "",
    extraNotes: "",
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    try {
      await axios.post("http://localhost:8080/api/maintenance", {
        ...form,
        price: parseInt(form.price) || 0,
        currentKM: parseInt(form.currentKM) || 0,
        upcomingCheckUpKM: parseInt(form.upcomingCheckUpKM) || 0,
      });
      setMessage("✅ Record uploaded successfully!");
      setForm({
        spareName: "",
        RC: "",
        issue: "",
        brandModel: "",
        price: "",
        currentKM: "",
        upcomingCheckUpKM: "",
        extraNotes: "",
      });
    } catch (error) {
      console.error(error);
      setMessage("❌ Upload failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-section">
      <div className="upload-card wide">
        <h2> Add Maintenance Record</h2>
        <form onSubmit={handleSubmit}>
          <div className="row">
            <div className="col-md-5 mb-3">
              <label className="form-label">Spare Name</label>
              <input
                type="text"
                name="spareName"
                value={form.spareName}
                onChange={handleChange}
                className="form-control dark-input"
                required
              />
            </div>

            <div className="col-md-3 mb-3">
              <label className="form-label">RC</label>
              <input
                type="text"
                name="RC"
                value={form.RC}
                onChange={handleChange}
                className="form-control dark-input"
                required
              />
            </div>

            <div className="col-md-4 mb-3">
              <label className="form-label">Brand / Model</label>
              <input
                type="text"
                name="brandModel"
                value={form.brandModel}
                onChange={handleChange}
                className="form-control dark-input"
                required
              />
            </div>

<div className="col-md-4 mb-3">
              <label className="form-label">Price (₹)</label>
              <input
                type="number"
                name="price"
                value={form.price}
                onChange={handleChange}
                className="form-control dark-input"
                required
              />
            </div>

            <div className="col-md-4 mb-3">
              <label className="form-label">Current KM</label>
              <input
                type="number"
                name="currentKM"
                value={form.currentKM}
                onChange={handleChange}
                className="form-control dark-input"
              />
            </div>

            <div className="col-md-4 mb-3">
              <label className="form-label">Upcoming Check-Up</label>
              <input
                type="number"
                name="upcomingCheckUpKM"
                value={form.upcomingCheckUpKM}
                onChange={handleChange}
                className="form-control dark-input"
              />
            </div>

            <div className="col-md-8 mb-3">
              <label className="form-label">Issue</label>
              <input
                type="text"
                name="issue"
                value={form.issue}
                onChange={handleChange}
                className="form-control dark-input"
              />
            </div>

            

            <div className="col-4 mb-3">
              <label className="form-label">Extra Notes</label>
              <textarea
                name="extraNotes"
                rows="3"
                value={form.extraNotes}
                onChange={handleChange}
                className="form-control dark-input"
              />
            </div>
          </div>

          <button className="upload-btn w-100" type="submit" disabled={loading}>
            {loading ? "⏳ Saving..." : "Save Record"}
          </button>

          {message && (
            <div
              className={`upload-alert ${
                message.includes("✅") ? "success" : "error"
              }`}
            >
              {message}
            </div>
          )}
        </form>
      </div>
    </div>
  );
}

export default UploadRecord;
