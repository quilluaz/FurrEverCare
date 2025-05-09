import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // Import useNavigate
import UserNavBar from "../components/UserNavBar";
import { Plus, Edit, Trash2, Loader } from "lucide-react";
import axios from "axios";
import AuthService from "../config/AuthService";
import AddTreatmentPlanModal from "../components/AddTreatmentPlanModal";
import DeleteConfTreatmentPlanModal from "../components/DeleteConfTreatmentPlanModal";
import EditConfTreatmentPlanModal from "../components/EditConfTreatmentPlanModal";
import CreateConfTreatmentPlanModal from "../components/CreateConfTreatmentPlanModal";

export default function TreatmentPlans() {
  const [plans, setPlans] = useState([]);
  const [userPets, setUserPets] = useState([]); // State for user's pets
  const [selectedPetId, setSelectedPetId] = useState(""); // State for selected pet ID
  const [showAddPlanModal, setShowAddPlanModal] = useState(false);
  const [showEditPlanModal, setShowEditPlanModal] = useState(false);
  const [selectedPlan, setSelectedPlan] = useState(null);
  const [loading, setLoading] = useState(true); // Start loading initially
  const [loadingPlanIds, setLoadingPlanIds] = useState([]);
  const [error, setError] = useState(null);
  const [showDeleteConfModal, setShowDeleteConfModal] = useState(false);
  const [showCreateConfModal, setShowCreateConfModal] = useState(false);
  const [showEditConfModal, setShowEditConfModal] = useState(false);
  const [isEditConfirmation, setIsEditConfirmation] = useState(true);
  const [planToDelete, setPlanToDelete] = useState(null);
  const [planToEdit, setPlanToEdit] = useState(null);
  const [lastAddedPlan, setLastAddedPlan] = useState(null);
  const [lastEditedPlan, setLastEditedPlan] = useState(null);

  const navigate = useNavigate(); // Initialize useNavigate

  const user = AuthService.getUser();
  const userID = user?.userId || null;
  //const API_BASE_URL = "https://furrevercare-deploy-8.onrender.com/api/users";
  const API_BASE_URL = "http://localhost:8080/api/users";
  
  // Get initial petID from URL query parameter (optional, can be overridden by dropdown)
  const urlParams = new URLSearchParams(window.location.search);
  const initialPetID = urlParams.get("petId");

  // --- DEBUGGING LOGS ---
  console.log("TreatmentPlans Mount: User:", user);
  console.log("TreatmentPlans Mount: UserID:", userID);
  console.log("TreatmentPlans Mount: Initial PetID from URL:", initialPetID);
  // --- END DEBUGGING LOGS ---

  useEffect(() => {
    AuthService.init(); // Ensure Axios interceptor is set up
  }, []);

  // Effect to fetch user's pets
  useEffect(() => {
    const fetchUserPets = async () => {
      if (!userID) {
        setError("Please log in to manage treatment plans.");
        setLoading(false);
        return;
      }
      setLoading(true); // Start loading
      setError(null);
      try {
        console.log(`Fetching pets from: ${API_BASE_URL}/${userID}/pets`);
        const res = await axios.get(`${API_BASE_URL}/${userID}/pets`);
        console.log("Pets API Response:", res);
        const fetchedPets = Array.isArray(res.data) ? res.data : [];
        setUserPets(fetchedPets);

        // Determine the pet ID to select
        let petToSelect = "";
        if (
          initialPetID &&
          fetchedPets.some((pet) => pet.petID === initialPetID)
        ) {
          petToSelect = initialPetID; // Use initial ID if valid
        } else if (fetchedPets.length > 0) {
          petToSelect = fetchedPets[0].petID; // Default to first pet
        }

        setSelectedPetId(petToSelect); // Set the selected pet ID

        // If no pet is selected after this (e.g., no pets found), stop loading
        if (!petToSelect) {
          setLoading(false);
          if (fetchedPets.length === 0) {
            setError("No pets found. Add a pet profile first.");
          }
        }
        // Note: Loading will be set to false in the fetchPlans effect if a pet is selected
      } catch (err) {
        console.error("Failed to fetch user pets:", err.response || err);
        setError("Failed to load your pets. Please try again.");
        setUserPets([]);
        setSelectedPetId("");
        setLoading(false); // Stop loading on error
        if (err.response?.status === 403) {
          AuthService.clearAuth();
          window.location.href = "/pawpedia";
        }
      }
    };

    fetchUserPets();
  }, [userID, initialPetID]); // Rerun if userID changes (initialPetID is stable after mount)

  const fetchPlans = async (currentPetId) => {
    // Accept petId as argument
    // --- DEBUGGING LOGS ---
    console.log(
      "fetchPlans called. UserID:",
      userID,
      "Selected PetID:",
      currentPetId
    );
    // --- END DEBUGGING LOGS ---

    if (!userID || !currentPetId) {
      // Clear plans if no pet is selected or user not logged in
      setPlans([]);
      // Set appropriate error/message if needed, but avoid overriding pet loading errors
      if (!userID) {
        setError("Please log in to view treatment plans.");
      } else if (userPets.length > 0 && !currentPetId) {
        setError("Please select a pet to view treatment plans.");
      }
      // No error message if pets are still loading or if there are no pets
      // --- DEBUGGING LOGS ---
      console.warn("fetchPlans aborted: Missing userID or selectedPetId.");
      // --- END DEBUGGING LOGS ---
      setLoading(false); // Ensure loading is stopped if we abort
      return;
    }
    setLoading(true); // Start loading plans
    setError(null); // Clear previous errors before fetching
    try {
      console.log(
        `Fetching plans from: ${API_BASE_URL}/${userID}/pets/${currentPetId}/treatmentPlans`
      ); // Use selectedPetId
      const res = await axios.get(
        `${API_BASE_URL}/${userID}/pets/${currentPetId}/treatmentPlans`
      );
      console.log("API Response:", res); // Log the full response
      // Ensure data is an array, otherwise default to empty array
      setPlans(Array.isArray(res.data) ? res.data : []);
      setError(null);
    } catch (err) {
      console.error("Failed to fetch treatment plans:", err.response || err); // Log the full error
      if (err.response?.status === 403) {
        setError("Authentication error (403). Redirecting to login.");
        AuthService.clearAuth();
        window.location.href = "/login";
      } else {
        setError(
          `Failed to fetch treatment plans. Status: ${
            err.response?.status || "Network Error"
          }`
        );
      }
      setPlans([]); // Clear plans on error
    } finally {
      setLoading(false); // Stop loading plans
    }
  };

  // Effect to fetch plans when selectedPetId changes
  useEffect(() => {
    // Fetch plans only when both userID and a selectedPetId are available
    if (userID && selectedPetId) {
      fetchPlans(selectedPetId); // Pass selectedPetId to fetchPlans
    } else {
      // Clear plans if userID or selectedPetId is missing
      setPlans([]);
      // Set loading to false if we are not fetching (unless pets are still loading)
      if (!loading) setLoading(false); // Avoid overriding initial pet loading state
      // Set appropriate messages based on state
      if (!userID) {
        setError("Please log in to view treatment plans.");
      } else if (userPets.length > 0 && !selectedPetId && !loading) {
        // This case might be brief if a pet is auto-selected
        setError("Please select a pet.");
      } else if (userPets.length === 0 && !loading) {
        // If done loading pets and there are none
        setError("No pets found. Add a pet profile first.");
      }
    }
    // Update URL query parameter when selectedPetId changes
    if (selectedPetId) {
      navigate(`?petId=${selectedPetId}`, { replace: true });
    } else {
      // Optionally clear the query param if no pet is selected
      // navigate(``, { replace: true });
    }
  }, [userID, selectedPetId, navigate]); // Rerun when userID or selectedPetId changes

  // Updated to accept planData and accessToken
  const handleAddPlan = async (planData, accessToken) => {
    if (!userID || !selectedPetId) {
      // Use selectedPetId
      setError("Please log in and select a pet to add a treatment plan.");
      return;
    }
    const tempId = `temp-${Date.now()}`;
    setShowAddPlanModal(false);

    // No need to parse from FormData anymore
    const tempPlan = {
      ...planData,
      planID: tempId,
      progressPercentage: planData.progressPercentage || 0,
    };
    setPlans((p) => [...p, tempPlan]);
    setLoadingPlanIds((ids) => [...ids, tempId]);

    try {
      // Use planData directly and add Authorization header
      const res = await axios.post(
        `${API_BASE_URL}/${userID}/pets/${selectedPetId}/treatmentPlans`,
        planData, // Send the planData object directly
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      const realId = res.data; // Assuming backend returns the ID of the created plan
      setPlans((p) =>
        p.map((pl) =>
          pl.planID === tempId ? { ...tempPlan, planID: realId } : pl
        )
      );
      setLoadingPlanIds((ids) => ids.filter((id) => id !== tempId));
      setLastAddedPlan({ ...tempPlan, planID: realId });
      setShowCreateConfModal(true);
    } catch (err) {
      console.error("Failed to add treatment plan:", err.response || err);
      setPlans((p) => p.filter((pl) => pl.planID !== tempId)); // Remove temporary plan on error
      setLoadingPlanIds((ids) => ids.filter((id) => id !== tempId));
      // Keep the 403 handling
      if (err.response?.status === 403 || err.response?.status === 401) {
        alert("Authentication failed. Please log in again.");
        AuthService.clearAuth();
        window.location.href = "/login";
      }
      setError("Failed to add treatment plan. Please try again.");
    }
  };

  const confirmEditPlan = (plan) => {
    setPlanToEdit(plan);
    setIsEditConfirmation(true);
    setShowEditConfModal(true);
  };

  const proceedWithEdit = () => {
    setShowEditConfModal(false);
    setSelectedPlan(planToEdit);
    setShowEditPlanModal(true); // Open the actual edit modal
  };

  // Updated to accept updatedPlanData and accessToken
  const handleEditPlan = async (updatedPlanData, accessToken) => {
    // Ensure selectedPlan (which holds the original ID) is available
    if (!userID || !selectedPetId || !selectedPlan || !selectedPlan.planID) {
      console.error("handleEditPlan Error: Missing data", { userID, selectedPetId, selectedPlan });
      setError("User, pet, or plan information is missing. Cannot update.");
      setShowEditPlanModal(false); // Close modal even on error
      setSelectedPlan(null); // Clear selected plan
      return; // Stop execution
    }

    const planId = selectedPlan.planID; // Get ID from the state variable
    console.log(`handleEditPlan: Submitting edit for plan ID: ${planId}`, updatedPlanData);

    setShowEditPlanModal(false);
    setLoadingPlanIds((ids) => [...ids, planId]);

    const originalPlans = [...plans];
    // Optimistic UI update: Merge the updated data with the existing plan
    setPlans((p) =>
      p.map((pl) =>
        pl.planID === planId ? { ...pl, ...updatedPlanData } : pl // Keep existing fields, overwrite with updates
      )
    );

    try {
      // Ensure updatedPlanData is exactly what the backend expects for a PUT request
      // The modal should format the date correctly (e.g., ISO string)
      await axios.put(
        `${API_BASE_URL}/${userID}/pets/${selectedPetId}/treatmentPlans/${planId}`,
        updatedPlanData, // Send the data prepared by the modal
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      // --- Success Handling ---
      console.log("handleEditPlan: Edit successful for plan ID:", planId);
      setLoadingPlanIds((ids) => ids.filter((id) => id !== planId));

      // Prepare data for the success confirmation modal (Optional, can be removed if not needed elsewhere)
      // const finalUpdatedPlan = { ...selectedPlan, ...updatedPlanData };
      // setLastEditedPlan(finalUpdatedPlan);

      // REMOVED: These lines showed the success confirmation modal
      // setIsEditConfirmation(false); // Set mode to "Success"
      // setShowEditConfModal(true); // Show the success confirmation modal

      setSelectedPlan(null); // Clear selected plan state after successful edit
      // Optional: Uncomment to fetch fresh data immediately after edit
      // fetchPlans(selectedPetId);

    } catch (err) {
      console.error("handleEditPlan Error: Failed to update treatment plan:", err.response || err);
      setPlans(originalPlans); // Revert optimistic update on error
      setLoadingPlanIds((ids) => ids.filter((id) => id !== planId));
      setSelectedPlan(null); // Clear selected plan state on error

      if (err.response?.status === 403 || err.response?.status === 401) {
        alert("Authentication failed. Please log in again.");
        AuthService.clearAuth();
        window.location.href = "/login";
      } else {
         // Provide more specific error feedback if possible
         const errorMsg = err.response?.data?.message || err.message || "An unknown error occurred.";
         setError(`Failed to update treatment plan: ${errorMsg}`);
      }
    }
  };

  const confirmDeletePlan = (plan) => {
    setPlanToDelete(plan);
    setShowDeleteConfModal(true);
  };

  const handleDeletePlan = async () => {
    if (!userID || !selectedPetId || !planToDelete) {
      // Use selectedPetId
      setError("Please log in and select a pet/plan to delete.");
      return;
    }
    setShowDeleteConfModal(false);
    const planId = planToDelete.planID;
    setLoadingPlanIds((ids) => [...ids, planId]);

    const originalPlans = [...plans];
    setPlans((p) => p.filter((pl) => pl.planID !== planId));

    try {
      await axios.delete(
        `${API_BASE_URL}/${userID}/pets/${selectedPetId}/treatmentPlans/${planId}`
      ); // Use selectedPetId
      setLoadingPlanIds((ids) => ids.filter((id) => id !== planId));
      setPlanToDelete(null); // Clear after successful deletion
    } catch (err) {
      console.error("Failed to delete treatment plan:", err.response || err);
      setPlans(originalPlans); // Revert optimistic update on error
      setLoadingPlanIds((ids) => ids.filter((id) => id !== planId));
      if (err.response?.status === 403) {
        AuthService.clearAuth();
        window.location.href = "/login";
      }
      setError("Failed to delete treatment plan. Please try again.");
    }
  };

  const isPlanLoading = (id) => loadingPlanIds.includes(id);

  // Format date safely
  const formatDate = (dateString) => {
    if (!dateString) return "N/A";
    try {
      // Assuming dateString is ISO 8601 or similar parseable format
      return new Date(dateString).toLocaleDateString();
    } catch (e) {
      console.error("Error formatting date:", dateString, e);
      return "Invalid Date";
    }
  };

  // Handle pet selection change
  const handlePetChange = (event) => {
    const newPetId = event.target.value;
    setSelectedPetId(newPetId);
    // The useEffect hook watching selectedPetId will trigger fetchPlans
  };

  return (
    <div className="w-full min-h-screen bg-[#FFF7EC] font-['Baloo'] overflow-x-hidden">
      <UserNavBar />
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6 mt-8">
          {/* Heading and Dropdown */}
          <div className="flex items-center gap-3 flex-wrap">
            {" "}
            {/* Added flex-wrap */}
            <h2 className="text-3xl md:text-4xl font-bold text-[#042C3C] flex-shrink-0">
              Treatment Plans
            </h2>
            {userID &&
              userPets.length > 0 && ( // Show dropdown only if logged in and pets are loaded
                <select
                  id="pet-select"
                  value={selectedPetId}
                  onChange={handlePetChange}
                  className="block w-full md:w-auto pl-3 pr-8 py-1.5 text-base border border-gray-300 focus:outline-none focus:ring-[#EA6C7B] focus:border-[#EA6C7B] rounded-md text-sm bg-white text-[#042C3C]"
                  disabled={loading} // Disable while loading pets or plans
                  aria-label="Select a Pet">
                  {/* Add a placeholder if no pet is selected initially */}
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
              )}
            {userID &&
              userPets.length === 0 &&
              !loading &&
              !error && ( // Message if no pets and not loading/error
                <p className="text-sm text-gray-500 ml-2">No pets found.</p>
              )}
          </div>

          {/* Add Button */}
          <button
            className="flex items-center gap-2 px-4 py-1.5 bg-[#EA6C7B] text-white rounded-full text-sm hover:bg-[#EA6C7B]/90 transition disabled:opacity-50 disabled:cursor-not-allowed self-end md:self-center"
            onClick={() => setShowAddPlanModal(true)}
            disabled={
              !userID || !selectedPetId || loading || loadingPlanIds.length > 0
            } // Disable if not logged in, no pet selected, loading, or processing a plan
          >
            <Plus className="h-4 w-4" />
            Add Treatment Plan
          </button>
        </div>
        {error && (
          <p className="text-red-500 mb-4 text-center text-sm font-semibold">
            {error}
          </p>
        )}
        {/* Loading state for initial pet load or plan load */}
        {loading ? ( // Show main loader if loading pets or plans initially
          <div className="flex flex-col items-center py-10">
            <Loader className="h-8 w-8 text-[#EA6C7B] animate-spin" />
            <p className="mt-2 text-gray-500 text-sm">
              {userPets.length === 0
                ? "Loading pets..."
                : "Loading treatment plans..."}
            </p>
          </div>
        ) : !loading && plans.length === 0 && selectedPetId ? ( // Show 'no plans' only if not loading, plans are empty, and a pet IS selected
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              No treatment plans available for{" "}
              {userPets.find((p) => p.petID === selectedPetId)?.name ||
                "this pet"}
              .
            </p>
          </div>
        ) : !selectedPetId && !error && userID && userPets.length > 0 ? ( // Show message if logged in, pets exist, but none selected (and not loading/error)
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please select a pet from the dropdown above to view their
              treatment plans.
            </p>
          </div>
        ) : !userID && !error ? ( // Message if not logged in (and no other error)
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">
              Please log in to manage treatment plans.
            </p>
          </div>
        ) : (
          // Display plans only if not initial loading and plans exist for the selected pet
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {plans.map((plan) => (
              <div
                key={plan.planID}
                className={`bg-white rounded-lg shadow relative overflow-hidden text-sm transition-opacity duration-300 ${
                  isPlanLoading(plan.planID) ? "opacity-50" : "opacity-100"
                }`}>
                {/* ... existing plan card structure ... */}
                {isPlanLoading(plan.planID) && (
                  <div className="absolute inset-0 bg-white/70 flex flex-col items-center justify-center z-10">
                    <Loader className="h-6 w-6 text-[#EA6C7B] animate-spin" />
                    <p className="mt-1 text-gray-500 text-xs">Processing...</p>
                  </div>
                )}
                <div className="p-4">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <h2 className="text-lg font-semibold text-[#042C3C] break-words">
                        {plan.name}
                      </h2>
                      <p className="text-xs text-gray-500 break-words mb-1">
                        {plan.description}
                      </p>
                    </div>
                    <div className="flex gap-1 flex-shrink-0">
                      <button
                        onClick={() => confirmEditPlan(plan)}
                        disabled={
                          !userID ||
                          isPlanLoading(plan.planID) ||
                          !selectedPetId
                        } // Also disable if no pet selected
                        className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded disabled:opacity-50 disabled:cursor-not-allowed"
                        aria-label={`Edit ${plan.name}`}>
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => confirmDeletePlan(plan)}
                        disabled={
                          !userID ||
                          isPlanLoading(plan.planID) ||
                          !selectedPetId
                        } // Also disable if no pet selected
                        className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded disabled:opacity-50 disabled:cursor-not-allowed"
                        aria-label={`Delete ${plan.name}`}>
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                  {/* Progress Bar */}
                  <div className="mb-3">
                    <div className="flex justify-between text-xs text-gray-600 mb-0.5">
                        <span>Progress</span>
                        <span>{plan.progressPercentage !== undefined ? `${plan.progressPercentage}%` : "N/A"}</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
                      <div
                        className="bg-[#68D391] h-2 rounded-full transition-all duration-500 ease-out"
                        style={{ width: `${plan.progressPercentage || 0}%` }}
                      ></div>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-x-4 gap-y-2 mb-3">
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Status
                      </p>
                      <p className="text-xs text-gray-500">
                        {plan.status || "N/A"}
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Progress
                      </p>
                      <p className="text-xs text-gray-500">
                        {plan.progressPercentage !== undefined
                          ? `${plan.progressPercentage}%`
                          : "N/A"}
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Goal
                      </p>
                      <p className="text-xs text-gray-500 break-words">
                        {plan.goal || "None"}
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Start Date
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatDate(plan.startDate)}
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        End Date
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatDate(plan.endDate)}
                      </p>
                    </div>
                    <div className="col-span-2">
                      <p className="text-[10px] font-medium text-[#042C3C]">
                        Notes
                      </p>
                      <p className="text-xs text-gray-500 break-words">
                        {plan.notes || "None"}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* --- Modals --- */}
      {/* Add Plan Modal */}
      <AddTreatmentPlanModal
        isOpen={showAddPlanModal}
        onClose={() => setShowAddPlanModal(false)}
        onSubmitPlan={handleAddPlan}
        userID={userID}
        petID={selectedPetId} // Pass selectedPetId
        key={`add-${showAddPlanModal}-${selectedPetId}`} // Add selectedPetId to key
      />

      {/* Edit Plan Modal (using AddTreatmentPlanModal in edit mode) */}
      <AddTreatmentPlanModal
        isOpen={showEditPlanModal}
        onClose={() => {
          setShowEditPlanModal(false);
          setSelectedPlan(null); // Clear selected plan on close
        }}
        onSubmitPlan={handleEditPlan}
        plan={selectedPlan} // Pass the plan to edit
        isEditMode={true} // Set to edit mode
        userID={userID}
        petID={selectedPetId} // Pass selectedPetId
        key={`edit-${selectedPlan?.planID}-${showEditPlanModal}-${selectedPetId}`} // Add selectedPetId to key
      />

      {/* Delete Confirmation Modal */}
      <DeleteConfTreatmentPlanModal
        isOpen={showDeleteConfModal}
        onCancel={() => setShowDeleteConfModal(false)}
        onConfirm={handleDeletePlan}
        planName={planToDelete?.name}
      />

      {/* Create Confirmation/Success Modal */}
      <CreateConfTreatmentPlanModal
        isOpen={showCreateConfModal}
        onClose={() => setShowCreateConfModal(false)}
        planName={lastAddedPlan?.name}
      />

      {/* Edit Confirmation/Success Modal */}
      <EditConfTreatmentPlanModal
        isOpen={showEditConfModal}
        onClose={() => {
          setShowEditConfModal(false);
          // Reset state depending on whether it was a confirmation prompt or success message
          if (isEditConfirmation) {
            setPlanToEdit(null); // Clear plan to edit if cancelling the prompt
          } else {
            setLastEditedPlan(null); // Clear last edited plan after viewing success message
          }
        }}
        onConfirm={
          isEditConfirmation
            ? proceedWithEdit
            : () => setShowEditConfModal(false)
        } // proceedWithEdit only for confirmation prompt
        planName={isEditConfirmation ? planToEdit?.name : lastEditedPlan?.name}
        isConfirmation={isEditConfirmation} // Differentiates between prompt and success message
      />
    </div>
  );}
