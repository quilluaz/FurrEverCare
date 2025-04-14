"use client"

import { useState } from "react"
import { X, Clock } from "lucide-react"

function AddMedicationModal({ isOpen, onClose, onAddMedication }) {
  const [medication, setMedication] = useState({
    name: "",
    dosage: "",
    time: "",
    pet: "",
    frequency: "daily",
    reminders: {
      email: false,
      sms: false,
      inApp: true,
    },
    notes: "",
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setMedication((prev) => ({ ...prev, [name]: value }))
  }

  const handleReminderToggle = (type) => {
    setMedication((prev) => ({
      ...prev,
      reminders: {
        ...prev.reminders,
        [type]: !prev.reminders[type],
      },
    }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (onAddMedication) {
      onAddMedication(medication)
    }

    // Reset form
    setMedication({
      name: "",
      dosage: "",
      time: "",
      pet: "",
      frequency: "daily",
      reminders: {
        email: false,
        sms: false,
        inApp: true,
      },
      notes: "",
    })

    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-[#042C3C]">Add Medication</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-[#EA6C7B] rounded-full p-1 hover:bg-gray-100">
              <X className="h-5 w-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-bold text-[#042C3C] mb-2">
                Medication Name
              </label>
              <input
                id="name"
                name="name"
                value={medication.name}
                onChange={handleChange}
                className="w-full p-3 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-[#A9A9A9]"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="dosage" className="block text-sm font-bold text-[#042C3C] mb-2">
                  Dosage
                </label>
                <input
                  id="dosage"
                  name="dosage"
                  value={medication.dosage}
                  onChange={handleChange}
                  placeholder="e.g., 1 tablet"
                  className="w-full p-3 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-[#A9A9A9]"
                  required
                />
              </div>

              <div>
                <label htmlFor="time" className="block text-sm font-bold text-[#042C3C] mb-2">
                  Time
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Clock className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="time"
                    name="time"
                    type="time"
                    value={medication.time}
                    onChange={handleChange}
                    className="w-full p-3 pl-10 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-[#A9A9A9]"
                    required
                  />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="pet" className="block text-sm font-bold text-[#042C3C] mb-2">
                Pet
              </label>
              <select
                id="pet"
                name="pet"
                value={medication.pet}
                onChange={handleChange}
                className="w-full p-3 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] appearance-none text-[#A9A9A9]"
                required
              >
                <option value="" disabled>
                  Select Pet
                </option>
                <option value="buddy">Buddy (Dog)</option>
                <option value="whiskers">Whiskers (Cat)</option>
                <option value="thumper">Thumper (Rabbit)</option>
              </select>
            </div>

            <div>
              <label htmlFor="frequency" className="block text-sm font-bold text-[#042C3C] mb-2">
                Frequency
              </label>
              <div className="grid grid-cols-3 gap-2">
                <label
                  className={`flex items-center justify-center p-2 rounded-md cursor-pointer ${
                    medication.frequency === "daily"
                      ? "bg-[#EA6C7B] text-white"
                      : "bg-[#FFF7EC] border-2 border-[#EA6C7B] text-[#042C3C]"
                  }`}
                >
                  <input
                    type="radio"
                    name="frequency"
                    value="daily"
                    checked={medication.frequency === "daily"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <span>Daily</span>
                </label>
                <label
                  className={`flex items-center justify-center p-2 rounded-md cursor-pointer ${
                    medication.frequency === "weekly"
                      ? "bg-[#EA6C7B] text-white"
                      : "bg-[#FFF7EC] border-2 border-[#EA6C7B] text-[#042C3C]"
                  }`}
                >
                  <input
                    type="radio"
                    name="frequency"
                    value="weekly"
                    checked={medication.frequency === "weekly"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <span>Weekly</span>
                </label>
                <label
                  className={`flex items-center justify-center p-2 rounded-md cursor-pointer ${
                    medication.frequency === "monthly"
                      ? "bg-[#EA6C7B] text-white"
                      : "bg-[#FFF7EC] border-2 border-[#EA6C7B] text-[#042C3C]"
                  }`}
                >
                  <input
                    type="radio"
                    name="frequency"
                    value="monthly"
                    checked={medication.frequency === "monthly"}
                    onChange={handleChange}
                    className="sr-only"
                  />
                  <span>Monthly</span>
                </label>
              </div>
            </div>

            <div>
              <label className="block text-sm font-bold text-[#042C3C] mb-2">Reminders</label>
              <div className="flex space-x-6">
                <label className="flex items-center space-x-2 cursor-pointer text-gray-500">
                  <div
                    className={`relative w-12 h-6 transition-colors duration-200 ease-linear rounded-full ${
                      medication.reminders.email ? "bg-[#EA6C7B]" : "bg-gray-300"
                    }`}
                    onClick={() => handleReminderToggle("email")}
                  >
                    <div
                      className={`absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-linear ${
                        medication.reminders.email ? "transform translate-x-6" : ""
                      }`}
                    ></div>
                  </div>
                  <span>Email</span>
                </label>

                <label className="flex items-center space-x-2 cursor-pointer text-gray-500">
                  <div
                    className={`relative w-12 h-6 transition-colors duration-200 ease-linear rounded-full ${
                      medication.reminders.sms ? "bg-[#EA6C7B]" : "bg-gray-300"
                    }`}
                    onClick={() => handleReminderToggle("sms")}
                  >
                    <div
                      className={`text-gray-500 absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-linear ${
                        medication.reminders.sms ? "transform translate-x-6" : ""
                      }`}
                    ></div>
                  </div>
                  <span>SMS</span>
                </label>

                <label className="flex items-center space-x-2 cursor-pointer text-gray-500">
                  <div
                    className={`relative w-12 h-6 transition-colors duration-200 ease-linear rounded-full ${
                      medication.reminders.inApp ? "bg-[#EA6C7B]" : "bg-gray-300"
                    }`}
                    onClick={() => handleReminderToggle("inApp")}
                  >
                    <div
                      className={`absolute left-1 top-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-linear ${
                        medication.reminders.inApp ? "transform translate-x-6" : ""
                      }`}
                    ></div>
                  </div>
                  <span>In-App</span>
                </label>
              </div>
            </div>

            <div>
              <label htmlFor="notes" className="block text-sm font-bold text-[#042C3C] mb-2">
                Notes
              </label>
              <textarea
                id="notes"
                name="notes"
                value={medication.notes}
                onChange={handleChange}
                placeholder="Additional instructions"
                className="w-full p-3 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B]"
                rows="3"
              />
            </div>

            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-6 py-2 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-full hover:bg-[#EA6C7B]/10 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#EA6C7B] text-white rounded-full hover:bg-[#EA6C7B]/90 transition-colors"
              >
                Add Medication
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default AddMedicationModal
