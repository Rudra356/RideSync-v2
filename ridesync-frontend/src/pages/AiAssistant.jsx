import React, { useEffect, useRef, useState } from "react";
import api, { initializeCsrf } from "../api";
import "../styles/ai-assistant.css";

function formatMessage(text) {
  // Safely render URLs as clickable links while preserving plain text.
  const parts = text.split(/(https?:\/\/[^\s)>\]]+)/g);

  return parts.map((part, index) => {
    if (/^https?:\/\//i.test(part)) {
      return (
        <a
          key={index}
          href={part.replace(/[.,;]+$/, "")}
          target="_blank"
          rel="noopener noreferrer"
        >
          {part.replace(/[.,;]+$/, "")}
        </a>
      );
    }

    return <React.Fragment key={index}>{part}</React.Fragment>;
  });
}

export default function AiAssistant() {
  const [messages, setMessages] = useState([
    {
      role: "assistant",
      content:
        "Welcome to RideSync AI. I'm your automotive research assistant. Ask me about vehicle maintenance, troubleshooting, specifications, or automotive technology.",
    },
  ]);

  const [prompt, setPrompt] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, loading]);

  const sendMessage = async (event) => {
    event.preventDefault();

    const question = prompt.trim();

    if (!question || loading) return;

    if (question.length > 2000) {
      setError("Your message cannot exceed 2000 characters.");
      return;
    }

    setError("");
    setPrompt("");

    setMessages((previous) => [
      ...previous,
      { role: "user", content: question },
    ]);

    setLoading(true);

    try {
      await initializeCsrf();

      const response = await api.post("/api/ai/chat", {
        prompt: question,
      });

      const answer = response.data?.response;

      if (!answer) {
        throw new Error("The AI returned an empty response.");
      }

      setMessages((previous) => [
        ...previous,
        { role: "assistant", content: answer },
      ]);
    } catch (err) {
      console.error("RideSync AI request failed:", err);

      const status = err.response?.status;

      if (status === 401 || status === 403) {
        setError(
          "Your session may have expired or access was denied. Please log in again and retry."
        );
      } else if (status === 429) {
        setError("AI request limit reached. Please try again later.");
      } else {
        setError(
          err.response?.data?.message ||
            err.message ||
            "Unable to connect to RideSync AI. Please try again."
        );
      }

      setPrompt(question);
    } finally {
      setLoading(false);
      inputRef.current?.focus();
    }
  };

  const suggestions = [
    "How often should I change motorcycle engine oil?",
    "What causes a motorcycle engine to overheat?",
    "How can I improve my bike's fuel efficiency?",
  ];

  return (
    <section className="rs-ai-page">
      <div className="rs-ai-container">
        <header className="rs-ai-header">
          <div>
            <span className="rs-ai-eyebrow">RIDESYNC INTELLIGENCE</span>
            <h1>AI Assistant<span>.</span></h1>
            <p>
              Automotive knowledge, maintenance guidance and research in
              one place.
            </p>
          </div>

          <div className="rs-ai-status">
            <span className="rs-ai-status-dot" />
            AI ASSISTANT
          </div>
        </header>

        <div className="rs-ai-workspace">
          <div className="rs-ai-chat-header">
            <div className="rs-ai-avatar">RS</div>

            <div>
              <h2>RideSync AI</h2>
              <span>Automotive Research Assistant</span>
            </div>

            <button
              type="button"
              className="rs-ai-clear"
              onClick={() => {
                setMessages([
                  {
                    role: "assistant",
                    content:
                      "New conversation started. What would you like to know about your vehicle?",
                  },
                ]);
                setError("");
              }}
              disabled={loading}
            >
              New chat ↗
            </button>
          </div>

          <div className="rs-ai-messages">
            {messages.map((message, index) => (
              <div
                key={index}
                className={`rs-ai-message ${
                  message.role === "user" ? "user" : "assistant"
                }`}
              >
                {message.role === "assistant" && (
                  <div className="rs-ai-message-avatar">RS</div>
                )}

                <div className="rs-ai-message-content">
                  <span className="rs-ai-message-label">
                    {message.role === "user" ? "YOU" : "RIDESYNC AI"}
                  </span>

                  <div className="rs-ai-message-text">
                    {formatMessage(message.content)}
                  </div>
                </div>
              </div>
            ))}

            {loading && (
              <div className="rs-ai-message assistant">
                <div className="rs-ai-message-avatar">RS</div>

                <div className="rs-ai-message-content">
                  <span className="rs-ai-message-label">RIDESYNC AI</span>
                  <div className="rs-ai-typing">
                    <span />
                    <span />
                    <span />
                    <small>Researching and generating response...</small>
                  </div>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {messages.length === 1 && (
            <div className="rs-ai-suggestions">
              <span>TRY ASKING</span>

              {suggestions.map((suggestion) => (
                <button
                  key={suggestion}
                  type="button"
                  onClick={() => setPrompt(suggestion)}
                >
                  {suggestion} <span>↗</span>
                </button>
              ))}
            </div>
          )}

          {error && (
            <div className="rs-ai-error" role="alert">
              {error}
            </div>
          )}

          <form className="rs-ai-input-area" onSubmit={sendMessage}>
            <textarea
              ref={inputRef}
              value={prompt}
              onChange={(event) => setPrompt(event.target.value)}
              onKeyDown={(event) => {
                if (event.key === "Enter" && !event.shiftKey) {
                  event.preventDefault();
                  sendMessage(event);
                }
              }}
              placeholder="Ask anything about your vehicle..."
              maxLength={2000}
              rows={2}
              disabled={loading}
            />

            <div className="rs-ai-input-footer">
              <span>{prompt.length}/2000 · Enter to send</span>

              <button
                type="submit"
                disabled={loading || !prompt.trim()}
              >
                {loading ? "Thinking..." : "Send message ↗"}
              </button>
            </div>
          </form>

          <p className="rs-ai-disclaimer">
            AI-generated information may contain errors. Verify important
            maintenance and safety decisions with your vehicle manufacturer
            or a qualified mechanic.
          </p>
        </div>
      </div>
    </section>
  );
}