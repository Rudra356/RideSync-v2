
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true,
  withXSRFToken: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
  headers: {
    "Content-Type": "application/json",
  },
});

// Obtain a CSRF token before sending protected state-changing requests.
export const initializeCsrf = async () => {
  const response = await api.get("/csrf");
  return response.data;
};

// Login using email and password.
export const loginUser = async (email, password) => {
  await initializeCsrf();

  const response = await api.post("/api/login", {
    email,
    password,
  });

  return response.data;
};

// Register a new account.
export const registerUser = async (email, userName, password) => {
  await initializeCsrf();

  const response = await api.post("/register", {
    email,
    userName,
    password,
  });

  return response.data;
};

export default api;
