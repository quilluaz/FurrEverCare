import axios from 'axios';
import AuthService from './AuthService';

const API_URL = 'https://furrevercare-deploy-8.onrender.com/api/users/'; // Base URL, adjust if needed

const getAuthHeaders = () => {
    const token = AuthService.getToken();
    if (token) {
        return { Authorization: `Bearer ${token}` };
    }
    return {};
};

const getTasks = (userId, petId) => {
    return axios.get(`${API_URL}${userId}/pets/${petId}/scheduledTasks`, { headers: getAuthHeaders() });
};

const getTaskById = (userId, petId, taskId) => {
    return axios.get(`${API_URL}${userId}/pets/${petId}/scheduledTasks/${taskId}`, { headers: getAuthHeaders() });
};

const addTask = (userId, petId, taskData) => {
    // Convert scheduledDateTime to ISO string if it's a Date object
    if (taskData.scheduledDateTime instanceof Date) {
        taskData.scheduledDateTime = taskData.scheduledDateTime.toISOString();
    }
    // Ensure status is set if not provided, backend defaults to PENDING
    if (!taskData.status) {
        taskData.status = 'PENDING';
    }
    return axios.post(`${API_URL}${userId}/pets/${petId}/scheduledTasks`, taskData, { headers: getAuthHeaders() });
};

const updateTask = (userId, petId, taskId, taskData) => {
    // Convert scheduledDateTime to ISO string if it's a Date object
    if (taskData.scheduledDateTime instanceof Date) {
        taskData.scheduledDateTime = taskData.scheduledDateTime.toISOString();
    }
    return axios.put(`${API_URL}${userId}/pets/${petId}/scheduledTasks/${taskId}`, taskData, { headers: getAuthHeaders() });
};

const deleteTask = (userId, petId, taskId) => {
    return axios.delete(`${API_URL}${userId}/pets/${petId}/scheduledTasks/${taskId}`, { headers: getAuthHeaders() });
};

const updateTaskStatus = (userId, petId, taskId, status) => {
    return axios.patch(`${API_URL}${userId}/pets/${petId}/scheduledTasks/${taskId}/status?status=${status}`, {}, { headers: getAuthHeaders() });
};


const TaskService = {
    getTasks,
    getTaskById,
    addTask,
    updateTask,
    deleteTask,
    updateTaskStatus,
};

export default TaskService;