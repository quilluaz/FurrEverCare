import { useState, useEffect } from "react";
import { X } from "lucide-react";

const TASK_TYPES = [
  'MEDICATION', 'FEEDING', 'WALK', 'VET_VISIT', 'GROOMING', 'APPOINTMENT', 'OTHER'
];

const TASK_STATUSES = [
  'PENDING', 'COMPLETED', 'SKIPPED', 'OVERDUE'
];

export default function EditTaskModal({ isOpen, onClose, onSubmit, task, petId, userID }) {
  const [formData, setFormData] = useState({
    description: "",
    taskType: TASK_TYPES[0] || '',
    scheduledDateTime: "",
    scheduledEndDateTime: "",
    notes: "",
    status: TASK_STATUSES[0] || 'PENDING',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (isOpen && task) {
      let formattedDateTime = "";
      if (task.scheduledDateTime) {
        try {
          const dateObject = task.scheduledDateTime instanceof Date
            ? task.scheduledDateTime
            : new Date(task.scheduledDateTime);
          
          if (!isNaN(dateObject)) {
            formattedDateTime = dateObject.toISOString().slice(0, 16);
          } else {
            console.warn("Invalid scheduled date/time received:", task.scheduledDateTime);
          }
        } catch (err) {
          console.error("Error parsing scheduled date/time:", err);
        }
      }

      let formattedEndDateTime = "";
      if (task.scheduledEndDateTime) {
        try {
          const endDateObject = task.scheduledEndDateTime instanceof Date
            ? task.scheduledEndDateTime
            : new Date(task.scheduledEndDateTime);
          
          if (!isNaN(endDateObject)) {
            formattedEndDateTime = endDateObject.toISOString().slice(0, 16);
          } else {
            console.warn("Invalid scheduled end date/time received:", task.scheduledEndDateTime);
          }
        } catch (err) {
          console.error("Error parsing scheduled end date/time:", err);
        }
      }

      setFormData({
        description: task.description || "",
        taskType: task.taskType || TASK_TYPES[0] || '',
        scheduledDateTime: formattedDateTime,
        scheduledEndDateTime: formattedEndDateTime,
        notes: task.notes || "",
        status: task.status || TASK_STATUSES[0] || 'PENDING',
      });
      setError("");
    }
  }, [isOpen, task]);

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
      setError("Description, Task Type, and Scheduled Start Date/Time are required.");
      setIsLoading(false);
      return;
    }

    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      setError("Authentication required. Please log in again.");
      setIsLoading(false);
      onClose();
      return;
    }

    if (!userID || !petId) {
      setError("User or Pet ID missing.");
      setIsLoading(false);
      return;
    }

    const scheduledStartDate = new Date(formData.scheduledDateTime);
    if (isNaN(scheduledStartDate.getTime())) {
      setError("Invalid Start Date/Time format.");
      setIsLoading(false);
      return;
    }

    let scheduledEndDateISO = null;
    if (formData.scheduledEndDateTime) {
      const scheduledEndDate = new Date(formData.scheduledEndDateTime);
      if (isNaN(scheduledEndDate.getTime())) {
        setError("Invalid End Date/Time format.");
        setIsLoading(false);
        return;
      }
      if (scheduledEndDate < scheduledStartDate) {
        setError("End Date/Time cannot be before Start Date/Time.");
        setIsLoading(false);
        return;
      }
      scheduledEndDateISO = scheduledEndDate.toISOString();
    }

    const taskDataToSend = {
      description: formData.description,
      taskType: formData.taskType,
      scheduledDateTime: scheduledStartDate.toISOString(),
      scheduledEndDateTime: scheduledEndDateISO,
      notes: formData.notes || null,
      status: formData.status,
      userID,
      petID: petId,
    };

    console.log("EditTaskModal: Updating task:", taskDataToSend);

    try {
      await onSubmit(taskDataToSend);
      onClose();
    } catch (err) {
      console.error("EditTaskModal: Error during onSubmit callback:", err);
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
          className="absolute top-3 right-3 text-gray-500 hover:text-gray-700 transition-colors"
          disabled={isLoading}
        >
          <X size={20} />
        </button>
        <h2 className="text-2xl font-bold text-[#042C3C] mb-6 text-center">
          Edit Task
        </h2>
        {error && <div className="mb-4 p-3 bg-red-100 text-red-700 border border-red-300 rounded text-sm">{error}</div>}
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
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-sm text-gray-700"
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
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-[#EA6C7B] focus:border-[#EA6C7B] bg-white text-sm text-gray-700"
            >
              {TASK_TYPES.map(type => (
                <option key={type} value={type}>{type.replace('_', ' ')}</option>
              ))}
            </select>
          </div>
          <div>
            <label htmlFor="scheduledDateTime" className="block text-sm font-medium text-gray-700 mb-1">
              Scheduled Start Date & Time <span className="text-red-500">*</span>
            </label>
            <input
              type="datetime-local"
              id="scheduledDateTime"
              name="scheduledDateTime"
              value={formData.scheduledDateTime}
              onChange={handleChange}
              required
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-sm text-gray-700 [&::-webkit-calendar-picker-indicator]:opacity-100 [&::-webkit-calendar-picker-indicator]:brightness-0 [&::-webkit-calendar-picker-indicator]:opacity-60"
            />
          </div>
          <div>
            <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-1">
              Status
            </label>
            <select
              id="status"
              name="status"
              value={formData.status}
              onChange={handleChange}
              disabled={isLoading}
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-[#EA6C7B] focus:border-[#EA6C7B] bg-white text-sm text-gray-700"
            >
              {TASK_STATUSES.map(status => (
                <option key={status} value={status}>{status}</option>
              ))}
            </select>
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
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-1 focus:ring-[#EA6C7B] focus:border-[#EA6C7B] text-sm text-gray-700"
            ></textarea>
          </div>
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              disabled={isLoading}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-gray-500 transition-colors disabled:opacity-70"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isLoading}
              className="px-4 py-2 text-sm font-medium text-white bg-[#EA6C7B] rounded-md hover:bg-[#d45f6e] focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-[#EA6C7B] transition-colors disabled:opacity-70"
            >
              {isLoading ? "Updating..." : "Update Task"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}