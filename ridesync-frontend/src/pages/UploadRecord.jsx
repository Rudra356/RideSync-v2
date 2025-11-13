import React, { useState } from "react";
import axios from "axios";

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
    <div className="container mt-4">
      <h3 className="text-center mb-4">Add Maintenance Record</h3>
      <form
        onSubmit={handleSubmit}
        className="p-4 border rounded bg-light shadow-sm"
      >
        {Object.entries(form).map(([key, value]) => (
          <div className="mb-3" key={key}>
            <label className="form-label text-capitalize">
              {key.replace(/([A-Z])/g, " $1")}
            </label>
            <input
              type={
                key.toLowerCase().includes("price") ||
                key.toLowerCase().includes("km")
                  ? "number"
                  : "text"
              }
              name={key}
              value={value}
              onChange={handleChange}
              className="form-control"
              required={
                ["spareName", "RC", "brandModel", "price"].includes(key)
              }
            />
          </div>
        ))}

        <button
          className="btn btn-success w-100"
          type="submit"
          disabled={loading}
        >
          {loading ? "Saving..." : "Save Record"}
        </button>

        {message && (
          <div
            className={`alert mt-3 ${
              message.includes("✅") ? "alert-success" : "alert-danger"
            }`}
          >
            {message}
          </div>
        )}
      </form>
    </div>
  );
}

export default UploadRecord;
