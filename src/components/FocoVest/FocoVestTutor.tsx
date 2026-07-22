import React, { useState, useRef, useEffect } from 'react';
import { useApp } from '../../context/AppContext';
import { Sparkles, Send, Trash2, Bot, User, RefreshCw, Cpu } from 'lucide-react';

export const FocoVestTutor: React.FC = () => {
  const { chatMessages, isChatLoading, sendChatMessage, clearChat } = useApp();
  const [inputText, setInputText] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [chatMessages, isChatLoading]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputText.trim() || isChatLoading) return;
    sendChatMessage(inputText);
    setInputText('');
  };

  const presetQuestions = [
    'Como resolver equações do 2º grau e aplicar o vértice da parábola no ENEM?',
    'Quais são os 5 elementos obrigatórios na Proposta de Intervenção da Redação ENEM?',
    'Explique a Primeira e Segunda Lei de Mendel na Genética com exemplos.',
    'Como calcular a Força Centrípeta e decompor vetores na Mecânica ITA?'
  ];

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Sparkles className="w-5 h-5 text-emerald-400" />
            Tutor IA Local & Gratuito
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Tire dúvidas de matérias, solicite exemplos práticos e explicações passo a passo.
          </p>
        </div>

        <button
          onClick={clearChat}
          className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 border border-slate-800 text-slate-400 hover:text-white rounded-xl text-xs flex items-center gap-1.5 transition"
        >
          <Trash2 className="w-3.5 h-3.5" />
          <span>Limpar Histórico</span>
        </button>
      </div>

      {/* Chat Messages Window */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-4 md:p-6 min-h-[380px] max-h-[500px] overflow-y-auto flex flex-col gap-4 shadow-xl">
        {chatMessages.map((msg) => (
          <div
            key={msg.id}
            className={`flex items-start gap-3 ${msg.sender === 'USER' ? 'flex-row-reverse' : ''}`}
          >
            <div
              className={`w-8 h-8 rounded-xl flex items-center justify-center font-bold shrink-0 ${
                msg.sender === 'USER'
                  ? 'bg-indigo-600 text-white'
                  : 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
              }`}
            >
              {msg.sender === 'USER' ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
            </div>

            <div
              className={`max-w-[85%] rounded-2xl p-4 text-xs leading-relaxed space-y-2 ${
                msg.sender === 'USER'
                  ? 'bg-indigo-600 text-white rounded-tr-none'
                  : 'bg-slate-950 border border-slate-800 text-slate-200 rounded-tl-none'
              }`}
            >
              <p className="whitespace-pre-wrap">{msg.text}</p>
              <p className="text-[10px] opacity-60 text-right">
                {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </p>
            </div>
          </div>
        ))}

        {isChatLoading && (
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-xl bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 flex items-center justify-center animate-pulse">
              <Bot className="w-4 h-4" />
            </div>
            <div className="p-3 bg-slate-950 border border-slate-800 rounded-2xl text-xs text-slate-400 flex items-center gap-2">
              <RefreshCw className="w-3.5 h-3.5 animate-spin text-emerald-400" />
              <span>O Tutor IA está formulando a explicação didática...</span>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Preset Questions Suggestions */}
      <div className="space-y-1.5">
        <p className="text-[11px] font-bold text-slate-400 uppercase tracking-wider flex items-center gap-1">
          <Cpu className="w-3 h-3 text-emerald-400" /> Sugestões Rápidas:
        </p>
        <div className="flex gap-2 overflow-x-auto pb-1">
          {presetQuestions.map((q, idx) => (
            <button
              key={idx}
              onClick={() => sendChatMessage(q)}
              className="px-3 py-1.5 bg-slate-900 hover:bg-slate-800 border border-slate-800 rounded-xl text-[11px] text-slate-300 hover:text-white shrink-0 text-left transition"
            >
              {q}
            </button>
          ))}
        </div>
      </div>

      {/* Input Box */}
      <form onSubmit={handleSubmit} className="flex gap-2">
        <input
          type="text"
          placeholder="Digite sua dúvida de estudos para o tutor..."
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          disabled={isChatLoading}
          className="flex-1 bg-slate-900 border border-slate-800 rounded-2xl px-4 py-3 text-xs text-slate-100 focus:outline-none focus:border-emerald-500 disabled:opacity-50"
        />
        <button
          type="submit"
          disabled={isChatLoading || !inputText.trim()}
          className="px-5 py-3 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-2xl text-xs flex items-center justify-center gap-2 transition disabled:opacity-50 shadow-lg shadow-emerald-500/20"
        >
          <Send className="w-4 h-4" />
          <span>Enviar</span>
        </button>
      </form>
    </div>
  );
};
