import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Clock, Plus, Trash2, Calendar, CheckSquare } from 'lucide-react';

export const RitVidaStudies: React.FC = () => {
  const { schedules, addSchedule, deleteSchedule } = useApp();

  const [dayOfWeek, setDayOfWeek] = useState('Segunda');
  const [durationMinutes, setDurationMinutes] = useState(120);
  const [subjectTitle, setSubjectTitle] = useState('');

  const handleAddSchedule = (e: React.FormEvent) => {
    e.preventDefault();
    if (!subjectTitle.trim()) return;
    addSchedule(dayOfWeek, Number(durationMinutes), subjectTitle);
    setSubjectTitle('');
  };

  const days = ['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'];

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <Clock className="w-5 h-5 text-indigo-400" />
          Horário Semanal de Estudos RITVIDA
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Organize sua rotina fixa de estudos dividida por dias da semana e tempos estimados.
        </p>
      </div>

      {/* Add Schedule Form */}
      <form onSubmit={handleAddSchedule} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
        <select
          value={dayOfWeek}
          onChange={(e) => setDayOfWeek(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
        >
          {days.map((d) => (
            <option key={d} value={d}>{d}</option>
          ))}
        </select>

        <input
          type="text"
          placeholder="Matéria / Assunto (ex: Matemática - Funções)..."
          value={subjectTitle}
          onChange={(e) => setSubjectTitle(e.target.value)}
          className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
          required
        />

        <input
          type="number"
          placeholder="Duração em minutos (ex: 120)"
          value={durationMinutes}
          onChange={(e) => setDurationMinutes(Number(e.target.value))}
          className="w-36 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          required
        />

        <button
          type="submit"
          className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition"
        >
          <Plus className="w-4 h-4" />
          Adicionar Bloco
        </button>
      </form>

      {/* Schedule per Day Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {days.map((day) => {
          const dayItems = schedules.filter((s) => s.dayOfWeek === day);
          return (
            <div key={day} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-3 shadow-md">
              <h3 className="text-xs font-extrabold text-amber-400 uppercase tracking-wider border-b border-slate-800 pb-2">
                {day}
              </h3>

              {dayItems.length === 0 ? (
                <p className="text-xs text-slate-500 italic py-2">Nenhum bloco cadastrado</p>
              ) : (
                <div className="space-y-2">
                  {dayItems.map((item) => (
                    <div
                      key={item.id}
                      className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 flex items-center justify-between text-xs"
                    >
                      <div>
                        <p className="font-bold text-slate-200">{item.subjectTitle}</p>
                        <p className="text-[10px] text-slate-400">{item.durationMinutes} minutos</p>
                      </div>

                      <button
                        onClick={() => deleteSchedule(item.id)}
                        className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
};
