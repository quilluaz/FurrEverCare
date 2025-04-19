import { useState, useEffect } from "react";
import { PawPrint, X } from "lucide-react";

export default function AddPetModal({ onPetAdded, isOpen, onClose, pet, isEditMode = false, userID }) {
  const [formData, setFormData] = useState({
    name: "",
    species: "",
    breed: "",
    gender: "",
    age: "",
    weight: "",
    allergies: "",
  });

  // Populate form with pet data when editing
  useEffect(() => {
    if (isEditMode && pet) {
      setFormData({
        petID: pet.petID || "",
        name: pet.name || "",
        species: pet.species || "",
        breed: pet.breed || "",
        gender: pet.gender || "",
        age: pet.age?.toString() || "",
        weight: pet.weight?.toString() || "",
        allergies: pet.allergies?.join(", ") || "",
      });
    }
  }, [isEditMode, pet]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const newPet = {
      ...formData,
      age: parseInt(formData.age) || 0,
      weight: parseFloat(formData.weight) || 0,
      allergies: formData.allergies
        .split(",")
        .map((item) => item.trim())
        .filter(Boolean),
    };

    if (isEditMode) {
      newPet.petID = formData.petID;
    }

    try {
      await onPetAdded(newPet);
      // Reset form
      setFormData({
        name: "",
        species: "",
        breed: "",
        gender: "",
        age: "",
        weight: "",
        allergies: "",
      });
      onClose();
    } catch (err) {
      alert(`Failed to ${isEditMode ? "update" : "add"} pet. Please try again.`);
      console.error(err);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl max-w-md w-full mx-4">
        <div className="flex justify-between items-center p-4 border-b">
          <h2 className="text-xl font-bold flex items-center text-[#042C3C]">
            <PawPrint className="h-5 w-5 mr-2 text-[#EA6C7B]" />
            {isEditMode ? "Edit Pet" : "Add New Pet"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-[#EA6C7B] rounded-full p-1 hover:bg-gray-100"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-4 space-y-4">
          <div>
            <label htmlFor="name" className="block text-sm font-medium text-[#042C3C] mb-1">
              Pet Name
            </label>
            <input
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              required
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="species" className="block text-sm font-medium text-[#042C3C] mb-1">
                Species
              </label>
              <select
                id="species"
                name="species"
                value={formData.species}
                onChange={(e) => handleSelectChange("species", e.target.value)}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
                required
              >
                <option value="" disabled>
                  Select species
                </option>
                <option value="dog">Dog</option>
                <option value="cat">Cat</option>
                <option value="bird">Bird</option>
                <option value="rabbit">Rabbit</option>
                <option value="other">Other</option>
              </select>
            </div>
            <div>
              <label htmlFor="breed" className="block text-sm font-medium text-[#042C3C] mb-1">
                Breed
              </label>
              <input
                id="breed"
                name="breed"
                value={formData.breed}
                onChange={handleChange}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div>
              <label htmlFor="gender" className="block text-sm font-medium text-[#042C3C] mb-1">
                Gender
              </label>
              <select
                id="gender"
                name="gender"
                value={formData.gender}
                onChange={(e) => handleSelectChange("gender", e.target.value)}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              >
                <option value="" disabled>
                  Select
                </option>
                <option value="male">Male</option>
                <option value="female">Female</option>
                <option value="unknown">Unknown</option>
              </select>
            </div>
            <div>
              <label htmlFor="age" className="block text-sm font-medium text-[#042C3C] mb-1">
                Age (years)
              </label>
              <input
                id="age"
                name="age"
                type="number"
                min="0"
                step="1"
                value={formData.age}
                onChange={handleChange}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              />
            </div>
            <div>
              <label htmlFor="weight" className="block text-sm font-medium text-[#042C3C] mb-1">
                Weight (kg)
              </label>
              <input
                id="weight"
                name="weight"
                type="number"
                min="0"
                step="0.1"
                value={formData.weight}
                onChange={handleChange}
                className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              />
            </div>
          </div>

          <div>
            <label htmlFor="allergies" className="block text-sm font-medium text-[#042C3C] mb-1">
              Allergies
            </label>
            <textarea
              id="allergies"
              name="allergies"
              value={formData.allergies}
              onChange={handleChange}
              placeholder="Enter allergies separated by commas"
              className="w-full p-2 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500"
              rows="3"
            />
            <p className="text-xs text-gray-500 mt-1">Separate multiple allergies with commas</p>
          </div>

          <div className="flex justify-end space-x-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-md hover:bg-[#EA6C7B]/10 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-[#8A973F] text-white rounded-md hover:bg-[#8A973F]/90 transition-colors"
            >
              {isEditMode ? "Update Pet" : "Add Pet"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}