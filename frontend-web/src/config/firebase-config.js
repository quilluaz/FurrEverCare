import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider, signInWithPopup } from "firebase/auth";
import { getFirestore } from "firebase/firestore"; // Add Firestore

const firebaseConfig = {
  apiKey: "AIzaSyBEPB7pJ_n_SH5pB4LIjB1RG1MA5l_S9wo",
  authDomain: "furrevercare-fe125.firebaseapp.com",
  projectId: "furrevercare-fe125",
  storageBucket: "furrevercare-fe125.firebasestorage.app",
  messagingSenderId: "49405850841",
  appId: "1:49405850841:web:eac68fc2deba65eb703ac8",
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const provider = new GoogleAuthProvider();
const db = getFirestore(app); 

export { auth, provider, signInWithPopup, db };