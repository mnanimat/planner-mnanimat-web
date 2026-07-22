import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Essay } from '../../types';
import { FileText, Sparkles, Trash2, Award, RefreshCw, Edit3, X } from 'lucide-react';

export const FocoVestRedacao: React.FC = () => {
  const { essays, isEssayCorrecting, correctEssay, updateEssay, deleteEssay } = useApp();

  const [title, setTitle] = useState('');
  const [text, setText] = useState('');

  // Edit Essay Modal
  const [editingEssay, setEditingEssay] = useState<Essay | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !text.trim() || isEssayCorrecting) return;
    correctEssay(title, text);
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingEssay || !editingEssay.title.trim()) return;
    updateEssay(editingEssay);
    setEditingEssay(null);
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <FileText className="w-5 h-5 text-amber-400" />
          Laboratório e Correção Oficial de Redação ENEM
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Receba notas detalhadas de 0 a 200 nas 5 competências do ENEM com plano de ação para nota 1000.
        </p>
      </div>

      {/* Write Essay Form */}
      <form onSubmit={handleSubmit} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-xl">
        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1">Tema da Redação / Título</label>
          <input
            type="text"
            placeholder="Ex: Desafios para a valorização de comunidades e povos tradicionais no Brasil"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
            required
          />
        </div>

        <div>
          <div className="flex justify-between items-center mb-1">
            <label className="block text-xs font-semibold text-slate-300">Texto Dissertativo-Argumentativo</label>
            <span className="text-[10px] text-slate-400">{text.split(/\s+/).filter(Boolean).length} palavras</span>
          </div>
          <textarea
            rows={8}
            placeholder="Cole ou digite aqui o texto da sua redação..."
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 leading-relaxed font-sans"
            required
          />
        </div>

        <button
          type="submit"
          disabled={isEssayCorrecting}
          className="w-full py-3 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 disabled:opacity-50"
        >
          {isEssayCorrecting ? (
            <>
              <RefreshCw className="w-4 h-4 animate-spin" />
              <span>Avaliando as 5 Competências do ENEM...</span>
            </>
          ) : (
            <>
              <Sparkles className="w-4 h-4" />
              <span>Corrigir Redação Agora com IA</span>
            </>
          )}
        </button>
      </form>

      {/* Corrected Essays History */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-slate-300 flex items-center gap-2">
          <Award className="w-4 h-4 text-amber-400" />
          Redações Corrigidas no Seu Histórico ({essays.length})
        </h3>

        {essays.map((essay) => (
          <div key={essay.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-lg">
            <div className="flex items-start justify-between gap-3 border-b border-slate-800 pb-3">
              <div>
                <span className="text-[10px] font-extrabold text-amber-400 uppercase bg-amber-500/10 px-2 py-0.5 rounded-full border border-amber-500/20">
                  Avaliador ENEM
                </span>
                <h4 className="text-base font-bold text-slate-100 mt-1">{essay.title}</h4>
                <p className="text-[10px] text-slate-400">
                  {new Date(essay.timestamp).toLocaleDateString('pt-BR')} às {new Date(essay.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                </p>
              </div>

              <div className="flex items-center gap-1">
                <button
                  onClick={() => setEditingEssay(essay)}
                  className="text-slate-400 hover:text-amber-400 p-2 rounded-xl hover:bg-slate-800 transition"
                  title="Editar Redação"
                >
                  <Edit3 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => deleteEssay(essay.id)}
                  className="text-slate-500 hover:text-red-400 p-2 rounded-xl hover:bg-slate-800 transition"
                  title="Excluir"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* AI Feedback */}
            <div className="p-4 bg-slate-950 border border-slate-800/80 rounded-xl space-y-2 text-xs text-slate-200 leading-relaxed font-mono whitespace-pre-wrap">
              {essay.feedback}
            </div>
          </div>
        ))}
      </div>

      {/* EDIT ESSAY MODAL */}
      {editingEssay && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-xl p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-amber-400" />
                Editar Redação e Feedback
              </h3>
              <button
                onClick={() => setEditingEssay(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Tema / Título</label>
                <input
                  type="text"
                  value={editingEssay.title}
                  onChange={(e) => setEditingEssay({ ...editingEssay, title: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Texto da Redação</label>
                <textarea
                  rows={5}
                  value={editingEssay.text}
                  onChange={(e) => setEditingEssay({ ...editingEssay, text: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 font-sans"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Avaliação / Feedback</label>
                <textarea
                  rows={5}
                  value={editingEssay.feedback}
                  onChange={(e) => setEditingEssay({ ...editingEssay, feedback: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 font-mono"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingEssay(null)}
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
