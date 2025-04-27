package cit.edu.furrevercare.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import cit.edu.furrevercare.entity.Pet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PetService {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public String addPetToUser(String userID, Pet pet, MultipartFile image) throws ExecutionException, InterruptedException, IOException {
        CollectionReference petsCollection = firestore.collection(COLLECTION_NAME).document(userID).collection("pets");
        DocumentReference petDoc = petsCollection.document();
        pet.setPetID(petDoc.getId());
        pet.setOwnerID(userID);

        if (image != null && !image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            pet.setImageBase64(base64Image);
        }

        petDoc.set(pet).get();
        return "Pet added successfully with ID: " + pet.getPetID();
    }

    public List<Pet> getUserPets(String userID) throws ExecutionException, InterruptedException {
        List<Pet> pets = new ArrayList<>();
        CollectionReference petsCollection = firestore.collection(COLLECTION_NAME).document(userID).collection("pets");
        List<QueryDocumentSnapshot> documents = petsCollection.get().get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            Pet pet = document.toObject(Pet.class);
            if (pet.getImageBase64() != null && pet.getImageBase64().isEmpty()) {
                pet.setImageBase64(null);
            }
            pets.add(pet);
        }
        return pets;
    }

    public Pet getPetById(String userID, String petID) throws ExecutionException, InterruptedException {
        DocumentReference petDoc = firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID);
        DocumentSnapshot document = petDoc.get().get();

        if (document.exists()) {
            Pet pet = document.toObject(Pet.class);
            if (pet.getImageBase64() != null && pet.getImageBase64().isEmpty()) {
                pet.setImageBase64(null);
            }
            return pet;
        }
        return null;
    }

    public String updatePet(String userID, String petID, Pet updatedPet, MultipartFile image) throws ExecutionException, InterruptedException, IOException {
        DocumentReference petDoc = firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID);

        if (image != null && !image.isEmpty()) {
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            updatedPet.setImageBase64(base64Image);
        }

        petDoc.set(updatedPet).get();
        return "Pet updated successfully!";
    }

    public String deletePet(String userID, String petID) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID).delete().get();
        return "Pet deleted successfully!";
    }
}