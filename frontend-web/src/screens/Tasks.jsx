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
  const [taskToDelete, setTaskToDelete] = useState(null);
  const [lastAddedTask, setLastAddedTask] = useState(null);
  const [lastEditedTask, setLastEditedTask] = useState(null);

  const [currentUser, setCurrentUser] = useState(null); // Step 1: Add state for user

  const navigate = useNavigate();
  const location = useLocation();

  // Step 2: Derive user IDs from currentUser state
  const userIdForURL = currentUser?.userId || currentUser?.userID || null;
  const effectUserID = currentUser?.userID || currentUser?.userId || null; 

const API_BASE_URL = "https://furrevercare-deploy-13.onrender.com/api";
 // const API_BASE_URL = "http://localhost:8080/api";
  

  const urlParams = new URLSearchParams(location.search);
  const initialPetID = urlParams.get("petId");

  useEffect(() => {
    AuthService.init(); // Initialize AuthService
    const userFromAuth = AuthService.getUser(); // Get user after init
    setCurrentUser(userFromAuth); // Step 1: Set user into state
  }, []); // Runs once on mount

  useEffect(() => {
    // This effect can be used for logging or other actions when effectUserID changes
    console.log("Tasks.jsx: UserID from state for effects:", effectUserID);
    // console.log("Tasks.jsx: Axios default headers:", axios.defaults.headers.common);
  }, [effectUserID]);

  useEffect(() => {
    const fetchUserPets = async () => {
      if (!effectUserID) { // Step 3: Use state-derived ID for checks and fetches
        setError("Please log in to manage tasks.");
        setLoading(false);
        return;
      }
      setLoading(true); 
      setError(null);
      setUserPets([]);
      setSelectedPetId("");
      setTasks([]);
      try {
        console.log(`Tasks.jsx: Fetching pets for userID: ${userIdForURL}`); 
        const res = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets`); 
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
          setLoading(false); 
        }
      } catch (err) {
        console.error("Tasks.jsx: Failed to fetch user pets:", err.response || err);
        setError("Failed to load your pets. Please try again.");
        setUserPets([]);
        setSelectedPetId("");
        setTasks([]);
        setLoading(false); 
        if (err.response?.status === 403) {
          console.log("Tasks.jsx: 403 on fetch pets, clearing auth");
          AuthService.clearAuth();
          setCurrentUser(null); // Clear user state
          navigate("/pawpedia");
        }
      }
    };

    fetchUserPets();
  }, [effectUserID, initialPetID, navigate]); // Step 3: Depend on state-derived ID

  const fetchTasks = async () => {
    if (!effectUserID || !selectedPetId) { // Step 3: Use state-derived ID
      setTasks([]);
      setLoading(false);
      return;
    }

    setLoading(true); 
    setError(null);
    setTasks([]); 

    try {
      console.log(`Tasks.jsx: Fetching tasks for userID: ${userIdForURL}, petID: ${selectedPetId}`); 
      const res = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks`); 
      const fetchedTasks = (Array.isArray(res.data) ? res.data : []).map(task => ({
        ...task,
        scheduledDateTime: task.scheduledDateTime ? new Date(task.scheduledDateTime) : null,
        completedAt: task.completedAt ? new Date(task.completedAt) : null,
      }));
      setTasks(fetchedTasks);
    } catch (err) {
      console.error("Tasks.jsx: Failed to fetch tasks:", err.response || err);
      setError(`Failed to load tasks for the selected pet. Please try again.`);
      setTasks([]);
      if (err.response?.status === 403) {
        console.log("Tasks.jsx: 403 on fetch tasks, clearing auth");
        AuthService.clearAuth();
        setCurrentUser(null); // Clear user state
        navigate("/login");
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Only fetch tasks if a user and pet are selected.
    // The fetchTasks function itself has guards, but this prevents unnecessary calls.
    if (effectUserID && selectedPetId) {
        fetchTasks();
    } else if (effectUserID && userPets.length > 0 && !selectedPetId) {
        // User is loaded, pets are loaded, but no pet is selected yet.
        // This might happen if initialPetID is not valid or not provided.
        setTasks([]); // Clear tasks, user needs to select a pet.
        setLoading(false);
    } else if (effectUserID && userPets.length === 0 && !loading && !error) {
        // User is loaded, pet fetch attempt completed, no pets found for this user.
        setTasks([]); // Clear tasks.
        setLoading(false);
    }

    if (selectedPetId) {
      navigate(`${location.pathname}?petId=${selectedPetId}`, { replace: true });
    }
    // Step 3: Depend on state-derived ID and other relevant states like userPets to manage task fetching lifecycle.
  }, [effectUserID, selectedPetId, navigate, location.pathname, userPets.length]); 



  const handleAddTaskClick = () => {
    if (!effectUserID) { // Step 3: Use state-derived ID
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
    if (!effectUserID || !selectedPetId) { // Step 3: Use state-derived ID
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
      userID: effectUserID, // Step 3: Use state-derived ID
    };
  
    setTasks(prev => [...prev, tempTask]);
    setLoadingTaskIds(ids => [...ids, tempId]);
    setError(null);
  
    try {
      const accessToken = AuthService.getToken();
      if (!accessToken) {
        throw new Error("Authentication token missing. Please log in again.");
      }
  
      const newTaskData = { ...taskData, userID: effectUserID, petID: selectedPetId }; // Step 3: Use state-derived ID
      if (newTaskData.scheduledDateTime && !(newTaskData.scheduledDateTime instanceof Date)) {
        newTaskData.scheduledDateTime = new Date(newTaskData.scheduledDateTime);
      }
      // Ensure dates are ISO strings for the API
      if (newTaskData.scheduledDateTime instanceof Date) {
        newTaskData.scheduledDateTime = newTaskData.scheduledDateTime.toISOString();
      }
      if (newTaskData.completedAt instanceof Date) {
        newTaskData.completedAt = newTaskData.completedAt.toISOString();
      }
  
      if (!userIdForURL || !selectedPetId) {
        throw new Error(`Invalid URL parameters: userIdForURL=${userIdForURL}, selectedPetId=${selectedPetId}`);
      }
  
      const url = `${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks`;
      console.log("Tasks.jsx: POST URL:", url);
      console.log("Tasks.jsx: Sending POST request with data:", JSON.stringify(newTaskData, null, 2));
      const headers = {
        ...axios.defaults.headers.common,
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      };
      console.log("Tasks.jsx: Headers for POST request:", headers);
  
      const res = await axios.post(url, newTaskData, { headers });
  
      const realId = res.data; 
      console.log("Tasks.jsx: Task created with ID:", realId);
      // Re-fetch the created task to get its full server representation including proper dates
      try {
        const fetchNewTaskRes = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${realId}`);
        const fetchedNewTaskRaw = fetchNewTaskRes.data;
        const fetchedNewTaskForState = {
            ...fetchedNewTaskRaw,
            scheduledDateTime: fetchedNewTaskRaw.scheduledDateTime ? new Date(fetchedNewTaskRaw.scheduledDateTime) : null,
            completedAt: fetchedNewTaskRaw.completedAt ? new Date(fetchedNewTaskRaw.completedAt) : null,
        };
        setTasks(prev => prev.map(task => 
            task.taskID === tempId ? fetchedNewTaskForState : task
        ));
        setLastAddedTask(fetchedNewTaskForState);
      } catch (fetchErr) {
        console.error("Tasks.jsx: Failed to re-fetch newly created task:", fetchErr);
        // Fallback to temp task if re-fetch fails
        setTasks(prev => prev.map(task => 
            task.taskID === tempId ? { ...tempTask, taskID: realId } : task // Update ID at least
        ));
        setLastAddedTask({ ...tempTask, taskID: realId });
      }
      setLoadingTaskIds(ids => ids.filter(id => id !== tempId));
      setShowCreateConfModal(true);
    } catch (err) {
      console.error("Tasks.jsx: Failed to add task:", err.response || err);
      console.log("Tasks.jsx: Error status:", err.response?.status);
      console.log("Tasks.jsx: Error data:", err.response?.data);
      setTasks(prev => prev.filter(task => task.taskID !== tempId));
      setLoadingTaskIds(ids => ids.filter(id => id !== tempId));
      if (err.response?.status === 403 || err.response?.status === 401) {
        setError("Authentication failed. Your session may have expired. Please log in again.");
        AuthService.clearAuth();
        setCurrentUser(null); // Clear user state
        navigate("/login");
      } else {
        setError(err.response?.data?.message || "Failed to add task. Please try again.");
      }
    }
  };

  const handleEditTaskSubmit = async (taskData) => {
    if (!effectUserID || !selectedPetId || !selectedTask?.taskID) { // Step 3: Use state-derived ID
      setError("User, Pet, or Task ID missing. Cannot update task.");
      return;
    }
    const taskId = selectedTask.taskID;
    setShowEditTaskModal(false);
    setLoadingTaskIds(ids => [...ids, taskId]);
    setError(null);

    const optimisticallyUpdatedTask = { 
      ...selectedTask, 
      ...taskData,
      scheduledDateTime: taskData.scheduledDateTime ? new Date(taskData.scheduledDateTime) : null,
      completedAt: taskData.completedAt ? new Date(taskData.completedAt) : null 
    };
    setTasks(prev => prev.map(task => 
      task.taskID === taskId ? optimisticallyUpdatedTask : task
    ));

    const updatedTaskDataForAPI = { 
      ...selectedTask, 
      ...taskData, 
      userID: effectUserID, // Step 3: Use state-derived ID
      petID: selectedPetId 
    };
    if (updatedTaskDataForAPI.scheduledDateTime instanceof Date) {
      updatedTaskDataForAPI.scheduledDateTime = updatedTaskDataForAPI.scheduledDateTime.toISOString();
    }
    if (updatedTaskDataForAPI.completedAt instanceof Date) {
      updatedTaskDataForAPI.completedAt = updatedTaskDataForAPI.completedAt.toISOString();
    }

    try {
      console.log("Tasks.jsx: Sending PUT request with data:", updatedTaskDataForAPI);
      await axios.put(
        `${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${taskId}`,
        updatedTaskDataForAPI
      );

      try {
        const fetchRes = await axios.get(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${taskId}`);
        const rawFetchedTask = fetchRes.data;
        const fetchedTaskForState = {
          ...rawFetchedTask,
          scheduledDateTime: rawFetchedTask.scheduledDateTime ? new Date(rawFetchedTask.scheduledDateTime) : null,
          completedAt: rawFetchedTask.completedAt ? new Date(rawFetchedTask.completedAt) : null,
        };
        
        setTasks(prev => prev.map(t => t.taskID === taskId ? fetchedTaskForState : t));
        setLastEditedTask(fetchedTaskForState); 
      } catch (fetchError) {
        console.error("Tasks.jsx: Failed to re-fetch task after update:", fetchError);
        setLastEditedTask(optimisticallyUpdatedTask); 
      }

      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setShowEditConfModal(true); 

    } catch (err) {
      console.error("Tasks.jsx: Failed to update task:", err.response || err);
      setTasks(prev => prev.map(task => 
        task.taskID === taskId ? selectedTask : task 
      ));
      setLoadingTaskIds(ids => ids.filter(id => id !== taskId));
      setError(err.response?.data?.message || "Failed to update task. Please try again.");
      if (err.response?.status === 403 || err.response?.status === 401) {
        console.log("Tasks.jsx: 403/401 on update task, clearing auth");
        AuthService.clearAuth();
        setCurrentUser(null); // Clear user state
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
    if (!effectUserID || !selectedPetId || !taskToDelete?.taskID) { // Step 3: Use state-derived ID
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
      await axios.delete(`${API_BASE_URL}/users/${userIdForURL}/pets/${selectedPetId}/scheduledTasks/${taskId}`); 
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
        setCurrentUser(null); // Clear user state
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
            {loading && userPets.length === 0 && !currentUser ? (
              <div className="flex items-center ml-2">
                <Loader className="h-4 w-4 text-[#EA6C7B] animate-spin mr-2" />
                <p className="text-sm text-gray-500">Loading user & pets...</p>
              </div>
            ) : loading && userPets.length === 0 && currentUser ? (
              <div className="flex items-center ml-2">
                <Loader className="h-4 w-4 text-[#EA6C7B] animate-spin mr-2" />
                <p className="text-sm text-gray-500">Loading pets...</p>
              </div>
            ) : userPets.length > 0 ? (
              <select
                id="pet-select"
                value={selectedPetId}
                onChange={handlePetChange}
                className="block w-full md:w-auto pl-3 pr-8 py-1.5 text-base border border-gray-300 focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] rounded-md text-sm bg-white text-[#042C3C]"
                disabled={loading} 
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
            ) : !loading && userPets.length === 0 && effectUserID && !error ? (
              <p className="text-sm text-gray-500 ml-2"></p>
            ) : null }
          </div>

          <button
            className="flex items-center gap-2 px-4 py-1.5 bg-[#EA6C7B] text-white rounded-full text-sm hover:bg-[#EA6C7B]/90 transition disabled:opacity-50 disabled:cursor-not-allowed self-end md:self-center"
            onClick={handleAddTaskClick}
            disabled={!effectUserID || !selectedPetId || loading || loadingTaskIds.length > 0}
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

        {loading ? (
          <div className="flex flex-col items-center py-10">
            <Loader className="h-8 w-8 text-[#EA6C7B] animate-spin" />
            <p className="mt-2 text-gray-500 text-sm">
              {(!currentUser) ? "Initializing user..." : (userPets.length === 0 && !error) ? "Loading pets..." : "Loading tasks..."}
            </p>
          </div>
        ) 
        : error ? (
          <div className="flex flex-col items-center py-10">
            
          </div>
        )
        : tasks.length === 0 && selectedPetId && effectUserID ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              No tasks scheduled for {userPets.find((p) => p.petID === selectedPetId)?.name || "this pet"} yet.
            </p>
          </div>
        ) 
        : !selectedPetId && effectUserID && userPets.length > 0 ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please select a pet from the dropdown above to view their tasks.
            </p>
          </div>
        ) 
        : !effectUserID && !error ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please log in to manage tasks.
            </p>
          </div>
        ) 
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
                      onClick={() => { setSelectedTask(task); setShowEditTaskModal(true); }}
                      disabled={!effectUserID || isTaskLoading(task.taskID) || !selectedPetId}
                      className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded disabled:opacity-50 disabled:cursor-not-allowed"
                      aria-label={`Edit ${task.description || "task"}`}>
                      <Edit className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => confirmDeleteTask(task)}
                      disabled={!effectUserID || isTaskLoading(task.taskID) || !selectedPetId}
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
      </div>

      <AddTaskModal
        isOpen={showAddTaskModal}
        onClose={() => setShowAddTaskModal(false)}
        onSubmit={handleAddTaskSubmit}
        petId={selectedPetId}
        userID={userIdForURL} 
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
        userID={userIdForURL} 
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
          setLastEditedTask(null); 
        }}
        task={lastEditedTask} 
        isEditConfirmation={false} 
      />
    </div>
  );
}