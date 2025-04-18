"use client"

import { useState } from "react"
import { Link, useNavigate, useLocation } from "react-router-dom"
import LogInModal from "./LogInModal"
import logo from "../assets/logo.png"

export default function GuestNavBar() {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false)
  const navigate = useNavigate()
  const location = useLocation()

  const openModal = () => setIsModalOpen(true)
  const closeModal = () => setIsModalOpen(false)
  const handleSignUpClick = () => {
    closeModal()
    navigate("/register")
  }

  const isActive = (path) => location.pathname === path

  return (
    <nav
      className="py-4 font-['Baloo'] relative z-50"
      style={{backgroundColor: "transparent" }}
    >
      <div className="container mx-auto flex items-center justify-between px-4">
        {/* Logo and Name */}
        <div className="flex items-center gap-3">
          <img src={logo || "/placeholder.svg"} alt="FurrEverCare Logo" className="h-18 object-contain" />
          <span className="font-bold text-xl md:text-2xl" style={{ color: "#042C3C", fontFamily: "'Baloo'" }}>
            FurrEverCare
          </span>
        </div>

        {/* Desktop Navigation Links */}
        <div className="hidden md:flex items-center gap-4 lg:gap-8">
          {[
            { to: "/", label: "PawPedia" },
            { to: "/our-app", label: "Our App" },
            { to: "/about-us", label: "About Us" },
          ].map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className="relative px-2 py-2 transition-colors duration-200 hover:text-[#EA6C7B] whitespace-nowrap"
              style={{
                color: isActive(to) ? "#EA6C7B" : "#042C3C",
                fontFamily: "'Baloo'",
                fontSize: "1.25rem",
                fontWeight: "bold",
              }}
            >
              {label}
              {isActive(to) && (
                <span className="absolute bottom-0 left-0 w-full h-1 bg-[#EA6C7B] rounded-t-md"></span>
              )}
            </Link>
          ))}

          <button
            onClick={openModal}
            className="ml-4 px-6 py-2 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-full hover:bg-[#EA6C7B]/10 transition-colors whitespace-nowrap"
            style={{ fontFamily: "'Baloo'", fontWeight: "bold" }}
          >
            Login
          </button>
        </div>

        {/* Mobile Menu Button */}
        <div className="md:hidden flex items-center gap-4">
          <button
            onClick={openModal}
            className="px-4 py-1.5 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-full hover:bg-[#EA6C7B]/10 transition-colors whitespace-nowrap"
            style={{ fontFamily: "'Baloo'", fontWeight: "bold" }}
          >
            Login
          </button>
          <button className="p-2 rounded-md hover:bg-gray-100" onClick={() => setMobileMenuOpen((prev) => !prev)}>
            <i className="fas fa-bars text-[#042C3C] text-xl"></i>
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden absolute top-full left-0 right-0 z-50 border-t border-gray-200" style={{ backgroundColor: "transparent" }}>
          {[
            { to: "/", label: "PawPedia" },
            { to: "/our-app", label: "Our App" },
            { to: "/about-us", label: "About Us" },
          ].map(({ to, label }) => (
            <Link
              key={to}
              to={to}
              className="block py-3 px-6 border-b border-gray-100 transition-colors duration-200 hover:bg-gray-100"
              style={{
                color: isActive(to) ? "#EA6C7B" : "#042C3C",
                fontFamily: "'Baloo'",
                fontWeight: "bold",
                backgroundColor: "transparent",
              }}
              onClick={() => setMobileMenuOpen(false)}
            >
              {label}
              {isActive(to) && <span className="ml-2 inline-block w-2 h-2 bg-[#EA6C7B] rounded-full"></span>}
            </Link>
          ))}
        </div>
      )}

      {/* Login Modal */}
      {isModalOpen && <LogInModal onClose={closeModal} onSignUp={handleSignUpClick} />}
    </nav>
  )
}
