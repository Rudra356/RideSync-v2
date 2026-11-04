
import React, { useEffect, useMemo, useState } from "react";
import api from "../api";

function ViewRecords() {
  const [page, setPage] = useState(0);
  const [records, setRecords] = useState([]);
  const [hasNext, setHasNext] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [retryKey, setRetryKey] = useState(0);

  const size = 5;

  useEffect(() => {
    let active = true;

    const fetchRecords = async () => {
      setLoading(true);
      setError("");

      try {
        const response = await api.get(
          `/api/maintenance/${page}/${size}`
        );

        const responseData = response.data;

        const fetchedRecords = Array.isArray(responseData)
          ? responseData
          : responseData?.content || [];

        if (!active) return;

        setRecords(fetchedRecords);

        setHasNext(
          typeof responseData?.hasNext === "boolean"
            ? responseData.hasNext
            : fetchedRecords.length === size
        );
      } catch (err) {
        if (!active) return;

        if (err.response?.status === 401) {
          setError("Your session has expired. Please log in again.");
        } else {
          setError(
            err.response?.data?.message ||
            err.message ||
            "Unable to load maintenance records."
          );
        }

        setRecords([]);
        setHasNext(false);
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    fetchRecords();

    return () => {
      active = false;
    };
  }, [page, retryKey]);

  const formatDate = (date) => {
    if (!date) return "—";

    const parsedDate = new Date(`${date}T00:00:00`);

    if (Number.isNaN(parsedDate.getTime())) return "—";

    return parsedDate.toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  const formatNumber = (value) => {
    if (value == null || value === "") return "—";

    const number = Number(value);

    return Number.isFinite(number)
      ? number.toLocaleString("en-IN")
      : value;
  };

  const formatCurrency = (value) => {
    if (value == null || value === "") return "—";

    const number = Number(value);

    return Number.isFinite(number)
      ? `₹${number.toLocaleString("en-IN")}`
      : "—";
  };

  const pageStats = useMemo(() => {
    const totalSpend = records.reduce(
      (total, record) => total + (Number(record.price) || 0),
      0
    );

    const upcomingCount = records.filter((record) => {
      const current = Number(record.currentKM);
      const upcoming = Number(record.upcomingCheckUpKM);

      return (
        record.currentKM != null &&
        record.upcomingCheckUpKM != null &&
        Number.isFinite(current) &&
        Number.isFinite(upcoming) &&
        upcoming > current
      );
    }).length;

    return {
      count: records.length,
      spend: totalSpend,
      upcoming: upcomingCount,
    };
  }, [records]);

  const handleNext = () => {
    if (hasNext && !loading) {
      setSelectedRecord(null);
      setPage((previous) => previous + 1);
    }
  };

  const handlePrev = () => {
    if (page > 0 && !loading) {
      setSelectedRecord(null);
      setPage((previous) => previous - 1);
    }
  };

  const handleRetry = () => {
    setRetryKey((previous) => previous + 1);
  };

  return (
    <div className="rs-records-page">

      {/* Cinematic Hero */}

      <section className="rs-records-hero">
        <div className="rs-hero-decoration" />

        <div className="rs-hero-content">
          <div className="rs-eyebrow">
            <span className="rs-eyebrow-line" />
            VEHICLE CARE & MANAGEMENT
          </div>

          <h1>
            Every detail.
            <br />
            <span>Every journey.</span>
          </h1>

          <p className="rs-hero-description">
            A considered record of every service, every replacement
            and every kilometre that keeps you moving.
          </p>

          <div className="rs-hero-footer">
            <span>RIDESYNC V2</span>
            <span className="rs-hero-divider" />
            <span>MAINTENANCE ARCHIVE</span>
          </div>
        </div>

        <div className="rs-hero-index">
          <span>01</span>
          <div />
          <span>03</span>
        </div>
      </section>

      {/* Statistics */}

      <section className="rs-records-overview">
        <div className="rs-section-heading">
          <div>
            <span className="rs-eyebrow">YOUR VEHICLE HISTORY</span>
            <h2>Maintenance overview</h2>
          </div>

          <span className="rs-page-indicator">
            PAGE {String(page + 1).padStart(2, "0")}
          </span>
        </div>

        <div className="rs-stat-grid">

          <article className="rs-stat-card">
            <span className="rs-stat-label">RECORDS ON THIS PAGE</span>
            <strong className="rs-stat-value">
              {String(pageStats.count).padStart(2, "0")}
            </strong>
            <span className="rs-stat-caption">
              Maintenance entries displayed
            </span>
          </article>

          <article className="rs-stat-card rs-stat-accent">
            <span className="rs-stat-label">PAGE EXPENDITURE</span>
            <strong className="rs-stat-value">
              {formatCurrency(pageStats.spend)}
            </strong>
            <span className="rs-stat-caption">
              Combined cost of displayed records
            </span>
          </article>

          <article className="rs-stat-card">
            <span className="rs-stat-label">UPCOMING CHECKPOINTS</span>
            <strong className="rs-stat-value">
              {String(pageStats.upcoming).padStart(2, "0")}
            </strong>
            <span className="rs-stat-caption">
              Records with a future check-up KM
            </span>
          </article>

        </div>
      </section>

      {/* Records */}

      <section className="rs-records-section">
        <div className="rs-records-heading">
          <div>
            <span className="rs-eyebrow">THE ARCHIVE</span>
            <h2>Service records</h2>
            <p>
              Your maintenance history, organised in one place.
            </p>
          </div>

          <div className="rs-record-count">
            {String(records.length).padStart(2, "0")}
            <span>ENTRIES</span>
          </div>
        </div>

        {loading ? (
          <div className="rs-record-state">
            <div className="rs-loading-line" />
            <p>Retrieving your maintenance history...</p>
          </div>
        ) : error ? (
          <div className="rs-record-state">
            <span className="rs-state-symbol">!</span>
            <h3>Unable to retrieve records</h3>
            <p>{error}</p>

            <button
              className="rs-action-button"
              onClick={handleRetry}
            >
              Try again
            </button>
          </div>
        ) : records.length === 0 ? (
          <div className="rs-record-state">
            <span className="rs-state-symbol">—</span>
            <h3>Your archive begins here.</h3>
            <p>
              No maintenance records were found on this page.
            </p>
          </div>
        ) : (
          <div className="rs-table-frame">
            <div className="table-responsive">
              <table className="table table-hover align-middle rs-record-table">
                <thead>
                  <tr>
                    <th>Spare</th>
                    <th>Registration</th>
                    <th>Issue</th>
                    <th>Vehicle</th>
                    <th>Cost</th>
                    <th>Odometer</th>
                    <th>Service date</th>
                    <th>Next checkpoint</th>
                    <th>Notes</th>
                    <th>Details</th>
                  </tr>
                </thead>

                <tbody>
                  {records.map((record, index) => (
                    <tr
                      key={record.id}
                      style={{ animationDelay: `${index * 80}ms` }}
                      className="rs-record-row"
                    >
                      <td className="rs-spare-cell">
                        {record.spareName || "—"}
                      </td>

                      <td>
                        {record.rc || record.RC || "—"}
                      </td>

                      <td className="rs-issue-cell">
                        {record.issue || "—"}
                      </td>

                      <td>
                        {record.brandModel || "—"}
                      </td>

                      <td className="rs-price-cell">
                        {formatCurrency(record.price)}
                      </td>

                      <td>{formatNumber(record.currentKM)}</td>

                      <td>{formatDate(record.replacingDate)}</td>

                      <td className="rs-checkpoint-cell">
                        {formatNumber(record.upcomingCheckUpKM)}
                      </td>

                      <td className="rs-notes-cell">
                        {record.extraNotes || "—"}
                      </td>

                      <td>
                        <button
                          className="rs-view-button"
                          onClick={() => setSelectedRecord(record)}
                        >
                          Explore <span>↗</span>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Pagination */}

        {!loading && !error && records.length > 0 && (
          <div className="rs-pagination">
            <button
              className="rs-page-button"
              disabled={page === 0}
              onClick={handlePrev}
            >
              ← Previous
            </button>

            <span>
              <span className="rs-current-page">
                {String(page + 1).padStart(2, "0")}
              </span>
              <span className="rs-page-separator"> / </span>
              PAGE
            </span>

            <button
              className="rs-page-button"
              disabled={!hasNext}
              onClick={handleNext}
            >
              Next →
            </button>
          </div>
        )}
      </section>

      {/* Record Details Modal */}

      {selectedRecord && (
        <div
          className="rs-modal-backdrop"
          role="presentation"
          onClick={() => setSelectedRecord(null)}
        >
          <div
            className="rs-detail-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="rs-detail-title"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="rs-detail-header">
              <div>
                <span className="rs-eyebrow">MAINTENANCE ARCHIVE</span>
                <h2 id="rs-detail-title">Record details</h2>
              </div>

              <button
                className="rs-detail-close"
                onClick={() => setSelectedRecord(null)}
                aria-label="Close details"
              >
                ×
              </button>
            </div>

            <div className="rs-detail-body">
              <div className="rs-detail-identity">
                <span>COMPONENT</span>
                <h3>{selectedRecord.spareName || "Maintenance item"}</h3>
                <p>
                  {selectedRecord.brandModel || "Vehicle not specified"}
                </p>
              </div>

              <div className="rs-detail-grid">
                {[
                  ["Record ID", selectedRecord.id],
                  ["Registration", selectedRecord.rc || selectedRecord.RC],
                  ["Price", formatCurrency(selectedRecord.price)],
                  ["Current KM", formatNumber(selectedRecord.currentKM)],
                  ["Replacing date", formatDate(selectedRecord.replacingDate)],
                  ["Upcoming check-up", formatNumber(selectedRecord.upcomingCheckUpKM)],
                ].map(([label, value]) => (
                  <div className="rs-detail-item" key={label}>
                    <span>{label}</span>
                    <strong>{value ?? "—"}</strong>
                  </div>
                ))}
              </div>

              <div className="rs-detail-description">
                <span>ISSUE DESCRIPTION</span>
                <p>
                  {selectedRecord.issue || "No issue description provided."}
                </p>
              </div>

              <div className="rs-detail-description">
                <span>ADDITIONAL NOTES</span>
                <p>
                  {selectedRecord.extraNotes || "No additional notes."}
                </p>
              </div>
            </div>

            <div className="rs-detail-footer">
              <span>RIDESYNC V2</span>

              <button
                className="rs-action-button"
                onClick={() => setSelectedRecord(null)}
              >
                Close record
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}

export default ViewRecords;
