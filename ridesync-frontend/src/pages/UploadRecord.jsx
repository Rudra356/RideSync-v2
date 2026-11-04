
import React, { useState } from "react";
import api from "../api";
import "../styles/custom.css";

const INITIAL_FORM = {
  spareName: "",
  RC: "",
  issue: "",
  brandModel: "",
  price: "",
  currentKM: "",
  replacingDate: "",
  upcomingCheckUpKM: "",
  extraNotes: "",
};

function UploadRecord() {
  const [form, setForm] = useState(INITIAL_FORM);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (loading) return;

    setLoading(true);
    setMessage("");
    setSuccess(false);

    try {
      const payload = {
        ...form,
        price: Number(form.price),
        currentKM:
          form.currentKM === "" ? null : Number(form.currentKM),
        upcomingCheckUpKM:
          form.upcomingCheckUpKM === ""
            ? null
            : Number(form.upcomingCheckUpKM),
      };

      await api.post("/api/maintenance", payload);

      setSuccess(true);
      setMessage("Maintenance record successfully added.");
      setForm({ ...INITIAL_FORM });
    } catch (error) {
      console.error("Upload error:", error);

      if (error.response?.status === 401) {
        setMessage("Your session has expired. Please log in again.");
      } else if (error.response?.status === 403) {
        setMessage("Request rejected. Please refresh and try again.");
      } else {
        const responseMessage = error.response?.data?.message;

        setMessage(
          typeof responseMessage === "string"
            ? responseMessage
            : error.message || "Upload failed. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="upload-section rs-upload-page">
      <div className="upload-card rs-upload-card">

        {/* HEADER */}
        <div className="rs-upload-header">
          <div className="rs-eyebrow">
            <span className="rs-eyebrow-line" />
            VEHICLE SERVICE LOG
          </div>

          <h2>Add Maintenance Record</h2>

          <p>
            Keep track of every service, replacement and maintenance
            milestone in one place.
          </p>

          <div className="rs-upload-divider" />
        </div>

        <form onSubmit={handleSubmit}>

          {/* SECTION 01 */}
          <div className="rs-form-section-heading">
            <span>01</span>
            <h3>Component Information</h3>
          </div>

          <div className="row g-4">

            <div className="col-md-5">
              <label htmlFor="spareName" className="form-label">
                Spare Name <span>*</span>
              </label>

              <input
                id="spareName"
                type="text"
                name="spareName"
                value={form.spareName}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="e.g. Engine Oil Filter"
                required
              />
            </div>

            <div className="col-md-3">
              <label htmlFor="RC" className="form-label">
                Registration Number <span>*</span>
              </label>

              <input
                id="RC"
                type="text"
                name="RC"
                value={form.RC}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="Vehicle RC"
                required
              />
            </div>

            <div className="col-md-4">
              <label htmlFor="brandModel" className="form-label">
                Brand / Model <span>*</span>
              </label>

              <input
                id="brandModel"
                type="text"
                name="brandModel"
                value={form.brandModel}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="e.g. Suzuki Gixxer SF"
                required
              />
            </div>

          </div>

          {/* SECTION 02 */}
          <div className="rs-form-section-heading rs-form-section-spaced">
            <span>02</span>
            <h3>Service & Cost Details</h3>
          </div>

          <div className="row g-4">

            <div className="col-md-4">
              <label htmlFor="price" className="form-label">
                Price (₹) <span>*</span>
              </label>

              <div className="rs-input-prefix">
                <span>₹</span>

                <input
                  id="price"
                  type="number"
                  name="price"
                  value={form.price}
                  onChange={handleChange}
                  className="form-control dark-input"
                  placeholder="0.00"
                  min="0"
                  step="1"
                  required
                />
              </div>
            </div>

            <div className="col-md-4">
              <label htmlFor="currentKM" className="form-label">
                Current Odometer (KM)
              </label>

              <input
                id="currentKM"
                type="number"
                name="currentKM"
                value={form.currentKM}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="Current mileage"
                min="0"
                step="1"
              />
            </div>

            <div className="col-md-4">
              <label htmlFor="replacingDate" className="form-label">
                Replacement Date <span>*</span>
              </label>

              <input
                id="replacingDate"
                type="date"
                name="replacingDate"
                value={form.replacingDate}
                onChange={handleChange}
                className="form-control dark-input"
                required
              />
            </div>

            <div className="col-md-4">
              <label htmlFor="upcomingCheckUpKM" className="form-label">
                Next Check-up (KM)
              </label>

              <input
                id="upcomingCheckUpKM"
                type="number"
                name="upcomingCheckUpKM"
                value={form.upcomingCheckUpKM}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="Target mileage"
                min="0"
                step="1"
              />
            </div>

            <div className="col-12">
              <label htmlFor="issue" className="form-label">
                Issue / Work Performed
              </label>

              <textarea
                id="issue"
                name="issue"
                rows="3"
                value={form.issue}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="Describe the issue or maintenance work performed..."
              />
            </div>

          </div>

          {/* SECTION 03 */}
          <div className="rs-form-section-heading rs-form-section-spaced">
            <span>03</span>
            <h3>Additional Information</h3>
          </div>

          <div className="row g-4">
            <div className="col-12">
              <label htmlFor="extraNotes" className="form-label">
                Extra Notes
              </label>

              <textarea
                id="extraNotes"
                name="extraNotes"
                rows="3"
                value={form.extraNotes}
                onChange={handleChange}
                className="form-control dark-input"
                placeholder="Add observations, recommendations or other details..."
              />
            </div>
          </div>

          {/* SUBMIT */}
          <div className="rs-upload-submit">
            <div className="rs-required-note">
              <span>*</span> Required fields
            </div>

            <button
              className="upload-btn"
              type="submit"
              disabled={loading}
            >
              {loading ? (
                <>
                  <span className="rs-button-spinner" />
                  Saving Record...
                </>
              ) : (
                <>
                  Save Maintenance Record
                  <span>↗</span>
                </>
              )}
            </button>
          </div>

          {/* RESPONSE MESSAGE */}
          {message && (
            <div
              className={`rs-upload-alert ${
                success ? "success" : "error"
              }`}
              role="alert"
            >
              <span>{success ? "✓" : "!"}</span>
              {message}
            </div>
          )}

        </form>
      </div>
    </div>
  );
}

export default UploadRecord;
