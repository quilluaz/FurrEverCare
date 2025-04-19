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
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 text-sm">
        <div className="p-4">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-bold text-[#042C3C]">Add Medication</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-[#EA6C7B] rounded-full p-1 hover:bg-gray-100">
              <X className="h-4 w-4" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-3">
            <div>
              <label htmlFor="name" className="block text-xs font-bold text-[#042C3C] mb-1">
                Medication Name
              </label>
              <input
                id="name"
                name="name"
                value={medication.name}
                onChange={handleChange}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-sm text-[#A9A9A9]"
                required
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="dosage" className="block text-xs font-bold text-[#042C3C] mb-1">
                  Dosage
                </label>
                <input
                  id="dosage"
                  name="dosage"
                  value={medication.dosage}
                  onChange={handleChange}
                  placeholder="e.g., 1 tablet"
                  className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-sm text-[#A9A9A9]"
                  required
                />
              </div>

              <div>
                <label htmlFor="time" className="block text-xs font-bold text-[#042C3C] mb-1">
                  Time
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Clock className="h-4 w-4 text-gray-400" />
                  </div>
                  <input
                    id="time"
                    name="time"
                    type="time"
                    value={medication.time}
                    onChange={handleChange}
                    className="w-full p-2 pl-10 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-sm text-[#A9A9A9]"
                    required
                  />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="pet" className="block text-xs font-bold text-[#042C3C] mb-1">
                Pet
              </label>
              <select
                id="pet"
                name="pet"
                value={medication.pet}
                onChange={handleChange}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] appearance-none text-sm text-[#A9A9A9]"
                required
              >
                <option value="" disabled>Select Pet</option>
                <option value="buddy">Buddy (Dog)</option>
                <option value="whiskers">Whiskers (Cat)</option>
                <option value="thumper">Thumper (Rabbit)</option>
              </select>
            </div>

            <div>
              <label htmlFor="frequency" className="block text-xs font-bold text-[#042C3C] mb-1">
                Frequency
              </label>
              <div className="grid grid-cols-3 gap-2 text-sm">
                {["daily", "weekly", "monthly"].map((freq) => (
                  <label
                    key={freq}
                    className={`flex items-center justify-center p-2 rounded-md cursor-pointer ${
                      medication.frequency === freq
                        ? "bg-[#EA6C7B] text-white"
                        : "bg-[#FFF7EC] border-2 border-[#EA6C7B] text-[#042C3C]"
                    }`}
                  >
                    <input
                      type="radio"
                      name="frequency"
                      value={freq}
                      checked={medication.frequency === freq}
                      onChange={handleChange}
                      className="sr-only"
                    />
                    <span className="capitalize">{freq}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-[#042C3C] mb-1">Reminders</label>
              <div className="flex space-x-4 text-xs text-gray-500">
                {["email", "sms", "inApp"].map((type) => (
                  <label key={type} className="flex items-center space-x-2 cursor-pointer">
                    <div
                      className={`relative w-10 h-5 transition-colors duration-200 ease-linear rounded-full ${
                        medication.reminders[type] ? "bg-[#EA6C7B]" : "bg-gray-300"
                      }`}
                      onClick={() => handleReminderToggle(type)}
                    >
                      <div
                        className={`absolute left-1 top-1 bg-white w-3.5 h-3.5 rounded-full transition-transform duration-200 ease-linear ${
                          medication.reminders[type] ? "transform translate-x-5" : ""
                        }`}
                      />
                    </div>
                    <span className="capitalize">{type === "inApp" ? "In-App" : type}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label htmlFor="notes" className="block text-xs font-bold text-[#042C3C] mb-1">
                Notes
              </label>
              <textarea
                id="notes"
                name="notes"
                value={medication.notes}
                onChange={handleChange}
                placeholder="Additional instructions"
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-sm"
                rows="2"
              />
            </div>

            <div className="flex justify-end space-x-2 pt-3">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-1.5 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-full hover:bg-[#EA6C7B]/10 transition-colors text-sm"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-1.5 bg-[#EA6C7B] text-white rounded-full hover:bg-[#EA6C7B]/90 transition-colors text-sm"
              >
                Add
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default AddMedicationModal
