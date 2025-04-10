package cit.edu.furrevercare.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import cit.edu.furrevercare.entity.Pet;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PetService {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public String addPetToUser(String userID, Pet pet) throws ExecutionException, InterruptedException {
        CollectionReference petsCollection = firestore.collection(COLLECTION_NAME).document(userID).collection("pets");
        DocumentReference petDoc = petsCollection.document();
        pet.setPetID(petDoc.getId());
        pet.setOwnerID(userID);
        petDoc.set(pet).get();
        return "Pet added successfully with ID: " + pet.getPetID();
    }

    public List<Pet> getUserPets(String userID) throws ExecutionException, InterruptedException {
        List<Pet> pets = new ArrayList<>();
        CollectionReference petsCollection = firestore.collection(COLLECTION_NAME).document(userID).collection("pets");
        List<QueryDocumentSnapshot> documents = petsCollection.get().get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            pets.add(document.toObject(Pet.class));
        }
        return pets;
    }

    public Pet getPetById(String userID, String petID) throws ExecutionException, InterruptedException {
        DocumentReference petDoc = firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID);
        DocumentSnapshot document = petDoc.get().get();

        return document.exists() ? document.toObject(Pet.class) : null;
    }

    public String updatePet(String userID, String petID, Pet updatedPet) throws ExecutionException, InterruptedException {
        DocumentReference petDoc = firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID);
        petDoc.set(updatedPet).get();
        return "Pet updated successfully!";
    }

    public String deletePet(String userID, String petID) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME).document(userID).collection("pets").document(petID).delete().get();
        return "Pet deleted successfully!";
    }
}
