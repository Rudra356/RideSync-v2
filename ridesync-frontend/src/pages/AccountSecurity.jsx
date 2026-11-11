
import React, { useState } from "react";
import { useAuth } from "../context/AuthContext";
import api, { initializeCsrf } from "../api";

function AccountSecurity() {
    const { user, refreshUser } = useAuth();

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const hasPassword = Boolean(user?.hasPassword);

    const handleSubmit = async (event) => {
        event.preventDefault();

        setMessage("");
        setError("");

        if (newPassword.length < 8) {
            setError("Password must contain at least 8 characters.");
            return;
        }

        if (newPassword !== confirmPassword) {
            setError("New password and confirmation do not match.");
            return;
        }

        setLoading(true);

        try {
            await initializeCsrf();

            if (hasPassword) {
                await api.put("/api/account/password", {
                    currentPassword,
                    newPassword,
                    confirmPassword
                });
            } else {
                await api.post("/api/account/password", {
                    newPassword,
                    confirmPassword
                });
            }

            setCurrentPassword("");
            setNewPassword("");
            setConfirmPassword("");

            setMessage(
                hasPassword
                    ? "Your password has been changed successfully."
                    : "Your password has been set successfully."
            );

            try {
                await refreshUser();
            } catch (refreshError) {
                console.error("Could not refresh account details:", refreshError);
            }

        } catch (err) {
            const responseMessage = err.response?.data?.message
                || err.response?.data?.error;

            setError(
                responseMessage
                || "Something went wrong. Please try again."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <section className="rs-security-page">

            <div className="rs-security-header">
                <span className="rs-security-eyebrow">
                    ACCOUNT MANAGEMENT
                </span>

                <h1>Account Security</h1>

                <p>
                    Manage your account credentials and connected
                    authentication providers.
                </p>
            </div>

            <div className="rs-security-grid">

                {/* Account information */}
                <div className="rs-security-card">

                    <div className="rs-security-card-heading">
                        <span>01 / ACCOUNT</span>
                        <h2>Account Information</h2>
                    </div>

                    <div className="rs-security-info">
                        <span>Email Address</span>
                        <strong>{user?.email || "Loading..."}</strong>
                    </div>

                    <div className="rs-security-info">
                        <span>Password Status</span>
                        <strong>
                            {hasPassword ? "Password configured" : "No password set"}
                        </strong>
                    </div>

                </div>

                {/* Connected providers */}
                <div className="rs-security-card">

                    <div className="rs-security-card-heading">
                        <span>02 / CONNECTIVITY</span>
                        <h2>Connected Accounts</h2>
                    </div>

                    <div className="rs-security-provider">
                        <div>
                            <strong>Google</strong>
                            <span>Google authentication</span>
                        </div>

                        <span className={
                            user?.googleLinked
                                ? "rs-security-status connected"
                                : "rs-security-status disconnected"
                        }>
                            {user?.googleLinked ? "CONNECTED" : "NOT CONNECTED"}
                        </span>
                    </div>

                    <div className="rs-security-provider">
                        <div>
                            <strong>GitHub</strong>
                            <span>GitHub authentication</span>
                        </div>

                        <span className={
                            user?.githubLinked
                                ? "rs-security-status connected"
                                : "rs-security-status disconnected"
                        }>
                            {user?.githubLinked ? "CONNECTED" : "NOT CONNECTED"}
                        </span>
                    </div>

                </div>

                {/* Password management */}
                <div className="rs-security-card rs-security-password-card">

                    <div className="rs-security-card-heading">
                        <span>03 / CREDENTIALS</span>

                        <h2>
                            {hasPassword ? "Change Password" : "Set Password"}
                        </h2>

                        <p>
                            {hasPassword
                                ? "Update your existing RideSync account password."
                                : "Create a password to enable manual email and password login alongside social authentication."
                            }
                        </p>
                    </div>

                    <form onSubmit={handleSubmit}>

                        {hasPassword && (
                            <div className="rs-security-field">
                                <label htmlFor="currentPassword">
                                    Current Password
                                </label>

                                <input
                                    id="currentPassword"
                                    type="password"
                                    value={currentPassword}
                                    onChange={(e) => setCurrentPassword(e.target.value)}
                                    placeholder="Enter current password"
                                    autoComplete="current-password"
                                    required
                                />
                            </div>
                        )}

                        <div className="rs-security-field">
                            <label htmlFor="newPassword">
                                New Password
                            </label>

                            <input
                                id="newPassword"
                                type="password"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                placeholder="Minimum 8 characters"
                                autoComplete="new-password"
                                minLength={8}
                                maxLength={72}
                                required
                            />
                        </div>

                        <div className="rs-security-field">
                            <label htmlFor="confirmPassword">
                                Confirm New Password
                            </label>

                            <input
                                id="confirmPassword"
                                type="password"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="Re-enter new password"
                                autoComplete="new-password"
                                minLength={8}
                                maxLength={72}
                                required
                            />
                        </div>

                        {error && (
                            <div className="rs-security-feedback error">
                                {error}
                            </div>
                        )}

                        {message && (
                            <div className="rs-security-feedback success">
                                {message}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="rs-security-submit"
                            disabled={loading}
                        >
                            {loading
                                ? "PLEASE WAIT..."
                                : hasPassword
                                    ? "UPDATE PASSWORD"
                                    : "SET PASSWORD"
                            }
                        </button>

                    </form>

                </div>

            </div>

        </section>
    );
}

export default AccountSecurity;
