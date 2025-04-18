"use client"

import { useState, useEffect } from "react"
import { X, ChevronUp, ChevronDown } from "lucide-react"

function UpdateProgressModal({ isOpen, onClose, initialProgress = 0, onUpdateProgress, treatmentName }) {
  const [progress, setProgress] = useState(initialProgress)

  useEffect(() => {
    if (isOpen) {
      setProgress(initialProgress)
    }
  }, [isOpen, initialProgress])

  const handleChange = (e) => {
    const value = Number.parseInt(e.target.value)
    if (!isNaN(value) && value >= 0 && value <= 100) {
      setProgress(value)
    }
  }

  const incrementProgress = () => {
    if (progress < 100) setProgress(progress + 5)
  }

  const decrementProgress = () => {
    if (progress > 0) setProgress(progress - 5)
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    if (onUpdateProgress) onUpdateProgress(progress)
    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-sm mx-4">
        <div className="p-4">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-bold text-[#042C3C]">Update Treatment Progress</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-[#8A973F] rounded-full p-1 hover:bg-gray-100">
              <X className="h-5 w-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {treatmentName && (
              <div className="bg-[#FFF7EC] p-3 rounded-md border-l-4 border-[#8A973F]">
                <p className="text-[#042C3C] font-medium text-sm">{treatmentName}</p>
              </div>
            )}

            <div>
              <label htmlFor="progress" className="block text-sm font-bold text-[#042C3C] mb-2">
                Progress (%)
              </label>
              <div className="flex items-center gap-3">
                <div className="relative flex-1">
                  <input
                    id="progress"
                    type="number"
                    min="0"
                    max="100"
                    value={progress}
                    onChange={handleChange}
                    className="w-full p-2 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F] pr-10 text-sm text-[#A9A9A9]"
                  />
                  <div className="absolute inset-y-0 right-0 flex flex-col border-l border-[#8A973F]/30">
                    <button
                      type="button"
                      onClick={incrementProgress}
                      className="flex-1 px-2 hover:bg-[#8A973F]/10"
                      tabIndex="-1"
                    >
                      <ChevronUp className="h-4 w-4 text-[#8A973F]" />
                    </button>
                    <button
                      type="button"
                      onClick={decrementProgress}
                      className="flex-1 px-2 hover:bg-[#8A973F]/10"
                      tabIndex="-1"
                    >
                      <ChevronDown className="h-4 w-4 text-[#8A973F]" />
                    </button>
                  </div>
                </div>
                <div className="text-lg font-bold text-[#042C3C] min-w-[50px]">{progress}%</div>
              </div>
            </div>

            <div className="w-full bg-gray-200 rounded-full h-2">
              <div
                className="bg-[#8A973F] h-2 rounded-full transition-all duration-300"
                style={{ width: `${progress}%` }}
              ></div>
            </div>

            <div className="flex justify-end space-x-2 pt-2">
              <button
                type="button"
                onClick={onClose}
                className="px-4 py-1.5 border-2 border-[#8A973F] text-[#8A973F] rounded-md hover:bg-[#8A973F]/10 transition-colors text-sm"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-4 py-1.5 bg-[#8A973F] text-white rounded-md hover:bg-[#8A973F]/90 transition-colors text-sm"
              >
                Update
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default UpdateProgressModal
