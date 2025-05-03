import { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import UserNavBar from "../components/UserNavBar";
import { Plus, Edit, Trash2, Loader, Calendar, Clock, AlertCircle } from "lucide-react";
import axios from "axios";
import AuthService from "../config/AuthService";
import AddTaskModal from "../components/tasks/AddTaskModal";
import EditTaskModal from "../components/tasks/EditTaskModal";
import DeleteConfTaskModal from "../components/tasks/DeleteConfTaskModal";
import CreateConfTaskModal from "../components/tasks/CreateConfTaskModal";
import EditConfTaskModal from "../components/tasks/EditConfTaskModal";

export default function Tasks() {
  const [tasks, setTasks] = useState([]);
  const [userPets, setUserPets] = useState([]);
  const [selectedPetId, setSelectedPetId] = useState("");
  const [showAddTaskModal, setShowAddTaskModal] = useState(false);
  const [showEditTaskModal, setShowEditTaskModal] = useState(false);
  const [selectedTask, setSelectedTask] = useState(null);
  const [loading, setLoading] = useState(true); // Start loading initially
  const [loadingTaskIds, setLoadingTaskIds] = useState([]);
  const [error, setError] = useState(null);
  const [showDeleteConfModal, setShowDeleteConfModal] = useState(false);
  const [showCreateConfModal, setShowCreateConfModal] = useState(false);
  const [showEditConfModal, setShowEditConfModal] = useState(false);
  const [isEditConfirmation, setIsEditConfirmation] = useState(true);
  const [taskToDelete, setTaskToDelete] = useState(null);
  const [taskToEdit, setTaskToEdit] = useState(null);
  const [lastAddedTask, setLastAddedTask] = useState(null);
  const [lastEditedTask, setLastEditedTask] = useState(null);

  const navigate = useNavigate();
  const location = useLocation();

  const user = AuthService.getUser();
  // Prioritize lowercase 'userId' for URL construction to match potential Google Auth token subject
  const userIdForURL = user?.userId || user?.userID || null;
  // Keep original userID for potential internal use or display if needed, though userIdForURL is now primary for API calls
  const userID = user?.userID || user?.userId || null; 
  const API_BASE_URL = "https://furrevercare-deploy-8.onrender.com/api";
  

  const urlParams = new URLSearchParams(location.search);
  const initialPetID = urlParams.get("petId");

  useEffect(() => {
    AuthService.init();
  }, []);

  useEffect(() => {
    console.log("Tasks.jsx: UserID from AuthService:", userID);
    console.log("Tasks.jsx: Axios default headers:", axios.defaults.headers.common);
  }, []);

  useEffect(() => {
    const fetchUserPets = async () => {
      if (!userID) {
        setError("Please log in to manage tasks.");
        setLoading(false);
        navigate("/about-us");
        return;
      }
      setLoading(true); // Start loading
      setError(null);
      setUserPets([]);
      setSelectedPetId("");
      setTasks([]);
      try {
        console.log(`Tasks.jsx: Fetching pets for userID: ${userIdForURL}`); // Use userIdForURL
        const res = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets`); // Use userIdForURL
        const fetchedPets = Array.isArray(res.data) ? res.data : [];
        setUserPets(fetchedPets);

        let petToSelect = "";
        if (initialPetID && fetchedPets.some((pet) => pet.petID === initialPetID)) {
          petToSelect = initialPetID;
        } else if (fetchedPets.length > 0) {
          petToSelect = fetchedPets[0].petID;
        }

        setSelectedPetId(petToSelect);

        if (!petToSelect && fetchedPets.length === 0) {
          setError("No pets found. Add a pet profile first to manage tasks.");
          // setLoading(true); // Incorrect: Should stop loading if no pets found
          setLoading(false); // Correct: Stop loading here
        }
        // Only set loading false here if pets were found and selected
        // Task loading will handle the rest
        // setLoading(false); // Let fetchTasks handle final loading state

      } catch (err) {
        console.error("Tasks.jsx: Failed to fetch user pets:", err.response || err);
        setError("Failed to load your pets. Please try again.");
        setUserPets([]);
        setSelectedPetId("");
        setTasks([]);
        // setLoading(True); // Incorrect: Should stop loading on error
        setLoading(false); // Correct: Stop loading on error
        if (err.response?.status === 403) {
          console.log("Tasks.jsx: 403 on fetch pets, clearing auth");
          AuthService.clearAuth();
          navigate("/pawpedia");
        }
      }
    };

    fetchUserPets();
  }, [userID, initialPetID, navigate]);

  const fetchTasks = async () => {
    if (!userID || !selectedPetId) {
      setTasks([]);
      setLoading(false);
      return;
    }

    setLoading(true); // Start loading tasks
    setError(null);
    setTasks([]); // Clear previous tasks while loading new ones

    try {
      console.log(`Tasks.jsx: Fetching tasks for userID: ${userIdForURL}, petID: ${selectedPetId}`); // Use userIdForURL
      const res = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks`); // Use userIdForURL
      const fetchedTasks = (Array.isArray(res.data) ? res.data : []).map(task => ({
        ...task,
        scheduledDateTime: task.scheduledDateTime?.seconds
          ? new Date(task.scheduledDateTime.seconds * 1000 + (task.scheduledDateTime.nanos || 0) / 1000000)
          : null,
        completedAt: task.completedAt?.seconds
          ? new Date(task.completedAt.seconds * 1000 + (task.completedAt.nanos || 0) / 1000000)
          : null,
      }));
      setTasks(fetchedTasks);
    } catch (err) {
      console.error("Tasks.jsx: Failed to fetch tasks:", err.response || err);
      setError(`Failed to load tasks for the selected pet. Please try again.`);
      setTasks([]);
      // setLoading(false); // Remove redundant setLoading(false) here
      if (err.response?.status === 403) {
        console.log("Tasks.jsx: 403 on fetch tasks, clearing auth");
        AuthService.clearAuth();
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTasks();
    if (selectedPetId) {
      navigate(`${location.pathname}?petId=${selectedPetId}`, { replace: true });
    }
  }, [userID, selectedPetId, navigate, location.pathname]);

  const handleAddTaskClick = () => {
    if (!userID) {
      setError("Please log in to add tasks.");
      navigate("/login");
      return;
    }
    if (!selectedPetId) {
      setError("Please select a pet first.");
      return;
    }
    const accessToken = AuthService.getToken();
    if (!accessToken) {
      setError("Authentication required. Please log in again.");
      navigate("/login");
      return;
    }
    console.log("Tasks.jsx: Token for add task:", accessToken);
    setSelectedTask(null);
    setShowAddTaskModal(true);
  };

  const handleAddTaskSubmit = async (taskData) => {
    if (!userID || !selectedPetId) {
      setError("User or Pet ID missing. Cannot add task.");
      setShowAddTaskModal(false);
      return;
    }

    setShowAddTaskModal(false);
    const tempId = `temp-${Date.now()}`;
    const tempTask = {
      ...taskData,
      taskID: tempId,
      petID: selectedPetId,
      userID: userID,
    };

    setTasks(prev => [...prev, tempTask]);
    setLoadingTaskIds(ids => [...ids, tempId]);
    setError(null);

    try {
      const newTaskData = { ...taskData, userID, petID: selectedPetId };
      if (newTaskData.scheduledDateTime && !(newTaskData.scheduledDateTime instanceof Date)) {
        newTaskData.scheduledDateTime = new Date(newTaskData.scheduledDateTime);
      }
      console.log("Tasks.jsx: Sending POST request with data:", newTaskData);
      // Log the specific headers for this request
      const headers = axios.defaults.headers.common;
      console.log("Tasks.jsx: Axios headers before POST:", headers);
      console.log("Tasks.jsx: Authorization Header:", headers['Authorization']); 
      const res = await axios.post(
        `${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks`, // Use userIdForURL for consistency
        newTaskData
      );

      const realId = res.data; // Expect string ID
      console.log("Tasks.jsx: Task created with ID:", realId);
      setTasks(prev => prev.map(task => 
        task.taskID === tempId ? { ...task, taskID: realId } : task
      ));
      setLoadingTaskIds(ids => ids.filter(id => id !== tempId));
      setLastAddedTask({ ...tempTask, taskID: realId });
      setShowCreateConfModal(true);
    } catch (err) {
      console.error("Tasks.jsx: Failed to add task:", err.response || err);
      setTasks(prev => prev.filter(task => task.taskID !== tempId));
      setLoadingTaskIds(ids => ids.filter(id => id !== tempId));
      setError(err.response?.data?.message || "Failed to add task. Please try again.");
      if (err.response?.status === 403 || err.response?.status === 401) {
        console.log("Tasks.jsx: 403/401 on add task, clearing auth");
        AuthService.clearAuth();
        navigate("/login");
      }
    }
  };

  const confirmEditTask = (task) => {
    setTaskToEdit(task);
    setIsEditConfirmation(true);
    setShowEditConfModal(true);
  };

  const proceedWithEdit = () => {
    setShowEditConfModal(false);
    setSelectedTask(taskToEdit);
    setShowEditTaskModal(true);
  };

  const handleEditTaskSubmit = async (taskData) => {
    if (!userID || !selectedPetId || !selectedTask?.taskID) {
      setError("User, Pet, or Task ID missing. Cannot update task.");
      return;
    }
    const taskId = selectedTask.taskID;
    setShowEditTaskModal(false);
    setLoadingTaskIds(ids => [...ids, taskId]);
    setError(null);

    const originalTasks = [...tasks];
    setTasks(prev => prev.map(task => 
      task.taskID === taskId ? { ...task, ...taskData } : task
    ));

    try {
      // Ensure updatedTaskData uses the consistent ID format
      const updatedTaskData = { ...selectedTask, ...taskData, userID: userIdForURL, petID: selectedPetId }; // Use userIdForURL
      if (updatedTaskData.scheduledDateTime && !(updatedTaskData.scheduledDateTime instanceof Date)) {
        updatedTaskData.scheduledDateTime = new Date(updatedTaskData.scheduledDateTime);
      }
      console.log("Tasks.jsx: Sending PUT request with data:", updatedTaskData);
      console.log("Tasks.jsx: Axios headers before PUT:", axios.defaults.headers.common);
      await axios.put(
        `${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${taskId}`, // Use userIdForURL
        updatedTaskData
      );
      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setLastEditedTask(updatedTaskData);
      setIsEditConfirmation(false);
      setShowEditConfModal(true);
    } catch (err) {
      console.error("Tasks.jsx: Failed to update task:", err.response || err);
      setTasks(originalTasks);
      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setError(err.response?.data?.message || "Failed to update task. Please try again.");
      if (err.response?.status === 403 || err.response?.status === 401) {
        console.log("Tasks.jsx: 403/401 on update task, clearing auth");
        AuthService.clearAuth();
        navigate("/login");
      }
    } finally {
      setSelectedTask(null);
    }
  };

  const confirmDeleteTask = (task) => {
    setTaskToDelete(task);
    setShowDeleteConfModal(true);
  };

  const handleDeleteTask = async () => {
    if (!userID || !selectedPetId || !taskToDelete?.taskID) {
      setError("User, Pet, or Task ID missing. Cannot delete task.");
      return;
    }
    const taskId = taskToDelete.taskID;
    setShowDeleteConfModal(false);
    setLoadingTaskIds(ids => [...ids, taskId]);
    setError(null);

    const originalTasks = [...tasks];
    setTasks(prev => prev.filter(task => task.taskID !== taskId));

    try {
      console.log("Tasks.jsx: Sending DELETE request for taskID:", taskId);
      console.log("Tasks.jsx: Axios headers before DELETE:", axios.defaults.headers.common);
      await axios.delete(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${taskId}`); // Use userIdForURL
      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setTaskToDelete(null);
    } catch (err) {
      console.error("Tasks.jsx: Failed to delete task:", err.response || err);
      setTasks(originalTasks);
      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setError(err.response?.data?.message || "Failed to delete task. Please try again.");
      if (err.response?.status === 403 || err.response?.status === 401) {
        console.log("Tasks.jsx: 403/401 on delete task, clearing auth");
        AuthService.clearAuth();
        navigate("/login");
      }
    }
  };

  const handlePetChange = (event) => {
    const newPetId = event.target.value;
    setSelectedPetId(newPetId);
  };

  const formatDateTime = (date) => {
    if (!date) return "N/A";
    return date.toLocaleString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric',
      hour: 'numeric', minute: '2-digit', hour12: true
    });
  };

  const isTaskLoading = (id) => loadingTaskIds.includes(id);

  return (
    <div className="w-full min-h-screen bg-[#FFF7EC] font-['Baloo'] overflow-x-hidden">
      <UserNavBar />
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6 mt-8">
          <div className="flex items-center gap-3 flex-wrap">
            <h2 className="text-3xl md:text-4xl font-bold text-[#042C3C] flex-shrink-0">
              Scheduled Tasks
            </h2>
            {/* Conditional Rendering for Pet Selection Area - Revised Logic */}
            {loading && userPets.length === 0 ? ( // Primary condition: Initial pet loading
              <div className="flex items-center ml-2">
                <Loader className="h-4 w-4 text-[#EA6C7B] animate-spin mr-2" />
                <p className="text-sm text-gray-500">Loading pets...</p>
              </div>
            ) : userPets.length > 0 ? ( // Pets are loaded, show dropdown
              <select
                id="pet-select"
                value={selectedPetId}
                onChange={handlePetChange}
                className="block w-full md:w-auto pl-3 pr-8 py-1.5 text-base border border-gray-300 focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] rounded-md text-sm bg-white text-[#042C3C]"
                disabled={loading} // Disable if tasks are still loading
                aria-label="Select a Pet">
                {selectedPetId === "" && (
                  <option value="" disabled>
                    -- Select a Pet --
                  </option>
                )}
                {userPets.map((pet) => (
                  <option key={pet.petID} value={pet.petID}>
                    {pet.name}
                  </option>
                ))}
              </select>
            ) : !loading && userPets.length === 0 && userID && !error ? ( // Explicitly check !loading for "No pets found"
              <p className="text-sm text-gray-500 ml-2"></p>
            ) : null /* Other cases like not logged in */ }
          </div>

          <button
            className="flex items-center gap-2 px-4 py-1.5 bg-[#EA6C7B] text-white rounded-full text-sm hover:bg-[#EA6C7B]/90 transition disabled:opacity-50 disabled:cursor-not-allowed self-end md:self-center"
            onClick={handleAddTaskClick}
            disabled={!userID || !selectedPetId || loading || loadingTaskIds.length > 0}
          >
            <Plus className="h-4 w-4" />
            Add Task
          </button>
        </div>

        {error && (
          <p className="text-red-500 mb-4 text-center text-sm font-semibold">
            {error}
          </p>
        )}

        {/* --- Start of Changes --- */}
        {/* Prioritize loading state */}
        {loading ? (
          <div className="flex flex-col items-center py-10">
            <Loader className="h-8 w-8 text-[#EA6C7B] animate-spin" />
            <p className="mt-2 text-gray-500 text-sm">
              {userPets.length === 0 ? "Loading pets..." : "Loading tasks..."}
            </p>
          </div>
        ) 
        /* Then, prioritize error state (if not loading) */
        : error ? (
          // Error is already displayed above, so this block can be empty or show a generic message if needed
          // Keeping it minimal to avoid redundancy with the error display above
          <div className="flex flex-col items-center py-10">
             {/* Optionally add a generic message here if needed when error is present */}
             {/* <p className="text-gray-500 text-sm">An error occurred.</p> */}
          </div>
        )
        /* If not loading and no error, check for no tasks */
        : tasks.length === 0 && selectedPetId ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              No tasks scheduled for {userPets.find((p) => p.petID === selectedPetId)?.name || "this pet"} yet.
            </p>
          </div>
        ) 
        /* If not loading, no error, and no pet selected (but pets exist) */
        : !selectedPetId && userID && userPets.length > 0 ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please select a pet from the dropdown above to view their tasks.
            </p>
          </div>
        ) 
        /* If not loading, no error, and not logged in */
        : !userID ? (
           // This case might be redundant if the error state handles 'Please log in'
           // But kept for clarity if other non-error scenarios lead to !userID
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please log in to manage tasks.
            </p>
          </div>
        ) 
        /* Finally, display tasks if not loading, no error, and tasks exist */
        : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {tasks.map((task) => (
              <div
                key={task.taskID}
                className={`bg-white rounded-lg shadow relative overflow-hidden text-sm transition-opacity duration-300 ${isTaskLoading(task.taskID) ? "opacity-50" : "opacity-100"}`}>
                {isTaskLoading(task.taskID) && (
                  <div className="absolute inset-0 bg-white/70 flex flex-col items-center justify-center z-10">
                    <Loader className="h-6 w-6 text-[#EA6C7B] animate-spin" />
                    <p className="mt-1 text-gray-500 text-xs">Processing...</p>
                  </div>
                )}
                <div className="flex justify-between items-start p-4 pb-0">
                  <h2 className="text-lg font-semibold text-[#042C3C] break-words">
                    {task.description || "Untitled Task"}
                  </h2>
                  <div className="flex gap-1 flex-shrink-0">
                    <button
                      onClick={() => confirmEditTask(task)}
                      disabled={!userID || isTaskLoading(task.taskID) || !selectedPetId}
                      className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded disabled:opacity-50 disabled:cursor-not-allowed"
                      aria-label={`Edit ${task.description || "task"}`}>
                      <Edit className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => confirmDeleteTask(task)}
                      disabled={!userID || isTaskLoading(task.taskID) || !selectedPetId}
                      className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded disabled:opacity-50 disabled:cursor-not-allowed"
                      aria-label={`Delete ${task.description || "task"}`}>
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
                <div className="p-4 pt-1">
                  <p className="text-xs text-gray-500 capitalize mb-3">
                    {task.taskType ? task.taskType.toLowerCase().replace('_', ' ') : 'Other'}
                  </p>
                  <div className="grid grid-cols-2 gap-x-4 gap-y-2">
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Status
                      </p>
                      <p className="text-xs font-medium capitalize">
                        <span className={`${task.status === 'COMPLETED' ? 'text-green-600' : task.status === 'PENDING' ? 'text-blue-600' : task.status === 'CANCELLED' ? 'text-gray-500' : 'text-gray-600'}`}>
                          {task.status ? task.status.toLowerCase() : 'Pending'}
                        </span>
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Progress
                      </p>
                      <p className="text-xs text-gray-500">
                        {task.status === 'COMPLETED' ? '100%' : task.status === 'CANCELLED' ? '0%' : '50%'}
                      </p>
                    </div>
                    <div className="col-span-2">
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Scheduled Date & Time
                      </p>
                      <p className="text-xs text-gray-500 flex items-center gap-1">
                        <Calendar className="h-3 w-3" />
                        {formatDateTime(task.scheduledDateTime)}
                      </p>
                    </div>
                    {task.notes && (
                      <div className="col-span-2">
                        <p className="text-[10px] font-medium text-[#042C3C]">
                          Notes
                        </p>
                        <p className="text-xs text-gray-500 break-words">
                          {task.notes}
                        </p>
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
        {/* --- End of Changes --- */}

        {/* Modals */} 
      </div>

      <AddTaskModal
        isOpen={showAddTaskModal}
        onClose={() => setShowAddTaskModal(false)}
        onSubmit={handleAddTaskSubmit}
        petId={selectedPetId}
        userID={userIdForURL} // Pass consistent userIdForURL
      />
      <EditTaskModal
        isOpen={showEditTaskModal}
        onClose={() => {
          setShowEditTaskModal(false);
          setSelectedTask(null);
        }}
        onSubmit={handleEditTaskSubmit}
        task={selectedTask}
        petId={selectedPetId}
        userID={userIdForURL} // Pass consistent userIdForURL
      />
      <DeleteConfTaskModal
        isOpen={showDeleteConfModal}
        onClose={() => setShowDeleteConfModal(false)}
        onConfirm={handleDeleteTask}
        task={taskToDelete}
      />
      <CreateConfTaskModal
        isOpen={showCreateConfModal}
        onClose={() => setShowCreateConfModal(false)}
        task={lastAddedTask}
      />
      <EditConfTaskModal
        isOpen={showEditConfModal}
        onClose={() => {
          setShowEditConfModal(false);
          if (!isEditConfirmation) setLastEditedTask(null);
        }}
        task={isEditConfirmation ? taskToEdit : lastEditedTask}
      />
    </div>
  );
}