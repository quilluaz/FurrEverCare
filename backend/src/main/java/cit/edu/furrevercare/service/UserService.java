package cit.edu.furrevercare.service;

import com.google.cloud.firestore.Firestore;
import cit.edu.furrevercare.entity.User;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    private static final String COLLECTION_NAME = "users";
    public final Firestore firestore;

    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        com.google.cloud.firestore.Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1);

        com.google.cloud.firestore.QuerySnapshot querySnapshot = query.get().get();

        if (querySnapshot.isEmpty()) {
            return null;
        }

        return querySnapshot.getDocuments().get(0).toObject(User.class);
    }

    public String saveUser(User user) {
        if (user.getUserID() == null || user.getUserID().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }

        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(user.getUserID());
        docRef.set(user);
        return "User saved successfully!";
    }

    public User getUserById(String userID) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(userID);
        DocumentSnapshot document = docRef.get().get();

        return document.exists() ? document.toObject(User.class) : null;
    }

    public String deleteUser(String userID) throws ExecutionException, InterruptedException {
        firestore.collection(COLLECTION_NAME).document(userID).delete().get();
        return "User deleted successfully!";
    }
}