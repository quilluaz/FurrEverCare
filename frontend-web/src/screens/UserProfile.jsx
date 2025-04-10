"use client"

import { useState } from "react"
import { User, Edit, LogOut } from "lucide-react"
import { useNavigate } from "react-router-dom"
import LogoutConfirmationModal from "../components/LogoutConfirmationModal"
import UserNavBar from "../components/UserNavBar"

const UserProfile = () => {
  const [user, setUser] = useState({
    firstName: "John",
    lastName: "Doe",
    email: "johndoe@example.com",
    phoneNumber: "(555) 000 - 0000",
    profileImage: null, // URL would go here if available
  })

  const [isEditing, setIsEditing] = useState(false)
  const [editedUser, setEditedUser] = useState({ ...user })
  const [showLogoutModal, setShowLogoutModal] = useState(false)
  const navigate = useNavigate()

  const handleEditToggle = () => {
    if (isEditing) {
      // Save changes
      setUser({ ...editedUser })
    }
    setIsEditing(!isEditing)
  }

  const handleChange = (e) => {
    const { name, value } = e.target
    setEditedUser((prev) => ({ ...prev, [name]: value }))
  }

  const handleLogoutClick = () => {
    setShowLogoutModal(true)
  }

  const handleLogoutConfirm = () => {
    // Implement actual logout logic here
    console.log("User logged out")
    setShowLogoutModal(false)
    navigate("/login")
  }

  const handleLogoutCancel = () => {
    setShowLogoutModal(false)
  }

  return (
    <div className="min-h-screen bg-gray-50 px-4 font-['Baloo']">
            <UserNavBar/>
      <div className="max-w-2xl mx-auto mt-10">
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

            {/* Actions */}
            <div className="text-gray-500 flex flex-col sm:flex-row gap-4 w-full max-w-md">
              <button
                onClick={handleEditToggle}
                className="flex-1 py-2 px-4 border-2 border-[#8A973F] text-[#8A973F] rounded-md hover:bg-[#8A973F]/10 transition-colors flex items-center justify-center gap-2"
              >
                <Edit className="h-4 w-4" />
                {isEditing ? "Save Profile" : "Edit Profile"}
              </button>
              <button
                onClick={handleLogoutClick}
                className="flex-1 py-2 px-4 bg-[#EA6C7B] text-white rounded-md hover:bg-[#EA6C7B]/90 transition-colors flex items-center justify-center gap-2"
              >
                <LogOut className="h-4 w-4" />
                Logout
              </button>
            </div>
          </div>
        </div>

        {/* Additional sections could go here */}
        <div className="mt-6 bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-bold text-[#042C3C] mb-4">Account Settings</h2>
          <div className="space-y-4">
            <div className="flex items-center justify-between p-3 border border-gray-100 rounded-md hover:bg-gray-50 cursor-pointer">
              <div>
                <h3 className="font-medium text-[#042C3C]">Password</h3>
                <p className="text-sm text-gray-500">Change your password</p>
              </div>
              <Edit className="h-4 w-4 text-gray-400" />
            </div>
            <div className="flex items-center justify-between p-3 border border-gray-100 rounded-md hover:bg-gray-50 cursor-pointer">
              <div>
                <h3 className="font-medium text-[#042C3C]">Notifications</h3>
                <p className="text-sm text-gray-500">Manage notification preferences</p>
              </div>
              <Edit className="h-4 w-4 text-gray-400" />
            </div>
            <div className="flex items-center justify-between p-3 border border-gray-100 rounded-md hover:bg-gray-50 cursor-pointer">
              <div>
                <h3 className="font-medium text-[#042C3C]">Privacy</h3>
                <p className="text-sm text-gray-500">Control your privacy settings</p>
              </div>
              <Edit className="h-4 w-4 text-gray-400" />
            </div>
          </div>
        </div>
      </div>

      {/* Logout Confirmation Modal */}
      <LogoutConfirmationModal isOpen={showLogoutModal} onClose={handleLogoutCancel} onConfirm={handleLogoutConfirm} />
    </div>
  )
}

export default UserProfile
