import { useState, useEffect } from "react";
import { X } from "lucide-react";

export default function AddTreatmentPlanModal({
  isOpen,
  onClose,
  onPlanAdded,
  plan,
  isEditMode = false,
  userID,
  petID,
}) {
  const [formData, setFormData] = useState({
    name: "",
    description: "",
    startDate: "",
    goal: "",
    status: "ACTIVE",
    progressPercentage: 0,
    notes: "",
    planID: "",
  });

  useEffect(() => {
    if (isEditMode && plan) {
      setFormData({
        name: plan.name || "",
        description: plan.description || "",
        startDate: plan.startDate
          ? new Date(plan.startDate.toDate()).toISOString().split("T")[0]
          : "",
        goal: plan.goal || "",
        status: plan.status || "ACTIVE",
        progressPercentage: plan.progressPercentage || 0,
        notes: plan.notes || "",
        planID: plan.planID || "",
      });
    }
  }, [isEditMode, plan]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === "progressPercentage" ? parseInt(value) || 0 : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Check for access token using the correct key 'token'
    const accessToken = localStorage.getItem('token'); // Corrected key
    if (!accessToken) {
      alert("Authentication required. Please log in again.");
      // Optionally redirect to login page
      // window.location.href = '/login';
      return;
    }

    if (!userID || !petID) {
      alert("User or pet information is missing.");
      return;
    }

    const planData = {
      ...formData,
      userID,
      petID,
      startDate: formData.startDate ? new Date(formData.startDate) : null,
    };

    // Pass both planData and accessToken to the callback
    onPlanAdded(planData, accessToken); // Pass the data object and token
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      {/* Reverted max-width back to max-w-md, reduced padding from p-6 */}
      <div className="bg-white rounded-lg w-full max-w-md mx-4 p-3 relative"> 
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-[#EA6C7B]"
        >
          <X className="h-5 w-5" />
        </button>
        {/* Reduced margin-bottom from mb-4 */}
        <h2 className="text-2xl font-bold text-[#042C3C] mb-2">
          {isEditMode ? "Edit Treatment Plan" : "Add Treatment Plan"}
        </h2>
        <form onSubmit={handleSubmit}>
          {/* Reduced vertical spacing from space-y-4 */}
          <div className="space-y-2">
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Name</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
                required
              />
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">
                Description
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
                rows="2" // Reduced rows from 3
              />
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">
                Start Date
              </label>
              <input
                type="date"
                name="startDate"
                value={formData.startDate}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
                required
              />
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Goal</label>
              <input
                type="text"
                name="goal"
                value={formData.goal}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
              />
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Status</label>
              <select
                name="status"
                value={formData.status}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
              >
                <option value="ACTIVE">Active</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">
                Progress (%)
              </label>
              <input
                type="number"
                name="progressPercentage"
                value={formData.progressPercentage}
                onChange={handleChange}
                min="0"
                max="100"
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
              />
            </div>
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Notes</label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" // Reduced py-2 to py-1
                rows="2" // Reduced rows from 3
              />
            </div>
          </div>
          {/* Reduced margin-top from mt-6 */}
          <div className="mt-4 flex justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm text-gray-500 hover:text-[#EA6C7B]"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-[#EA6C7B] text-white rounded-md text-sm hover:bg-[#EA6C7B]/90"
            >
              {isEditMode ? "Update Plan" : "Add Plan"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}