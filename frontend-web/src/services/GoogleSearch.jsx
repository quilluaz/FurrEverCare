// components/GoogleSearch.jsx
import { useEffect } from "react";

export default function GoogleSearch() {
  useEffect(() => {
    const script = document.createElement("script");
    script.src = "https://cse.google.com/cse.js?cx=9216ec274a0e74c8a";
    script.async = true;
    document.body.appendChild(script);
  }, []);

  return <div className="gcse-search" />;
}
