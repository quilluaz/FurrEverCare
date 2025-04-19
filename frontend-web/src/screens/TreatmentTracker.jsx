import { useState } from "react";
import { Plus, Clock, Bell, Calendar, Activity, Check, X, AlertCircle } from "lucide-react";
import DailyTaskForm from "../components/DailyTaskForm";
import UserNavBar from "../components/UserNavBar";
import AddMedicationModal from "../components/AddMedicationModal";
import LogTreatmentModal from "../components/LogTreatmentModal";
import UpdateProgressModal from "../components/UpdateProgressModal";

export default function TreatmentTracker() {
  // State for modals
  const [showAddMedicationModal, setShowAddMedicationModal] = useState(false);
  const [showLogTreatmentModal, setShowLogTreatmentModal] = useState(false);
  const [showUpdateProgressModal, setShowUpdateProgressModal] = useState(false);
  const [selectedTreatment, setSelectedTreatment] = useState(null);

  // State for medications, treatments, tasks, and alerts
  const [medications, setMedications] = useState([
    {
      id: 1,
      name: "Heart Medication",
      pet: "Buddy",
      dosage: "1 tablet",
      time: "8:00 AM",
      completed: true,
    },
    {
      id: 2,
      name: "Joint Supplement",
      pet: "Buddy",
      dosage: "1 scoop",
      time: "6:00 PM",
      completed: false,
    },
  ]);

  const [treatments, setTreatments] = useState([
    {
      id: 1,
      name: "Diet Plan",
      pet: "Buddy",
      description: "Low-calorie diet",
      startDate: "2023-04-20",
      endDate: "2023-06-20",
      progress: 60,
      duration: "2 weeks left",
    },
  ]);

  const [dailyTasks, setDailyTasks] = useState([
    {
      id: 1,
      name: "Annual Checkup",
      pet: "Buddy",
      time: "9:00 AM",
      status: "completed",
    },
    {
      id: 2,
      name: "Eye Drops",
      pet: "Buddy",
      time: "12:00 PM",
      status: "missed",
    },
    {
      id: 3,
      name: "Joint Supplement",
      pet: "Buddy",
      time: "6:00 PM",
      status: "upcoming",
    },
  ]);

  const [alerts, setAlerts] = useState([]);

  const [showDailyTaskForm, setShowDailyTaskForm] = useState(false);

  // Handlers to open modals
  const handleOpenAddMedicationModal = () => setShowAddMedicationModal(true);
  const handleOpenLogTreatmentModal = () => setShowLogTreatmentModal(true);
  const handleOpenUpdateProgressModal = (treatment) => {
    setSelectedTreatment(treatment);
    setShowUpdateProgressModal(true);
  };

  // Handlers to close modals
  const handleCloseAddMedicationModal = () => setShowAddMedicationModal(false);
  const handleCloseLogTreatmentModal = () => setShowLogTreatmentModal(false);
  const handleCloseUpdateProgressModal = () => {
    setShowUpdateProgressModal(false);
    setSelectedTreatment(null);
  };

  // Toggle medication completion
  const toggleMedicationCompletion = (id) => {
    setMedications(medications.map((med) => (med.id === id ? { ...med, completed: !med.completed } : med)));
  };

  // Update treatment progress
  const updateProgress = (id, newProgress) => {
    setTreatments(
      treatments.map((treatment) => (treatment.id === id ? { ...treatment, progress: newProgress } : treatment))
    );
  };

  // Add a new daily task
  const addDailyTask = (task) => {
    setDailyTasks([...dailyTasks, { ...task, id: Date.now(), status: "upcoming" }]);
  };

  return (
    <div className="min-h-screen bg-[#FFF7EC] font-['Baloo'] w-full overflow-hidden">
      <UserNavBar />

      {/* Main content area */}
      <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 py-6 sm:py-8">
        {/* Header */}
        <div className="mb-6">
          <h1 className="text-xl sm:text-2xl font-bold text-[#042C3C]">
            Treatment Tracker
          </h1>
          <p className="text-xs sm:text-sm text-gray-600 mt-1">
            Interactive tool to manage and track your pet's medication and
            treatments.
          </p>
        </div>

        {/* Action buttons */}
        <div className="flex flex-wrap gap-2 sm:gap-4 mb-6 sm:mb-8">
          <button
            onClick={handleOpenAddMedicationModal}
            className="flex items-center gap-1 sm:gap-2 px-4 sm:px-6 py-2 bg-[#EA6C7B] text-white rounded-full hover:bg-[#EA6C7B]/90 transition-colors text-xs sm:text-sm"
          >
            <Plus className="h-4 w-4" />
            <span className="whitespace-nowrap">Add Medication</span>
          </button>

          <button
            onClick={handleOpenLogTreatmentModal}
            className="flex items-center gap-1 sm:gap-2 px-4 sm:px-6 py-2 bg-[#8A973F] text-white rounded-full hover:bg-[#8A973F]/90 transition-colors text-xs sm:text-sm"
          >
            <Activity className="h-4 w-4" />
            <span className="whitespace-nowrap">Log Treatment</span>
          </button>

          <button
            onClick={() => setShowDailyTaskForm(true)}
            className="flex items-center gap-1 sm:gap-2 px-4 sm:px-6 py-2 bg-[#F0B542] text-white rounded-full hover:bg-[#F0B542]/90 transition-colors text-xs sm:text-sm"
          >
            <Calendar className="h-4 w-4" />
            <span className="whitespace-nowrap">Add Daily Task</span>
          </button>
        </div>

        {/* Content grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          {/* Daily Medications */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-lg shadow-sm border-2 border-[#EA6C7B] h-full">
              <div className="p-3 sm:p-4 border-b border-gray-100">
                <h2 className="text-base sm:text-lg font-bold text-[#042C3C] flex items-center gap-2">
                  <Clock className="h-5 w-5 text-[#EA6C7B]" />
                  Daily Medications
                </h2>
              </div>
              <div className="p-4 sm:p-6">
                {medications.length === 0 ? (
                  <p className="text-xs text-gray-500 text-center">
                    You have not added any medications yet.
                  </p>
                ) : (
                  <div className="space-y-3 sm:space-y-4">
                    {medications.map((med) => (
                      <div
                        key={med.id}
                        className="p-3 sm:p-4 bg-gray-50 rounded-lg border-l-4 border-[#EA6C7B] flex justify-between items-center flex-wrap sm:flex-nowrap gap-2"
                      >
                        <div className="flex items-center gap-2 sm:gap-3 w-full sm:w-auto">
                          <button
                            onClick={() => toggleMedicationCompletion(med.id)}
                            className={`min-w-6 h-6 rounded-full flex items-center justify-center ${
                              med.completed
                                ? "bg-green-500 text-white"
                                : "border-2 border-gray-300 text-transparent hover:border-[#EA6C7B]"
                            }`}
                          >
                            {med.completed && <Check className="h-4 w-4" />}
                          </button>
                          <div className="overflow-hidden">
                            <h3 className="font-medium text-[#042C3C] truncate text-xs sm:text-sm">
                              {med.name}
                            </h3>
                            <p className="text-[10px] sm:text-xs text-gray-500 flex flex-wrap gap-1 sm:gap-2">
                              <span>◦ {med.time}</span>
                              <span>{med.dosage}</span>
                            </p>
                          </div>
                        </div>
                        <div className="flex items-center w-full sm:w-auto justify-end">
                          <span
                            className={`px-2 py-1 text-[10px] sm:text-xs rounded-full ${
                              med.completed
                                ? "bg-green-100 text-green-800"
                                : "bg-yellow-100 text-yellow-800"
                            }`}
                          >
                            {med.completed ? "Completed" : "Pending"}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Daily Tasks */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border-2 border-[#F0B542] h-full">
              <div className="p-3 sm:p-4 border-b border-gray-100">
                <h2 className="text-base sm:text-lg font-bold text-[#042C3C] flex items-center gap-2">
                  <Calendar className="h-5 w-5 text-[#F0B542]" />
                  Daily Tasks
                </h2>
              </div>
              <div className="p-4 sm:p-6">
                {dailyTasks.length === 0 ? (
                  <p className="text-xs text-gray-500 text-center">
                    No tasks for today.
                  </p>
                ) : (
                  <div className="space-y-2 sm:space-y-3">
                    {dailyTasks.map((task) => (
                      <div
                        key={task.id}
                        className="p-2 sm:p-3 rounded-lg bg-gray-50"
                      >
                        <div className="flex items-start gap-2 sm:gap-3">
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-1 sm:gap-2 flex-wrap">
                              <span className="text-[10px] sm:text-xs font-medium text-gray-500">
                                {task.time}
                              </span>
                              <span
                                className={`px-2 py-0.5 text-[9px] sm:text-[10px] rounded-full ${
                                  task.status === "completed"
                                    ? "bg-green-100 text-green-800"
                                    : task.status === "missed"
                                    ? "bg-red-100 text-red-800"
                                    : "bg-yellow-100 text-yellow-800"
                                }`}
                              >
                                {task.status.charAt(0).toUpperCase() +
                                  task.status.slice(1)}
                              </span>
                            </div>
                            <p className="font-medium text-[#042C3C] truncate text-xs sm:text-sm">
                              {task.name}
                            </p>
                            <p className="text-[10px] sm:text-xs text-gray-500 truncate">
                              For {task.pet}
                            </p>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Ongoing Treatments */}
          <div className="md:col-span-2">
            <div className="bg-white rounded-lg shadow-sm border-2 border-[#8A973F] h-full">
              <div className="p-3 sm:p-4 border-b border-gray-100">
                <h2 className="text-base sm:text-lg font-bold text-[#042C3C] flex items-center gap-2">
                  <Activity className="h-5 w-5 text-[#8A973F]" />
                  Ongoing Treatments
                </h2>
              </div>
              <div className="p-4 sm:p-6">
                {treatments.length === 0 ? (
                  <p className="text-xs text-gray-500 text-center">
                    No ongoing treatments.
                  </p>
                ) : (
                  <div className="space-y-3 sm:space-y-4">
                    {treatments.map((t) => (
                      <div
                        key={t.id}
                        className="p-3 sm:p-4 bg-gray-50 rounded-lg"
                      >
                        <div className="flex flex-wrap sm:flex-nowrap justify-between items-start mb-2 gap-2">
                          <div className="min-w-0">
                            <h3 className="font-medium text-[#042C3C] truncate text-xs sm:text-sm">
                              {t.name}
                            </h3>
                            <p className="text-[10px] sm:text-xs text-gray-500">
                              {t.description}
                            </p>
                          </div>
                          <div className="text-right">
                            <span className="px-2 py-1 text-[9px] sm:text-[10px] rounded-full bg-[#8A973F]/20 text-[#8A973F] whitespace-nowrap">
                              {t.duration}
                            </span>
                          </div>
                        </div>
                        <div className="mt-3">
                          <div className="flex justify-between text-[9px] sm:text-[10px] text-gray-500 mb-1">
                            <span>Progress</span>
                            <span>{t.progress}%</span>
                          </div>
                          <div className="w-full bg-gray-200 rounded-full h-2 mb-3">
                            <div
                              className="bg-[#8A973F] h-2 rounded-full"
                              style={{ width: `${t.progress}%` }}
                            />
                          </div>
                          <div className="flex justify-end">
                            <button
                              onClick={() => handleOpenUpdateProgressModal(t)}
                              className="px-3 sm:px-4 py-1.5 text-[9px] sm:text-xs border border-[#8A973F] text-[#8A973F] rounded-md hover:bg-[#8A973F]/10"
                            >
                              Update Progress
                            </button>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Alerts */}
          <div className="md:col-span-1">
            <div className="bg-white rounded-lg shadow-sm border-2 border-[#042C3C] h-full">
              <div className="p-3 sm:p-4 border-b border-gray-100">
                <h2 className="text-base sm:text-lg font-bold text-[#042C3C] flex items-center gap-2">
                  <Bell className="h-5 w-5 text-[#042C3C]" />
                  Alerts
                </h2>
              </div>
              <div className="p-4 sm:p-6 flex items-center justify-center min-h-[200px]">
                {alerts.length === 0 ? (
                  <p className="text-xs text-gray-500 text-center">
                    No active alerts.
                  </p>
                ) : (
                  <div className="w-full space-y-2 sm:space-y-3">
                    {alerts.map((alert) => (
                      <div
                        key={alert.id}
                        className={`p-3 rounded-lg relative ${
                          alert.type === "missed" ? "bg-red-50" : "bg-yellow-50"
                        }`}
                      >
                        <button
                          onClick={() =>
                            setAlerts(alerts.filter((a) => a.id !== alert.id))
                          }
                          className="absolute top-2 right-2 text-gray-400 hover:text-gray-600"
                        >
                          <X className="h-4 w-4" />
                        </button>
                        <div className="flex items-start gap-2 sm:gap-3 pr-6">
                          <AlertCircle
                            className={`h-5 w-5 mt-0.5 ${
                              alert.type === "missed"
                                ? "text-red-500"
                                : "text-yellow-500"
                            }`}
                          />
                          <div className="min-w-0">
                            <p className="font-medium text-[#042C3C] break-words text-xs sm:text-sm">
                              {alert.message}
                            </p>
                            <p className="text-[9px] sm:text-[10px] text-gray-500">
                              {alert.date}
                            </p>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Modals */}
      {showAddMedicationModal && (
        <AddMedicationModal
          isOpen={showAddMedicationModal}
          onClose={handleCloseAddMedicationModal}
        />
      )}
      {showLogTreatmentModal && (
        <LogTreatmentModal
          isOpen={showLogTreatmentModal}
          onClose={handleCloseLogTreatmentModal}
        />
      )}
      {showUpdateProgressModal && selectedTreatment && (
        <UpdateProgressModal
          isOpen={showUpdateProgressModal}
          onClose={handleCloseUpdateProgressModal}
          initialProgress={selectedTreatment.progress}
          treatmentName={selectedTreatment.name}
          onUpdateProgress={(newProgress) =>
            updateProgress(selectedTreatment.id, newProgress)
          }
        />
      )}

      {/* Daily Task Form Modal */}
      {showDailyTaskForm && (
        <DailyTaskForm
          isOpen={showDailyTaskForm}
          onClose={() => setShowDailyTaskForm(false)}
          onAddTask={addDailyTask}
        />
      )}
    </div>
  );
}