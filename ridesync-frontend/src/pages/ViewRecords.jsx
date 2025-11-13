import React, { useState } from "react";
import { useQuery, gql } from "@apollo/client";

// ✅ GraphQL Query — fetches paginated records
const GET_RECORDS = gql`
  query GetRecords($page: Int!, $size: Int!) {
    getAllPaginated(page: $page, size: $size) {
      id
      spareName
      RC
      brandModel
      price
      currentKM
      upcomingCheckUpKM
    }
  }
`;

function ViewRecords() {
  const [page, setPage] = useState(0);
  const size = 5;

  const { data, loading, error, refetch } = useQuery(GET_RECORDS, {
    variables: { page, size },
    fetchPolicy: "cache-and-network", // ✅ Ensures it updates even after pagination
  });

  // ✅ handle page navigation
  const handleNext = () => {
    setPage((prev) => prev + 1);
    refetch({ page: page + 1, size });
  };

  const handlePrev = () => {
    if (page > 0) {
      setPage((prev) => prev - 1);
      refetch({ page: page - 1, size });
    }
  };

  if (loading) return <p className="text-center mt-4">Loading records...</p>;
  if (error) return <p className="text-danger mt-4">Error: {error.message}</p>;

  const records = data?.getAllPaginated || [];

  return (
    <div className="container mt-4">
      <h3 className="text-center mb-3">Maintenance Records</h3>

      {records.length === 0 ? (
        <p className="text-center text-muted">No records found.</p>
      ) : (
        <table className="table table-hover table-bordered">
          <thead className="table-dark">
            <tr>
              <th>Spare</th>
              <th>RC</th>
              <th>Brand Model</th>
              <th>Price</th>
              <th>KM</th>
              <th>Upcoming KM</th>
            </tr>
          </thead>
          <tbody>
            {records.map((rec) => (
              <tr key={rec.id}>
                <td>{rec.spareName}</td>
                <td>{rec.RC}</td>
                <td>{rec.brandModel}</td>
                <td>₹{rec.price}</td>
                <td>{rec.currentKM}</td>
                <td>{rec.upcomingCheckUpKM}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Pagination Controls */}
      <div className="d-flex justify-content-between mt-3">
        <button
          className="btn btn-outline-secondary"
          disabled={page === 0}
          onClick={handlePrev}
        >
          ◀ Previous
        </button>
        <span className="align-self-center">Page {page + 1}</span>
        <button
          className="btn btn-outline-primary"
          onClick={handleNext}
          disabled={records.length < size} // disable next if less than page size
        >
          Next ▶
        </button>
      </div>
    </div>
  );
}

export default ViewRecords;
