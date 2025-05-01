import { useState, useEffect } from "react";
import { PawPrint, X, Upload, Trash2 } from "lucide-react";

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
  const [image, setImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [isDragging, setIsDragging] = useState(false);

  useEffect(() => {
    if (isEditMode && pet) {
      setFormData({
        petID: pet.petID || "",
        ownerID: pet.ownerID || userID, // Ensure ownerID is included
        name: pet.name || "",
        species: pet.species || "",
        breed: pet.breed || "",
        gender: pet.gender || "",
        age: pet.age?.toString() || "",
        weight: pet.weight?.toString() || "",
        allergies: pet.allergies?.join(", ") || "",
      });
      setImagePreview(pet.imageBase64 && pet.imageBase64 !== "" ? `data:image/jpeg;base64,${pet.imageBase64}` : null);
    } else {
      setFormData({
        name: "",
        species: "",
        breed: "",
        gender: "",
        age: "",
        weight: "",
        allergies: "",
      });
      setImage(null);
      setImagePreview(null);
    }
  }, [isEditMode, pet]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (name, value) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const compressImage = (file, maxSizeKB = 100) => {
    return new Promise((resolve) => {
      const img = new Image();
      const reader = new FileReader();
      reader.onload = (e) => {
        img.src = e.target.result;
      };
      reader.readAsDataURL(file);
      img.onload = () => {
        const canvas = document.createElement("canvas");
        let width = img.width;
        let height = img.height;
        const maxDimension = 800;
        if (width > height && width > maxDimension) {
          height *= maxDimension / width;
          width = maxDimension;
        } else if (height > maxDimension) {
          width *= maxDimension / height;
          height = maxDimension;
        }
        canvas.width = width;
        canvas.height = height;
        const ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, width, height);
        let quality = 0.9;
        let dataUrl;
        do {
          dataUrl = canvas.toDataURL("image/jpeg", quality);
          quality -= 0.1;
        } while (dataUrl.length / 1024 > maxSizeKB * 1.33 && quality > 0.1);
        const compressedFile = dataURLtoFile(dataUrl, file.name);
        resolve(compressedFile);
      };
    });
  };

  const dataURLtoFile = (dataUrl, filename) => {
    const arr = dataUrl.split(",");
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime });
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (file) {
      const compressedFile = await compressImage(file);
      setImage(compressedFile);
      const reader = new FileReader();
      reader.onloadend = () => setImagePreview(reader.result);
      reader.readAsDataURL(compressedFile);
    } else {
      setImage(null);
      setImagePreview(null);
    }
  };

  const handleDrop = async (e) => {
    e.preventDefault();
    setIsDragging(false);
    const file = e.dataTransfer.files[0];
    if (file && file.type.startsWith("image/")) {
      const compressedFile = await compressImage(file);
      setImage(compressedFile);
      const reader = new FileReader();
      reader.onloadend = () => setImagePreview(reader.result);
      reader.readAsDataURL(compressedFile);
    }
  };

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleClearImage = () => {
    setImage(null);
    setImagePreview(null);
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

    const formDataToSend = new FormData();
    formDataToSend.append("pet", new Blob([JSON.stringify(newPet)], { type: "application/json" }));
    if (image) {
      formDataToSend.append("image", image);
    }

    try {
      await onPetAdded(formDataToSend);
      setFormData({
        name: "",
        species: "",
        breed: "",
        gender: "",
        age: "",
        weight: "",
        allergies: "",
      });
      setImage(null);
      setImagePreview(null);
      onClose();
    } catch (err) {
      alert(`Failed to ${isEditMode ? "update" : "add"} pet.`);
      console.error(err);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 font-['Baloo']">
      <div className="bg-white rounded-lg shadow-xl max-w-sm w-full mx-4">
        <div className="flex justify-between items-center p-3 border-b">
          <h2 className="text-lg font-bold flex items-center text-[#042C3C]">
            <PawPrint className="h-4 w-4 mr-2 text-[#EA6C7B]" />
            {isEditMode ? "Edit Pet" : "Add New Pet"}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-[#EA6C7B] rounded-full p-1 hover:bg-gray-100"
          >
            <X className="h-4 w-4" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-3 space-y-3">
          <div>
            <label htmlFor="name" className="block text-xs font-medium text-[#042C3C] mb-1">
              Pet Name
            </label>
            <input
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-medium text-[#042C3C] mb-1">
              Pet Photo
            </label>
            <div
              className={`w-full p-4 border-2 ${
                isDragging ? "border-[#EA6C7B] bg-[#FFF7EC]" : "border-[#EA6C7B]/50 bg-[#FFF7EC]/50"
              } rounded-md text-center cursor-pointer`}
              onDragOver={handleDragOver}
              onDragLeave={handleDragLeave}
              onDrop={handleDrop}
            >
              <input
                id="image"
                name="image"
                type="file"
                accept="image/*"
                onChange={handleImageChange}
                className="hidden"
              />
              <label htmlFor="image" className="flex flex-col items-center gap-2">
                <Upload className="h-5 w-5 text-[#EA6C7B]" />
                <span className="text-xs text-gray-500">
                  {isDragging ? "Drop image here" : "Drag & drop or click to upload"}
                </span>
                <button
                  type="button"
                  className="px-3 py-1 bg-[#EA6C7B] text-white rounded-md text-xs hover:bg-[#EA6C7B]/90"
                >
                  Choose File
                </button>
              </label>
            </div>
            {imagePreview && (
              <div className="mt-2 flex items-center gap-2">
                <img
                  src={imagePreview}
                  alt="Pet preview"
                  className="w-16 h-16 object-cover rounded-full"
                />
                {isEditMode && (
                  <button
                    type="button"
                    onClick={handleClearImage}
                    className="flex items-center gap-1 px-2 py-1 bg-red-500 text-white rounded-md text-xs hover:bg-red-600"
                  >
                    <Trash2 className="h-3 w-3" />
                    Clear Photo
                  </button>
                )}
              </div>
            )}
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label htmlFor="species" className="block text-xs font-medium text-[#042C3C] mb-1">
                Species
              </label>
              <select
                id="species"
                name="species"
                value={formData.species}
                onChange={(e) => handleSelectChange("species", e.target.value)}
                className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
                required
              >
                <option value="" disabled>
                  Select
                </option>
                <option value="dog">Dog</option>
                <option value="cat">Cat</option>
                <option value="bird">Bird</option>
                <option value="rabbit">Rabbit</option>
                <option value="other">Other</option>
              </select>
            </div>
            <div>
              <label htmlFor="breed" className="block text-xs font-medium text-[#042C3C] mb-1">
                Breed
              </label>
              <input
                id="breed"
                name="breed"
                value={formData.breed}
                onChange={handleChange}
                className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label htmlFor="gender" className="block text-xs font-medium text-[#042C3C] mb-1">
                Gender
              </label>
              <select
                id="gender"
                name="gender"
                value={formData.gender}
                onChange={(e) => handleSelectChange("gender", e.target.value)}
                className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
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
              <label htmlFor="age" className="block text-xs font-medium text-[#042C3C] mb-1">
                Age (yrs)
              </label>
              <input
                id="age"
                name="age"
                type="number"
                min="0"
                step="1"
                value={formData.age}
                onChange={handleChange}
                className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
              />
            </div>
            <div>
              <label htmlFor="weight" className="block text-xs font-medium text-[#042C3C] mb-1">
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
                className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
              />
            </div>
          </div>

          <div>
            <label htmlFor="allergies" className="block text-xs font-medium text-[#042C3C] mb-1">
              Allergies
            </label>
            <textarea
              id="allergies"
              name="allergies"
              value={formData.allergies}
              onChange={handleChange}
              placeholder="Enter allergies separated by commas"
              className="w-full p-1.5 bg-[#FFF7EC] border-2 border-[#EA6C7B] rounded-md focus:outline-none focus:ring-2 focus:ring-[#EA6C7B] text-gray-500 text-sm"
              rows="2"
            />
            <p className="text-[10px] text-gray-500 mt-1">Separate multiple allergies with commas</p>
          </div>

          <div className="flex justify-end space-x-2 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-3 py-1 border-2 border-[#EA6C7B] text-[#EA6C7B] rounded-md text-sm hover:bg-[#EA6C7B]/10 transition-colors"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-3 py-1 bg-[#8A973F] text-white rounded-md text-sm hover:bg-[#8A973F]/90 transition-colors"
            >
              {isEditMode ? "Update Pet" : "Add Pet"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}