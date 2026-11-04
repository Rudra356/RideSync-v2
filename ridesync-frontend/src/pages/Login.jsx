
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (submitting) return;

    setError("");
    setSubmitting(true);

    try {
      await login(email.trim(), password);
      navigate("/view", { replace: true });
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else {
        setError(
          err.response?.data?.message ||
          "Unable to log in. Please check your connection and try again."
        );
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="rs-login-page">

      <div className="rs-login-layout">

        {/* LEFT BRAND PANEL */}
        <section className="rs-login-intro">

          <div className="rs-login-brand">
            <span className="rs-login-brand-name">
              RIDESYNC
            </span>
            <span className="rs-login-version">V2</span>
          </div>

          <div className="rs-login-brand-rule">
            <span />
          </div>

          <div className="rs-login-intro-content">

            <span className="rs-login-overline">
              ENGINEERED FOR THE ROAD
            </span>

            <h1>
              Every journey.
              <br />
              <span>One history.</span>
            </h1>

            <p>
              A smarter way to manage your vehicles.
              Keep every service, replacement and
              maintenance record organised with RideSync.
            </p>

          </div>

          <div className="rs-login-intro-bottom">
            <span>YOUR VEHICLE. YOUR RECORDS.</span>
            <span>EST. 2026</span>
          </div>

          {/* Decorative graphic */}
          <div className="rs-login-decoration">
            <span>RS</span>
            <div />
          </div>

        </section>

        {/* RIGHT LOGIN PANEL */}
        <section className="rs-login-form-panel">

          <div className="rs-login-card">

            <div className="rs-login-card-heading">

              <div className="rs-login-eyebrow">
                <span />
                MEMBER ACCESS
              </div>

              <h2>Welcome Back.</h2>

              <p>
                Sign in to access your RideSync account.
              </p>

            </div>

            <div className="rs-login-divider" />

            <form onSubmit={handleSubmit}>

              {/* EMAIL */}
              <div className="rs-login-field">
                <label htmlFor="login-email">
                  EMAIL ADDRESS
                </label>

                <input
                  id="login-email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="name@example.com"
                  autoComplete="email"
                  required
                />
              </div>

              {/* PASSWORD */}
              <div className="rs-login-field">
                <label htmlFor="login-password">
                  PASSWORD
                </label>

                <input
                  id="login-password"
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="Enter your password"
                  autoComplete="current-password"
                  required
                />
              </div>

              {/* ERROR */}
              {error && (
                <div className="rs-login-error" role="alert">
                  <span>!</span>
                  {error}
                </div>
              )}

              {/* SUBMIT */}
              <button
                type="submit"
                className="rs-login-submit"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="rs-login-spinner" />
                    Signing in...
                  </>
                ) : (
                  <>
                    Sign In
                    <span>↗</span>
                  </>
                )}
              </button>

            </form>

            <div className="rs-login-register">
              <span>Don't have an account?</span>
              <Link to="/register">
                Create one ↗
              </Link>
            </div>

            <div className="rs-login-secure">
              <span className="rs-login-secure-dot" />
              SECURE SESSION AUTHENTICATION
            </div>

          </div>

        </section>

      </div>

    </main>
  );
}

export default Login;
