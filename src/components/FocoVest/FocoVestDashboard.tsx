import React from 'react';
import { useApp } from '../../context/AppContext';
import { countdownEventsList } from '../../data/initialData';
import { generatePdfReport } from '../../utils/pdfReport';
import {
  Calendar,
  GraduationCap,
  FileText,
  Clock,
  CheckSquare,
  Sparkles,
  Download,
  Plus,
  Trash2,
  CheckCircle,
  ExternalLink
} from 'lucide-react';

export const FocoVestDashboard: React.FC = () => {
  const {
    subjects,
    schedules,
    customCronogramaItems,
    addCustomCronogramaItem,
    toggleCustomCronogramaItem,
    deleteCustomCronogramaItem,
    hoursList,
    setSelectedFocoVestTab
  } = useApp();

  const [newItemText, setNewItemText] = React.useState('');
  const [newItemWeek, setNewItemWeek] = React.useState('Semana 1');

  // Calculations
  const totalSubjects = subjects.length;
  const completedSubjectsCount = subjects.filter((s) => {
    return s.stepAula && s.stepResumo && s.stepAutoexplicacao && s.stepExercicios && s.stepCadernoErros && s.stepRevisao && s.stepSimulado;
  }).length;

  const handleAddCronograma = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newItemText.trim()) return;
    addCustomCronogramaItem(newItemText, newItemWeek, 'Período Atual');
    setNewItemText('');
  };

  return (
    <div className="space-y-6">
      {/* Top Welcome Banner */}
      <div className="bg-gradient-to-r from-amber-500/15 via-indigo-500/10 to-slate-900 border border-amber-500/20 rounded-3xl p-6 text-slate-100 relative overflow-hidden shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 relative z-10">
          <div className="space-y-1">
            <span className="text-xs font-bold text-amber-400 tracking-wider uppercase flex items-center gap-1.5">
              <Sparkles className="w-4 h-4" />
              Painel de Desempenho FOCOVEST
            </span>
            <h2 className="text-2xl font-extrabold tracking-tight text-white">
              Sua Trilha Rumo ao ENEM & ITA 🎯
            </h2>
            <p className="text-xs text-slate-300 max-w-xl">
              Metodologia de 7 passos de estudos, cartões de repetição espaçada, cronograma de matérias e correção automatizada de redação.
            </p>
          </div>

          <button
            onClick={() => generatePdfReport(hoursList, subjects, customCronogramaItems)}
            className="px-4 py-2.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs flex items-center gap-2 transition shadow-lg shadow-amber-500/20 self-start md:self-auto shrink-0"
          >
            <Download className="w-4 h-4" />
            <span>Gerar Relatório PDF</span>
          </button>
        </div>
      </div>

      {/* Countdown to Exams */}
      <div className="space-y-3">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <Calendar className="w-4 h-4 text-amber-400" />
          Contagem Regressiva para as Provas
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {countdownEventsList.map((ev, idx) => (
            <div
              key={idx}
              className="bg-slate-900 border border-slate-800 rounded-2xl p-4 text-center space-y-1 shadow-md hover:border-amber-500/30 transition"
            >
              <p className="text-xs font-bold text-amber-400 truncate">{ev.title}</p>
              <p className="text-2xl font-extrabold text-white">{ev.daysLeft} <span className="text-xs font-normal text-slate-400">dias</span></p>
              <p className="text-[11px] text-slate-400">{ev.weeksLeft} semanas ({ev.date})</p>
            </div>
          ))}
        </div>
      </div>

      {/* Overview Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div
          onClick={() => setSelectedFocoVestTab(2)} // Trilhas tab
          className="bg-slate-900 border border-slate-800 rounded-2xl p-5 cursor-pointer hover:border-indigo-500/40 transition space-y-3 shadow-md"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-indigo-400 uppercase tracking-wider">Trilhas de Estudo</span>
            <GraduationCap className="w-5 h-5 text-indigo-400" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white">{completedSubjectsCount} / {totalSubjects}</div>
            <p className="text-xs text-slate-400">assuntos com 7 passos concluídos</p>
          </div>
          <div className="w-full bg-slate-800 rounded-full h-2 overflow-hidden">
            <div
              className="bg-indigo-500 h-2 rounded-full transition-all"
              style={{ width: `${totalSubjects > 0 ? (completedSubjectsCount / totalSubjects) * 100 : 0}%` }}
            />
          </div>
        </div>

        <div
          onClick={() => setSelectedFocoVestTab(1)} // Cronograma tab
          className="bg-slate-900 border border-slate-800 rounded-2xl p-5 cursor-pointer hover:border-amber-500/40 transition space-y-3 shadow-md"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-amber-400 uppercase tracking-wider">Cronograma de Atividades</span>
            <CheckSquare className="w-5 h-5 text-amber-400" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-white">
              {customCronogramaItems.filter((c) => c.isCompleted).length} / {customCronogramaItems.length}
            </div>
            <p className="text-xs text-slate-400">metas do cronograma concluídas</p>
          </div>
          <div className="w-full bg-slate-800 rounded-full h-2 overflow-hidden">
            <div
              className="bg-amber-500 h-2 rounded-full transition-all"
              style={{ width: `${customCronogramaItems.length > 0 ? (customCronogramaItems.filter((c) => c.isCompleted).length / customCronogramaItems.length) * 100 : 0}%` }}
            />
          </div>
        </div>

        <div
          onClick={() => setSelectedFocoVestTab(6)} // Tutor IA tab
          className="bg-slate-900 border border-slate-800 rounded-2xl p-5 cursor-pointer hover:border-emerald-500/40 transition space-y-3 shadow-md"
        >
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider">Tutor IA Local</span>
            <Sparkles className="w-5 h-5 text-emerald-400" />
          </div>
          <div>
            <div className="text-lg font-bold text-white">Assistência Gratuita</div>
            <p className="text-xs text-slate-400">Esclarecimento de dúvidas e resolução de questões</p>
          </div>
          <p className="text-[11px] text-emerald-400 font-semibold flex items-center gap-1">
            Clique para abrir o Chat Tutor →
          </p>
        </div>
      </div>

      {/* Weekly Schedule Section */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-lg">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <Clock className="w-4 h-4 text-indigo-400" />
          Quadro Semanal de Estudos
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
          {['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta'].map((day) => {
            const daySchedules = schedules.filter((s) => s.dayOfWeek.toLowerCase() === day.toLowerCase());
            return (
              <div key={day} className="bg-slate-950 border border-slate-800/80 rounded-xl p-3 space-y-2">
                <p className="text-xs font-extrabold text-amber-400 border-b border-slate-800 pb-1">{day}</p>
                {daySchedules.length === 0 ? (
                  <p className="text-[11px] text-slate-500 italic">Nenhum horário registrado</p>
                ) : (
                  daySchedules.map((item) => (
                    <div key={item.id} className="p-2 bg-slate-900 rounded-lg text-xs space-y-0.5 border border-slate-800">
                      <p className="font-semibold text-slate-200">{item.subjectTitle}</p>
                      <p className="text-[10px] text-slate-400">{item.durationMinutes} min de estudo</p>
                    </div>
                  ))
                )}
              </div>
            );
          })}
        </div>
      </div>

      {/* Quick Custom Cronograma Add */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-lg">
        <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
          <CheckSquare className="w-4 h-4 text-amber-400" />
          Minhas Metas Personalizadas do Cronograma
        </h3>

        <form onSubmit={handleAddCronograma} className="flex flex-col md:flex-row gap-2">
          <input
            type="text"
            placeholder="Digite uma nova meta (ex: Resolver 20 questões de Física)..."
            value={newItemText}
            onChange={(e) => setNewItemText(e.target.value)}
            className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
          />
          <select
            value={newItemWeek}
            onChange={(e) => setNewItemWeek(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          >
            <option value="Semana 1">Semana 1</option>
            <option value="Semana 2">Semana 2</option>
            <option value="Semana 3">Semana 3</option>
            <option value="Semana 4">Semana 4</option>
          </select>
          <button
            type="submit"
            className="px-4 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition"
          >
            <Plus className="w-4 h-4" />
            Adicionar
          </button>
        </form>

        <div className="space-y-2 max-h-60 overflow-y-auto pr-1">
          {customCronogramaItems.map((item) => (
            <div
              key={item.id}
              className={`p-3 rounded-xl border flex items-center justify-between text-xs transition ${
                item.isCompleted
                  ? 'bg-emerald-500/10 border-emerald-500/20 text-slate-400 line-through'
                  : 'bg-slate-950 border-slate-800 text-slate-200'
              }`}
            >
              <div className="flex items-center gap-2.5">
                <button
                  type="button"
                  onClick={() => toggleCustomCronogramaItem(item.id)}
                  className={`w-5 h-5 rounded-md flex items-center justify-center border transition ${
                    item.isCompleted
                      ? 'bg-emerald-500 border-emerald-500 text-slate-950'
                      : 'border-slate-700 hover:border-amber-500'
                  }`}
                >
                  {item.isCompleted && <CheckCircle className="w-3.5 h-3.5" />}
                </button>
                <span>{item.content}</span>
                <span className="text-[10px] px-2 py-0.5 rounded-full bg-slate-800 text-slate-400">
                  {item.week}
                </span>
              </div>

              <button
                type="button"
                onClick={() => deleteCustomCronogramaItem(item.id)}
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
