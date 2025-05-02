import axios from 'axios';
import AuthService from './AuthService';

const API_URL = '/api/users/'; // Base URL for user-related endpoints

const getAuthHeaders = () => {
    const token = AuthService.getToken();
    if (token) {
        return { Authorization: `Bearer ${token}` };
    }
    return {};
};

/**
 * Fetches all pets belonging to a specific user.
 * Assumes the backend endpoint is /api/users/{userId}/pets
 * @param {string} userId - The ID of the user whose pets to fetch.
 * @returns {Promise<AxiosResponse<any>>} Axios promise containing the list of pets.
 */
const getUserPets = (userId) => {
    if (!userId) {
        return Promise.reject(new Error('User ID is required to fetch pets.'));
    }
    return axios.get(`${API_URL}${userId}/pets`, { headers: getAuthHeaders() });
};

// Add other pet-related API calls here as needed (e.g., addPet, updatePet, deletePet)
// Example:
// const addPet = (userId, petData) => {
//     return axios.post(`${API_URL}${userId}/pets`, petData, { headers: getAuthHeaders() });
// };

const PetService = {
    getUserPets,
    // addPet, // Uncomment if you implement addPet
};

export default PetService;