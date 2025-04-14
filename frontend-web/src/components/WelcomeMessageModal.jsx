"use client"

import { useState, useEffect } from "react"
import { X } from "lucide-react"
import logo from "../assets/logo.png";

const WelcomeMessageModal = ({ isOpen, onClose, userName }) => {
  // If userName is provided, we'll personalize the greeting
  const greeting = userName ? `Welcome back, ${userName}!` : "Welcome to FurrEverCare!"

  // Animation states
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    if (isOpen) {
      setIsVisible(true)
    }
  }, [isOpen])

  const handleClose = () => {
    setIsVisible(false)
    // Delay the actual closing to allow for animation
    setTimeout(() => {
      onClose()
    }, 300)
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo'] transition-opacity duration-300 ease-in-out">
      <div
        className={`bg-white rounded-lg shadow-xl max-w-md w-full mx-4 overflow-hidden transition-all duration-300 ease-in-out ${
          isVisible ? "opacity-100 scale-100" : "opacity-0 scale-95"
        }`}
      >
        <div className="relative p-6">
          {/* Close button */}
          <button
            onClick={handleClose}
            className="absolute top-4 right-4 text-gray-500 hover:text-[#042C3C] transition-colors"
            aria-label="Close"
          >
            <X className="h-6 w-6" />
          </button>

          {/* Logo */}
          <div className="flex justify-center mb-6 mt-4">
            <div className="w-24 h-24 rounded-full bg-white flex items-center justify-center">
            <img
            src={logo}
            alt="FurrEverCare Logo"
            className="w-full h-full object-contain"
            />
            </div>
          </div>

          {/* Welcome message */}
          <div className="text-center mb-6">
            <h2 className="text-2xl font-bold text-[#042C3C] mb-4 flex items-center justify-center">
              <span className="text-[#EA6C7B] mr-2">🐾</span>
              {greeting}
              <span className="text-[#EA6C7B] ml-2">🐾</span>
            </h2>
            <p className="text-[#042C3C] mb-6 leading-relaxed">
              We're thrilled to have you with us! FurrEverCare is your all-in-one pet wellness platform designed to help
              you provide the best care for your furry friends.
            </p>
          </div>

          {/* Action button */}
          <div className="flex justify-center">
            <button
              onClick={handleClose}
              className="px-8 py-3 bg-[#EA6C7B] text-white rounded-full hover:bg-[#EA6C7B]/90 transition-colors font-medium"
            >
              Get Started
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default WelcomeMessageModal
