import React, { useState } from 'react'; 
import UserNavBar from "../components/UserNavBar"
import { Plus, Edit, Trash2, Calendar, Activity, FileText } from "lucide-react"
import AddPetModal from "../components/AddPetModal"

 function PetProfiles() {
  const [pets, setPets] = useState([])
  const [showAddPetModal, setShowAddPetModal] = useState(false)

  const handleAddPet = (newPet) => {
    setPets([...pets, { ...newPet, id: Date.now() }])
  }

  const deletePet = (id) => {
    setPets(pets.filter((pet) => pet.id !== id))
  }

  return (
    <div className="min-h-screen bg-[#F8F9FA] font-['Baloo'] overflow-auto">
      <UserNavBar/>

      {/* Main content area */}
      <div className="max-w-7xl mx-auto px-6 py-8">
        {/* Content header with title and add button */}
        <div className="flex justify-between items-center mb-10 -ml-60">
          <h1 className="text-2xl font-bold text-[#042C3C]">Pet Profiles</h1>
          <button
            className="px-6 py-2 bg-[#EA6C7B] text-white rounded-full hover:bg-[#EA6C7B]/90 transition-colors flex items-center gap-2 mr-10"
            onClick={() => setShowAddPetModal(true)}
          >
            <Plus className="h-5 w-5" />
            Add Pet
          </button>
        </div>

        {/* Empty state */}
        {pets.length === 0 && (
          <div className="flex flex-col items-center justify-center py-20">
            <p className="text-xl text-gray-500">You have no pets at the moment.</p>
          </div>
        )}

        {/* Pet cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {pets.map((pet) => (
            <div key={pet.id} className="bg-white rounded-lg shadow-md overflow-auto">
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div className="flex items-center gap-4">
                    <div className="text-gray-500 w-16 h-16 rounded-full bg-[#F0B542]/20 flex items-center justify-center text-[#F0B542] text-xl font-bold">
                      {pet.name.charAt(0)}
                    </div>
                    <div>
                      <h2 className="text-xl font-bold text-[#042C3C]">{pet.name}</h2>
                      <p className="text-gray-500">
                        {pet.species} • {pet.breed}
                      </p>
                    </div>
                  </div>
                  <div className="flex gap-2">
                    <button className="p-2 text-gray-500 hover:text-[#EA6C7B] rounded-full hover:bg-gray-100">
                      <Edit className="h-4 w-4" />
                    </button>
                    <button
                      className="p-2 text-gray-500 hover:text-[#EA6C7B] rounded-full hover:bg-gray-100"
                      onClick={() => deletePet(pet.id)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <p className="text-xs text-gray-500">Age</p>
                    <p className="font-medium">{pet.age} years</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500">Weight</p>
                    <p className="font-medium">{pet.weight} kg</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500">Gender</p>
                    <p className="font-medium">{pet.gender || "Unknown"}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500">Allergies</p>
                    <p className="font-medium">
                      {pet.allergies && pet.allergies.length > 0 ? pet.allergies.join(", ") : "None"}
                    </p>
                  </div>
                </div>
              </div>

              <div className="border-t border-gray-100 grid grid-cols-3 divide-x divide-gray-100">
                <button className="py-3 flex items-center justify-center gap-1 text-sm text-[#042C3C] hover:bg-gray-50">
                  <Calendar className="h-4 w-4" />
                  <span>Schedule</span>
                </button>
                <button className="py-3 flex items-center justify-center gap-1 text-sm text-[#042C3C] hover:bg-gray-50">
                  <Activity className="h-4 w-4" />
                  <span>Health</span>
                </button>
                <button className="py-3 flex items-center justify-center gap-1 text-sm text-[#042C3C] hover:bg-gray-50">
                  <FileText className="h-4 w-4" />
                  <span>Records</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Add Pet Modal */}
      <AddPetModal isOpen={showAddPetModal} onClose={() => setShowAddPetModal(false)} onPetAdded={handleAddPet} />

      {/* Bottom border line */}
      <div className="max-w-7xl mx-auto px-6">
        <div className="border-t border-gray-200"></div>
      </div>
    </div>
  )
}

export default PetProfiles