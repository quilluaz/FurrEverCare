import { X } from "lucide-react";
import { CheckCircle } from "lucide-react";

export default function EditConfTaskModal({ isOpen, onClose, task, isEditConfirmation }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
        <div className="flex justify-between items-center p-4 border-b">
          <h2 className="text-xl font-semibold text-[#042C3C]">
            {isEditConfirmation ? "Edit Task" : "Task Updated"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-[#EA6C7B] transition-colors"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="p-6 flex flex-col items-center">
          {isEditConfirmation ? (
            <>
              <p className="text-gray-700 text-center mb-6">
                Do you want to edit the task "{task?.description || 'Untitled Task'}"?
              </p>

              <div className="flex justify-center gap-3 w-full">
                <button
                  type="button"
                  onClick={onClose}
                  className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 rounded-md hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
                >
                  Cancel
                </button>
                <button
                  type="button"
                  onClick={() => onClose(true)} // Pass true to indicate proceeding with edit
                  className="px-4 py-2 text-sm font-medium text-white bg-[#EA6C7B] rounded-md hover:bg-[#EA6C7B]/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#EA6C7B]"
                >
                  Edit Task
                </button>
              </div>
            </>
          ) : (
            <>
              <CheckCircle className="h-16 w-16 text-green-500 mb-4" />
              <p className="text-gray-700 text-center mb-2">
                Task "{task?.description || 'Untitled Task'}" has been successfully updated.
              </p>
              <p className="text-gray-500 text-sm text-center mb-6">
                The changes have been saved and are now visible in your tasks list.
              </p>

              <button
                type="button"
                onClick={onClose}
                className="px-6 py-2 text-sm font-medium text-white bg-[#EA6C7B] rounded-md hover:bg-[#EA6C7B]/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-[#EA6C7B]"
              >
                OK
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}