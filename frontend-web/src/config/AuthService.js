import axios from 'axios';
import React from "react";
import { auth, provider, signInWithPopup } from '../config/firebase-config';

const API_BASE_URL = 'https://furrevercare-deploy-8.onrender.com/api';

let requestInterceptor = null;

const setupAxiosInterceptors = (token) => {
  // Remove previous interceptor if it exists
  if (requestInterceptor !== null) {
    axios.interceptors.request.eject(requestInterceptor);
  }
  
  // Add new interceptor
  requestInterceptor = axios.interceptors.request.use(
    config => {
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('Added Authorization header:', config.headers.Authorization);
      } else {
        console.log('No token available for Authorization header');
      }
      return config;
    },
    error => Promise.reject(error)
  );
};

class AuthService {
  setToken(token) {
    console.log('Setting token:', token);
    localStorage.setItem('token', token);
    setupAxiosInterceptors(token);
  }

  getToken() {
    const token = localStorage.getItem('token');
    console.log('Getting token:', token);
    return token;
  }

  isAuthenticated() {
    const isAuth = !!this.getToken();
    console.log('Is authenticated:', isAuth);
    return isAuth;
  }

  setUser(user) {
    console.log('Setting user:', user);
    const normalizedUser = {
      userId: user.userID || user.userId,
      name: user.name,
      email: user.email,
      phone: user.phone
    };
    localStorage.setItem('user', JSON.stringify(normalizedUser));
  }

  getUser() {
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : null;
    console.log('Getting user:', user);
    return user;
  }

  clearAuth() {
    console.log('Clearing auth');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setupAxiosInterceptors(null); // Clear the interceptor by passing null token
  }

  async login(email, password) {
    try {
      console.log('Login attempt:', { email });
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        email,
        password
      });
      console.log('Login response:', response.data);
      const { userId, token } = response.data;
      this.setToken(token);
      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      console.log('User response:', userResponse.data);
      this.setUser(userResponse.data);
      return userResponse.data;
    } catch (error) {
      console.error('Login error:', error.response?.data || error.message);
      throw error.response?.data || new Error('Login failed');
    }
  }

  async register(name, phone, email, password) {
    try {
      console.log('Register attempt:', { name, email });
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        name,
        phone,
        email,
        password
      });
      console.log('Register response:', response.data);
      const { userId, token } = response.data;
      this.setToken(token);
      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      console.log('User response:', userResponse.data);
      this.setUser(userResponse.data);
      return userResponse.data;
    } catch (error) {
      console.error('Register error:', error.response?.data || error.message);
      throw error.response?.data || new Error('Registration failed');
    }
  }

  async loginWithGoogle() {
    try {
      console.log('Google login start');
      const result = await signInWithPopup(auth, provider);
      const user = result.user;
      const idToken = await user.getIdToken();
      console.log('Firebase idToken:', idToken);
      const response = await axios.post(`${API_BASE_URL}/auth/google-auth`, {
        idToken
      });
      console.log('Google auth response:', response.data);
      const { token, user: userData } = response.data;
      this.setToken(token);
      this.setUser(userData);
      return userData;
    } catch (error) {
      console.error('Google login error:', error.response?.data || error.message);
      throw error;
    }
  }

  async logout() {
    try {
      console.log('Logging out');
      await axios.post(`${API_BASE_URL}/auth/logout`);
      this.clearAuth();
    } catch (error) {
      console.error('Logout error:', error);
      this.clearAuth();
    }
  }

  async updateProfile(userData) {
    try {
      const token = this.getToken();
      if (!token) {
        console.log('No token found, redirecting to login');
        window.location.href = '/login';
        return;
      }
      console.log('Token before request:', token);
      console.log('Updating profile:', userData);
      const response = await axios.put(`${API_BASE_URL}/users/profile`, {
        userID: userData.userId,
        name: userData.name,
        email: userData.email,
        phone: userData.phone
      });
      console.log('Profile update response:', response.data);
      const updatedUser = {
        userId: userData.userId,
        name: userData.name,
        email: userData.email,
        phone: userData.phone
      };
      this.setUser(updatedUser);
      return updatedUser;
    } catch (error) {
      console.error('Profile update error:', error.response?.data || error.message);
      if (error.response?.status === 403) {
        console.log('Token likely invalid or expired, redirecting to login');
        this.clearAuth();
        window.location.href = '/login';
      }
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