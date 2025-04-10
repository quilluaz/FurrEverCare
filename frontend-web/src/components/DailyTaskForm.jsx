"use client"

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

    onAddTask({
      name: task.name,
      pet: task.pet,
      date: task.date,
      time: task.time,
      notes: task.notes,
      reminder: task.reminder,
    })

    // Reset form
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
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4 text-gray-500 ">
        <div className="flex justify-between items-center p-4 border-b">
          <h2 className="text-xl font-bold flex items-center text-[#042C3C]">
            <Calendar className="h-5 w-5 mr-2 text-[#F0B542]" />
            Add Daily Task
          </h2>
          <button onClick={onClose} className="text-gray-500 hover:text-[#F0B542] rounded-full p-1 hover:bg-gray-100">
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4 space-y-4">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-[#042C3C] mb-1">
              Task Name
            </label>
            <input
              id="name"
              name="name"
              value={task.name}
              onChange={handleChange}
              className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
              required
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
              className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
              required
            >
              <option value="" disabled>
                Select pet
              </option>
              <option value="Buddy">Buddy (Dog)</option>
              <option value="Whiskers">Whiskers (Cat)</option>
              <option value="Thumper">Thumper (Rabbit)</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
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
                className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
                required
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
                className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
                required
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
              className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
            >
              <option value="0">No reminder</option>
              <option value="15">15 minutes before</option>
              <option value="30">30 minutes before</option>
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
              className="text-gray-500 w-full p-2 bg-[#FFF7EC] border-2 border-[#F0B542] rounded-md focus:outline-none focus:ring-2 focus:ring-[#F0B542]"
              rows="3"
            />
          </div>

          <div className="flex justify-end space-x-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border-2 border-[#F0B542] text-[#F0B542] rounded-md hover:bg-[#F0B542]/10 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-[#F0B542] text-white rounded-md hover:bg-[#F0B542]/90 transition-colors"
            >
              Add Task
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}
