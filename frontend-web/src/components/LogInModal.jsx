import React, { useState } from 'react';
import logoandname from "../assets/logoandname.svg";
import cat from "../assets/cat.png";
import { useNavigate } from 'react-router-dom';
import AuthService from "../config/AuthService";

const LogInModal = ({ onClose, previousRoute }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setIsLoading(true);
      setError('');
      
      await AuthService.login(email, password);
      localStorage.setItem("showWelcomeModal", "true");
      
      onClose();
      navigate('/home-pawpedia');
    } catch (err) {
      setError(err.message || 'Invalid email or password');
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    onClose();
    navigate(previousRoute);
  };

  const handleGoogleLogin = async () => {
    try {
      setIsLoading(true);
      setError('');
      
      await AuthService.loginWithGoogle();
      
      onClose();
      navigate('/home-pawpedia');
    } catch (error) {
      console.error("Google Sign-In failed:", error);
      let errorMessage = "Google Sign-In failed. Please try again.";
      if (error.code === 'auth/popup-closed-by-user') {
        errorMessage = "Sign-in was cancelled. Please try again.";
      } else if (error.code === 'auth/network-request-failed') {
        errorMessage = "Network error. Check your internet connection.";
      }
      setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const styles = {
    modalOverlay: {
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0, 0, 0, 0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000
    },
    modalContainer: {
      display: 'flex',
      borderRadius: '40px',
      overflow: 'hidden',
      boxShadow: '0 4px 20px rgba(0, 0, 0, 0.15)',
      width: '800px',
      height: '460px',
      backgroundColor: 'white',
      position: 'relative'
    },
    leftPanel: {
      flex: '1',
      padding: '30px',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: '#FFF7EC'
    },
    rightPanel: {
      flex: '1',
      backgroundColor: '#F5B941',
      overflow: 'hidden',
      display: 'flex',
      alignItems: 'flex-end',
      justifyContent: 'center'
    },
    catImage: {
      width: '350%',
      height: '310px',
      objectFit: 'cover',
      objectPosition: 'center bottom',
      transform: 'rotate(-3deg) translate(7px) translateY(60px)'
    },
    logo: {
      width: '200px',
      height: '150px',
      marginTop: '-5px'
    },
    subtitle: {
      color: '#042C3C',
      fontSize: '12px',
      marginTop: '-1px',
      marginBottom: '5px',
      fontFamily: 'Arial, sans-serif'
    },
    form: {
      width: '100%',
      maxWidth: '250px'
    },
    errorMessage: {
      padding: '8px',
      marginBottom: '10px',
      backgroundColor: '#FEECEC',
      border: '1px solid #F0B542',
      borderRadius: '20px',
      color: '#EA6C7B',
      fontSize: '12px',
      textAlign: 'center'
    },
    inputGroup: {
      marginBottom: '10px',
    },
    input: {
      width: '100%',
      padding: '10px 15px',
      border: '1px solid #F0B542',
      color: "#8A973F",
      borderRadius: '20px',
      fontSize: '14px',
      outline: 'none',
      boxSizing: 'border-box'
    },
    forgotPassword: {
      textAlign: 'right',
      fontSize: '10px',
      transform: 'translateX(-12px)',
      color: '#666',
      marginBottom: '10px',
      marginTop: '-8px',
      cursor: 'pointer',
      textDecoration: 'none'
    },
    loginButton: {
      width: '90%',
      padding: '8px',
      backgroundColor: '#EA6C7B',
      color: 'white',
      border: 'none',
      borderRadius: '16px',
      fontSize: '14px',
      fontWeight: 'bold',
      cursor: 'pointer',
      margin: '5px auto',
      display: 'block'
    },
    orDivider: {
      display: 'flex',
      alignItems: 'center',
      color: '#666',
      fontSize: '12px',
      margin: '5px 0',
      fontWeight: 'bold'
    },
    googleButton: {
      width: '60%',
      padding: '8px',
      backgroundColor: 'white',
      color: '#666',
      border: '1px solid #E0E0E0',
      borderRadius: '20px',
      fontSize: '14px',
      cursor: 'pointer',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      marginBottom: '2px'
    },
    googleIcon: {
      marginRight: '10px',
      width: '18px',
      height: '18'
    },
    signupText: {
      fontSize: '12px',
      color: '#666',
      marginTop: '15px',
      textAlign: 'center'
    },
    signupLink: {
      color: '#EA6C7B',
      textDecoration: 'none',
      cursor: 'pointer',
      fontWeight: 'bold'
    }
  };

  return (
    <div style={styles.modalOverlay}>
      <div style={styles.modalContainer}>
        <div style={styles.leftPanel}>
          <img src={logoandname} style={styles.logo} alt="FurrEverCare Logo" />
          <p style={styles.subtitle}>Sign In to Continue</p>

          {error && <div style={styles.errorMessage}>{error}</div>}

          <form style={styles.form} onSubmit={handleSubmit}>
            <div style={styles.inputGroup}>
              <input
                type="email"
                placeholder="Email"
                style={styles.input}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div style={styles.inputGroup}>
              <input
                type="password"
                placeholder="Password"
                style={styles.input}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <div style={styles.forgotPassword}>
              <a href="/forgot-password" style={{ color: 'inherit', textDecoration: 'none' }}>
                Forgot Password?
              </a>
            </div>
            <button 
              type="submit" 
              style={styles.loginButton}
              disabled={isLoading}
            >
              {isLoading ? "Logging in..." : "Login"}
            </button>
          </form>

          <div style={styles.orDivider}>OR</div>

          <button 
            style={styles.googleButton} 
            onClick={handleGoogleLogin}
            disabled={isLoading}
          >
            <svg width="18" height="18" viewBox="0 0 18 18" style={styles.googleIcon}>
              <path fill="#4285F4" d="M17.64 9.2c0-.637-.057-1.251-.164-1.84H9v3.481h4.844c-.209 1.125-.843 2.078-1.796 2.717v2.258h2.908c1.702-1.567 2.684-3.874 2.684-6.615z"></path>
              <path fill="#34A853" d="M9 18c2.43 0 4.467-.806 5.956-2.18l-2.908-2.259c-.806.54-1.837.86-3.048.86-2.344 0-4.328-1.584-5.036-3.711H.957v2.332A8.997 8.997 0 0 0 9 18z"></path>
              <path fill="#FBBC05" d="M3.964 10.71A5.41 5.41 0 0 1 3.682 9c0-.593.102-1.17.282-1.71V4.958H.957A8.996 8.996 0 0 0 0 9c0 1.452.348 2.827.957 4.042l3.007-2.332z"></path>
              <path fill="#EA4335" d="M9 3.58c1.321 0 2.508.454 3.44 1.345l2.582-2.58C13.463.891 11.426 0 9 0A8.997 8.997 0 0 0 .957 4.958L3.964 7.29C4.672 5.163 6.656 3.58 9 3.58z"></path>
            </svg>
            Continue with Google
          </button>

          <div style={styles.signupText}>
            Don't have an Account?&nbsp;
            <span 
              style={styles.signupLink} 
              onClick={() => {
                handleClose();
                navigate('/signup');
              }}
            >
              Sign Up
            </span>
          </div>
        </div>

        <div style={styles.rightPanel}>
          <div>
            <img src={cat} alt="Cat" style={styles.catImage} />
            <button style={{
              position: 'absolute',
              top: '10px',
              right: '30px',
              border: 'none',
              backgroundColor: 'transparent',
              fontSize: '30px',
              color: "#042C3C",
              cursor: 'pointer'
            }} onClick={handleClose}>
              ×
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LogInModal;