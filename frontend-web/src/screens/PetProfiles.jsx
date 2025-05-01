import { useState, useEffect } from "react";
import UserNavBar from "../components/UserNavBar";
import {
  Plus,
  Edit,
  Trash2,
  Calendar,
  Activity,
  FileText,
  Loader,
} from "lucide-react";
import AddPetModal from "../components/pets/AddPetModal";
import DeleteConfPetModal from "../components/pets/DeleteConfPetModal";
import CreateConfPetModal from "../components/pets/CreateConfPetModal";
import EditConfPetModal from "../components/pets/EditConfPetModal";
import axios from "axios";
import AuthService from "../config/AuthService";

export default function PetProfiles() {
  const [pets, setPets] = useState([]);
  const [showAddPetModal, setShowAddPetModal] = useState(false);
  const [showEditPetModal, setShowEditPetModal] = useState(false);
  const [selectedPet, setSelectedPet] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingPetIds, setLoadingPetIds] = useState([]);
  const [error, setError] = useState(null);
  const [showDeleteConfModal, setShowDeleteConfModal] = useState(false);
  const [showCreateConfModal, setShowCreateConfModal] = useState(false);
  const [showEditConfModal, setShowEditConfModal] = useState(false);
  const [isEditConfirmation, setIsEditConfirmation] = useState(true);
  const [petToDelete, setPetToDelete] = useState(null);
  const [petToEdit, setPetToEdit] = useState(null);
  const [lastAddedPet, setLastAddedPet] = useState(null);
  const [lastEditedPet, setLastEditedPet] = useState(null);

  const user = AuthService.getUser();
  const userID = user?.userId || null;
  const API_BASE_URL = "https://furrevercare-deploy-8.onrender.com/api/users";

  useEffect(() => {
    AuthService.init();
  }, []);

  const fetchPets = async () => {
    if (!userID) {
      setError("Please log in to view your pets.");
      return;
    }
    setLoading(true);
    try {
      const res = await axios.get(`${API_BASE_URL}/${userID}/pets`);
      const sanitizedPets = res.data.map((pet) => ({
        ...pet,
        imageBase64:
          pet.imageBase64 && pet.imageBase64 !== "" ? pet.imageBase64 : null,
      }));
      setPets(sanitizedPets);
      setError(null);
    } catch {
      setError("Failed to fetch pets. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPets();
  }, [userID]);

  const handleAddPet = async (formData) => {
    if (!userID) {
      setError("Please log in to add a pet.");
      return;
    }
    const tempId = `temp-${Date.now()}`;
    setShowAddPetModal(false);
    const tempPetData = JSON.parse(await formData.get("pet").text());
    const tempPet = {
      ...tempPetData,
      petID: tempId,
      allergies: tempPetData.allergies || [],
      imageBase64: null,
    };
    setPets((p) => [...p, tempPet]);
    setLoadingPetIds((ids) => [...ids, tempId]);

    try {
      const res = await axios.post(`${API_BASE_URL}/${userID}/pets`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      const petIdMatch = res.data.match(/ID: (\S+)/);
      const realId = petIdMatch ? petIdMatch[1] : tempId;
      setPets((p) =>
        p.map((pt) =>
          pt.petID === tempId
            ? { ...tempPet, petID: realId, imageBase64: null }
            : pt
        )
      );
      setLoadingPetIds((ids) => ids.filter((id) => id !== tempId));
      setLastAddedPet({ ...tempPet, petID: realId, imageBase64: null });
      setShowCreateConfModal(true);
      fetchPets(); // Refresh pets to get updated imageBase64
    } catch (err) {
      setPets((p) => p.filter((pt) => pt.petID !== tempId));
      setLoadingPetIds((ids) => ids.filter((id) => id !== tempId));
      setError("Failed to add pet. Please try again.");
      console.error(err);
    }
  };

  const confirmEditPet = (pet) => {
    setPetToEdit(pet);
    setIsEditConfirmation(true);
    setShowEditConfModal(true);
  };

  const proceedWithEdit = () => {
    setShowEditConfModal(false);
    setSelectedPet(petToEdit);
    setShowEditPetModal(true);
  };

  const handleEditPet = async (formData) => {
    if (!userID) {
      setError("Please log in to edit a pet.");
      return;
    }
    setShowEditPetModal(false);
    const updatedPetData = JSON.parse(await formData.get("pet").text());
    const petId = updatedPetData.petID;
    setLoadingPetIds((ids) => [...ids, petId]);
    setPets((p) =>
      p.map((pt) =>
        pt.petID === petId
          ? {
              ...updatedPetData,
              petID: petId,
              imageBase64: updatedPetData.imageBase64 || null,
            }
          : pt
      )
    );

    try {
      await axios.put(`${API_BASE_URL}/${userID}/pets/${petId}`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      setLoadingPetIds((ids) => ids.filter((id) => id !== petId));
      setLastEditedPet({
        ...updatedPetData,
        imageBase64: updatedPetData.imageBase64 || null,
      });
      setIsEditConfirmation(false);
      setShowEditConfModal(true);
      fetchPets(); // Refresh pets to get updated imageBase64
    } catch (err) {
      setLoadingPetIds((ids) => ids.filter((id) => id !== petId));
      setError("Failed to update pet. Please try again.");
      fetchPets();
      console.error(err);
    }
  };

  const confirmDeletePet = (pet) => {
    setPetToDelete(pet);
    setShowDeleteConfModal(true);
  };

  const handleDeletePet = async () => {
    if (!userID || !petToDelete) {
      setError("Please log in to delete a pet.");
      return;
    }
    setShowDeleteConfModal(false);
    const petId = petToDelete.petID;
    setLoadingPetIds((ids) => [...ids, petId]);
    try {
      await axios.delete(`${API_BASE_URL}/${userID}/pets/${petId}`);
      setPets((p) => p.filter((pt) => pt.petID !== petId));
      setLoadingPetIds((ids) => ids.filter((id) => id !== petId));
      setPetToDelete(null);
    } catch {
      setLoadingPetIds((ids) => ids.filter((id) => id !== petId));
      setError("Failed to delete pet. Please try again.");
    }
  };

  const isPetLoading = (id) => loadingPetIds.includes(id);

  return (
    <div className="w-full min-h-screen bg-[#FFF7EC] font-['Baloo'] overflow-x-hidden">
      <UserNavBar />
      <div className="max-w-6xl mx-auto px-4 py-6">
        <div className="flex justify-between items-center mb-6 mt-8">
          <h2 className="text-3xl md:text-4xl font-bold text-[#042C3C]">
            Pet Profiles
          </h2>
          <button
            className="flex items-center gap-2 px-4 py-1.5 bg-[#EA6C7B] text-white rounded-full text-sm hover:bg-[#EA6C7B]/90 transition"
            onClick={() => setShowAddPetModal(true)}
            disabled={!userID}>
            <Plus className="h-4 w-4" />
            Add Pet
          </button>
        </div>
        {error && (
          <p className="text-red-500 mb-4 text-center text-sm">{error}</p>
        )}
        {loading && pets.length === 0 ? (
          <div className="flex flex-col items-center py-10">
            <Loader className="h-8 w-8 text-[#EA6C7B] animate-spin" />
            <p className="mt-2 text-gray-500 text-sm">Loading your pets...</p>
          </div>
        ) : pets.length === 0 ? (
          <div className="flex flex-col items-center py-10">
            <p className="text-gray-500 text-sm">You have no pets yet.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {pets.map((pet) => (
              <div
                key={pet.petID}
                className="bg-white rounded-lg shadow relative overflow-hidden text-sm">
                {isPetLoading(pet.petID) && (
                  <div className="absolute inset-0 bg-white/70 flex flex-col items-center justify-center z-10">
                    <Loader className="h-6 w-6 text-[#EA6C7B] animate-spin" />
                    <p className="mt-1 text-gray-500 text-xs">Processing...</p>
                  </div>
                )}
                <div className="p-4">
                  <div className="flex justify-between items-start mb-3">
                    <div className="flex items-center gap-3">
                      {pet.imageBase64 ? (
                        <img
                          src={`data:image/jpeg;base64,${pet.imageBase64}`}
                          alt={`${pet.name}'s photo`}
                          className="w-12 h-12 rounded-full object-cover"
                        />
                      ) : (
                        <div className="w-12 h-12 rounded-full bg-[#F0B542]/20 flex items-center justify-center text-[#F0B542] text-lg font-bold">
                          {pet.name?.[0]?.toUpperCase() || ""}
                        </div>
                      )}
                      <div>
                        <h2 className="text-lg font-semibold text-[#042C3C]">
                          {pet.name}
                        </h2>
                        <p className="text-xs text-gray-500">
                          {pet.species} · {pet.breed}
                        </p>
                      </div>
                    </div>
                    <div className="flex gap-1">
                      <button
                        onClick={() => confirmEditPet(pet)}
                        disabled={!userID || isPetLoading(pet.petID)}
                        className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded">
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => confirmDeletePet(pet)}
                        disabled={!userID || isPetLoading(pet.petID)}
                        className="p-1 text-gray-500 hover:text-[#EA6C7B] rounded">
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-2 mb-3">
                    <div>
                      <p className="text-[10px] text-[#042C3C]">Age</p>
                      <p className="text-xs text-gray-500">{pet.age} yrs</p>
                    </div>
                    <div>
                      <p className="text-[10px] text-[#042C3C]">Weight</p>
                      <p className="text-xs text-gray-500">{pet.weight} kg</p>
                    </div>
                    <div>
                      <p className="text-[10px] text-[#042C3C]">Gender</p>
                      <p className="text-xs text-gray-500">
                        {pet.gender || "Unknown"}
                      </p>
                    </div>
                    <div>
                      <p className="text-[10px] text-[#042C3C]">Allergies</p>
                      <p className="text-xs text-gray-500">
                        {pet.allergies && pet.allergies.length
                          ? pet.allergies.join(", ")
                          : "None"}
                      </p>
                    </div>
                  </div>
                </div>
                <div className="border-t border-gray-100 grid grid-cols-3 divide-x text-xs">
                  <button className="py-2 flex flex-col items-center justify-center hover:bg-gray-50">
                    <Calendar className="h-4 w-4" />
                    <span>Schedule</span>
                  </button>
                  <button className="py-2 flex flex-col items-center justify-center hover:bg-gray-50">
                    <Activity className="h-4 w-4" />
                    <span>Health</span>
                  </button>
                  <button className="py-2 flex flex-col items-center justify-center hover:bg-gray-50">
                    <FileText className="h-4 w-4" />
                    <span>Records</span>
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <AddPetModal
        isOpen={showAddPetModal}
        onClose={() => setShowAddPetModal(false)}
        onPetAdded={handleAddPet}
        userID={userID}
      />
      <AddPetModal
        isOpen={showEditPetModal}
        onClose={() => setShowEditPetModal(false)}
        onPetAdded={handleEditPet}
        pet={selectedPet}
        isEditMode
        userID={userID}
      />
      <DeleteConfPetModal
        isOpen={showDeleteConfModal}
        onCancel={() => setShowDeleteConfModal(false)}
        onConfirm={handleDeletePet}
        petName={petToDelete?.name}
      />
      <CreateConfPetModal
        isOpen={showCreateConfModal}
        onClose={() => setShowCreateConfModal(false)}
        petName={lastAddedPet?.name}
      />
      <EditConfPetModal
        isOpen={showEditConfModal}
        onClose={() => {
          setShowEditConfModal(false);
          if (!isEditConfirmation) setLastEditedPet(null);
        }}
        onConfirm={proceedWithEdit}
        petName={isEditConfirmation ? petToEdit?.name : lastEditedPet?.name}
        isConfirmation={isEditConfirmation}
      />
    </div>
  );
}
