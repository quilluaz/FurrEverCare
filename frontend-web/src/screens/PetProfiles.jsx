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
  const API_BASE_URL = "https://furrevercare-deploy-13.onrender.com/api/users";
  //const API_BASE_URL = "http://localhost:8080/api/users";

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
                className="bg-white rounded-xl shadow-lg relative overflow-hidden text-sm transition-transform duration-200 hover:scale-[1.02] hover:shadow-xl">
                {/* Accent Bar */}
                <div className="absolute left-0 top-0 h-full w-1.5 bg-gradient-to-b from-[#EA6C7B] to-[#68D391]" />
                
                {isPetLoading(pet.petID) && (
                  <div className="absolute inset-0 bg-white/80 backdrop-blur-sm flex flex-col items-center justify-center z-10">
                    <Loader className="h-6 w-6 text-[#EA6C7B] animate-spin" />
                    <p className="mt-1 text-gray-500 text-xs">Processing...</p>
                  </div>
                )}
                <div className="p-5">
                  <div className="flex justify-between items-start mb-4">
                    <div className="flex items-center gap-3">
                      {pet.imageBase64 ? (
                        <div className="relative">
                          <img
                            src={`data:image/jpeg;base64,${pet.imageBase64}`}
                            alt={`${pet.name}'s photo`}
                            className="w-14 h-14 rounded-full object-cover border-2 border-[#FFF7EC] shadow-sm"
                          />
                          <div className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full bg-[#FFF7EC] flex items-center justify-center">
                            <div className="w-3 h-3 rounded-full bg-[#8A973F]"></div>
                          </div>
                        </div>
                      ) : (
                        <div className="relative">
                          <div className="w-14 h-14 rounded-full bg-gradient-to-br from-[#F0B542]/20 to-[#EA6C7B]/20 flex items-center justify-center text-[#F0B542] text-xl font-bold border-2 border-[#FFF7EC] shadow-sm">
                            {pet.name?.[0]?.toUpperCase() || ""}
                          </div>
                          <div className="absolute -bottom-1 -right-1 w-5 h-5 rounded-full bg-[#FFF7EC] flex items-center justify-center">
                            <div className="w-3 h-3 rounded-full bg-[#8A973F]"></div>
                          </div>
                        </div>
                      )}
                      <div>
                        <h2 className="text-lg font-bold text-[#042C3C]">
                          {pet.name}
                        </h2>
                        <div className="flex items-center gap-2">
                          <span className="text-xs px-2 py-0.5 bg-[#FFF7EC] text-[#EA6C7B] rounded-full">
                            {pet.species}
                          </span>
                          <span className="text-xs text-gray-500">
                            {pet.breed}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div className="flex gap-1">
                      <button
                        onClick={() => confirmEditPet(pet)}
                        disabled={!userID || isPetLoading(pet.petID)}
                        className="p-1.5 text-gray-500 hover:text-[#EA6C7B] hover:bg-[#FFF7EC] rounded-lg transition-colors">
                        <Edit className="h-4 w-4" />
                      </button>
                      <button
                        onClick={() => confirmDeletePet(pet)}
                        disabled={!userID || isPetLoading(pet.petID)}
                        className="p-1.5 text-gray-500 hover:text-[#EA6C7B] hover:bg-[#FFF7EC] rounded-lg transition-colors">
                        <Trash2 className="h-4 w-4" />
                      </button>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-3 mb-4">
                    <div className="bg-[#FFF7EC] rounded-lg p-2">
                      <p className="text-[10px] font-medium text-[#042C3C] mb-0.5">Age</p>
                      <p className="text-xs text-gray-600">{pet.age} yrs</p>
                    </div>
                    <div className="bg-[#FFF7EC] rounded-lg p-2">
                      <p className="text-[10px] font-medium text-[#042C3C] mb-0.5">Weight</p>
                      <p className="text-xs text-gray-600">{pet.weight} kg</p>
                    </div>
                    <div className="bg-[#FFF7EC] rounded-lg p-2">
                      <p className="text-[10px] font-medium text-[#042C3C] mb-0.5">Gender</p>
                      <p className="text-xs text-gray-600">
                        {pet.gender || "Unknown"}
                      </p>
                    </div>
                    <div className="bg-[#FFF7EC] rounded-lg p-2">
                      <p className="text-[10px] font-medium text-[#042C3C] mb-0.5">Allergies</p>
                      <p className="text-xs text-gray-600 line-clamp-1">
                        {pet.allergies && pet.allergies.length
                          ? pet.allergies.join(", ")
                          : "None"}
                      </p>
                    </div>
                  </div>
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
