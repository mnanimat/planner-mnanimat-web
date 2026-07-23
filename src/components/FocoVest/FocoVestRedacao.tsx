import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Essay } from '../../types';
import { FileText, ExternalLink, Copy, Check, Save, Trash2, Edit3, X, Sparkles, MessageSquare } from 'lucide-react';

const CHATGPT_CORRETOR_URL = 'https://chatgpt.com/g/g-6a4097e8a61c8191bf2837784b586203-corretor-de-redacoes-enem-fuvest-e-ita';

export const FocoVestRedacao: React.FC = () => {
  const { essays, correctEssay, updateEssay, deleteEssay } = useApp();

  const [title, setTitle] = useState('');
  const [text, setText] = useState('');
  const [copiedCurrent, setCopiedCurrent] = useState(false);
  const [copiedId, setCopiedId] = useState<number | null>(null);

  // Edit Essay Modal
  const [editingEssay, setEditingEssay] = useState<Essay | null>(null);

  const wordCount = text.split(/\s+/).filter(Boolean).length;

  const handleCopyText = (textToCopy: string, isCurrent: boolean, id?: number) => {
    navigator.clipboard.writeText(textToCopy);
    if (isCurrent) {
      setCopiedCurrent(true);
      setTimeout(() => setCopiedCurrent(false), 2000);
    } else if (id) {
      setCopiedId(id);
      setTimeout(() => setCopiedId(null), 2000);
    }
  };

  const handleSaveDraft = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !text.trim()) return;
    correctEssay(title, text);
    setTitle('');
    setText('');
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingEssay || !editingEssay.title.trim()) return;
    updateEssay(editingEssay);
    setEditingEssay(null);
  };

  return (
    <div className="space-y-6">
      {/* Header Title */}
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <FileText className="w-5 h-5 text-amber-400" />
          Laboratório de Redação & Corretor Especializado
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Escreva seus rascunhos com cópia rápida e utilize o Corretor de Redações ENEM, FUVEST e ITA no ChatGPT (OpenAI).
        </p>
      </div>

      {/* Featured ChatGPT Link Banner */}
      <div className="bg-gradient-to-r from-emerald-950 via-slate-900 to-indigo-950 border border-emerald-500/30 rounded-3xl p-6 text-slate-100 shadow-2xl relative overflow-hidden">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-5 relative z-10">
          <div className="space-y-2 max-w-xl">
            <span className="text-[11px] font-extrabold text-emerald-400 tracking-wider uppercase flex items-center gap-1.5 bg-emerald-500/10 px-3 py-1 rounded-full border border-emerald-500/20 w-fit">
              <Sparkles className="w-3.5 h-3.5" />
              Corretor Especializado de IA
            </span>
            <h3 className="text-lg md:text-xl font-extrabold text-white leading-tight">
              Corretor de Redação Especializado (ENEM, FUVEST e ITA)
            </h3>
            <p className="text-xs text-slate-300 dark:text-slate-100 leading-relaxed">
              Utilize o modelo especializado no Chat GPT (OpenAI) para avaliação detalhada das 5 competências do ENEM, critérios da FUVEST e alto nível do ITA.
            </p>
          </div>

          <a
            href={CHATGPT_CORRETOR_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="px-5 py-3.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-extrabold rounded-2xl text-xs md:text-sm flex items-center justify-center gap-2.5 transition shadow-lg shadow-emerald-500/20 shrink-0 self-start md:self-auto hover:scale-[1.02] active:scale-[0.98]"
          >
            <MessageSquare className="w-4 h-4 fill-slate-950" />
            <span>Abrir Corretor no ChatGPT</span>
            <ExternalLink className="w-4 h-4" />
          </a>
        </div>
      </div>

      {/* Rascunho Local & Ferramenta de Cópia Rápida */}
      <form onSubmit={handleSaveDraft} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-xl">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
            <Edit3 className="w-4 h-4 text-amber-400" />
            Ferramenta de Rascunho Local & Cópia Rápida
          </h3>
          <span className="text-[10px] text-slate-400 bg-slate-950 px-2.5 py-1 rounded-full border border-slate-800">
            {wordCount} palavras
          </span>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1">Tema da Redação / Título</label>
          <input
            type="text"
            placeholder="Ex: Os impactos da inteligência artificial na sociedade contemporânea"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
            required
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1">Texto Dissertativo-Argumentativo</label>
          <textarea
            rows={8}
            placeholder="Digite ou rascunhe seu texto aqui. Depois, copie com 1 clique para colar direto no ChatGPT..."
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 leading-relaxed font-sans"
            required
          />
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-2.5 pt-1">
          <button
            type="button"
            onClick={() => {
              const fullFormatted = `Tema: ${title}\n\nTexto:\n${text}`;
              handleCopyText(fullFormatted, true);
            }}
            disabled={!title.trim() && !text.trim()}
            className={`py-2.5 px-3 font-bold rounded-xl text-xs transition flex items-center justify-center gap-2 border disabled:opacity-40 disabled:cursor-not-allowed ${
              copiedCurrent
                ? 'bg-emerald-500/20 border-emerald-500 text-emerald-300'
                : 'bg-indigo-600 hover:bg-indigo-500 border-indigo-500 text-white shadow-md shadow-indigo-600/20'
            }`}
          >
            {copiedCurrent ? (
              <>
                <Check className="w-4 h-4 text-emerald-400" />
                <span>Rascunho Copiado!</span>
              </>
            ) : (
              <>
                <Copy className="w-4 h-4" />
                <span>Copiar Rascunho (1-Clique)</span>
              </>
            )}
          </button>

          <a
            href={CHATGPT_CORRETOR_URL}
            target="_blank"
            rel="noopener noreferrer"
            className="py-2.5 px-3 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 font-bold rounded-xl text-xs transition flex items-center justify-center gap-2"
          >
            <ExternalLink className="w-4 h-4 text-emerald-400" />
            <span>Ir para o ChatGPT</span>
          </a>

          <button
            type="submit"
            disabled={!title.trim() || !text.trim()}
            className="py-2.5 px-3 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition flex items-center justify-center gap-2 shadow-lg shadow-amber-500/20 disabled:opacity-40 disabled:cursor-not-allowed sm:col-span-2 md:col-span-1"
          >
            <Save className="w-4 h-4" />
            <span>Salvar no Histórico Local</span>
          </button>
        </div>
      </form>

      {/* Saved Drafts History */}
      <div className="space-y-4">
        <h3 className="text-sm font-bold text-slate-300 flex items-center gap-2">
          <FileText className="w-4 h-4 text-amber-400" />
          Rascunhos e Histórico Salvo ({essays.length})
        </h3>

        {essays.length === 0 ? (
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 text-center text-xs text-slate-400">
            Nenhum rascunho salvo ainda. Digite sua redação acima para salvar e enviar ao ChatGPT.
          </div>
        ) : (
          essays.map((essay) => {
            const formattedDraft = `Tema: ${essay.title}\n\nTexto:\n${essay.text}`;
            const isCopied = copiedId === essay.id;

            return (
              <div key={essay.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-lg">
                <div className="flex items-start justify-between gap-3 border-b border-slate-800 pb-3">
                  <div>
                    <span className="text-[10px] font-extrabold text-amber-400 uppercase bg-amber-500/10 px-2 py-0.5 rounded-full border border-amber-500/20">
                      Rascunho Local
                    </span>
                    <h4 className="text-base font-bold text-slate-100 mt-1">{essay.title}</h4>
                    <p className="text-[10px] text-slate-400">
                      {new Date(essay.timestamp).toLocaleDateString('pt-BR')} às {new Date(essay.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </p>
                  </div>

                  <div className="flex items-center gap-1.5">
                    <button
                      type="button"
                      onClick={() => handleCopyText(formattedDraft, false, essay.id)}
                      className="px-2.5 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold rounded-lg flex items-center gap-1.5 transition border border-slate-700"
                      title="Copiar rascunho completo"
                    >
                      {isCopied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5 text-indigo-400" />}
                      <span>{isCopied ? 'Copiado!' : 'Copiar'}</span>
                    </button>

                    <a
                      href={CHATGPT_CORRETOR_URL}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="p-1.5 text-slate-400 hover:text-emerald-400 bg-slate-800/80 rounded-lg hover:bg-slate-800 transition"
                      title="Abrir Corretor no ChatGPT"
                    >
                      <ExternalLink className="w-4 h-4" />
                    </a>

                    <button
                      type="button"
                      onClick={() => setEditingEssay(essay)}
                      className="p-1.5 text-slate-400 hover:text-amber-400 bg-slate-800/80 rounded-lg hover:bg-slate-800 transition"
                      title="Editar Rascunho"
                    >
                      <Edit3 className="w-4 h-4" />
                    </button>

                    <button
                      type="button"
                      onClick={() => deleteEssay(essay.id)}
                      className="p-1.5 text-slate-500 hover:text-red-400 bg-slate-800/80 rounded-lg hover:bg-slate-800 transition"
                      title="Excluir"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>

                {/* Essay Text Preview */}
                <div className="p-3.5 bg-slate-950 border border-slate-800/80 rounded-xl text-xs text-slate-300 leading-relaxed font-sans whitespace-pre-wrap max-h-48 overflow-y-auto">
                  {essay.text}
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* EDIT ESSAY DRAFT MODAL */}
      {editingEssay && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-xl p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-amber-400" />
                Editar Rascunho de Redação
              </h3>
              <button
                type="button"
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
                  rows={8}
                  value={editingEssay.text}
                  onChange={(e) => setEditingEssay({ ...editingEssay, text: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500 font-sans leading-relaxed"
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
