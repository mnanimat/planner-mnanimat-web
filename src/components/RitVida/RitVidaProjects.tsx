import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Briefcase, Plus, Trash2, CheckCircle2 } from 'lucide-react';

export const RitVidaProjects: React.FC = () => {
  const { projects, addProject, updateProjectProgress, deleteProject } = useApp();

  const [name, setName] = useState('');
  const [progressPercentage, setProgressPercentage] = useState(50);
  const [targetDateString, setTargetDateString] = useState('2026-08-30');

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    addProject(name, Number(progressPercentage), targetDateString);
    setName('');
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <Briefcase className="w-5 h-5 text-indigo-400" />
          Projetos Ativos e Entregáveis
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Acompanhe percentual de conclusão e prazos de entrega dos seus projetos pessoais e profissionais.
        </p>
      </div>

      {/* Add Project Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
        <input
          type="text"
          placeholder="Nome do projeto..."
          value={name}
          onChange={(e) => setName(e.target.value)}
          className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
          required
        />

        <input
          type="number"
          min="0"
          max="100"
          placeholder="Progresso (%)"
          value={progressPercentage}
          onChange={(e) => setProgressPercentage(Number(e.target.value))}
          className="w-32 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          required
        />

        <input
          type="date"
          value={targetDateString}
          onChange={(e) => setTargetDateString(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          required
        />

        <button
          type="submit"
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition"
        >
          <Plus className="w-4 h-4" />
          Adicionar Projeto
        </button>
      </form>

      {/* Projects List */}
      <div className="space-y-3">
        {projects.map((p) => (
          <div key={p.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-md">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-bold text-slate-100">{p.name}</h3>
                <p className="text-[10px] text-slate-400">Prazo Estimado: {p.targetDateString}</p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-indigo-400">{p.progressPercentage}%</span>
                <button
                  onClick={() => deleteProject(p.id)}
                  className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Slider / Range */}
            <div className="space-y-1">
              <input
                type="range"
                min="0"
                max="100"
                value={p.progressPercentage}
                onChange={(e) => updateProjectProgress(p.id, Number(e.target.value))}
                className="w-full accent-indigo-500 bg-slate-950 rounded-lg cursor-pointer"
              />
              <div className="w-full bg-slate-950 rounded-full h-2 overflow-hidden border border-slate-800">
                <div
                  className="bg-indigo-500 h-2 rounded-full transition-all"
                  style={{ width: `${p.progressPercentage}%` }}
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
