import GuestNavBar from "../components/GuestNavBar";
import pawpedia from "../assets/pawpedia.png";
import { useState, useEffect } from "react";
import AIModal from "../components/AIModal";


export default function PawPedia() {
  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
  };

  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [results, setResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
 


  const filterKeywords = [
    "animal", "pet", "dog", "cat", "canine", "feline", "puppy", "kitten", "rabbit", 
    "parrot", "hamster", "illness", "disease", "condition", "infection", "allergy", 
    "vet", "veterinary", "symptom", "sick", "vomiting", "diarrhea", "limping", "skin", 
    "fur", "ticks", "fleas", "worms", "respiratory", "eyes", "ears", "health", 
    "treatment", "vaccination", "diagnosis", "medication", "rabies",
    "Addison's Disease in Dogs", "Anaemia in Pets", "Anal Sac Disease in Dogs", "Asthma In Cats", 
    "Atrial Fibrillation in Dogs", "Bladder Tumours in Cats & Dogs", "Chronic Diarrhoea", 
    "Chronic Vomiting in Pets", "Congestive Heart Failure in Cats", "Congestive Heart Failure in Dogs", 
    "Cruciate Disease in Dogs", "Cushing's Disease in Dogs", "Degenerative Mitral Valve Disease in Dogs", 
    "Diabetes in Cats", "Diabetes in Dogs", "Dilated Cardiomyopathy in Dogs", "Dystocia in Dogs", 
    "Ear Disease (Otitis) in Dogs", "Elbow Dysplasia in Dogs", "Feline Aortic Thromboembolism in Cats", 
    "Feline Infectious Peritonitis (FIP) in Cats", "Feline Lower Urinary Tract Disease (FLUTD)", 
    "Fleas in Cats & Dogs", "Flystrike in Rabbits", "Fractures In Pets", "Heart Disease in Cats & Dogs", 
    "Hip Dysplasia in Dogs", "Hypercalcaemia In Pets", "Hypertension (high blood pressure) in Cats & Dogs", 
    "Hyperthyroidism in Cats", "Hypertrophic Cardiomyopathy In Cats", "Hypothyroidism In Dogs", 
    "Immune-Mediated Haemolytic Anaemia (IMHA)", "Immune-Mediated Thrombocytopenia In Pets", 
    "Kidney Disease In Cats And Dogs", "Laryngeal Paralysis In Dogs", "Leishmaniosis In Dogs", 
    "Leptospirosis in Dogs", "Liver Disease", "Lumps & Bumps In Pets", "Lungworm in Dogs", "Lymphoma in Cats", 
    "Lymphoma in Dogs", "Mammary (breast) Tumours in Cats & Dogs", "Mass Removal In Pets", 
    "Mast Cell Tumours in Dogs", "Nasal Disease In Dogs", "Oral Tumours In Cats & Dogs", 
    "Osteoarthritis In Cats", "Osteoarthritis in Dogs", "Over-Grooming in Cats", "Pancreatitis In Dogs", 
    "Panosteitis In Dogs", "Parvovirus in Dogs", "Patella Luxation in Dogs", "Pericardial Effusion In Dogs", 
    "Polyuria-Polydipsia (PUPD) In Pets", "Porto-systemic Shunts", "Pregnancy in Dogs", 
    "Prostatic Disease In Dogs", "Pruritus (Itching) In Dogs", "Pyometra in Dogs & Cats", 
    "Scrotal Urethrostomy In Dogs", "Seizures In Pets", "Soft Tissue Sarcoma in Pets", 
    "Splenic Masses in Dogs", "Ticks In Cats &  Urethral Obstruction In Cats", "Urinary Incontinence in Dogs", "Urolithiasis In Pets", 
  "Vestibular Disease In Dogs", "Worms in Cats"
];
  
  // Fetch autocomplete suggestions
  useEffect(() => {
    if (query.length < 3) {
      setSuggestions([]);
      return;
    }
  
    fetch(
      `https://en.wikipedia.org/w/api.php?action=opensearch&search=${query}&limit=5&namespace=0&format=json&origin=*`
    )
      .then((res) => res.json())
      .then((data) => {
        const filteredSuggestions = data[1].filter((item) =>
          filterKeywords.some((keyword) => item.toLowerCase().includes(keyword))
        );
        setSuggestions(filteredSuggestions);
      });
  }, [query]);
  

  const handleSearch = (searchTerm = query) => {
    if (searchTerm.trim() === "") return;
  
    setHasSearched(true);

    fetch(
      `https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=${searchTerm}&utf8=&format=json&origin=*`
    )
      .then((res) => res.json())
      .then((data) => {
        const filtered = data.query.search.filter((item) => {
          const content = (item.title + item.snippet).toLowerCase();
          // Apply the filter for pet-related keywords
          return filterKeywords.some((keyword) => content.includes(keyword));
        });
  
        // If no relevant results are found, provide a fallback message
        if (filtered.length === 0) {
          alert("No relevant pet-related results found.");
        }
  
        setResults(filtered); // Update state with filtered results
        setSuggestions([]); // Clear suggestions when search is made
      });
  };
  
  const handleSuggestionClick = (term) => {
    setQuery(term);
    handleSearch(term);
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
      {/* Yellow background circles */}
      {[...Array(5)].map((_, i) => (
        <div
          key={i}
          style={{
            position: "absolute",
            borderRadius: "50%",
            backgroundColor: colors.yellow,
            ...(i === 0 && { top: 0, left: 0, width: "230px", height: "230px", transform: "translate(-30%, -30%)" }),
            ...(i === 1 && { top: "120px", left: "180px", width: "100px", height: "100px" }),
            ...(i === 2 && { bottom: 0, right: 0, width: "300px", height: "300px", transform: "translate(40%, 40%)" }),
            ...(i === 3 && { bottom: "150px", right: "100px", width: "80px", height: "80px" }),
            ...(i === 4 && { bottom: "300px", right: "200px", width: "60px", height: "60px" }),
          }}
        ></div>
      ))}

      <GuestNavBar />

      {/* Main content */}
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
        <div style={{ display: "flex", flexDirection: "column", alignItems: "center", padding: "20px" }}>
          <img
            src={pawpedia}
            alt="Pets lineup"
            style={{
              width: "130%",
              maxWidth: "470px",
              height: "auto",
              objectFit: "contain",
              marginTop: "-30px",
              marginBottom: "-20px",
              display: "block",
            }}
          />

          {/* Search bar with autocomplete */}
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
              maxWidth: "500px",
              position: "relative",
            }}
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
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
                placeholder="Search pet conditions, diseases, or illnesses..."
                value={query}
                onChange={(e) => {
                  const val = e.target.value;
                  setQuery(val);
                  if (val.length < 2) {
                    setSuggestions([]);
                    setResults([]); // Optional: clear results when query is too short
                  }
                }}
                
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
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
                onClick={() => handleSearch()}
              >
                Search
              </button>
            </div>

            {/* Autocomplete suggestions */}
            {suggestions.length > 0 && (
              <div
                style={{
                  position: "absolute",
                  top: "110%",
                  left: 0,
                  right: 0,
                  backgroundColor: "white",
                  border: "1px solid #ccc",
                  borderRadius: "8px",
                  boxShadow: "0 2px 6px rgba(0, 0, 0, 0.1)",
                  zIndex: 10,
                }}
              >
                {suggestions.map((sugg, idx) => (
                  <div
                    key={idx}
                    onClick={() => handleSuggestionClick(sugg)}
                    style={{
                      padding: "10px",
                      cursor: "pointer",
                      fontSize: "13px",
                      color: colors.darkBlue,
                      borderBottom: idx !== suggestions.length - 1 ? "1px solid #eee" : "none",
                    }}
                  >
                    {sugg}
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Results */}
          <div style={{ marginTop: "20px", width: "100%", maxWidth: "600px" }}>
            {results.length > 0 ? (
              results.map((item, idx) => (
                <div
                  key={idx}
                  style={{
                    marginBottom: "20px",
                    padding: "15px",
                    border: "1px solid #ddd",
                    borderRadius: "12px",
                    backgroundColor: "#fff",
                  }}
                >
                  <h3 style={{ margin: "0 0 5px 0", color: colors.darkBlue }}>{item.title}</h3>
                  <p
                    style={{ margin: 0, fontSize: "13px", color: "#333" }}
                    dangerouslySetInnerHTML={{ __html: item.snippet + "..." }}
                  />
                  <a
                    href={`https://en.wikipedia.org/?curid=${item.pageid}`}
                    target="_blank"
                    rel="noreferrer"
                    style={{ fontSize: "12px", color: colors.coral, marginTop: "5px", display: "inline-block" }}
                  >
                    Read more on Wikipedia →
                  </a>
                </div>
              ))
            ) : (
                hasSearched && results.length === 0 && (
                  <p style={{ fontSize: "14px", color: "#666", marginTop: "20px" }}>
                   
                  </p>
                )
            )}
          </div>
        </div>
      </div>

      <AIModal />
    </div>
  );
}
