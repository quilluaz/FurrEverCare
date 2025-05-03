import { X } from "lucide-react";

export default function DeleteConfTaskModal({ isOpen, onClose, onConfirm, task }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
        <div className="flex justify-between items-center p-4 border-b">
          <h2 className="text-xl font-semibold text-[#042C3C]">Delete Task</h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-[#EA6C7B] transition-colors"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="p-6">
          <p className="text-gray-700 mb-6">
            Are you sure you want to delete the task "{task?.description || 'Untitled Task'}"? This action cannot be undone.
          </p>

          <div className="flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
            >
              Cancel
            </button>
            <button
              type="button"
              onClick={onConfirm}
              className="px-4 py-2 text-sm font-medium text-white bg-red-600 rounded-md hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
              Delete Task
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}