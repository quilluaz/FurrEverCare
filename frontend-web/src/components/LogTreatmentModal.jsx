import { useState } from "react"
import { X, Calendar } from "lucide-react"

function LogTreatmentModal({ isOpen, onClose, onLogTreatment }) {
  const [treatment, setTreatment] = useState({
    name: "",
    type: "",
    pet: "",
    startDate: "",
    endDate: "",
    notes: "",
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setTreatment((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (onLogTreatment) {
      onLogTreatment(treatment)
    }

    // Reset form
    setTreatment({
      name: "",
      type: "",
      pet: "",
      startDate: "",
      endDate: "",
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
            <h2 className="text-2xl font-bold text-[#042C3C]">Log Treatment</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-[#8A973F] rounded-full p-1 hover:bg-gray-100">
              <X className="h-5 w-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label htmlFor="name" className="block text-sm font-bold text-[#042C3C] mb-2">
                Treatment Name
              </label>
              <input
                id="name"
                name="name"
                value={treatment.name}
                onChange={handleChange}
                className="w-full p-3 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F]"
                required
              />
            </div>

            <div>
              <label htmlFor="type" className="block text-sm font-bold text-[#042C3C] mb-2">
                Treatment Type
              </label>
              <select
                id="type"
                name="type"
                value={treatment.type}
                onChange={handleChange}
                className="text-gray-500 w-full p-3 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F] appearance-none"
                required
              >
                <option value="" disabled>
                  Select Treatment Type
                </option>
                <option value="medication">Medication</option>
                <option value="therapy">Therapy</option>
                <option value="surgery">Surgery</option>
                <option value="vaccination">Vaccination</option>
                <option value="diet">Diet Plan</option>
                <option value="other">Other</option>
              </select>
            </div>

            <div>
              <label htmlFor="pet" className="block text-sm font-bold text-[#042C3C] mb-2">
                Pet
              </label>
              <select
                id="pet"
                name="pet"
                value={treatment.pet}
                onChange={handleChange}
                className="text-gray-500 w-full p-3 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F] appearance-none"
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

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="startDate" className="block text-sm font-bold text-[#042C3C] mb-2">
                  Start Date
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Calendar className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="startDate"
                    name="startDate"
                    type="date"
                    value={treatment.startDate}
                    onChange={handleChange}
                    className="text-gray-500 w-full p-3 pl-10 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F]"
                    required
                  />
                </div>
              </div>

              <div>
                <label htmlFor="endDate" className="block text-sm font-bold text-[#042C3C] mb-2">
                  End Date
                </label>
                <div className="relative">
                  <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                    <Calendar className="h-5 w-5 text-gray-400" />
                  </div>
                  <input
                    id="endDate"
                    name="endDate"
                    type="date"
                    value={treatment.endDate}
                    onChange={handleChange}
                    className="text-gray-500 w-full p-3 pl-10 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F]"
                  />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="notes" className="block text-sm font-bold text-[#042C3C] mb-2">
                Notes
              </label>
              <textarea
                id="notes"
                name="notes"
                value={treatment.notes}
                onChange={handleChange}
                placeholder="Treatment details"
                className="text-gray-500 w-full p-3 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F]"
                rows="4"
              />
            </div>

            <div className="flex justify-end space-x-3 pt-4">
              <button
                type="button"
                onClick={onClose}
                className="px-6 py-2 border-2 border-[#8A973F] text-[#8A973F] rounded-full hover:bg-[#8A973F]/10 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-[#8A973F] text-white rounded-full hover:bg-[#8A973F]/90 transition-colors"
              >
                Log Treatment
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
export default LogTreatmentModal;
