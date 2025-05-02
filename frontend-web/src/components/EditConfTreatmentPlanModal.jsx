import { useState, useEffect } from "react";
import { X } from "lucide-react";

export default function EditConfTreatmentPlanModal({
  isOpen,
  onClose,
  onConfirm,
  title,
  confirmButtonText,
  showCancelButton = true,
}) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg w-full max-w-sm mx-4 p-5 relative">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-3 right-3 text-gray-400 hover:text-gray-600"
        >
          <X className="h-5 w-5" />
        </button>

        {/* Title */}
        <h2 className="text-xl font-semibold text-[#042C3C] mb-3">{title}</h2>


        {/* Hardcoded Message */}
        <p className="text-sm text-gray-600 mb-5">
          Are you sure you want to edit this treatment plan?
        </p>

        {/* Action Buttons */}
        <div className="flex justify-end gap-3">
          {showCancelButton && (
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-1.5 text-sm text-gray-500 hover:text-gray-700 rounded-md border border-gray-300"
            >
              Cancel
            </button>
          )}
          <button
            type="button"
            onClick={onConfirm}
            className="px-4 py-1.5 bg-[#EA6C7B] text-white rounded-md text-sm hover:bg-[#EA6C7B]/90"
          >
            Yes {confirmButtonText}
            
          </button>
        </div>
      </div>
    </div>
  );
}
