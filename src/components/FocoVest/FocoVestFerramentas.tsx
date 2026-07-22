import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { StudyError } from '../../types';
import { Timer, Play, Pause, RotateCcw, AlertTriangle, Plus, Trash2, Edit3, X } from 'lucide-react';

export const FocoVestFerramentas: React.FC = () => {
  const {
    pomodoroSecondsLeft,
    pomodoroIsRunning,
    pomodoroBlockMinutes,
    selectPomodoroBlock,
    startPomodoro,
    pausePomodoro,
    resetPomodoro,
    errors,
    addNewError,
    updateError,
    deleteError
  } = useApp();

  const [subject, setSubject] = useState('Física - Mecânica');
  const [questionText, setQuestionText] = useState('');
  const [errorReason, setErrorReason] = useState('');
  const [correctConcept, setCorrectConcept] = useState('');

  // Edit Error State
  const [editingError, setEditingError] = useState<StudyError | null>(null);

  const formatTime = (totalSec: number) => {
    const m = Math.floor(totalSec / 60);
    const s = totalSec % 60;
    return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
  };

  const handleAddError = (e: React.FormEvent) => {
    e.preventDefault();
    if (!questionText.trim() || !errorReason.trim()) return;
    addNewError(subject, questionText, errorReason, correctConcept);
    setQuestionText('');
    setErrorReason('');
    setCorrectConcept('');
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingError || !editingError.questionText.trim()) return;
    updateError(editingError);
    setEditingError(null);
  };

  return (
    <div className="space-y-6">
      {/* Pomodoro Section */}
      <div className="bg-slate-900 border border-slate-800 rounded-3xl p-6 md:p-8 space-y-6 shadow-2xl text-center">
        <div className="flex items-center justify-center gap-2 text-amber-400 font-extrabold text-sm uppercase tracking-wider">
          <Timer className="w-5 h-5" />
          <span>Cronômetro Pomodoro de Foco Imersivo</span>
        </div>

        <div className="text-6xl md:text-7xl font-mono font-extrabold text-white tracking-widest my-4">
          {formatTime(pomodoroSecondsLeft)}
        </div>

        {/* Time Blocks Selection */}
        <div className="flex items-center justify-center gap-2">
          {[15, 25, 50].map((mins) => (
            <button
              key={mins}
              onClick={() => selectPomodoroBlock(mins)}
              className={`px-4 py-1.5 rounded-xl text-xs font-bold transition ${
                pomodoroBlockMinutes === mins
                  ? 'bg-amber-500 text-slate-950 font-extrabold shadow-md'
                  : 'bg-slate-950 text-slate-400 hover:text-white border border-slate-800'
              }`}
            >
              {mins} Minutos
            </button>
          ))}
        </div>

        {/* Control buttons */}
        <div className="flex items-center justify-center gap-3 pt-2">
          {!pomodoroIsRunning ? (
            <button
              onClick={startPomodoro}
              className="px-6 py-3 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-2xl text-xs flex items-center gap-2 transition shadow-lg shadow-emerald-500/20"
            >
              <Play className="w-4 h-4 fill-slate-950" />
              <span>Iniciar Foco</span>
            </button>
          ) : (
            <button
              onClick={pausePomodoro}
              className="px-6 py-3 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-2xl text-xs flex items-center gap-2 transition shadow-lg shadow-amber-500/20"
            >
              <Pause className="w-4 h-4 fill-slate-950" />
              <span>Pausar</span>
            </button>
          )}

          <button
            onClick={resetPomodoro}
            className="px-4 py-3 bg-slate-950 hover:bg-slate-800 text-slate-300 font-bold rounded-2xl text-xs flex items-center gap-2 border border-slate-800 transition"
          >
            <RotateCcw className="w-4 h-4" />
            <span>Resetar</span>
          </button>
        </div>
      </div>

      {/* Caderno de Erros Section */}
      <div className="space-y-4">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <AlertTriangle className="w-5 h-5 text-red-400" />
            Caderno de Erros Estratégico
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Registre onde você errou nas questões e garanta que não cometerá o mesmo deslize na prova real.
          </p>
        </div>

        {/* Add Error Form */}
        <form onSubmit={handleAddError} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">Matéria / Frente</label>
              <input
                type="text"
                placeholder="Ex: Química - Estequiometria"
                value={subject}
                onChange={(e) => setSubject(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">Enunciado / Questão Sintetizada</label>
              <input
                type="text"
                placeholder="Ex: Questão sobre rendimento de reações com reagente em excesso..."
                value={questionText}
                onChange={(e) => setQuestionText(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                required
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">Motivo do Erro (O que me fez errar?)</label>
              <input
                type="text"
                placeholder="Ex: Erro de conta na conversão de gramas para mols..."
                value={errorReason}
                onChange={(e) => setErrorReason(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-400 mb-1">Conceito Correto para a Prova</label>
              <input
                type="text"
                placeholder="Ex: Sempre identificar primeiro qual reagente limita a reação..."
                value={correctConcept}
                onChange={(e) => setCorrectConcept(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-2.5 bg-red-600 hover:bg-red-500 text-white font-bold rounded-xl text-xs transition shadow-lg shadow-red-600/20"
          >
            Registrar no Caderno de Erros
          </button>
        </form>

        {/* Existing Errors */}
        <div className="space-y-3">
          {errors.map((err) => (
            <div key={err.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-2 shadow-md">
              <div className="flex items-center justify-between">
                <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-full bg-red-500/10 text-red-400 border border-red-500/20">
                  {err.subject}
                </span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => setEditingError(err)}
                    className="text-slate-400 hover:text-amber-400 p-1 rounded-lg hover:bg-slate-800 transition"
                    title="Editar registro de erro"
                  >
                    <Edit3 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => deleteError(err.id)}
                    className="text-slate-500 hover:text-red-400 p-1 rounded-lg hover:bg-slate-800 transition"
                    title="Excluir"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>

              <h4 className="text-xs font-bold text-slate-100">{err.questionText}</h4>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-[11px] pt-1">
                <div className="p-2.5 bg-slate-950 rounded-xl border border-slate-800">
                  <p className="font-bold text-red-400">Motivo do Erro:</p>
                  <p className="text-slate-300 mt-0.5">{err.errorReason}</p>
                </div>

                <div className="p-2.5 bg-slate-950 rounded-xl border border-slate-800">
                  <p className="font-bold text-emerald-400">Conceito Correto:</p>
                  <p className="text-slate-300 mt-0.5">{err.correctConcept}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* EDIT MODAL FOR ERRORS */}
      {editingError && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-red-400" />
                Editar Registro do Caderno de Erros
              </h3>
              <button
                onClick={() => setEditingError(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Matéria / Frente</label>
                <input
                  type="text"
                  value={editingError.subject}
                  onChange={(e) => setEditingError({ ...editingError, subject: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Enunciado / Questão Sintetizada</label>
                <input
                  type="text"
                  value={editingError.questionText}
                  onChange={(e) => setEditingError({ ...editingError, questionText: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Motivo do Erro</label>
                <input
                  type="text"
                  value={editingError.errorReason}
                  onChange={(e) => setEditingError({ ...editingError, errorReason: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Conceito Correto para a Prova</label>
                <input
                  type="text"
                  value={editingError.correctConcept}
                  onChange={(e) => setEditingError({ ...editingError, correctConcept: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingError(null)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-red-600 hover:bg-red-500 text-white font-bold rounded-xl text-xs transition shadow-lg shadow-red-600/20"
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
