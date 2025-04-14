// AuthService.js - Handles authentication operations

import axios from 'axios';
import { auth, provider, signInWithPopup } from '../config/firebase-config';

const API_BASE_URL = 'http://localhost:8080/api'; // Backend URL

// Setup axios interceptor for JWT tokens
const setupAxiosInterceptors = (token) => {
  axios.interceptors.request.use(
    config => {
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    error => Promise.reject(error)
  );
};

class AuthService {
  setToken(token) {
    localStorage.setItem('token', token);
    setupAxiosInterceptors(token);
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isAuthenticated() {
    return !!this.getToken();
  }

  setUser(user) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  clearAuth() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  async login(email, password) {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        email,
        password
      });

      const { userId, token } = response.data;
      this.setToken(token);

      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      this.setUser(userResponse.data);

      return userResponse.data;
    } catch (error) {
      console.error("Login failed:", error.response?.data || error.message);
      throw error.response?.data || new Error("Login failed");
    }
  }

  async register(name, phone, email, password) {
    try {
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        name,
        phone, // Single phone number
        email,
        password
      });

      const { userId, token } = response.data;
      this.setToken(token);

      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      this.setUser(userResponse.data);

      return userResponse.data;
    } catch (error) {
      console.error("Registration failed:", error.response?.data || error.message);
      throw error.response?.data || new Error("Registration failed");
    }
  }

  async loginWithGoogle() {
    try {
      console.log("Starting Google login process");
      const result = await signInWithPopup(auth, provider);
      const user = result.user;

      const idToken = await user.getIdToken();

      const response = await axios.post(`${API_BASE_URL}/auth/google-auth`, {
        idToken
      });

      const { token, user: userData } = response.data;
      this.setToken(token);
      this.setUser(userData);
      return userData;
    } catch (error) {
      console.error("Google login failed:", error);
      if (error.response) {
        console.error("Response data:", error.response.data);
        console.error("Response status:", error.response.status);
      }
      throw error;
    }
  }

  async logout() {
    try {
      await axios.post(`${API_BASE_URL}/auth/logout`);
      this.clearAuth();
    } catch (error) {
      console.error("Logout failed:", error);
      this.clearAuth();
    }
  }

  async updateProfile(userData) {
    try {
      const response = await axios.put(`${API_BASE_URL}/users/profile`, userData);
      const updatedUser = response.data;
      this.setUser(updatedUser);
      return updatedUser;
    } catch (error) {
      console.error("Profile update failed:", error);
      throw error;
    }
  }

  init() {
    const token = this.getToken();
    if (token) {
      setupAxiosInterceptors(token);
    }
  }
}

export default new AuthService();