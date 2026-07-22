import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Simulado } from '../../types';
import { Award, Plus, Edit3, Trash2, X } from 'lucide-react';

export const FocoVestSimulados: React.FC = () => {
  const { simulados, addNewSimulado, updateSimulado, deleteSimulado } = useApp();

  const [subject, setSubject] = useState('ENEM 2026 Dia 1');
  const [totalQuestions, setTotalQuestions] = useState(90);
  const [correctAnswers, setCorrectAnswers] = useState(70);
  const [durationMinutes, setDurationMinutes] = useState(270);

  // Edit Modal State
  const [editingSimulado, setEditingSimulado] = useState<Simulado | null>(null);

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (totalQuestions <= 0) return;
    addNewSimulado(subject, totalQuestions, correctAnswers, durationMinutes);
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingSimulado || editingSimulado.totalQuestions <= 0) return;
    updateSimulado(editingSimulado);
    setEditingSimulado(null);
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <Award className="w-5 h-5 text-amber-400" />
          Registro e Métricas de Simulados
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Acompanhe seu percentual de acertos, tempo por questão e rendimento ao longo das provas simuladas.
        </p>
      </div>

      {/* Add Simulado Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <Plus className="w-4 h-4 text-amber-400" />
          Registrar Novo Simulado
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Nome / Prova</label>
            <input
              type="text"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Total de Questões</label>
            <input
              type="number"
              value={totalQuestions}
              onChange={(e) => setTotalQuestions(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Questões Acertadas</label>
            <input
              type="number"
              value={correctAnswers}
              onChange={(e) => setCorrectAnswers(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Duração (Minutos)</label>
            <input
              type="number"
              value={durationMinutes}
              onChange={(e) => setDurationMinutes(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-2.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition shadow-lg shadow-amber-500/20"
        >
          Salvar Resultado de Simulado
        </button>
      </form>

      {/* History Grid */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold text-slate-300">Histórico de Provas ({simulados.length})</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {simulados.map((sim) => {
            const pct = Math.round((sim.correctAnswers / Math.max(1, sim.totalQuestions)) * 100);
            const minutesPerQuestion = (sim.durationMinutes / Math.max(1, sim.totalQuestions)).toFixed(1);

            return (
              <div
                key={sim.id}
                className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-md hover:border-amber-500/30 transition"
              >
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-slate-300">{sim.subject}</span>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-extrabold text-amber-400 bg-amber-500/10 px-2.5 py-0.5 rounded-full border border-amber-500/20">
                      {pct}% de Acertos
                    </span>
                    <button
                      onClick={() => setEditingSimulado(sim)}
                      className="p-1 text-slate-400 hover:text-amber-400 rounded-lg hover:bg-slate-800 transition"
                      title="Editar Simulado"
                    >
                      <Edit3 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => deleteSimulado(sim.id)}
                      className="p-1 text-slate-500 hover:text-red-400 rounded-lg hover:bg-slate-800 transition"
                      title="Excluir Simulado"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-2 py-2 bg-slate-950 rounded-xl p-3 text-center border border-slate-800/80">
                  <div>
                    <p className="text-[10px] text-slate-400">Acertos</p>
                    <p className="text-sm font-bold text-emerald-400">{sim.correctAnswers} / {sim.totalQuestions}</p>
                  </div>
                  <div>
                    <p className="text-[10px] text-slate-400">Tempo Total</p>
                    <p className="text-sm font-bold text-indigo-300">{sim.durationMinutes} min</p>
                  </div>
                  <div>
                    <p className="text-[10px] text-slate-400">Min / Questão</p>
                    <p className="text-sm font-bold text-amber-300">{minutesPerQuestion} min</p>
                  </div>
                </div>

                {/* Progress bar */}
                <div className="w-full bg-slate-950 rounded-full h-2 overflow-hidden border border-slate-800">
                  <div
                    className="bg-gradient-to-r from-emerald-500 to-amber-500 h-2 rounded-full"
                    style={{ width: `${pct}%` }}
                  />
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* EDIT MODAL */}
      {editingSimulado && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-amber-400" />
                Editar Resultado de Simulado
              </h3>
              <button
                onClick={() => setEditingSimulado(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Nome / Prova</label>
                <input
                  type="text"
                  value={editingSimulado.subject}
                  onChange={(e) => setEditingSimulado({ ...editingSimulado, subject: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Total de Questões</label>
                  <input
                    type="number"
                    value={editingSimulado.totalQuestions}
                    onChange={(e) => setEditingSimulado({ ...editingSimulado, totalQuestions: Number(e.target.value) })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Questões Acertadas</label>
                  <input
                    type="number"
                    value={editingSimulado.correctAnswers}
                    onChange={(e) => setEditingSimulado({ ...editingSimulado, correctAnswers: Number(e.target.value) })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Duração (Minutos)</label>
                <input
                  type="number"
                  value={editingSimulado.durationMinutes}
                  onChange={(e) => setEditingSimulado({ ...editingSimulado, durationMinutes: Number(e.target.value) })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingSimulado(null)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20 transition"
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
