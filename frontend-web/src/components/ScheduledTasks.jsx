import React, { useState, useEffect, useCallback, useRef } from 'react';
import TaskService from '../config/TaskService';
import AuthService from '../config/AuthService';
import PetService from '../config/PetService'; // Assuming you have a PetService similar to TaskService

// Define Enums locally for dropdowns/display
const TaskType = {
    MEDICATION: 'Medication',
    FEEDING: 'Feeding',
    WALK: 'Walk',
    VET_VISIT: 'Vet Visit',
    GROOMING: 'Grooming',
    APPOINTMENT: 'Appointment',
    OTHER: 'Other'
};

const TaskStatus = {
    PENDING: 'Pending',
    COMPLETED: 'Completed',
    SKIPPED: 'Skipped',
    OVERDUE: 'Overdue' // Note: Backend doesn't seem to set this automatically
};

const RecurrenceRule = {
    NONE: 'None',
    DAILY: 'Daily',
    WEEKLY: 'Weekly',
    MONTHLY: 'Monthly'
};

const MIN_LOADING_TIME = 500; // Minimum time in ms to show loading indicator


function ScheduledTasks() {
    const [tasks, setTasks] = useState([]);
    const [pets, setPets] = useState([]);
    const [selectedPetId, setSelectedPetId] = useState('');
    const [isPetsLoading, setIsPetsLoading] = useState(true); // Start loading pets initially
    const [isTasksLoading, setIsTasksLoading] = useState(false);
    const [error, setError] = useState(null);
    const [editingTask, setEditingTask] = useState(null); // Task object being edited or null for new task
    const [showForm, setShowForm] = useState(false);
    const petLoadStartTime = useRef(null);
    const taskLoadStartTime = useRef(null);

    const user = AuthService.getUser();
    const userId = user?.uid || user?.userID; // Adapt based on your AuthService user object structure

    const fetchPets = useCallback(async () => {
        if (!userId) {
            setIsPetsLoading(false); // Stop loading if no user
            return;
        }
        petLoadStartTime.current = Date.now(); // Record start time
        setIsPetsLoading(true);
        setError(null); // Clear previous errors
        setPets([]); // Clear pets before fetching
        setSelectedPetId(''); // Reset selected pet
        setTasks([]); // Clear tasks when pets are re-fetched
        try {
            // Assuming PetService.getUserPets exists and works like TaskService
            const response = await PetService.getUserPets(userId);
            setPets(response.data || []);
            if (response.data && response.data.length > 0) {
                setSelectedPetId(response.data[0].petID); // Default to first pet
            } else {
                 setSelectedPetId(''); // No pets found
            }
        } catch (err) {
            setError('Failed to fetch pets.');
            console.error(err);
        } finally {
            const elapsedTime = Date.now() - petLoadStartTime.current;
            const remainingTime = MIN_LOADING_TIME - elapsedTime;
            if (remainingTime > 0) {
                setTimeout(() => setIsPetsLoading(false), remainingTime);
            } else {
                setIsPetsLoading(false);
            }
        }
    }, [userId]);


    const fetchTasks = useCallback(async () => {
        if (!userId || !selectedPetId) {
            setTasks([]); // Clear tasks if no user or pet selected
            setIsTasksLoading(false); // Ensure loading stops if no fetch happens
            return;
        };
        taskLoadStartTime.current = Date.now(); // Record start time
        setIsTasksLoading(true);
        setError(null);
        try {
            const response = await TaskService.getTasks(userId, selectedPetId);
            // Sort tasks by scheduledDateTime, newest first for display might be better
            const sortedTasks = (response.data || []).sort((a, b) =>
                new Date(b.scheduledDateTime._seconds * 1000) - new Date(a.scheduledDateTime._seconds * 1000)
            );
            setTasks(sortedTasks);
        } catch (err) {
            setError('Failed to fetch tasks. Please ensure you are logged in.');
            console.error(err);
            if (err.response && err.response.status === 403) {
                setError('Authorization error. Please log in again.');
                // Optionally redirect to login: navigate('/login');
            }
        } finally {
            const elapsedTime = Date.now() - taskLoadStartTime.current;
            const remainingTime = MIN_LOADING_TIME - elapsedTime;
            if (remainingTime > 0) {
                setTimeout(() => setIsTasksLoading(false), remainingTime);
            } else {
                setIsTasksLoading(false);
            }
        }
    }, [userId, selectedPetId]);

    useEffect(() => {
        fetchPets();
    }, [fetchPets]);

    useEffect(() => {
        fetchTasks();
    }, [fetchTasks]); // Re-fetch when selectedPetId changes

    const handlePetChange = (event) => {
        setSelectedPetId(event.target.value);
    };

    const handleAddNewClick = () => {
        setEditingTask({ // Initialize with defaults for a new task
            taskType: 'OTHER',
            description: '',
            scheduledDateTime: new Date(),
            status: 'PENDING',
            recurrenceRule: 'NONE',
            notes: ''
        });
        setShowForm(true);
    };

    const handleEditClick = (task) => {
        // Convert Firestore Timestamp to JS Date for the form
        const jsDate = task.scheduledDateTime && task.scheduledDateTime._seconds
            ? new Date(task.scheduledDateTime._seconds * 1000)
            : new Date(); // Default to now if invalid
        setEditingTask({ ...task, scheduledDateTime: jsDate });
        setShowForm(true);
    };

    const handleDeleteClick = async (taskId) => {
        if (window.confirm('Are you sure you want to delete this task?')) {
            // No need to set loading here, fetchTasks will handle it
            // setIsLoading(true); 
            try {
                await TaskService.deleteTask(userId, selectedPetId, taskId);
                fetchTasks(); // Refresh list (will set isTasksLoading)
            } catch (err) {
                setError('Failed to delete task.');
                console.error(err);
                // No need to set loading false here, fetchTasks finally block handles it
                // setIsLoading(false); 
            }
        }
    };

    const handleFormSubmit = async (event) => {
        event.preventDefault();
        if (!editingTask || !userId || !selectedPetId) return;

        // No need to set loading here, fetchTasks will handle it after success
        // setIsLoading(true); 
        setError(null);

        // Prepare data for backend (convert date back if needed, ensure enums are strings)
        const taskData = {
            ...editingTask,
            // Ensure date is in a format backend expects (ISO string)
            scheduledDateTime: editingTask.scheduledDateTime instanceof Date
                ? editingTask.scheduledDateTime.toISOString()
                : editingTask.scheduledDateTime, // Assume it's already a string/correct format if not Date
            taskType: editingTask.taskType, // Should be the enum string key
            status: editingTask.status,     // Should be the enum string key
        };
        // Remove IDs that shouldn't be sent on create/update if they are empty/null
        delete taskData.taskID;
        delete taskData.userID;
        delete taskData.petID;
        delete taskData.completedAt; // Let backend handle this based on status

        try {
            if (editingTask.taskID) { // If taskID exists, it's an update
                await TaskService.updateTask(userId, selectedPetId, editingTask.taskID, taskData);
            } else { // Otherwise, it's a new task
                await TaskService.addTask(userId, selectedPetId, taskData);
            }
            setShowForm(false);
            setEditingTask(null);
            fetchTasks(); // Refresh list (will set isTasksLoading)
        } catch (err) {
            setError(`Failed to ${editingTask.taskID ? 'update' : 'add'} task.`);
            console.error(err);
            // No need to set loading false here, fetchTasks finally block handles it
            // setIsLoading(false); 
        }
    };

    const handleFormChange = (event) => {
        const { name, value, type } = event.target;
        setEditingTask(prev => ({
            ...prev,
            [name]: type === 'datetime-local' ? new Date(value) : value
        }));
    };

    const handleCancelEdit = () => {
        setShowForm(false);
        setEditingTask(null);
    };

    // Helper to format Firestore Timestamp
    const formatTimestamp = (timestamp) => {
        if (!timestamp || !timestamp._seconds) return 'N/A';
        try {
            return new Date(timestamp._seconds * 1000).toLocaleString();
        } catch (e) {
            return 'Invalid Date';
        }
    };

    // Helper to format Date for datetime-local input
    const formatDateForInput = (date) => {
        if (!date || !(date instanceof Date)) return '';
        // Format: YYYY-MM-DDTHH:mm
        const pad = (num) => num.toString().padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
    };


    return (
        <div className="container mx-auto p-6 font-['Baloo']" style={{ color: "#042C3C" }}>
            <h1 className="text-2xl md:text-3xl font-bold mb-4" style={{ color: "#042C3C" }}>Scheduled Tasks</h1>

            {/* Pet Selector - Conditional Rendering */}
            <div className="mb-4">
                <label htmlFor="pet-select" className="mr-2 font-semibold">Select Pet:</label>
                {isPetsLoading ? (
                    <span className="text-sm text-gray-500 italic">Loading pets...</span>
                ) : pets.length > 0 ? (
                    <select
                        id="pet-select"
                        value={selectedPetId}
                        onChange={handlePetChange}
                        disabled={isTasksLoading} // Disable only when tasks for the selected pet are loading
                        className="p-2 border rounded"
                        style={{ borderColor: "#8A973F" }}
                    >
                        {/* Default prompt option */}
                        {selectedPetId === '' && <option value="" disabled>-- Select a Pet --</option>}
                        {pets.map(pet => (
                            <option key={pet.petID} value={pet.petID}>{pet.name}</option>
                        ))}
                    </select>
                ) : !isPetsLoading && pets.length === 0 ? (
                    <span className="text-sm text-gray-500 italic">No pets found.</span>
                ) : null /* Handle other states like error if needed */}
            </div>


            {error && <p className="text-red-500 bg-red-100 p-3 rounded mb-4">{error}</p>}

            <button
                onClick={handleAddNewClick}
                disabled={isPetsLoading || isTasksLoading || !selectedPetId}
                className="bg-[#EA6C7B] text-white px-4 py-2 rounded hover:bg-opacity-80 transition duration-200 mb-4 disabled:opacity-50"
            >
                Add New Task
            </button>

            {/* Add/Edit Form (Modal would be better) */}
            {showForm && editingTask && (
                <form onSubmit={handleFormSubmit} className="mb-6 p-4 border rounded bg-gray-50" style={{ borderColor: "#8A973F" }}>
                    <h2 className="text-xl font-semibold mb-3">{editingTask.taskID ? 'Edit Task' : 'Add New Task'}</h2>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label htmlFor="taskType" className="block mb-1 font-medium">Task Type:</label>
                            <select id="taskType" name="taskType" value={editingTask.taskType} onChange={handleFormChange} required className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }}>
                                {Object.entries(TaskType).map(([key, value]) => (
                                    <option key={key} value={key}>{value}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label htmlFor="description" className="block mb-1 font-medium">Description:</label>
                            <input type="text" id="description" name="description" value={editingTask.description} onChange={handleFormChange} required className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }} />
                        </div>
                        <div>
                            <label htmlFor="scheduledDateTime" className="block mb-1 font-medium">Scheduled Date & Time:</label>
                            <input type="datetime-local" id="scheduledDateTime" name="scheduledDateTime" value={formatDateForInput(editingTask.scheduledDateTime)} onChange={handleFormChange} required className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }} />
                        </div>
                         <div>
                            <label htmlFor="status" className="block mb-1 font-medium">Status:</label>
                            <select id="status" name="status" value={editingTask.status} onChange={handleFormChange} required className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }}>
                                {Object.entries(TaskStatus).map(([key, value]) => (
                                    <option key={key} value={key}>{value}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label htmlFor="recurrenceRule" className="block mb-1 font-medium">Recurrence:</label>
                            <select id="recurrenceRule" name="recurrenceRule" value={editingTask.recurrenceRule} onChange={handleFormChange} className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }}>
                                 {Object.entries(RecurrenceRule).map(([key, value]) => (
                                    <option key={key} value={key}>{value}</option>
                                ))}
                            </select>
                        </div>
                        <div className="md:col-span-2">
                            <label htmlFor="notes" className="block mb-1 font-medium">Notes:</label>
                            <textarea id="notes" name="notes" value={editingTask.notes} onChange={handleFormChange} rows="3" className="w-full p-2 border rounded" style={{ borderColor: "#ccc" }}></textarea>
                        </div>
                    </div>
                    <div className="mt-4 flex gap-3">
                        <button type="submit" disabled={isTasksLoading} className="bg-[#8A973F] text-white px-4 py-2 rounded hover:bg-opacity-80 transition duration-200 disabled:opacity-50">
                            {isTasksLoading ? 'Saving...' : (editingTask.taskID ? 'Update Task' : 'Add Task')}
                        </button>
                        <button type="button" onClick={handleCancelEdit} disabled={isTasksLoading} className="bg-gray-400 text-white px-4 py-2 rounded hover:bg-opacity-80 transition duration-200">
                            Cancel
                        </button>
                    </div>
                </form>
            )}


            {/* Task List */}
            {isTasksLoading && <p>Loading tasks...</p>}
            {!isPetsLoading && !isTasksLoading && tasks.length === 0 && !error && selectedPetId && <p>No tasks found for the selected pet.</p>}
            {!isPetsLoading && !isTasksLoading && tasks.length === 0 && !error && !selectedPetId && pets.length > 0 && <p>Please select a pet to view tasks.</p>}
            {/* Removed the condition for !isLoading && tasks.length > 0, now it's the default case when not loading and tasks exist */}
            {!isPetsLoading && !isTasksLoading && tasks.length > 0 && (
                <div className="overflow-x-auto">
                    <table className="min-w-full bg-white border" style={{ borderColor: "#8A973F" }}>
                        <thead className="bg-[#042C3C] text-white">
                            <tr>
                                <th className="py-2 px-4 text-left">Type</th>
                                <th className="py-2 px-4 text-left">Description</th>
                                <th className="py-2 px-4 text-left">Scheduled Time</th>
                                <th className="py-2 px-4 text-left">Status</th>
                                <th className="py-2 px-4 text-left">Recurrence</th>
                                <th className="py-2 px-4 text-left">Notes</th>
                                <th className="py-2 px-4 text-left">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tasks.map(task => (
                                <tr key={task.taskID} className="border-b" style={{ borderColor: "#E0E0E0" }}>
                                    <td className="py-2 px-4">{TaskType[task.taskType] || task.taskType}</td>
                                    <td className="py-2 px-4">{task.description}</td>
                                    <td className="py-2 px-4">{formatTimestamp(task.scheduledDateTime)}</td>
                                    <td className="py-2 px-4">{TaskStatus[task.status] || task.status}</td>
                                    <td className="py-2 px-4">{RecurrenceRule[task.recurrenceRule] || task.recurrenceRule}</td>
                                    <td className="py-2 px-4">{task.notes || '-'}</td>
                                    <td className="py-2 px-4 whitespace-nowrap">
                                        <button
                                            onClick={() => handleEditClick(task)}
                                            className="text-blue-600 hover:text-blue-800 mr-2"
                                            disabled={isTasksLoading} // Disable during task loading
                                        >
                                            Edit
                                        </button>
                                        <button
                                            onClick={() => handleDeleteClick(task.taskID)}
                                            className="text-red-600 hover:text-red-800"
                                            disabled={isTasksLoading} // Disable during task loading
                                        >
                                            Delete
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

export default ScheduledTasks;