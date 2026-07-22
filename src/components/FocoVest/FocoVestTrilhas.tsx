import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { StudySubject } from '../../types';
import { GraduationCap, Plus, Trash2, CheckCircle2, Circle, Filter, Edit3, X } from 'lucide-react';

export const FocoVestTrilhas: React.FC = () => {
  const { subjects, toggleSubjectStep, addNewSubject, updateSubject, deleteSubject } = useApp();

  const [titleInput, setTitleInput] = useState('');
  const [categoryInput, setCategoryInput] = useState('Matemática');
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState('TODAS');

  // Edit Subject State
  const [editingSubject, setEditingSubject] = useState<StudySubject | null>(null);

  const categories = ['Matemática', 'Física', 'Química', 'Biologia', 'Redação', 'Linguagens', 'História', 'Geografia'];

  const filteredSubjects = subjects.filter((s) => {
    if (selectedCategoryFilter === 'TODAS') return true;
    return s.category === selectedCategoryFilter;
  });

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!titleInput.trim()) return;
    addNewSubject(titleInput, categoryInput);
    setTitleInput('');
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingSubject || !editingSubject.title.trim()) return;
    updateSubject(editingSubject);
    setEditingSubject(null);
  };

  const stepsList = [
    { key: 'aula', label: '1. Aula' },
    { key: 'resumo', label: '2. Resumo' },
    { key: 'autoexplicacao', label: '3. Autoexplicação' },
    { key: 'exercicios', label: '4. Exercícios' },
    { key: 'cadernoerros', label: '5. Erros' },
    { key: 'revisao', label: '6. Revisão' },
    { key: 'simulado', label: '7. Simulado' }
  ];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <GraduationCap className="w-5 h-5 text-indigo-400" />
          Trilhas de Aprendizagem em 7 Etapas
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Garanta fixação profunda de conteúdos marcando a progressão contínua em cada disciplina.
        </p>
      </div>

      {/* Add New Subject Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
        <input
          type="text"
          placeholder="Nome do assunto ou matéria (ex: Biologia - Citologia e Membrana)..."
          value={titleInput}
          onChange={(e) => setTitleInput(e.target.value)}
          className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
          required
        />
        <select
          value={categoryInput}
          onChange={(e) => setCategoryInput(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none"
        >
          {categories.map((c) => (
            <option key={c} value={c}>{c}</option>
          ))}
        </select>
        <button
          type="submit"
          className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-2 transition shadow-lg shadow-indigo-600/20"
        >
          <Plus className="w-4 h-4" />
          Adicionar Trilha
        </button>
      </form>

      {/* Filter by category */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1">
        <span className="text-xs font-bold text-slate-400 flex items-center gap-1 shrink-0">
          <Filter className="w-3.5 h-3.5" /> Categorias:
        </span>
        <button
          onClick={() => setSelectedCategoryFilter('TODAS')}
          className={`px-3 py-1 rounded-lg text-xs font-bold shrink-0 transition ${
            selectedCategoryFilter === 'TODAS'
              ? 'bg-slate-100 text-slate-900'
              : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
          }`}
        >
          Todas
        </button>
        {categories.map((cat) => (
          <button
            key={cat}
            onClick={() => setSelectedCategoryFilter(cat)}
            className={`px-3 py-1 rounded-lg text-xs font-bold shrink-0 transition ${
              selectedCategoryFilter === cat
                ? 'bg-indigo-600 text-white'
                : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Subjects Cards */}
      <div className="space-y-3">
        {filteredSubjects.map((sub) => {
          const completedSteps = [
            sub.stepAula,
            sub.stepResumo,
            sub.stepAutoexplicacao,
            sub.stepExercicios,
            sub.stepCadernoErros,
            sub.stepRevisao,
            sub.stepSimulado
          ].filter(Boolean).length;
          const pct = Math.round((completedSteps / 7) * 100);

          return (
            <div
              key={sub.id}
              className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md hover:border-slate-700 transition"
            >
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                      {sub.category}
                    </span>
                    <span className="text-xs font-bold text-slate-400">{pct}% Concluído</span>
                  </div>
                  <h3 className="text-base font-bold text-slate-100 mt-1">{sub.title}</h3>
                </div>

                <div className="flex items-center gap-1 self-end md:self-center">
                  <button
                    onClick={() => setEditingSubject(sub)}
                    className="p-2 text-slate-400 hover:text-amber-400 rounded-xl hover:bg-slate-800 transition"
                    title="Editar Trilha"
                  >
                    <Edit3 className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => deleteSubject(sub.id)}
                    className="p-2 text-slate-500 hover:text-red-400 rounded-xl hover:bg-slate-800 transition"
                    title="Excluir"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {/* Progress Bar */}
              <div className="w-full bg-slate-950 rounded-full h-2 overflow-hidden border border-slate-800/80">
                <div
                  className="bg-gradient-to-r from-indigo-500 to-amber-500 h-2 rounded-full transition-all duration-300"
                  style={{ width: `${pct}%` }}
                />
              </div>

              {/* Step Buttons */}
              <div className="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-7 gap-2">
                {stepsList.map((step) => {
                  let isDone = false;
                  if (step.key === 'aula') isDone = sub.stepAula;
                  if (step.key === 'resumo') isDone = sub.stepResumo;
                  if (step.key === 'autoexplicacao') isDone = sub.stepAutoexplicacao;
                  if (step.key === 'exercicios') isDone = sub.stepExercicios;
                  if (step.key === 'cadernoerros') isDone = sub.stepCadernoErros;
                  if (step.key === 'revisao') isDone = sub.stepRevisao;
                  if (step.key === 'simulado') isDone = sub.stepSimulado;

                  return (
                    <button
                      key={step.key}
                      onClick={() => toggleSubjectStep(sub.id, step.key)}
                      className={`p-2 rounded-xl text-[11px] font-bold flex flex-col items-center justify-center gap-1 border transition ${
                        isDone
                          ? 'bg-emerald-500/15 border-emerald-500/30 text-emerald-400'
                          : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-slate-200 hover:border-slate-700'
                      }`}
                    >
                      {isDone ? <CheckCircle2 className="w-4 h-4 text-emerald-400" /> : <Circle className="w-4 h-4 text-slate-600" />}
                      <span>{step.label}</span>
                    </button>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>

      {/* EDIT MODAL */}
      {editingSubject && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-indigo-400" />
                Editar Trilha de Estudo
              </h3>
              <button
                onClick={() => setEditingSubject(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Título do Assunto / Conteúdo</label>
                <input
                  type="text"
                  value={editingSubject.title}
                  onChange={(e) => setEditingSubject({ ...editingSubject, title: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Categoria / Disciplina</label>
                <select
                  value={editingSubject.category}
                  onChange={(e) => setEditingSubject({ ...editingSubject, category: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                >
                  {categories.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingSubject(null)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition shadow-lg shadow-indigo-600/20"
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
