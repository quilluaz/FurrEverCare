import { useState, useEffect } from "react";
import { X } from "lucide-react";

export default function AddTreatmentPlanModal({
  isOpen,
  onClose,
  onSubmitPlan, // Correct prop name
  plan,
  isEditMode = false,
  userID,
  petID,
}) {
  const [formData, setFormData] = useState({ // Initial state
    name: "", description: "", startDate: "", endDate: "",
    goal: "",
    status: "ACTIVE", notes: "", // Removed progressPercentage
  });
  const [isLoading, setIsLoading] = useState(false); // Optional loading state
  const [error, setError] = useState(null); // Optional error state

  // Effect to populate form when in edit mode and plan data is available
  useEffect(() => {
    if (isEditMode && plan) {
      console.log("Populating edit form with plan:", plan);
      let formattedStartDate = "";
      if (plan.startDate) {
        try {
          const dateObject = plan.startDate.seconds
            ? new Date(plan.startDate.seconds * 1000)
            : new Date(plan.startDate);
          if (!isNaN(dateObject)) {
            formattedStartDate = dateObject.toISOString().split("T")[0];
          } else {
            console.warn("Invalid start date received:", plan.startDate);
          }
        } catch (error) {
          console.error("Error parsing start date:", error);
        }
      }

      let formattedEndDate = ""; // For endDate
      if (plan.endDate) {
        try {
          const dateObject = plan.endDate.seconds
            ? new Date(plan.endDate.seconds * 1000)
            : new Date(plan.endDate);
          if (!isNaN(dateObject)) {
            formattedEndDate = dateObject.toISOString().split("T")[0];
          } else {
            console.warn("Invalid end date received:", plan.endDate);
          }
        } catch (error) {
          console.error("Error parsing end date:", error);
        }
      }

      setFormData({
        name: plan.name || "",
        description: plan.description || "",
        startDate: formattedStartDate,
        endDate: formattedEndDate,
        goal: plan.goal || "",
        status: plan.status || "ACTIVE",
        notes: plan.notes || "",
      });
      setError(null); // Reset error on open/plan change
    } else if (!isEditMode) {
      // Reset form if switching to add mode or closing
      setFormData({
        name: "", description: "", startDate: "", endDate: "",
        goal: "",
        status: "ACTIVE", notes: "",
      });
      setError(null);
    }
  }, [isOpen, isEditMode, plan]); // Rerun effect if modal opens, mode changes, or plan data changes

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    setFormData((prev) => {
      let newValue = value;
      // Allow empty string for progressPercentage, otherwise parse or default to 0
      if (name === 'progressPercentage') {
        newValue = value === '' ? '' : parseInt(value) || 0;
      } else if (type === 'number') {
        newValue = parseInt(value) || 0;
      }
      return {
        ...prev,
        [name]: newValue,
      };
    });
  };

  // Handle form submission
  const handleSubmit = (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    const accessToken = localStorage.getItem("token");
    if (!accessToken) {
      setError("Authentication required. Please log in again.");
      setIsLoading(false);
      return;
    }

    if (!userID || !petID) {
        setError("User or Pet ID missing in modal props.");
        setIsLoading(false);
        return;
    }

    const startDateObj = formData.startDate ? new Date(formData.startDate) : null;
    const endDateObj = formData.endDate ? new Date(formData.endDate) : null;

    if (startDateObj && endDateObj && endDateObj < startDateObj) {
        setError("End date cannot be before the start date.");
        setIsLoading(false);
        return;
    }

    // Prepare data for submission - ensure this matches backend expectations for PUT
    const planDataToSend = {
      name: formData.name,
      description: formData.description,
      startDate: startDateObj ? startDateObj.toISOString() : null,
      endDate: endDateObj ? endDateObj.toISOString() : null,
      goal: formData.goal,
      status: formData.status,
      notes: formData.notes,
    };

    console.log("AddTreatmentPlanModal: Submitting data:", planDataToSend); // Log the data being sent

    // Call the parent component's submit handler (handleEditPlan in this case)
    // Wrap in Promise.resolve to handle potential non-async parent handlers gracefully
    Promise.resolve(onSubmitPlan(planDataToSend, accessToken))
      .catch((err) => {
        console.error("AddTreatmentPlanModal: Error during onSubmitPlan callback:", err);
        // Display error message received from the parent handler or a generic one
        setError(err?.message || "An error occurred during submission.");
      })
      .finally(() => {
        setIsLoading(false); // Stop loading regardless of outcome
      });
  };

  // Helper function for rendering checklist preview
  function renderActionPlanPreview(notes) {
    if (!notes) return <div className="text-gray-400 italic">No action plan.</div>;
    return notes.split('\n').map((line, idx) => {
      const match = line.match(/^\-\s\[( |x)\]\s(.+)$/);
      if (match) {
        const checked = match[1] === 'x';
        const text = match[2];
        return (
          <div key={idx} className="flex items-center gap-2 mb-1">
            <input type="checkbox" checked={checked} readOnly className="accent-[#EA6C7B] w-4 h-4" />
            <span style={{ textDecoration: checked ? 'line-through' : 'none', color: checked ? '#9CA3AF' : '#042C3C' }}>{text}</span>
          </div>
        );
      }
      return <div key={idx} className="text-xs text-gray-500">{line}</div>;
    });
  }

  if (!isOpen) return null;

  // --- UI Section ---
  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg w-full max-w-md mx-4 p-3 relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-[#EA6C7B]"
          disabled={isLoading}>
          <X className="h-5 w-5" />
        </button>
        <h2 className="text-2xl font-bold text-[#042C3C] mb-2">
          {isEditMode ? "Edit Treatment Plan" : "Add New Treatment Plan"}
        </h2>

        <form onSubmit={handleSubmit}>
          <div className="space-y-2 max-h-[60vh] overflow-y-auto pr-2">
            {/* Form fields (Name, Description, StartDate, EndDate, Goal, Status, Progress, Notes) */}
            {/* Name */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Name</label>
              <input type="text" name="name" value={formData.name} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" required disabled={isLoading} />
            </div>
            {/* Description */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Description</label>
              <textarea name="description" value={formData.description} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" rows="2" disabled={isLoading} />
            </div>
            {/* Start Date */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Start Date</label>
              <input type="date" name="startDate" value={formData.startDate} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 [&::-webkit-calendar-picker-indicator]:opacity-100 [&::-webkit-calendar-picker-indicator]:brightness-0 [&::-webkit-calendar-picker-indicator]:opacity-60" required disabled={isLoading} />
            </div>
            {/* End Date */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">End Date</label>
              <input type="date" name="endDate" value={formData.endDate} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 [&::-webkit-calendar-picker-indicator]:opacity-100 [&::-webkit-calendar-picker-indicator]:brightness-0 [&::-webkit-calendar-picker-indicator]:opacity-60" disabled={isLoading} />
            </div>
            {/* Goal */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Goal</label>
              <input type="text" name="goal" value={formData.goal} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" disabled={isLoading} />
            </div>
            {/* Status */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Status</label>
              <select name="status" value={formData.status} onChange={handleChange} className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500" disabled={isLoading}>
                <option value="ACTIVE">Active</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
            {/* Notes */}
            <div>
              <label className="block text-sm text-[#042C3C] mb-1">Action Plan</label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                className="w-full px-3 py-1 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
                rows="4"
                disabled={isLoading}
                placeholder={"- [ ] Give medicine every morning\n- [x] Buy special food\n- [ ] Schedule vet visit"}
              />
              <div className="text-xs text-gray-500 mt-1 mb-2">
                Use checklist format: <code>- [ ] Step</code> or <code>- [x] Done step</code>. You can check/uncheck after saving.
              </div>
              <div className="bg-[#FFF7EC] rounded p-2 mt-1">
                <span className="block text-xs text-[#042C3C] mb-1 font-semibold">Preview:</span>
                {renderActionPlanPreview(formData.notes)}
              </div>
            </div>
          </div>

          {/* Error Message Display */}
          {error && <p className="text-sm text-red-600 mt-2">{error}</p>}

          {/* Action Buttons */}
          <div className="mt-4 flex justify-end gap-2">
            <button type="button" onClick={onClose} className="px-4 py-2 text-sm text-gray-500 hover:text-[#EA6C7B]" disabled={isLoading}>Cancel</button>
            <button type="submit" className="px-4 py-2 bg-[#EA6C7B] text-white rounded-md text-sm hover:bg-[#EA6C7B]/90 disabled:opacity-50" disabled={isLoading}>
              {isLoading ? "Saving..." : (isEditMode ? "Update Plan" : "Add Plan")}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}