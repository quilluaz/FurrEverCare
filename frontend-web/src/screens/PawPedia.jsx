import GuestNavBar from "../components/GuestNavBar";
import logo from "../assets/logo.png";
import pawpedia from "../assets/pawpedia.png";
import Fuse from "fuse.js"; // Import Fuse.js for fuzzy search
import { useState, useEffect } from "react"; // Add useEffect for fetching data
import { db } from "../config/firebase-config"; // Import Firestore (from your firebase-config.js)
import { collection, getDocs } from "firebase/firestore"; // Firestore methods

"use client"

export default function PawPedia() {
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
  };

  // State for diseases, search query, and results
  const [diseases, setDiseases] = useState([]);
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  // Fetch data from Firestore on component mount
  useEffect(() => {
    const fetchDiseases = async () => {
      try {
        const diseasesCollection = collection(db, "petHealthEncyclopedia");
        const diseasesSnapshot = await getDocs(diseasesCollection);
        const diseasesList = diseasesSnapshot.docs.map((doc) => ({
          id: doc.id,
          ...doc.data(),
        }));
        // Flatten the data for Fuse.js (since Firestore data may have nested subcollections)
        const flattenedDiseases = diseasesList.map((disease) => ({
          name: disease.id, // e.g., "cat"
          animal: disease.id, // Use the document ID as the animal type
          description: disease.dog?.breeds || "No description available", // Extract nested data if available
        }));
        setDiseases(flattenedDiseases);
      } catch (error) {
        console.error("Error fetching diseases:", error);
      }
    };

    fetchDiseases();
  }, []);

  // Initialize Fuse.js with the fetched dataset
  const fuse = new Fuse(diseases, {
    keys: ["name", "animal", "description"], // Fields to search in
    threshold: 0.3, // Adjust for fuzziness (0 = exact match, 1 = very loose)
  });

  // Handle search input
  const handleSearch = (e) => {
    const searchQuery = e.target.value;
    setQuery(searchQuery);
    if (searchQuery.length > 0) {
      const searchResults = fuse.search(searchQuery);
      setResults(searchResults.map((result) => result.item));
    } else {
      setResults(diseases); // Show all diseases if query is empty
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        backgroundColor: colors.cream,
        position: "relative",
        overflow: "hidden",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Background Circles */}
      <div
        style={{
          position: "absolute",
          top: 0,
          left: 0,
          width: "230px",
          height: "230px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
          transform: "translate(-30%, -30%)",
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          top: "120px",
          left: "180px",
          width: "100px",
          height: "100px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          bottom: 0,
          right: 0,
          width: "300px",
          height: "300px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
          transform: "translate(40%, 40%)",
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          bottom: "150px",
          right: "100px",
          width: "80px",
          height: "80px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>
      <div
        style={{
          position: "absolute",
          bottom: "300px",
          right: "200px",
          width: "60px",
          height: "60px",
          borderRadius: "50%",
          backgroundColor: colors.yellow,
        }}
      ></div>
      <GuestNavBar />
      {/* Main Content */}
      <div
        style={{
          maxWidth: "1000px",
          margin: "0 auto",
          padding: "0 20px",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          flexGrow: 1,
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            padding: "20px",
          }}
        >
          {/* Pets Image */}
          <img
            src={pawpedia}
            alt="Pets lineup"
            style={{
              width: "130%",
              maxWidth: "470px",
              height: "auto",
              objectFit: "contain",
              marginTop: "0px",
              marginBottom: "-20px",
              display: "block",
            }}
          />
          {/* Search Bar */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              width: "100%",
              maxWidth: "500px",
              border: "3px solid #042C3C",
              borderRadius: "9999px",
              backgroundColor: "white",
              padding: "8px 16px",
            }}
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="20"
              height="20"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#9CA3AF"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
              style={{ marginRight: "8px" }}
            >
              <circle cx="11" cy="11" r="8"></circle>
              <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
            </svg>
            <input
              type="text"
              placeholder="Search conditions, diseases or illnesses..."
              value={query}
              onChange={handleSearch}
              style={{
                flexGrow: 1,
                color: "rgba(0, 0, 0, 1)",
                backgroundColor: "transparent",
                outline: "none",
                border: "none",
                fontSize: "13px",
              }}
            />
            <button
              style={{
                backgroundColor: colors.coral,
                color: "white",
                fontSize: "12px",
                padding: "6px 15px",
                borderRadius: "9999px",
                border: "none",
                cursor: "pointer",
              }}
              onClick={() => alert("AI search functionality can be added here!")}
            >
              Ask AI
            </button>
          </div>
          {/* Search Results */}
          {results.length > 0 && (
            <div
              style={{
                marginTop: "20px",
                width: "100%",
                maxWidth: "300px",
                backgroundColor: "white",
                borderRadius: "10px",
                boxShadow: "0 2px 8px rgba(0, 0, 0, 0.1)",
                padding: "10px",
              }}
            >
              {results.map((result, index) => (
                <div
                  key={index}
                  style={{
                    padding: "10px",
                    borderBottom:
                      index < results.length - 1 ? "1px solid #e5e7eb" : "none",
                  }}
                >
                  <h3 style={{ margin: 0, fontSize: "16px", color: colors.darkBlue }}>
                    {result.name}
                  </h3>
                  <p style={{ margin: "5px 0 0", fontSize: "14px", color: "#6b7280" }}>
                    {result.animal} - {result.description}
                  </p>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
      {/* Font imports */}
      <style
        dangerouslySetInnerHTML={{
          __html: `
            @import url('https://fonts.googleapis.com/css2?family=Baloo+2:wght@400;500;600;700;800&family=Plus+Jakarta+Sans:wght@200;300;400;500;600;700;800&display=swap');
            html, body {
              margin: 0;
              padding: 0;
              height: 100%;
              width: 100%;
              overflow-x: hidden;
            }
            #root {
              min-height: 100vh;
              display: flex;
              flex-direction: column;
            }
          `,
        }}
      />
    </div>
  );
}