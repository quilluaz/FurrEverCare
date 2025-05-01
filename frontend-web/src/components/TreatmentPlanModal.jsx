import React, { useEffect, useState } from 'react';
import axios from 'axios';
import AuthService from '../config/AuthService';

// Update to use the correct API base URL
const API_BASE = 'http://localhost:8080/api/treatment-plans';

// Rename this component to avoid confusion
const TreatmentPlanList = () => {
  const [plans, setPlans] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [formData, setFormData] = useState({
    petName: '',
    treatmentType: '',
    startDate: '',
    endDate: '',
  });
  const [editId, setEditId] = useState(null);
  const [error, setError] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Improved authentication check
  useEffect(() => {
    // Check if token exists and is valid
    const token = localStorage.getItem('jwtToken');
    const user = AuthService.getUser();
    
    if (token && user) {
      setIsAuthenticated(true);
      console.log("User authenticated:", user);
    } else {
      setIsAuthenticated(false);
      setError('You must be logged in to view treatment plans');
      console.log("Authentication failed: No valid token or user");
    }
  }, []);

  // Get the current user and token
  const user = AuthService.getUser();
  const userID = user?.userId;
  const token = localStorage.getItem('jwtToken');

  const fetchPlans = async () => {
    try {
      // Check if user is authenticated
      if (!token) {
        setError('You must be logged in to view treatment plans');
        console.log("No token available for API request");
        return;
      }

      console.log("Fetching plans with token:", token.substring(0, 10) + "...");
      
      // Include the JWT token in the request headers
      const res = await axios.get(API_BASE, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      console.log("API response:", res.data);
      
      if (Array.isArray(res.data)) {
        setPlans(res.data);
      } else {
        console.error("API returned non-array data:", res.data);
        setPlans([]);
      }
      
      setError(null);
    } catch (err) {
      console.error('Failed to fetch treatment plans:', err);
      if (err.response) {
        console.log("Error response:", err.response.status, err.response.data);
        
        if (err.response.status === 403) {
          setError('You do not have permission to view treatment plans');
        } else if (err.response.status === 401) {
          setError('Your session has expired. Please log in again.');
          localStorage.removeItem('jwtToken'); // Clear invalid token
        } else {
          setError(`Failed to load treatment plans: ${err.response.status}`);
        }
      } else {
        setError('Failed to connect to the server');
      }
    }
  };

  useEffect(() => {
    if (isAuthenticated) {
      fetchPlans();
    }
  }, [isAuthenticated]); // Only fetch when authenticated

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Check if user is authenticated
      if (!token) {
        setError('You must be logged in to save treatment plans');
        return;
      }

      // Include user ID and pet ID if needed by your backend
      const dataToSubmit = {
        ...formData,
        userID: userID, // Add user ID if required by your API
      };

      if (editId) {
        await axios.put(`${API_BASE}/${editId}`, dataToSubmit, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
      } else {
        await axios.post(API_BASE, dataToSubmit, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
      }
      fetchPlans();
      closeModal();
    } catch (err) {
      console.error('Error saving treatment plan:', err);
      if (err.response && err.response.status === 403) {
        setError('You do not have permission to save treatment plans');
      } else {
        setError('Failed to save treatment plan');
      }
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this treatment plan?')) return;
    try {
      // Check if user is authenticated
      if (!token) {
        setError('You must be logged in to delete treatment plans');
        return;
      }

      await axios.delete(`${API_BASE}/${id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      fetchPlans();
    } catch (err) {
      console.error('Error deleting treatment plan:', err);
      if (err.response && err.response.status === 403) {
        setError('You do not have permission to delete treatment plans');
      } else {
        setError('Failed to delete treatment plan');
      }
    }
  };

  const openModal = (plan = null) => {
    if (plan) {
      setFormData(plan);
      setEditId(plan.id);
    } else {
      setFormData({ petName: '', treatmentType: '', startDate: '', endDate: '' });
      setEditId(null);
    }
    setIsOpen(true);
  };

  const closeModal = () => setIsOpen(false);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Treatment Plans</h1>
      
      {isAuthenticated ? (
        <button 
          onClick={() => openModal()} 
          className="bg-green-500 text-white px-4 py-2 rounded mb-4"
        >
          Add Treatment
        </button>
      ) : (
        <button 
          onClick={() => window.location.href = '/about-us'} 
          className="bg-blue-500 text-white px-4 py-2 rounded mb-4"
        >
          Log in to manage treatment plans
        </button>
      )}

      {/* Display error message if there is one */}
      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <div className="grid gap-4">
        {plans.map(plan => (
          <div key={plan.id} className="border p-4 rounded shadow-md">
            <h2 className="text-lg font-semibold">{plan.petName}</h2>
            <p>Treatment: {plan.treatmentType}</p>
            <p>Start: {plan.startDate}</p>
            <p>End: {plan.endDate}</p>
            <div className="mt-2 space-x-2">
              <button onClick={() => openModal(plan)} className="text-blue-500">Edit</button>
              <button onClick={() => handleDelete(plan.id)} className="text-red-500">Delete</button>
            </div>
          </div>
        ))}
      </div>

      {isOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="bg-white p-6 rounded-xl w-full max-w-md shadow-xl">
            <h2 className="text-xl font-bold mb-4">{editId ? 'Edit' : 'Add'} Treatment</h2>
            <form onSubmit={handleSubmit} className="grid gap-4">
              <input type="text" placeholder="Pet Name" value={formData.petName} onChange={(e) => setFormData({ ...formData, petName: e.target.value })} required className="border p-2 rounded" />
              <input type="text" placeholder="Treatment Type" value={formData.treatmentType} onChange={(e) => setFormData({ ...formData, treatmentType: e.target.value })} required className="border p-2 rounded" />
              <input type="date" value={formData.startDate} onChange={(e) => setFormData({ ...formData, startDate: e.target.value })} required className="border p-2 rounded" />
              <input type="date" value={formData.endDate} onChange={(e) => setFormData({ ...formData, endDate: e.target.value })} required className="border p-2 rounded" />
              <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded">Save</button>
            </form>
            <button onClick={closeModal} className="text-gray-500 mt-4 text-sm">Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
};

// Export the TreatmentPlanModal component for use in TreatmentPlanPage
export const TreatmentPlanModal = ({ visible, onCancel, onSubmit, initialValues, petID, userID }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    goal: '',
    startDate: '',
    endDate: '',
    status: 'ACTIVE',
    progressPercentage: 0,
    notes: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (initialValues) {
      // Convert Firestore timestamps to date strings if needed
      const startDate = initialValues.startDate ? 
        (typeof initialValues.startDate.toDate === 'function' ? 
          initialValues.startDate.toDate().toISOString().split('T')[0] : initialValues.startDate) : '';
      
      const endDate = initialValues.endDate ? 
        (typeof initialValues.endDate.toDate === 'function' ? 
          initialValues.endDate.toDate().toISOString().split('T')[0] : initialValues.endDate) : '';
      
      setFormData({
        name: initialValues.name || '',
        description: initialValues.description || '',
        goal: initialValues.goal || '',
        startDate: startDate,
        endDate: endDate,
        status: initialValues.status || 'ACTIVE',
        progressPercentage: initialValues.progressPercentage || 0,
        notes: initialValues.notes || ''
      });
    } else {
      // Reset form when adding new plan
      setFormData({
        name: '',
        description: '',
        goal: '',
        startDate: '',
        endDate: '',
        status: 'ACTIVE',
        progressPercentage: 0,
        notes: ''
      });
    }
  }, [initialValues]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Call the onSubmit function passed from parent component
      await onSubmit(formData);
      onCancel(); // Close modal on success
    } catch (err) {
      console.error('Error saving treatment plan:', err);
      setError('Failed to save treatment plan. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  if (!visible) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="bg-white p-6 rounded-xl w-full max-w-md shadow-xl">
        <h2 className="text-xl font-bold mb-4">{initialValues ? 'Edit' : 'Add'} Treatment Plan</h2>
        
        {error && <div className="text-red-500 mb-4">{error}</div>}
        
        <form onSubmit={handleSubmit} className="grid gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Name</label>
            <input 
              type="text" 
              name="name"
              value={formData.name} 
              onChange={handleChange} 
              required 
              className="w-full border p-2 rounded" 
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
            <textarea 
              name="description"
              value={formData.description} 
              onChange={handleChange} 
              className="w-full border p-2 rounded" 
              rows="3"
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Goal</label>
            <input 
              type="text" 
              name="goal"
              value={formData.goal} 
              onChange={handleChange} 
              className="w-full border p-2 rounded" 
            />
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
              <input 
                type="date" 
                name="startDate"
                value={formData.startDate} 
                onChange={handleChange} 
                required 
                className="w-full border p-2 rounded" 
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">End Date</label>
              <input 
                type="date" 
                name="endDate"
                value={formData.endDate} 
                onChange={handleChange} 
                className="w-full border p-2 rounded" 
              />
            </div>
          </div>
          
          {initialValues && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
              <select 
                name="status"
                value={formData.status} 
                onChange={handleChange} 
                className="w-full border p-2 rounded"
              >
                <option value="ACTIVE">Active</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
          )}
          
          {initialValues && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Progress: {formData.progressPercentage}%
              </label>
              <input 
                type="range" 
                name="progressPercentage"
                min="0" 
                max="100" 
                value={formData.progressPercentage} 
                onChange={handleChange} 
                className="w-full" 
              />
            </div>
          )}
          
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Notes</label>
            <textarea 
              name="notes"
              value={formData.notes} 
              onChange={handleChange} 
              className="w-full border p-2 rounded" 
              rows="2"
            />
          </div>
          
          <div className="flex justify-end gap-2 mt-4">
            <button 
              type="button" 
              onClick={onCancel} 
              className="px-4 py-2 text-gray-600 hover:text-gray-800"
            >
              Cancel
            </button>
            <button 
              type="submit" 
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
              disabled={loading}
            >
              {loading ? 'Saving...' : 'Save'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// Export the TreatmentPlanList as default
export default TreatmentPlanList;

