import { X } from "lucide-react";

export default function CreateConfTreatmentPlanModal({ isOpen, onClose, planName }) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg w-full max-w-md mx-4 p-6 relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-gray-500 hover:text-[#EA6C7B]"
        >
          <X className="h-5 w-5" />
        </button>
        <h2 className="text-2xl font-bold text-[#042C3C] mb-4">
          Treatment Plan Added
        </h2>
        <p className="text-sm text-gray-500 mb-6">
          The treatment plan "{planName}" has been successfully added.
        </p>
        <div className="flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-[#EA6C7B] text-white rounded-md text-sm hover:bg-[#EA6C7B]/90"
          >
            OK
          </button>
        </div>
      </div>
    </div>
  );
}