import React, { useState } from 'react';
import logoandname from "../assets/logoandname.svg";

const SignUpModal = ({ onSignUp }) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSignUp) {
      onSignUp(firstName, lastName, phoneNumber, email, password, confirmPassword);
    }
  };

  return (
    <div className="fixed top-0 left-0 right-0 bottom-0 bg-black/50 flex justify-center items-center z-50">
      <div className="flex rounded-3xl overflow-hidden shadow-lg w-[90%] max-w-[700px] bg-white">
        
        {/* Left Panel - Logo */}
        <div className="w-1/2 p-10 flex flex-col items-center justify-center" style={{ backgroundColor: '#FFF7EC' }}>
          <img src={logoandname} className="w-[250px] h-[300px]" alt="Logo" />
        </div>
        
        {/* Right Panel - Form */}
        <div className="w-1/2 p-10 flex flex-col justify-center" style={{ backgroundColor: '#F0B542' }}>
          <h2 className="text-2xl font-baloo mb-4 text-center" style={{ color: '#042C3C' }}>
            Join Our Community!
          </h2>

          <form className="w-full" onSubmit={handleSubmit}>
            <div className="mb-4">
              <input
                type="text"
                placeholder="First Name"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                required
              />
            </div>
            <div className="mb-4">
              <input
                type="text"
                placeholder="Last Name"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <input
                type="tel"
                placeholder="Phone Number"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <input
                type="email"
                placeholder="Email"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="mb-4">
              <input
                type="password"
                placeholder="Password"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="mb-6">
              <input
                type="password"
                placeholder="Confirm Password"
                className="w-full p-3 rounded-2xl text-sm outline-none font-semibold"
                style={{ backgroundColor: '#FFF7EC', color: '#8A973F', border: 'none' }}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="w-full py-3 border-none rounded-3xl text-sm font-bold cursor-pointer"
              style={{ backgroundColor: '#EA6C7B', color: 'white' }}
            >
              Sign Up
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default SignUpModal;
