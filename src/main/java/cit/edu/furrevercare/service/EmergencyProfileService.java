package cit.edu.furrevercare.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import cit.edu.furrevercare.entity.EmergencyProfile;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class EmergencyProfileService {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public String addEmergencyProfile(String userID, String petID, EmergencyProfile profile) throws ExecutionException, InterruptedException {
        DocumentReference emergencyProfileDoc = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("emergencyProfile").document("profile");

        emergencyProfileDoc.set(profile).get();
        return "Emergency profile added successfully!";
    }

    public EmergencyProfile getEmergencyProfile(String userID, String petID) throws ExecutionException, InterruptedException {
        DocumentReference emergencyProfileDoc = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("emergencyProfile").document("profile");

        DocumentSnapshot document = emergencyProfileDoc.get().get();

        return document.exists() ? document.toObject(EmergencyProfile.class) : null;
    }

    public String updateEmergencyProfile(String userID, String petID, EmergencyProfile profile) throws ExecutionException, InterruptedException {
        DocumentReference emergencyProfileDoc = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("emergencyProfile").document("profile");

        emergencyProfileDoc.set(profile).get();
        return "Emergency profile updated successfully!";
    }

    public String deleteEmergencyProfile(String userID, String petID) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("emergencyProfile").document("profile").delete().get();
        return "Emergency profile deleted successfully!";
    }
}
