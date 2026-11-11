
import React, { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Navbar() {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const navigate = useNavigate();

  const [loggingOut, setLoggingOut] = useState(false);
  const [logoutError, setLogoutError] = useState("");
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = async () => {
    setLogoutError("");
    setLoggingOut(true);

    try {
      await logout();
      setMenuOpen(false);
      navigate("/login", { replace: true });
    } catch (error) {
      console.error("Logout failed:", error);
      setLogoutError("Unable to log out. Please try again.");
    } finally {
      setLoggingOut(false);
    }
  };

  const closeMenu = () => setMenuOpen(false);

  const navLinkClass = ({ isActive }) =>
    `rs-nav-link${isActive ? " active" : ""}`;

  return (
    <>
      <nav className="navbar navbar-expand-lg rs-navbar">
        <div className="container-fluid rs-navbar-container">

          <Link
            className="navbar-brand rs-brand"
            to="/"
            onClick={closeMenu}
          >
            <span className="rs-brand-name">RIDESYNC</span>
            <span className="rs-brand-version">V2</span>
            <span className="rs-brand-line" />
          </Link>

          <button
            className="navbar-toggler rs-toggler"
            type="button"
            aria-controls="rsNavbar"
            aria-expanded={menuOpen}
            aria-label="Toggle navigation"
            onClick={() => setMenuOpen((prev) => !prev)}
          >
            <span />
            <span />
          </button>

          <div
            className={`collapse navbar-collapse rs-navbar-collapse${
              menuOpen ? " show" : ""
            }`}
            id="rsNavbar"
          >
            <ul className="navbar-nav ms-auto align-items-lg-center rs-nav-list">

              {/* Public Navigation */}
              <li className="nav-item">
                <NavLink
                  className={navLinkClass}
                  to="/about"
                  onClick={closeMenu}
                >
                  About
                </NavLink>
              </li>

              {/* Authenticated Navigation */}
              {!loading && isAuthenticated && (
                <>
                  <li className="nav-item">
                    <NavLink
                      className={navLinkClass}
                      to="/view"
                      onClick={closeMenu}
                    >
                      Maintenance
                    </NavLink>
                  </li>

                  <li className="nav-item">
                    <NavLink
                      className={navLinkClass}
                      to="/upload"
                      onClick={closeMenu}
                    >
                      Add Record
                    </NavLink>
                  </li>

                  <li className="nav-item">
                    <NavLink
                      className={navLinkClass}
                      to="/account/security"
                      onClick={closeMenu}
                    >
                      Account Security
                    </NavLink>
                  </li>

                  {/* Account Information */}
                  <li className="nav-item rs-account">
                    <span className="rs-account-label">
                      SIGNED IN AS
                    </span>

                    <span className="rs-account-email">
                      {user?.email}
                    </span>
                  </li>

                  {/* Logout */}
                  <li className="nav-item">
                    <button
                      type="button"
                      className="rs-logout"
                      onClick={handleLogout}
                      disabled={loggingOut}
                    >
                      {loggingOut ? "Signing out..." : "Logout"}
                      <span> ↗</span>
                    </button>
                  </li>
                </>
              )}

              {/* Unauthenticated Navigation */}
              {!loading && !isAuthenticated && (
                <>
                  <li className="nav-item">
                    <NavLink
                      className={navLinkClass}
                      to="/login"
                      onClick={closeMenu}
                    >
                      Login
                    </NavLink>
                  </li>

                  <li className="nav-item">
                    <NavLink
                      className="rs-register"
                      to="/register"
                      onClick={closeMenu}
                    >
                      Get Started <span>↗</span>
                    </NavLink>
                  </li>
                </>
              )}

            </ul>
          </div>
        </div>
      </nav>

      {/* Logout Error */}
      {logoutError && (
        <div className="alert alert-danger mx-3 mt-2" role="alert">
          {logoutError}
        </div>
      )}
    </>
  );
}

export default Navbar;
