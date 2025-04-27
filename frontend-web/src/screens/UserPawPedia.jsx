
import { useState, useEffect } from "react";
import UserNavBar from "../components/UserNavBar";
import WelcomeMessageModal from "../components/WelcomeMessageModal";
import AIModal from "../components/AIModal";
import pawpedia from "../assets/pawpedia.png";

export default function UserPawPedia() {
  const [showWelcomeModal, setShowWelcomeModal] = useState(false);
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [results, setResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);

  const colors = {
    yellow: "#F0B542",
    darkBlue: "#042C3C",
    coral: "#EA6C7B",
    cream: "#FFF7EC",
  };

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
    "Splenic Masses in Dogs", "Ticks In Cats & Dogs", "Urethral Obstruction In Cats",
    "Urinary Incontinence in Dogs", "Urolithiasis In Pets", "Vestibular Disease In Dogs", "Worms in Cats"
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

  // Inject floating animation keyframes
  useEffect(() => {
    const styleTag = document.createElement("style");
    styleTag.innerHTML = `
      @keyframes float {
        0% { transform: translate(0, 0); }
        25% { transform: translate(-10px, 15px); }
        50% { transform: translate(10px, -10px); }
        75% { transform: translate(-15px, -5px); }
        100% { transform: translate(0, 0); }
      }
    `;
    document.head.appendChild(styleTag);
    return () => {
      document.head.removeChild(styleTag);
    };
  }, []);

  // Handle Wikipedia search
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
          return filterKeywords.some((keyword) => content.includes(keyword));
        });

        if (filtered.length === 0) {
          alert("No relevant pet-related results found.");
        }

        setResults(filtered);
        setSuggestions([]);
      });
  };

  const handleSuggestionClick = (term) => {
    setQuery(term);
    handleSearch(term);
  };

  // Welcome modal logic
  useEffect(() => {
    const hasSeenWelcome = sessionStorage.getItem("seenWelcomePawPedia");
    if (!hasSeenWelcome) {
      setShowWelcomeModal(true);
      sessionStorage.setItem("seenWelcomePawPedia", "true");
    }
  }, []);

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
      <div style={{ position: "relative", zIndex: 10 }}>
        <UserNavBar />
      </div>

      <WelcomeMessageModal
        isOpen={showWelcomeModal}
        onClose={() => setShowWelcomeModal(false)}
        userName=""
      />

      <div
        style={{
          maxWidth: "1200px",
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
          <img
            src={pawpedia || "/placeholder.svg"}
            alt="Pets lineup"
            style={{
              width: "130%",
              maxWidth: "470px",
              height: "auto",
              objectFit: "contain",
              marginTop: "0",
              marginBottom: "-20px",
              display: "block",
            }}
          />

          {/* Search Bar with Autocomplete */}
          <div
            style={{
              display: "flex",
              flexDirection: "column",
              width: "100%",
              maxWidth: "500px",
              position: "relative",
              marginBottom: "30px",
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
                value={query}
                onChange={(e) => {
                  const val = e.target.value;
                  setQuery(val);
                  if (val.length < 2) {
                    setSuggestions([]);
                    setResults([]);
                  }
                }}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                placeholder="Search conditions, diseases or illnesses..."
                style={{
                  flexGrow: 1,
                  color: "rgba(0, 0, 0, 1)",
                  backgroundColor: "transparent",
                  outline: "none",
                  border: "none",
                  fontSize: "14px",
                }}
              />
              <button
                onClick={() => handleSearch()}
                style={{
                  backgroundColor: colors.coral,
                  color: "white",
                  fontSize: "13px",
                  padding: "6px 16px",
                  borderRadius: "9999px",
                  border: "none",
                  cursor: "pointer",
                }}
              >
                Search
              </button>
            </div>

            {/* Autocomplete Suggestions */}
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

          {/* Search Results */}
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
                  No relevant pet-related results found.
                </p>
              )
            )}
          </div>
        </div>
      </div>

      {/* AI Modal */}
      <AIModal />

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
