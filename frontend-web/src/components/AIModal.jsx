  
import { useState, useEffect, useRef } from "react";
import { X, Send, Bot, User } from "lucide-react";

export default function AIModal() {
  const [messages, setMessages] = useState([
    {
      sender: "ai",
      text: "Hello! I'm your AI Pet Health Assistant. Please describe your pet's symptoms, and I'll help guide you. Remember, I provide general information only — always consult your veterinarian for specific medical advice."
    }
  ]);
  const [input, setInput] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const modalRef = useRef(null);
  const chatContainerRef = useRef(null);

  // Auto-scroll to bottom when new messages are added
  useEffect(() => {
    if (chatContainerRef.current) {
      chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
    }
  }, [messages]);

  // Close modal when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    }

    if (isOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [isOpen]);

  // Close modal on ESC
  useEffect(() => {
    function handleEscKey(event) {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    }

    if (isOpen) {
      document.addEventListener("keydown", handleEscKey);
    } else {
      document.removeEventListener("keydown", handleEscKey);
    }

    return () => {
      document.removeEventListener("keydown", handleEscKey);
    };
  }, [isOpen]);

  // Generate response using Hugging Face Inference API with Qwen-7B-Chat
  const handleSend = async () => {
    if (!input.trim()) return;

    // Add user message to chat
    const userMessage = { sender: "user", text: input };
    setMessages(prev => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      // Create a concise prompt for Qwen-7B-Chat
      const prompt = `Veterinary assistant AI: Respond to "${input}". Suggest causes, when to see a vet, and note to consult a veterinarian.`;

      // Call the Hugging Face Inference API
      const response = await fetch(
        "https://api-inference.huggingface.co/models/Qwen/Qwen-7B-Chat",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${import.meta.env.VITE_HUGGINGFACE_API_KEY}`,
          },
          body: JSON.stringify({
            inputs: prompt,
            parameters: {
              max_new_tokens: 150, // Reduced for concise responses
              temperature: 0.7,
              top_p: 0.9,
              return_full_text: false
            }
          }),
        }
      );

      const result = await response.json();
      let aiResponse = "";

      // Handle the response
      if (Array.isArray(result) && result.length > 0 && result[0].generated_text) {
        aiResponse = result[0].generated_text.trim();
      } else if (result.error) {
        if (result.error.includes("loading")) {
          aiResponse = "The AI model is still loading. Please try again in a moment.";
        } else {
          throw new Error(result.error);
        }
      } else {
        aiResponse = "I couldn't generate a response. Please try again.";
      }

      // Add AI response to chat
      setMessages(prev => [...prev, { sender: "ai", text: aiResponse }]);
    } catch (error) {
      console.error("Error generating response:", error.message);
      const errorMessage = `I'm having trouble processing your request: ${error.message}. For any concerning pet symptoms, it's best to consult with your veterinarian for proper diagnosis and treatment.`;
      setMessages(prev => [...prev, { sender: "ai", text: errorMessage }]);
    }

    setIsLoading(false);
  };

  return (
    <>
      {!isOpen && (
        <button
          onClick={() => setIsOpen(true)}
          className="fixed bottom-5 left-5 h-12 w-12 rounded-full bg-[#042C3C] hover:bg-[#EA6C7B] border border-white text-white flex items-center justify-center shadow-lg transition-all"
        >
          <Bot className="h-6 w-6" />
        </button>
      )}

      {isOpen && <div className="fixed inset-0 bg-black/70 z-40" />}

      {isOpen && (
        <div
          ref={modalRef}
          className="fixed z-50 top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-full max-w-md bg-white rounded-lg rounded-b-2xl shadow-lg flex flex-col max-h-[80vh]"
        >
          {/* Header */}
          <div className="flex justify-between items-center p-4 border-b">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-[#042C3C] flex items-center justify-center">
                <Bot className="h-5 w-5 text-white" />
              </div>
              <div>
                <h2 className="font-semibold text-gray-800">Pet Health Assistant</h2>
                <p className="text-xs text-[#8A973F]">Online</p>
              </div>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="h-8 w-8 flex items-center justify-center rounded-full hover:bg-gray-100"
            >
              <X className="h-5 w-5 text-gray-500" />
            </button>
          </div>

          {/* Chat Container */}
          <div 
            ref={chatContainerRef}
            className="flex-1 p-4 overflow-y-auto flex flex-col gap-3 rounded-b-2xl"
          >   
            {messages.map((message, index) => (
              <div 
                key={index} 
                className={`flex ${message.sender === "user" ? "justify-end" : "justify-start"}`}
              >
                {message.sender === "ai" && (
                  <div className="w-8 h-8 rounded-full bg-[#042C3C] flex items-center justify-center mr-2 self-end">
                    <Bot className="h-4 w-4 text-white" />
                  </div>
                )}
                <div 
                  className={`max-w-[75%] p-3 rounded-lg ${
                    message.sender === "user" 
                      ? "bg-[#EA6C7B] text-white rounded-br-none" 
                      : "bg-[#FFF7EC] text-gray-800 rounded-bl-none"
                  }`}
                >
                  <p className="text-sm">{message.text}</p>
                </div>
                {message.sender === "user" && (
                  <div className="w-8 h-8 rounded-full bg-gray-300 flex items-center justify-center ml-2 self-end">
                    <User className="h-4 w-4 text-gray-600" />
                  </div>
                )}
              </div>
            ))}
            {isLoading && (
              <div className="flex justify-start">
                <div className="w-8 h-8 rounded-full bg-[#042C3C] flex items-center justify-center mr-2">
                  <Bot className="h-4 w-4 text-white" />
                </div>
                <div className="bg-gray-200 p-3 rounded-lg rounded-bl-none">
                  <div className="flex gap-1">
                    <div className="w-2 h-2 rounded-full bg-gray-500 animate-bounce" style={{ animationDelay: "0ms" }}></div>
                    <div className="w-2 h-2 rounded-full bg-gray-500 animate-bounce" style={{ animationDelay: "300ms" }}></div>
                    <div className="w-2 h-2 rounded-full bg-gray-500 animate-bounce" style={{ animationDelay: "600ms" }}></div>
                  </div>
                </div>
              </div>
            )}
          </div>

          {/* Input Area */}
          <div className="p-3 border-t bg-white">
            <p className="text-xs text-gray-500 mb-2">
              This AI assistant provides general information only. Always consult your veterinarian for specific medical advice.
            </p>
            <div className="relative flex items-center">
              <input
                type="text"
                placeholder="Type your message..."
                className="w-full px-4 py-2 pr-12 rounded-full border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#8A973F] focus:border-transparent placeholder-gray-400 text-[#042C3C]"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSend();
                  }
                }}
              />
              <button
                className="absolute right-1 h-8 w-8 flex items-center justify-center rounded-full bg-[#8A973F] hover:bg-[#73863B] text-white"
                onClick={handleSend}
                disabled={isLoading || !input.trim()}
              >
                <Send className="h-4 w-4" />
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
