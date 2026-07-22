import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Flashcard } from '../../types';
import { Layers, RotateCw, Plus, Trash2, Check, HelpCircle, Flame, Edit3, X } from 'lucide-react';

export const FocoVestAnki: React.FC = () => {
  const { flashcards, addNewFlashcard, updateFlashcard, answerFlashcard, deleteFlashcard } = useApp();

  const [questionInput, setQuestionInput] = useState('');
  const [answerInput, setAnswerInput] = useState('');
  const [activeCardIndex, setActiveCardIndex] = useState(0);
  const [isFlipped, setIsFlipped] = useState(false);

  // Edit Flashcard state
  const [editingCard, setEditingCard] = useState<Flashcard | null>(null);

  // Cards due for review
  const dueCards = flashcards.filter((c) => c.dueDate <= Date.now());
  const currentCard = dueCards[activeCardIndex] || dueCards[0] || flashcards[0];

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!questionInput.trim() || !answerInput.trim()) return;
    addNewFlashcard(questionInput, answerInput);
    setQuestionInput('');
    setAnswerInput('');
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingCard || !editingCard.question.trim() || !editingCard.answer.trim()) return;
    updateFlashcard(editingCard);
    setEditingCard(null);
  };

  const handleAnswer = (difficulty: number) => {
    if (!currentCard) return;
    answerFlashcard(currentCard, difficulty);
    setIsFlipped(false);
    if (activeCardIndex >= dueCards.length - 1) {
      setActiveCardIndex(0);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Layers className="w-5 h-5 text-amber-400" />
            Flashcards - Repetição Espaçada
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Algoritmo inteligente de retenção de memória de longo prazo (Repetição Espaçada).
          </p>
        </div>

        <div className="flex items-center gap-3 bg-slate-900 border border-slate-800 px-4 py-2 rounded-2xl">
          <Flame className="w-5 h-5 text-amber-500" />
          <div>
            <p className="text-xs font-bold text-white">{dueCards.length} cartões para revisar</p>
            <p className="text-[10px] text-slate-400">{flashcards.length} cartões no total</p>
          </div>
        </div>
      </div>

      {/* Review Section */}
      {dueCards.length > 0 && currentCard ? (
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 md:p-8 space-y-6 shadow-2xl text-center relative overflow-hidden">
          <div className="flex items-center justify-between text-xs text-slate-400 border-b border-slate-800/80 pb-3">
            <span>Revisão {activeCardIndex + 1} de {dueCards.length}</span>
            <span className="text-amber-400 font-bold">Fator de Facilidade: {currentCard.easeFactor.toFixed(2)}</span>
          </div>

          <div
            onClick={() => setIsFlipped(!isFlipped)}
            className="min-h-[180px] flex flex-col items-center justify-center p-6 bg-slate-950 border border-slate-800 rounded-2xl cursor-pointer hover:border-amber-500/40 transition space-y-4"
          >
            {!isFlipped ? (
              <>
                <HelpCircle className="w-8 h-8 text-amber-400 opacity-80" />
                <h3 className="text-lg md:text-xl font-bold text-slate-100">{currentCard.question}</h3>
                <p className="text-xs text-slate-500 flex items-center gap-1">
                  <RotateCw className="w-3.5 h-3.5" /> Clique no cartão para ver a resposta
                </p>
              </>
            ) : (
              <>
                <Check className="w-8 h-8 text-emerald-400" />
                <h3 className="text-lg md:text-xl font-bold text-emerald-300">{currentCard.answer}</h3>
                <p className="text-xs text-slate-400">Pergunta: {currentCard.question}</p>
              </>
            )}
          </div>

          {/* Difficulty Rating Buttons */}
          {isFlipped && (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3 pt-2">
              <button
                onClick={() => handleAnswer(1)}
                className="p-3 rounded-xl bg-red-500/15 border border-red-500/30 text-red-400 hover:bg-red-500/25 font-bold text-xs transition"
              >
                Errei / Muito Difícil (1d)
              </button>
              <button
                onClick={() => handleAnswer(3)}
                className="p-3 rounded-xl bg-amber-500/15 border border-amber-500/30 text-amber-400 hover:bg-amber-500/25 font-bold text-xs transition"
              >
                Difícil / Requer Aumento (2d)
              </button>
              <button
                onClick={() => handleAnswer(4)}
                className="p-3 rounded-xl bg-indigo-500/15 border border-indigo-500/30 text-indigo-300 hover:bg-indigo-500/25 font-bold text-xs transition"
              >
                Bom / Normal (4d)
              </button>
              <button
                onClick={() => handleAnswer(5)}
                className="p-3 rounded-xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-400 hover:bg-emerald-500/25 font-bold text-xs transition"
              >
                Fácil / Dominado (7d+)
              </button>
            </div>
          )}
        </div>
      ) : (
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-8 text-center space-y-2">
          <Check className="w-10 h-10 text-emerald-400 mx-auto" />
          <h3 className="text-lg font-bold text-slate-100">Sua fila de revisão está limpa!</h3>
          <p className="text-xs text-slate-400">Todos os flashcards pendentes para hoje foram revisados. Adicione novos cartões abaixo.</p>
        </div>
      )}

      {/* Add Flashcard Form */}
      <form onSubmit={handleCreate} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <Plus className="w-4 h-4 text-amber-400" />
          Criar Novo Flashcard
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Frente (Pergunta / Conceito)</label>
            <input
              type="text"
              placeholder="Ex: Qual a fórmula da energia cinética?"
              value={questionInput}
              onChange={(e) => setQuestionInput(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Verso (Resposta / Definição)</label>
            <input
              type="text"
              placeholder="Ex: Ec = (m * v²) / 2"
              value={answerInput}
              onChange={(e) => setAnswerInput(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-2.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition shadow-lg shadow-amber-500/20"
        >
          Salvar Flashcard
        </button>
      </form>

      {/* Existing Flashcards List */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold text-slate-300">Todos os Cartões Cadastrados ({flashcards.length})</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {flashcards.map((card) => (
            <div key={card.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex items-start justify-between gap-3 shadow-md">
              <div className="space-y-1">
                <p className="text-xs font-bold text-slate-100">{card.question}</p>
                <p className="text-xs text-emerald-400 font-semibold">{card.answer}</p>
                <p className="text-[10px] text-slate-500">
                  Repetições: {card.repetitions} • Intervalo: {card.intervalDays}d
                </p>
              </div>

              <div className="flex items-center gap-1">
                <button
                  onClick={() => setEditingCard(card)}
                  className="text-slate-400 hover:text-amber-400 p-1.5 rounded-lg hover:bg-slate-800 transition"
                  title="Editar Flashcard"
                >
                  <Edit3 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => deleteFlashcard(card.id)}
                  className="text-slate-500 hover:text-red-400 p-1.5 rounded-lg hover:bg-slate-800 transition"
                  title="Excluir"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* EDIT MODAL */}
      {editingCard && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-amber-400" />
                Editar Flashcard
              </h3>
              <button
                onClick={() => setEditingCard(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Frente (Pergunta / Conceito)</label>
                <input
                  type="text"
                  value={editingCard.question}
                  onChange={(e) => setEditingCard({ ...editingCard, question: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Verso (Resposta / Definição)</label>
                <input
                  type="text"
                  value={editingCard.answer}
                  onChange={(e) => setEditingCard({ ...editingCard, answer: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingCard(null)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition shadow-lg shadow-amber-500/20"
                >
                  Salvar Alterações
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
