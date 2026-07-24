import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { generatePdfReport } from '../../utils/pdfReport';
import {
  Activity,
  Clock,
  Wallet,
  Briefcase,
  ShieldCheck,
  FileText,
  Plus,
  Trash2,
  TrendingUp,
  Download
} from 'lucide-react';

export const RitVidaOverview: React.FC = () => {
  const {
    hoursList,
    ritVidaFinances,
    projects,
    addWorkedHours,
    deleteHour,
    subjects,
    customCronogramaItems,
    setActiveModule,
    setSelectedFocoVestTab
  } = useApp();

  const [funcInput, setFuncInput] = useState('Estudante');
  const [hoursInput, setHoursInput] = useState(2);
  const [dateInput, setDateInput] = useState(new Date().toISOString().split('T')[0]);

  // Calculations
  const totalHours = hoursList.reduce((acc, item) => acc + item.hours, 0);
  const netBalance = ritVidaFinances.reduce(
    (acc, t) => (t.type === 'REVENUE' ? acc + t.amount : acc - t.amount),
    0
  );
  const avgProjectProgress =
    projects.length > 0
      ? Math.round(projects.reduce((acc, p) => acc + p.progressPercentage, 0) / projects.length)
      : 0;

  const handleAddHour = (e: React.FormEvent) => {
    e.preventDefault();
    if (hoursInput <= 0) return;
    addWorkedHours(funcInput, Number(hoursInput), dateInput);
  };

  // Group hours by function
  const hoursByFunction: Record<string, number> = {};
  hoursList.forEach((h) => {
    hoursByFunction[h.functionName] = (hoursByFunction[h.functionName] || 0) + h.hours;
  });

  return (
    <div className="space-y-6">
      {/* Welcome Banner */}
      <div className="bg-gradient-to-r from-indigo-600/20 via-purple-500/10 to-slate-900 border border-indigo-500/20 rounded-3xl p-6 text-slate-100 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="space-y-1">
            <h2 className="text-2xl font-extrabold tracking-tight text-white flex items-center gap-2">
              Painel Integrado RITVIDA 🌀
            </h2>
            <p className="text-xs text-slate-300 max-w-xl">
              Equilíbrio de tempo, acompanhamento de saúde, finanças pessoais e projetos ativos em um ambiente 100% privado.
            </p>
          </div>

          <button
            onClick={() => generatePdfReport(hoursList, subjects, customCronogramaItems)}
            className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center gap-2 transition shadow-lg shadow-indigo-600/20 shrink-0"
          >
            <Download className="w-4 h-4" />
            <span>Gerar PDF Consolidado</span>
          </button>
        </div>
      </div>

      {/* Local Data Security Tag */}
      <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs w-fit">
        <ShieldCheck className="w-4 h-4" />
        <span className="font-bold">PROCESSAMENTO LOCAL & SEGURO</span>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-2 shadow-sm dark:shadow-md">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-amber-500 dark:text-amber-400 uppercase">Total de Horas</span>
            <Clock className="w-5 h-5 text-amber-500 dark:text-amber-400" />
          </div>
          <p className="text-3xl font-extrabold text-amber-500 dark:text-amber-400">{totalHours.toFixed(1)}h</p>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Tempo registrado em todas as funções</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-2 shadow-sm dark:shadow-md">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-emerald-600 dark:text-emerald-400 uppercase">Saldo Consolidado</span>
            <Wallet className="w-5 h-5 text-emerald-600 dark:text-emerald-400" />
          </div>
          <p className="text-3xl font-extrabold text-emerald-600 dark:text-emerald-400">R$ {netBalance.toFixed(2)}</p>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">Balanço de receitas e despesas RITVIDA</p>
        </div>

        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-5 space-y-2 shadow-sm dark:shadow-md">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-purple-600 dark:text-purple-400 uppercase">Progresso dos Projetos</span>
            <Briefcase className="w-5 h-5 text-purple-600 dark:text-purple-400" />
          </div>
          <p className="text-3xl font-extrabold text-purple-600 dark:text-purple-400">{avgProjectProgress}%</p>
          <p className="text-[11px] text-slate-500 dark:text-slate-400">{projects.length} projetos cadastrados</p>
        </div>
      </div>

      {/* Hours Distribution by Function */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-lg">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <Clock className="w-4 h-4 text-indigo-400" />
          Distribuição de Horas por Função
        </h3>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {Object.entries(hoursByFunction).map(([func, hrs]) => (
            <div key={func} className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 space-y-1">
              <p className="text-xs font-bold text-indigo-300">{func}</p>
              <p className="text-lg font-extrabold text-white">{hrs.toFixed(1)}h</p>
              <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                <div
                  className="bg-indigo-500 h-1.5 rounded-full"
                  style={{ width: `${Math.min(100, (hrs / (totalHours || 1)) * 100)}%` }}
                />
              </div>
            </div>
          ))}
        </div>

        {/* Add Hours Form */}
        <form onSubmit={handleAddHour} className="pt-2 flex flex-col md:flex-row gap-2 border-t border-slate-800">
          <select
            value={funcInput}
            onChange={(e) => setFuncInput(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          >
            <option value="Estudante">Estudante</option>
            <option value="Trabalho">Trabalho</option>
            <option value="Saúde">Saúde</option>
            <option value="Administrativo">Administrativo</option>
          </select>

          <input
            type="number"
            step="0.5"
            placeholder="Horas (ex: 2.5)"
            value={hoursInput}
            onChange={(e) => setHoursInput(Number(e.target.value))}
            className="w-32 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <input
            type="date"
            value={dateInput}
            onChange={(e) => setDateInput(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <button
            type="submit"
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition"
          >
            <Plus className="w-4 h-4" />
            Registrar Horas
          </button>
        </form>

        {/* Logged Hours List */}
        <div className="space-y-2 max-h-48 overflow-y-auto pt-2">
          {hoursList.map((h) => (
            <div key={h.id} className="p-2.5 bg-slate-950 rounded-xl border border-slate-800/80 flex items-center justify-between text-xs">
              <div className="flex items-center gap-3">
                <span className="font-bold text-indigo-300">{h.functionName}</span>
                <span className="text-slate-200">{h.hours} horas</span>
                <span className="text-[10px] text-slate-500">{h.dateString}</span>
              </div>
              <button
                onClick={() => deleteHour(h.id)}
                className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
