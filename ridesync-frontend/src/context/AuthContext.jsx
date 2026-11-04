
import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useState,
} from "react";

import api, {
  loginUser,
  registerUser,
  initializeCsrf,
} from "../api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Restore the server-side session.
  const refreshUser = useCallback(async () => {
    try {
      const response = await api.get("/api/me");
      setUser(response.data);
      return response.data;
    } catch (error) {
      if (error.response?.status === 401) {
        setUser(null);
        return null;
      }

      throw error;
    }
  }, []);

  // Check for an existing session on application startup.
  useEffect(() => {
    const restoreSession = async () => {
      try {
        await refreshUser();
      } catch (error) {
        console.error("Could not restore session:", error);
      } finally {
        setLoading(false);
      }
    };

    restoreSession();
  }, [refreshUser]);

  // Login.
  const login = async (email, password) => {
    await loginUser(email, password);
    return await refreshUser();
  };

  // Register.
  const register = async (email, userName, password) => {
    return await registerUser(email, userName, password);
  };

  // Logout.
  const logout = async () => {
    await initializeCsrf();
    await api.post("/logout");
    setUser(null);
  };

  const value = {
    user,
    loading,
    isAuthenticated: Boolean(user),
    login,
    register,
    logout,
    refreshUser,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
