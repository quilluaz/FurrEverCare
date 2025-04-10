package cit.edu.furrevercare.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import cit.edu.furrevercare.entity.MedicalRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MedicalRecordService {
    private static final String COLLECTION_NAME = "users";
    private final Firestore firestore = FirestoreClient.getFirestore();

    public String addMedicalRecord(String userID, String petID, MedicalRecord record) throws ExecutionException, InterruptedException {
        CollectionReference medicalRecordsCollection = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("medicalRecords");

        DocumentReference recordDoc = medicalRecordsCollection.document();
        record.setRecordID(recordDoc.getId());
        recordDoc.set(record).get();
        return "Medical record added successfully with ID: " + record.getRecordID();
    }

    public List<MedicalRecord> getMedicalRecords(String userID, String petID) throws ExecutionException, InterruptedException {
        List<MedicalRecord> records = new ArrayList<>();
        CollectionReference medicalRecordsCollection = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("medicalRecords");

        List<QueryDocumentSnapshot> documents = medicalRecordsCollection.get().get().getDocuments();

        for (QueryDocumentSnapshot document : documents) {
            records.add(document.toObject(MedicalRecord.class));
        }
        return records;
    }

    public String updateMedicalRecord(String userID, String petID, String recordID, MedicalRecord record) throws ExecutionException, InterruptedException {
        DocumentReference recordDoc = firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("medicalRecords").document(recordID);

        recordDoc.set(record).get();
        return "Medical record updated successfully!";
    }

    public String deleteMedicalRecord(String userID, String petID, String recordID) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME)
                .document(userID).collection("pets").document(petID).collection("medicalRecords").document(recordID).delete().get();
        return "Medical record deleted successfully!";
    }
}
