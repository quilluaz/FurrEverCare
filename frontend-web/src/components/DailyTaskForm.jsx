import { useState } from "react"
import { X, Calendar } from "lucide-react"

export default function DailyTaskForm({ isOpen, onClose, onAddTask }) {
  const [task, setTask] = useState({
    name: "",
    pet: "",
    date: new Date().toISOString().split("T")[0],
    time: "",
    notes: "",
    reminder: "15",
  })

  const handleChange = (e) => {
    const { name, value } = e.target
    setTask((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    onAddTask(task)
    setTask({
      name: "",
      pet: "",
      date: new Date().toISOString().split("T")[0],
      time: "",
      notes: "",
      reminder: "15",
    })
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-sm mx-4 text-gray-500">
        {/* Header */}
        <div className="flex justify-between items-center p-3 border-b">
          <h2 className="text-lg font-bold flex items-center text-[#042C3C]">
            <Calendar className="h-5 w-5 mr-2 text-[#F0B542]" />
            Add Daily Task
          </h2>
          <button onClick={onClose} className="text-gray-500 hover:text-[#F0B542] rounded-full p-1 hover:bg-gray-100">
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-3 space-y-3">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-[#042C3C] mb-1">
              Task Name
            </label>
            <input
              id="name"
              name="name"
              value={task.name}
              onChange={handleChange}
              required
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
            />
          </div>

          <div>
            <label htmlFor="pet" className="block text-sm font-medium text-[#042C3C] mb-1">
              Pet
            </label>
            <select
              id="pet"
              name="pet"
              value={task.pet}
              onChange={handleChange}
              required
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
            >
              <option value="" disabled>Select pet</option>
              <option value="Buddy">Buddy (Dog)</option>
              <option value="Whiskers">Whiskers (Cat)</option>
              <option value="Thumper">Thumper (Rabbit)</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="date" className="block text-sm font-medium text-[#042C3C] mb-1">
                Date
              </label>
              <input
                id="date"
                name="date"
                type="date"
                value={task.date}
                onChange={handleChange}
                required
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
              />
            </div>
            <div>
              <label htmlFor="time" className="block text-sm font-medium text-[#042C3C] mb-1">
                Time
              </label>
              <input
                id="time"
                name="time"
                type="time"
                value={task.time}
                onChange={handleChange}
                required
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
              />
            </div>
          </div>

          <div>
            <label htmlFor="reminder" className="block text-sm font-medium text-[#042C3C] mb-1">
              Reminder
            </label>
            <select
              id="reminder"
              name="reminder"
              value={task.reminder}
              onChange={handleChange}
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
            >
              <option value="0">No reminder</option>
              <option value="15">15 mins before</option>
              <option value="30">30 mins before</option>
              <option value="60">1 hour before</option>
              <option value="1440">1 day before</option>
            </select>
          </div>

          <div>
            <label htmlFor="notes" className="block text-sm font-medium text-[#042C3C] mb-1">
              Notes
            </label>
            <textarea
              id="notes"
              name="notes"
              value={task.notes}
              onChange={handleChange}
              placeholder="Add any special instructions or notes..."
              rows="2"
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:ring-[#F0B542] focus:outline-none"
            />
          </div>

          <div className="flex justify-end space-x-2 pt-1">
            <button
              type="button"
              onClick={onClose}
              className="px-3 py-1.5 border-2 border-[#F0B542] text-[#F0B542] rounded-md hover:bg-[#F0B542]/10 transition"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-3 py-1.5 bg-[#F0B542] text-white rounded-md hover:bg-[#F0B542]/90 transition"
            >
              Add Task
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
