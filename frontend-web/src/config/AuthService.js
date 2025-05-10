import axios from 'axios';
import React from "react";
import { auth, provider, signInWithPopup } from '../config/firebase-config';
import { createUserWithEmailAndPassword, signInWithEmailAndPassword, updateProfile } from 'firebase/auth'; 

const API_BASE_URL = 'https://furrevercare-deploy-13.onrender.com/api'; 
//const API_BASE_URL = 'http://localhost:8080/api';

let requestInterceptor = null;

const setupAxiosInterceptors = (token) => {
  console.log('AuthService: Setting up Axios interceptor with token:', token ? 'present' : 'null');
  if (requestInterceptor !== null) {
    axios.interceptors.request.eject(requestInterceptor);
    console.log('AuthService: Ejected previous interceptor');
  }
  
  requestInterceptor = axios.interceptors.request.use(
    config => {
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('AuthService: Added Authorization header:', config.headers.Authorization);
      } else {
        console.log('AuthService: No token available for Authorization header');
      }
      return config;
    },
    error => {
      console.error('AuthService: Interceptor error:', error);
      return Promise.reject(error);
    }
  );
};

class AuthService {
  setToken(token) {
    console.log('AuthService: Setting token:', token);
    localStorage.setItem('token', token);
    setupAxiosInterceptors(token);
  }

  getToken() {
    const token = localStorage.getItem('token');
    console.log('AuthService: Getting token:', token ? 'present' : 'null');
    return token;
  }

  isAuthenticated() {
    const isAuth = !!this.getToken();
    console.log('AuthService: Is authenticated:', isAuth);
    return isAuth;
  }

  setUser(user) {
    console.log('AuthService: Setting user:', user);
    const normalizedUser = {
        userId: user.userID || user.userId,
        userID: user.userID || user.userId, // Add userID with uppercase I to maintain compatibility
        name: user.name,
        email: user.email,
        phone: user.phone
    };
    localStorage.setItem('user', JSON.stringify(normalizedUser));
}

  getUser() {
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : null;
    console.log('AuthService: Getting user:', user);
    return user;
  }

  clearAuth() {
    console.log('AuthService: Clearing auth');
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setupAxiosInterceptors(null);
  }

  async login(email, password) {
    try {
      console.log('AuthService: Login attempt with Firebase:', { email });
      // Sign in with Firebase Authentication
      const userCredential = await signInWithEmailAndPassword(auth, email, password);
      const user = userCredential.user;
  
      // Get Firebase ID token
      const idToken = await user.getIdToken();
      console.log('AuthService: Firebase idToken:', idToken ? 'present' : 'null');
  
      // Send to backend
      const response = await axios.post(`${API_BASE_URL}/auth/login`, {
        email,
        idToken,
        firebaseUid: user.uid
      });
      console.log('AuthService: Login response:', response.data);
  
      const { userId, token } = response.data;
      this.setToken(token);
      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      console.log('AuthService: User response:', userResponse.data);
      this.setUser(userResponse.data);
      return userResponse.data;
    } catch (error) {
      console.error('AuthService: Login error:', error.response?.data || error.message);
      throw error.response?.data || new Error('Login failed');
    }
  }

  async register(name, phone, email, password) {
    try {
      console.log('AuthService: Register attempt with Firebase:', { name, email });
      // Create user in Firebase Authentication
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      const user = userCredential.user;
  
      // Update Firebase user profile with name
      await updateProfile(user, { displayName: name });
  
      // Get Firebase ID token
      const idToken = await user.getIdToken();
      console.log('AuthService: Firebase idToken:', idToken ? 'present' : 'null');
  
      // Send user data to backend
      const response = await axios.post(`${API_BASE_URL}/auth/register`, {
        name,
        phone,
        email,
        firebaseUid: user.uid,
        idToken
      });
      console.log('AuthService: Register response:', response.data);
  
      const { userId, token } = response.data;
      this.setToken(token);
      const userResponse = await axios.get(`${API_BASE_URL}/users/${userId}`);
      console.log('AuthService: User response:', userResponse.data);
      this.setUser(userResponse.data);
      return userResponse.data;
    } catch (error) {
      console.error('AuthService: Register error:', error.response?.data || error.message);
      throw error.response?.data || new Error('Registration failed');
    }
  }

  async loginWithGoogle() {
    try {
      console.log('AuthService: Google login start');
      const result = await signInWithPopup(auth, provider);
      const user = result.user;
      const idToken = await user.getIdToken();
      console.log('AuthService: Firebase idToken:', idToken ? 'present' : 'null');
      const response = await axios.post(`${API_BASE_URL}/auth/google-auth`, {
        idToken
      });
      console.log('AuthService: Google auth response:', response.data);
      const { token, user: userData } = response.data;
      this.setToken(token);
      this.setUser(userData);
      return userData;
    } catch (error) {
      console.error('AuthService: Google login error:', error.response?.data || error.message);
      throw error;
    }
  }

  async logout() {
    try {
      console.log('AuthService: Logging out');
      await axios.post(`${API_BASE_URL}/auth/logout`);
      this.clearAuth();
    } catch (error) {
      console.error('AuthService: Logout error:', error);
      this.clearAuth();
    }
  }

  async updateProfile(userData) {
    try {
      const token = this.getToken();
      if (!token) {
        console.log('AuthService: No token found, redirecting to login');
        window.location.href = '/';
        return;
      }
      console.log('AuthService: Token before request:', token);
      console.log('AuthService: Updating profile:', userData);
      const response = await axios.put(`${API_BASE_URL}/users/profile`, {
        userID: userData.userId,
        name: userData.name,
        email: userData.email,
        phone: userData.phone
      });
      console.log('AuthService: Profile update response:', response.data);
      const updatedUser = {
        userId: userData.userId,
        name: userData.name,
        email: userData.email,
        phone: userData.phone
      };
      this.setUser(updatedUser);
      return updatedUser;
    } catch (error) {
      console.error('AuthService: Profile update error:', error.response?.data || error.message);
      if (error.response?.status === 403) {
        console.log('AuthService: Token likely invalid or expired, redirecting to login');
        this.clearAuth();
        window.location.href = '/';
      }
      throw error;
    }
  }

  init() {
    const token = this.getToken();
    console.log('AuthService: Initializing with token:', token ? 'present' : 'null');
    // Setup request interceptor (adds token to headers)
    setupAxiosInterceptors(token);

    // Setup response interceptor (handles errors globally)
    axios.interceptors.response.use(
      response => response, // Pass through successful responses
      error => {
        console.error('AuthService: Response interceptor error:', error.response || error);
        if (error.response && error.response.status === 403) {
          console.log('AuthService: Received 403 Forbidden error. Clearing auth and redirecting to /');
          this.clearAuth(); // Clear token and user data
          // Use window.location to force a full page reload and redirect
          if (window.location.pathname !== '/') {
             window.location.href = '/';
          }
        } else if (error.response && error.response.status === 401) {
            console.log('AuthService: Received 401 Unauthorized error. Clearing auth and redirecting to /login');
            this.clearAuth();
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }
        // Important: Reject the promise so component-level error handling can still catch it
        return Promise.reject(error);
      }
    );
  }
}

export default new AuthService();