"use client"

import { useState, useEffect } from "react"
import { User, Edit, LogOut } from "lucide-react"
import { useNavigate } from "react-router-dom"
import LogoutConfirmationModal from "../components/user/LogoutConfirmationModal"
import SaveModal from "../components/user/SaveModal"
import UserNavBar from "../components/UserNavBar"
import AuthService from "../config/AuthService"
// First, remove the import for SaveConfirmationModal since we won't need it anymore
// Remove this line:
// import SaveConfirmationModal from "../components/user/SaveConfirmationModal"

const UserProfile = () => {
  const [user, setUser] = useState({
    userId: "",
    name: "",
    email: "",
    phone: "",
    profileImage: null,
  })

  const [isEditing, setIsEditing] = useState(false)
  const [editedUser, setEditedUser] = useState({ ...user })
  const [showLogoutModal, setShowLogoutModal] = useState(false)
  const [showSaveModal, setShowSaveModal] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const navigate = useNavigate()
  // Then update the state management - replace showSaveConfirmModal with a mode for SaveModal
  // Remove this line:
  // const [showSaveConfirmModal, setShowSaveConfirmModal] = useState(false)

  // Load user data when component mounts
  useEffect(() => {
    const loadUserData = () => {
      const userData = AuthService.getUser()
      console.log("AuthService.getUser():", userData)
      if (!userData) {
        setError("Please log in to view your profile.")
        navigate("/login")
        return
      }
      setUser({
        userId: userData.userId || "",
        name: userData.name || "",
        email: userData.email || "",
        phone: userData.phone || "",
        profileImage: userData.profileImage || null,
      })
      setEditedUser({
        userId: userData.userId || "",
        name: userData.name || "",
        email: userData.email || "",
        phone: userData.phone || "",
        profileImage: userData.profileImage || null,
      })
    }

    loadUserData()
  }, [navigate])

  // Update the handleEditToggle function to use the SaveModal for confirmation
  const handleEditToggle = () => {
    if (isEditing) {
      // Show confirmation modal before saving
      setShowSaveModal(true)
    } else {
      // Enter edit mode
      setIsEditing(true)
    }
  }

  // Update the handleSaveConfirm function to handle the actual save operation
  const handleSaveConfirm = async () => {
    try {
      setIsLoading(true)
      setError("")

      const updatedUser = await AuthService.updateProfile(editedUser)
      setUser(updatedUser)
      setIsEditing(false)

      // Change SaveModal to success mode
      setShowSaveModal(true)
    } catch (err) {
      setError(err.response?.data?.message || "Failed to update profile")
      console.error("Update profile error:", err.response || err)
    } finally {
      setIsLoading(false)
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
      await AuthService.logout()
      setShowLogoutModal(false)
      navigate("/")
    } catch (error) {
      console.error("Logout failed:", error)
      navigate("/")
    }
  }

  const handleLogoutCancel = () => {
    setShowLogoutModal(false)
  }

  // Redirect if not authenticated
  useEffect(() => {
    if (!AuthService.isAuthenticated()) {
      setError("Please log in to view your profile.")
      navigate("/login")
    }
  }, [navigate])

  return (
    
    <div className="min-h-screen bg-[#FFF7EC]  font-['Baloo']">
      <UserNavBar />
      <div className="max-w-2xl mx-auto mt-10">
        {error && <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded-lg">{error}</div>}

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
                  alt={user.name}
                  className="w-full h-full rounded-full object-cover"
                />
              ) : (
                <User className="h-12 w-12 text-gray-400" />
              )}
            </div>

            {/* Name */}
            <h2 className="text-xl font-bold text-[#042C3C] mb-6">{user.name || "User"}</h2>

            {/* User Information */}
            <div className="text-gray-500 w-full bg-[#FFF7EC] rounded-lg border border-[#8A973F]/30 p-6 mb-6">
              {isEditing ? (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label htmlFor="name" className="block text-sm font-medium text-gray-600 mb-1">
                      Name
                    </label>
                    <input
                      id="name"
                      name="name"
                      type="text"
                      value={editedUser.name}
                      onChange={handleChange}
                      className="w-full p-2 border border-gray-300 rounded-md"
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
                      className="w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                  <div>
                    <label htmlFor="phone" className="block text-sm font-medium text-gray-600 mb-1">
                      Phone
                    </label>
                    <input
                      id="phone"
                      name="phone"
                      type="tel"
                      value={editedUser.phone}
                      onChange={handleChange}
                      className="w-full p-2 border border-gray-300 rounded-md"
                    />
                  </div>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-gray-500">Name</p>
                    <p className="font-medium text-[#042C3C]">{user.name || "N/A"}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Email</p>
                    <p className="font-medium text-[#042C3C]">{user.email || "N/A"}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-500">Phone</p>
                    <p className="font-medium text-[#042C3C]">{user.phone || "N/A"}</p>
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
      <LogoutConfirmationModal isOpen={showLogoutModal} onCancel={handleLogoutCancel} onConfirm={handleLogoutConfirm} />

      {/* Update the SaveModal component in the JSX
      Replace the SaveModal and SaveConfirmationModal components with this: */}
      <SaveModal
        isOpen={showSaveModal}
        onClose={() => setShowSaveModal(false)}
        onConfirm={handleSaveConfirm}
        userName={user.name}
        isConfirmation={isEditing} // If still in editing mode, show confirmation dialog
      />
    </div>
  )
}

export default UserProfile
