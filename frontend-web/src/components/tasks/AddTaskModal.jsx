import { useState, useEffect } from "react";
import { X } from "lucide-react";

const TASK_TYPES = [
  'MEDICATION', 'FEEDING', 'WALK', 'VET_VISIT', 'GROOMING', 'APPOINTMENT', 'OTHER'
];

export default function AddTaskModal({ isOpen, onClose, onSubmit, petId, userID }) {
  const [formData, setFormData] = useState({
    description: "",
    taskType: TASK_TYPES[0] || '',
    scheduledDateTime: "",
    notes: "",
    status: "PENDING",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (isOpen) {
      setFormData({
        description: "",
        taskType: TASK_TYPES[0] || '',
        scheduledDateTime: "",
        notes: "",
        status: "PENDING",
      });
      setError("");
    }
  }, [isOpen]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    if (!formData.description || !formData.taskType || !formData.scheduledDateTime) {
      setError("Description, Task Type, and Scheduled Date/Time are required.");
      setIsLoading(false);
      return;
    }

    if (!userID || !petId) {
      setError("User or Pet ID missing.");
      setIsLoading(false);
      return;
    }

    const scheduledDate = new Date(formData.scheduledDateTime);
    if (isNaN(scheduledDate.getTime())) {
      setError("Invalid Date/Time format.");
      setIsLoading(false);
      return;
    }

    const taskDataToSend = {
      description: formData.description,
      taskType: formData.taskType,
      scheduledDateTime: scheduledDate.toISOString(),
      notes: formData.notes || null,
      status: formData.status,
      petID: petId,
      userID,
    };

    console.log("AddTaskModal: Submitting task:", taskDataToSend);

    try {
      await onSubmit(taskDataToSend);
      onClose();
    } catch (err) {
      console.error("AddTaskModal: Error during onSubmit callback:", err);
      setError(err?.message || err.response?.data?.message || "An error occurred during submission.");
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex justify-center items-center z-50 p-4 font-['Baloo']">
      <div className="bg-[#FEFDF9] p-6 rounded-lg shadow-xl w-full max-w-md relative">
        <button
          onClick={onClose}
          className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
          disabled={isLoading}
        >
          <X size={24} />
        </button>
        <h2 className="text-2xl font-bold text-[#042C3C] mb-4">
          Add New Task
        </h2>
        {error && <div className="mb-4 p-2 bg-red-100 text-red-700 border border-red-300 rounded text-sm">{error}</div>}
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-1">
              Description <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              id="description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              required
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-gray-600"
            />
          </div>
          <div>
            <label htmlFor="taskType" className="block text-sm font-medium text-gray-700 mb-1">
              Task Type <span className="text-red-500">*</span>
            </label>
            <select
              id="taskType"
              name="taskType"
              value={formData.taskType}
              onChange={handleChange}
              required
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] bg-white text-gray-600"
            >
              {TASK_TYPES.map(type => (
                <option key={type} value={type}>{type.replace('_', ' ')}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="scheduledDateTime" className="block text-sm font-medium text-gray-700 mb-1">
              Scheduled Date & Time <span className="text-red-500">*</span>
            </label>
            <input
              type="datetime-local"
              id="scheduledDateTime"
              name="scheduledDateTime"
              value={formData.scheduledDateTime}
              onChange={handleChange}
              required
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-gray-600"
              style={{ color: '#4B5563' }}
            />
          </div>
          <div>
            <label htmlFor="notes" className="block text-sm font-medium text-gray-700 mb-1">
              Notes
            </label>
            <textarea
              id="notes"
              name="notes"
              value={formData.notes}
              onChange={handleChange}
              rows="3"
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-gray-600"
            ></textarea>
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={isLoading}
              className="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors disabled:opacity-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-4 py-2 bg-[#EA6C7B] text-white rounded-md hover:bg-[#d45f6e] transition-colors disabled:opacity-50"
            >
              {isLoading ? "Adding..." : "Add Task"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}