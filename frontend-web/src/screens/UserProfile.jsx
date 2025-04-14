"use client"

import { useState, useEffect } from "react"
import { User, Edit, LogOut } from "lucide-react"
import { useNavigate } from "react-router-dom"
import LogoutConfirmationModal from "../components/LogoutConfirmationModal"
import UserNavBar from "../components/UserNavBar"
import AuthService from "../config/AuthService"

const UserProfile = () => {
  const [user, setUser] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    profileImage: null,
  })

  const [isEditing, setIsEditing] = useState(false)
  const [editedUser, setEditedUser] = useState({ ...user })
  const [showLogoutModal, setShowLogoutModal] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const navigate = useNavigate()

  // Load user data when component mounts
  useEffect(() => {
    const loadUserData = () => {
      const userData = AuthService.getUser();
      if (!userData) {
        // Redirect to login if no user data found
        navigate('/login');
        return;
      }
      setUser(userData);
      setEditedUser(userData);
    };

    loadUserData();
  }, [navigate]);

  const handleEditToggle = async () => {
    if (isEditing) {
      // Save changes
      try {
        setIsLoading(true);
        setError("");
        const updatedUser = await AuthService.updateProfile(editedUser);
        setUser(updatedUser);
        setIsEditing(false);
      } catch (err) {
        setError(err.response?.data?.message || "Failed to update profile");
      } finally {
        setIsLoading(false);
      }
    } else {
      // Enter edit mode
      setIsEditing(true);
    }
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setEditedUser((prev) => ({ ...prev, [name]: value }))
  }

  const handleLogoutClick = () => {
    setShowLogoutModal(true)
  }

  const handleLogoutConfirm = async () => {
    try {
      await AuthService.logout();
      setShowLogoutModal(false);
      navigate("/login");
    } catch (error) {
      console.error("Logout failed:", error);
      // Still navigate away even if backend logout fails
      navigate("/login");
    }
  }

  const handleLogoutCancel = () => {
    setShowLogoutModal(false)
  }

  // Redirect if not authenticated
  useEffect(() => {
    if (!AuthService.isAuthenticated()) {
      navigate('/login');
    }
  }, [navigate]);

  return (
    <div className="min-h-screen bg-gray-50 px-4 font-['Baloo']">
      <UserNavBar/>
      <div className="max-w-2xl mx-auto mt-10">
        {error && (
          <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-lg">
            {error}
          </div>
        )}
        
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {/* Header */}
          <div className="p-6 border-b border-gray-100">
            <h2 className="text-3xl font-bold text-[#042C3C] flex items-center">
              <User className="h-5 w-5 mr-2 text-[#EA6C7B]" />
              User Profile
            </h2>
          </div>

          {/* Profile Content */}
          <div className="p-6 flex flex-col items-center">
            {/* Avatar */}
            <div className="w-24 h-24 rounded-full border-2 border-gray-200 flex items-center justify-center bg-gray-100 mb-4">
              {user.profileImage ? (
                <img
                  src={user.profileImage || "/placeholder.svg"}
                  alt={`${user.firstName} ${user.lastName}`}
                  className="w-full h-full rounded-full object-cover"
                />
              ) : (
                <User className="h-12 w-12 text-gray-400" />
              )}
            </div>

            {/* Name */}
            <h2 className="text-xl font-bold text-[#042C3C] mb-6">{`${user.firstName} ${user.lastName}`}</h2>

            {/* User Information */}
            <div className="text-gray-500 w-full bg-[#FFF7EC] rounded-lg border border-[#8A973F]/30 p-6 mb-6">
              {isEditing ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="firstName" className="block text-sm font-medium text-gray-600 mb-1">
                      First Name
                    </label>
                    <input
                      id="firstName"
                      name="firstName"
                      type="text"
                      value={editedUser.firstName}
                      onChange={handleChange}
                      className="w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                  <div>
                    <label htmlFor="lastName" className="block text-sm font-medium text-gray-600 mb-1">
                      Last Name
                    </label>
                    <input
                      id="lastName"
                      name="lastName"
                      type="text"
                      value={editedUser.lastName}
                      onChange={handleChange}
                      className="text-gray-500 w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                  <div>
                    <label htmlFor="email" className="block text-sm font-medium text-gray-600 mb-1">
                      Email
                    </label>
                    <input
                      id="email"
                      name="email"
                      type="email"
                      value={editedUser.email}
                      onChange={handleChange}
                      className="text-gray-500 w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                  <div>
                    <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-600 mb-1">
                      Phone Number
                    </label>
                    <input
                      id="phoneNumber"
                      name="phoneNumber"
                      type="tel"
                      value={editedUser.phoneNumber}
                      onChange={handleChange}
                      className="text-gray-500 w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500">First Name</p>
                    <p className="font-medium text-[#042C3C]">{user.firstName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Last Name</p>
                    <p className="font-medium text-[#042C3C]">{user.lastName}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="font-medium text-[#042C3C]">{user.email}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Phone Number</p>
                    <p className="font-medium text-[#042C3C]">{user.phoneNumber}</p>
                  </div>
                </div>
              )}
            </div>

            {/* Action Buttons */}
            <div className="flex gap-4">
              <button
                onClick={handleEditToggle}
                disabled={isLoading}
                className="flex items-center gap-2 px-4 py-2 bg-[#EA6C7B] text-white rounded-lg hover:bg-[#d95969] transition disabled:opacity-50"
              >
                <Edit className="h-4 w-4" />
                {isEditing ? (isLoading ? "Saving..." : "Save") : "Edit Profile"}
              </button>
              <button
                onClick={handleLogoutClick}
                className="flex items-center gap-2 px-4 py-2 bg-gray-300 text-[#042C3C] rounded-lg hover:bg-gray-400 transition"
              >
                <LogOut className="h-4 w-4" />
                Logout
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Logout Confirmation Modal */}
      <LogoutConfirmationModal
        isOpen={showLogoutModal}
        onCancel={handleLogoutCancel}
        onConfirm={handleLogoutConfirm}
      />
    </div>
  );
};

export default UserProfile;
