
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Register() {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [userName, setUserName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (submitting) return;

    setError("");
    setSuccess("");

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setSubmitting(true);

    try {
      const response = await register(
        email.trim(),
        userName.trim(),
        password
      );

      setSuccess(response.message || "Registration successful.");

      setTimeout(() => {
        navigate("/login", { replace: true });
      }, 1200);
    } catch (err) {
      setError(
        err.response?.data?.message ||
        "Registration failed. Please try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="rs-register-page">
      <div className="rs-register-layout">

        {/* LEFT INTRODUCTION */}
        <section className="rs-register-intro">

          <div className="rs-register-intro-content">
            <span className="rs-register-overline">
              ENGINEERED FOR THE ROAD
            </span>

            <h1>
              Start your
              <br />
              <span>journey here.</span>
            </h1>

            <p>
              Create your RideSync account and take control
              of your vehicle's maintenance history.
              Every service, every replacement, every detail.
            </p>
          </div>

          <div className="rs-register-intro-bottom">
            <span>YOUR VEHICLE. YOUR RECORDS.</span>
            <span>RIDESYNC V2</span>
          </div>

        </section>

        {/* RIGHT REGISTRATION FORM */}
        <section className="rs-register-form-panel">

          <div className="rs-register-card">

            <div className="rs-register-card-heading">
              <div className="rs-register-eyebrow">
                <span />
                NEW MEMBERSHIP
              </div>

              <h2>Create Account.</h2>

              <p>
                Set up your account to get started with RideSync.
              </p>
            </div>

            <div className="rs-register-divider" />

            <form onSubmit={handleSubmit}>

              <div className="rs-register-field">
                <label htmlFor="register-username">
                  USERNAME
                </label>

                <input
                  id="register-username"
                  type="text"
                  value={userName}
                  onChange={(event) => setUserName(event.target.value)}
                  placeholder="Choose a username"
                  autoComplete="username"
                  maxLength={100}
                />
              </div>

              <div className="rs-register-field">
                <label htmlFor="register-email">
                  EMAIL ADDRESS
                </label>

                <input
                  id="register-email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  placeholder="name@example.com"
                  autoComplete="email"
                  maxLength={254}
                  required
                />
              </div>

              <div className="rs-register-field">
                <label htmlFor="register-password">
                  PASSWORD
                </label>

                <input
                  id="register-password"
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  placeholder="Create a password"
                  autoComplete="new-password"
                  required
                />
              </div>

              <div className="rs-register-field">
                <label htmlFor="register-confirm-password">
                  CONFIRM PASSWORD
                </label>

                <input
                  id="register-confirm-password"
                  type="password"
                  value={confirmPassword}
                  onChange={(event) =>
                    setConfirmPassword(event.target.value)
                  }
                  placeholder="Enter your password again"
                  autoComplete="new-password"
                  required
                />
              </div>

              {error && (
                <div className="rs-register-message error" role="alert">
                  <span>!</span>
                  {error}
                </div>
              )}

              {success && (
                <div className="rs-register-message success" role="status">
                  <span>✓</span>
                  {success}
                </div>
              )}

              <button
                type="submit"
                className="rs-register-submit"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="rs-register-spinner" />
                    Creating Account...
                  </>
                ) : (
                  <>
                    Create Account
                    <span>↗</span>
                  </>
                )}
              </button>

            </form>

            <div className="rs-register-login">
              <span>Already have an account?</span>
              <Link to="/login">Sign In ↗</Link>
            </div>

            <div className="rs-register-secure">
              <span />
              ACCOUNT REGISTRATION
            </div>

          </div>

        </section>

      </div>
    </main>
  );
}

export default Register;
