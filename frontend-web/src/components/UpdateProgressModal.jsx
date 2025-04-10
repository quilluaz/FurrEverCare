"use client"

import { useState, useEffect } from "react"
import { X, ChevronUp, ChevronDown } from "lucide-react"

function UpdateProgressModal({ isOpen, onClose, initialProgress = 0, onUpdateProgress, treatmentName }) {
  const [progress, setProgress] = useState(initialProgress)

  // Update local state when initialProgress prop changes
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
    if (progress < 100) {
      setProgress(progress + 5)
    }
  }

  const decrementProgress = () => {
    if (progress > 0) {
      setProgress(progress - 5)
    }
  }

  const handleSubmit = (e) => {
    e.preventDefault()

    if (onUpdateProgress) {
      onUpdateProgress(progress)
    }

    onClose()
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-2xl font-bold text-[#042C3C]">Update Treatment Progress</h2>
            <button onClick={onClose} className="text-gray-500 hover:text-[#8A973F] rounded-full p-1 hover:bg-gray-100">
              <X className="h-5 w-5" />
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {treatmentName && (
              <div className="bg-[#FFF7EC] p-3 rounded-md border-l-4 border-[#8A973F]">
                <p className="text-[#042C3C] font-medium">{treatmentName}</p>
              </div>
            )}

            <div>
              <label htmlFor="progress" className="block text-sm font-bold text-[#042C3C] mb-3">
                Progress (%)
              </label>
              <div className="flex items-center gap-4">
                <div className="relative flex-1">
                  <input
                    id="progress"
                    type="number"
                    min="0"
                    max="100"
                    value={progress}
                    onChange={handleChange}
                    className="w-full p-3 bg-[#FFF7EC] border-2 border-[#8A973F]/30 rounded-md focus:outline-none focus:ring-2 focus:ring-[#8A973F] pr-10 text-[#A9A9A9]"
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
                <div className="text-2xl font-bold text-[#042C3C] min-w-[60px]">{progress}%</div>
              </div>
            </div>

            <div className="w-full bg-gray-200 rounded-full h-2.5 mb-4">
              <div
                className="bg-[#8A973F] h-2.5 rounded-full transition-all duration-300"
                style={{ width: `${progress}%` }}
              ></div>
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
                Update Progress
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}

export default UpdateProgressModal
