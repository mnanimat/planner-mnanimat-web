import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { VisualTask } from '../../types';
import {
  Calendar,
  Plus,
  Trash2,
  Clock,
  CheckSquare,
  Tag,
  Edit3,
  Kanban as KanbanIcon,
  BarChart2,
  CheckCircle2,
  X,
  Sparkles,
  ChevronLeft,
  ChevronRight,
  ExternalLink,
  Target,
  Briefcase,
  Building2,
  Dumbbell,
  FileText,
  Check,
  Circle,
  Filter,
  Layers,
  ArrowRight
} from 'lucide-react';

const MONTH_NAMES_PT = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];

const WEEKDAYS_PT = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];

export interface UnifiedAgendaItem {
  id: string;
  title: string;
  startDate: string; // YYYY-MM-DD
  endDate: string; // YYYY-MM-DD
  startTime?: string;
  endTime?: string;
  moduleOrigin: 'FOCOVEST' | 'RITVIDA' | 'MEI';
  categoryTag: string;
  status: 'A Fazer' | 'Em Progresso' | 'Concluído';
  originalType: 'VISUAL_TASK' | 'CRONOGRAMA' | 'PROJECT' | 'MEI_INVOICE' | 'MEI_TX' | 'GYM';
  rawObject?: any;
  details?: string;
}

export const RitVidaVisual: React.FC = () => {
  const {
    visualTasks,
    insertVisualTask,
    deleteVisualTask,
    updateVisualTask,
    customCronogramaItems,
    toggleCustomCronogramaItem,
    projects,
    updateProject,
    meiInvoices,
    updateMeiInvoice,
    meiTransactions,
    updateMeiTransaction,
    gymWorkouts,
    toggleGymWorkoutStatus,
    setActiveModule,
    setSelectedFocoVestTab,
    setSelectedRitVidaTab,
    setSelectedMeiTab
  } = useApp();

  // Granularities & Layout Views
  const [timeGranularity, setTimeGranularity] = useState<'DIA' | 'MES' | 'ANO'>('DIA');
  const [layoutView, setLayoutView] = useState<'TIMELINE' | 'GANTT' | 'KANBAN'>('TIMELINE');
  const [originFilter, setOriginFilter] = useState<'TODOS' | 'FOCOVEST' | 'RITVIDA' | 'MEI'>('TODOS');

  // Date Navigation State
  const todayISO = new Date().toISOString().split('T')[0];
  const [selectedDateISO, setSelectedDateISO] = useState<string>(todayISO);

  const [currentMonth, setCurrentMonth] = useState<number>(new Date().getMonth());
  const [currentYear, setCurrentYear] = useState<number>(new Date().getFullYear());

  // Modal State
  const [editingTask, setEditingTask] = useState<VisualTask | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [moduleChoice, setModuleChoice] = useState<'FOCOVEST' | 'RITVIDA' | 'MEI'>('RITVIDA');
  const [func, setFunc] = useState('Trabalho');
  const [tag, setTag] = useState('Urgente');
  const [startDateInput, setStartDateInput] = useState(todayISO);
  const [endDateInput, setEndDateInput] = useState(todayISO);
  const [startTimeInput, setStartTimeInput] = useState('08:00');
  const [endTimeInput, setEndTimeInput] = useState('10:00');
  const [status, setStatus] = useState<'A Fazer' | 'Em Progresso' | 'Concluído'>('A Fazer');
  const [checklistInput, setChecklistInput] = useState('');

  // Drag State
  const [draggedTaskId, setDraggedTaskId] = useState<number | null>(null);

  // Helper date format
  const formatBRDate = (isoStr: string) => {
    if (!isoStr) return '';
    const parts = isoStr.split('-');
    if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    return isoStr;
  };

  const formatTaskDuration = (startTime?: string, endTime?: string, defaultHours: number = 1) => {
    if (startTime && endTime) {
      const [sH = 0, sM = 0] = startTime.split(':').map((v) => parseInt(v, 10) || 0);
      const [eH = 0, eM = 0] = endTime.split(':').map((v) => parseInt(v, 10) || 0);
      let diffMins = (eH * 60 + eM) - (sH * 60 + sM);
      if (diffMins <= 0) diffMins += 24 * 60;
      const h = Math.floor(diffMins / 60);
      const m = diffMins % 60;
      if (h > 0 && m > 0) return `${h}h ${m}m`;
      if (h > 0) return `${h}h 00m`;
      return `${m}m`;
    }
    const h = Math.floor(defaultHours);
    const m = Math.round((defaultHours - h) * 60);
    if (h > 0 && m > 0) return `${h}h ${m}m`;
    if (h > 0) return `${h}h 00m`;
    return `${m}m`;
  };

  // 1. UNIFIED AGENDA AGGREGATOR (Interlinking FocoVest, RitVida, MEI)
  const getUnifiedItems = (): UnifiedAgendaItem[] => {
    const items: UnifiedAgendaItem[] = [];

    // Native RitVida Visual Tasks
    visualTasks.forEach((vt) => {
      items.push({
        id: `vt-${vt.id}`,
        title: vt.title,
        startDate: vt.startDate || todayISO,
        endDate: vt.endDate || vt.startDate || todayISO,
        startTime: vt.startTime || '08:00',
        endTime: vt.endTime || '10:00',
        moduleOrigin: 'RITVIDA',
        categoryTag: vt.function || 'Rotina',
        status: vt.status || 'A Fazer',
        originalType: 'VISUAL_TASK',
        rawObject: vt,
        details: vt.tag
      });
    });

    // FocoVest Cronograma Items
    customCronogramaItems.forEach((c) => {
      items.push({
        id: `crono-${c.id}`,
        title: `[Estudo] ${c.content}`,
        startDate: todayISO, // assigned active date
        endDate: todayISO,
        startTime: '09:00',
        endTime: '11:00',
        moduleOrigin: 'FOCOVEST',
        categoryTag: c.targetSchedule || 'ENEM/ITA',
        status: c.isCompleted ? 'Concluído' : 'A Fazer',
        originalType: 'CRONOGRAMA',
        rawObject: c,
        details: `Semana: ${c.week} | Período: ${c.dateInterval}`
      });
    });

    // RitVida Projects
    projects.forEach((p) => {
      const pDate = p.targetDateString || todayISO;
      items.push({
        id: `proj-${p.id}`,
        title: `[Projeto] ${p.name}`,
        startDate: pDate,
        endDate: pDate,
        startTime: '14:00',
        endTime: '16:00',
        moduleOrigin: 'RITVIDA',
        categoryTag: `Meta ${p.progressPercentage}%`,
        status: p.isCompleted || p.progressPercentage === 100 ? 'Concluído' : 'Em Progresso',
        originalType: 'PROJECT',
        rawObject: p,
        details: `Progresso Atual: ${p.progressPercentage}%`
      });
    });

    // MEI Invoices
    meiInvoices.forEach((inv) => {
      const invDate = inv.dueDate || todayISO;
      items.push({
        id: `inv-${inv.id}`,
        title: `[MEI Cobrança] NF: ${inv.serviceDescription} (${inv.clientName})`,
        startDate: invDate,
        endDate: invDate,
        startTime: '10:00',
        endTime: '11:00',
        moduleOrigin: 'MEI',
        categoryTag: `R$ ${inv.amount.toFixed(2)}`,
        status: inv.isReceived ? 'Concluído' : 'A Fazer',
        originalType: 'MEI_INVOICE',
        rawObject: inv,
        details: inv.isReceived ? 'Pagamento Recebido' : 'Aguardando Pagamento'
      });
    });

    // MEI Transactions (Pendentes)
    meiTransactions.forEach((tx) => {
      const txDate = tx.dateString || todayISO;
      items.push({
        id: `tx-${tx.id}`,
        title: `[MEI Financeiro] ${tx.transactionType === 'RECEITA' ? 'Receita' : 'Despesa'}: ${tx.description}`,
        startDate: txDate,
        endDate: txDate,
        startTime: '15:00',
        endTime: '16:00',
        moduleOrigin: 'MEI',
        categoryTag: `R$ ${tx.amount.toFixed(2)}`,
        status: tx.status === 'Pago' ? 'Concluído' : 'A Fazer',
        originalType: 'MEI_TX',
        rawObject: tx,
        details: `Conta: ${tx.accountType} | Categoria: ${tx.category}`
      });
    });

    // Gym Workouts
    gymWorkouts.forEach((gym) => {
      const gymDate = gym.dateString || todayISO;
      items.push({
        id: `gym-${gym.id}`,
        title: `[Saúde/Treino] ${gym.exercise}`,
        startDate: gymDate,
        endDate: gymDate,
        startTime: '18:00',
        endTime: '19:00',
        moduleOrigin: 'RITVIDA',
        categoryTag: `${gym.sets}x${gym.reps} - ${gym.weightKg}kg`,
        status: gym.isCompleted ? 'Concluído' : 'A Fazer',
        originalType: 'GYM',
        rawObject: gym,
        details: `Séries: ${gym.sets} | Carga: ${gym.weightKg}kg`
      });
    });

    return items;
  };

  const allUnifiedItems = getUnifiedItems();

  // Filter items by Module Origin
  const filteredItemsByOrigin = allUnifiedItems.filter((item) => {
    if (originFilter === 'TODOS') return true;
    return item.moduleOrigin === originFilter;
  });

  // Toggle item completion across modules
  const handleToggleItemCompletion = (item: UnifiedAgendaItem) => {
    if (item.originalType === 'VISUAL_TASK') {
      const vt = item.rawObject as VisualTask;
      const nextStatus = vt.status === 'Concluído' ? 'A Fazer' : 'Concluído';
      updateVisualTask({ ...vt, status: nextStatus });
    } else if (item.originalType === 'CRONOGRAMA') {
      toggleCustomCronogramaItem(item.rawObject.id);
    } else if (item.originalType === 'PROJECT') {
      const p = item.rawObject;
      const nextCompleted = !p.isCompleted;
      updateProject({
        ...p,
        isCompleted: nextCompleted,
        progressPercentage: nextCompleted ? 100 : 50
      });
    } else if (item.originalType === 'MEI_INVOICE') {
      const inv = item.rawObject;
      updateMeiInvoice({ ...inv, isReceived: !inv.isReceived });
    } else if (item.originalType === 'MEI_TX') {
      const tx = item.rawObject;
      updateMeiTransaction({ ...tx, status: tx.status === 'Pago' ? 'Pendente' : 'Pago' });
    } else if (item.originalType === 'GYM') {
      toggleGymWorkoutStatus(item.rawObject.id);
    }
  };

  // Navigate to corresponding module page
  const handleJumpToModule = (origin: 'FOCOVEST' | 'RITVIDA' | 'MEI', type: string) => {
    if (origin === 'FOCOVEST') {
      setActiveModule('FOCOVEST');
      if (type === 'CRONOGRAMA') setSelectedFocoVestTab(1); // Cronograma
      else setSelectedFocoVestTab(0);
    } else if (origin === 'MEI') {
      setActiveModule('MEI_PRO');
      if (type === 'MEI_INVOICE') setSelectedMeiTab(3); // Cobranças
      else setSelectedMeiTab(1); // Transações
    } else if (origin === 'RITVIDA') {
      setActiveModule('RITVIDA');
      if (type === 'PROJECT') setSelectedRitVidaTab(5); // Projetos
      else if (type === 'GYM') setSelectedRitVidaTab(3); // Gym/Saúde
      else setSelectedRitVidaTab(2); // Agenda
    }
  };

  // Modal handlers
  const handleOpenAddModal = (presetHour?: number) => {
    setEditingTask(null);
    setTitle('');
    setModuleChoice('RITVIDA');
    setFunc('Trabalho');
    setTag('Urgente');
    setStartDateInput(selectedDateISO);
    setEndDateInput(selectedDateISO);
    const startH = presetHour !== undefined ? presetHour : 8;
    const endH = Math.min(24, startH + 2);
    setStartTimeInput(`${startH.toString().padStart(2, '0')}:00`);
    setEndTimeInput(`${endH.toString().padStart(2, '0')}:00`);
    setStatus('A Fazer');
    setChecklistInput('');
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (task: VisualTask) => {
    setEditingTask(task);
    setTitle(task.title);
    setFunc(task.function);
    setTag(task.tag);
    setStartDateInput(task.startDate || todayISO);
    setEndDateInput(task.endDate || task.startDate || todayISO);
    setStartTimeInput(task.startTime || `${task.startHour.toString().padStart(2, '0')}:00`);
    setEndTimeInput(task.endTime || `${Math.min(24, task.startHour + task.durationHours).toString().padStart(2, '0')}:00`);
    setStatus(task.status || 'A Fazer');

    const checklistClean = (task.checklistRaw || '')
      .split('|')
      .filter(Boolean)
      .map((item) => item.split(':')[0])
      .join('\n');
    setChecklistInput(checklistClean);
    setIsModalOpen(true);
  };

  const handleSaveTask = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) return;

    let checklistFormatted = '';
    if (editingTask) {
      const existingItemsMap = new Map<string, boolean>();
      (editingTask.checklistRaw || '').split('|').filter(Boolean).forEach((raw) => {
        const [n, s] = raw.split(':');
        if (n) existingItemsMap.set(n.trim(), s === 'true');
      });

      checklistFormatted = checklistInput
        .split('\n')
        .map((s) => s.trim())
        .filter(Boolean)
        .map((itemName) => `${itemName}:${existingItemsMap.get(itemName) ? 'true' : 'false'}`)
        .join('|');
    } else {
      checklistFormatted = checklistInput
        .split('\n')
        .map((s) => s.trim())
        .filter(Boolean)
        .map((itemName) => `${itemName}:false`)
        .join('|');
    }

    const startStr = startTimeInput.trim() || '08:00';
    const endStr = endTimeInput.trim() || '10:00';

    const [sH = 8, sM = 0] = startStr.split(':').map((v) => parseInt(v, 10) || 0);
    const [eH = 10, eM = 0] = endStr.split(':').map((v) => parseInt(v, 10) || 0);

    const startMinutesTotal = sH * 60 + sM;
    let endMinutesTotal = eH * 60 + eM;
    if (endMinutesTotal <= startMinutesTotal) {
      endMinutesTotal = startMinutesTotal + 60;
    }

    const calculatedStartHour = Math.min(23, Math.max(0, sH));
    const computedDuration = Math.max(1, Math.round((endMinutesTotal - startMinutesTotal) / 60));

    const startTimeFormatted = `${sH.toString().padStart(2, '0')}:${sM.toString().padStart(2, '0')}`;
    const endTimeFormatted = `${eH.toString().padStart(2, '0')}:${eM.toString().padStart(2, '0')}`;

    if (editingTask) {
      updateVisualTask({
        ...editingTask,
        title,
        startDate: startDateInput || todayISO,
        endDate: endDateInput || startDateInput || todayISO,
        startTime: startTimeFormatted,
        endTime: endTimeFormatted,
        startHour: calculatedStartHour,
        durationHours: computedDuration,
        function: func,
        tag,
        status,
        checklistRaw: checklistFormatted
      });
    } else {
      insertVisualTask(
        title,
        startDateInput || todayISO,
        startTimeFormatted,
        endDateInput || startDateInput || todayISO,
        endTimeFormatted,
        calculatedStartHour,
        computedDuration,
        func,
        tag,
        checklistFormatted
      );
    }

    setIsModalOpen(false);
  };

  const toggleChecklistItem = (task: VisualTask, itemIndex: number) => {
    const items = (task.checklistRaw || '').split('|').filter(Boolean);
    if (!items[itemIndex]) return;
    const [name, statusStr] = items[itemIndex].split(':');
    const newStatus = statusStr === 'true' ? 'false' : 'true';
    items[itemIndex] = `${name}:${newStatus}`;

    updateVisualTask({
      ...task,
      checklistRaw: items.join('|')
    });
  };

  // Date Navigation Helpers for DIA
  const handleStepDay = (deltaDays: number) => {
    const curDate = new Date(selectedDateISO + 'T12:00:00');
    curDate.setDate(curDate.getDate() + deltaDays);
    const nextISO = curDate.toISOString().split('T')[0];
    setSelectedDateISO(nextISO);
    setCurrentMonth(curDate.getMonth());
    setCurrentYear(curDate.getFullYear());
  };

  // Month navigation
  const handleStepMonth = (deltaMonths: number) => {
    let m = currentMonth + deltaMonths;
    let y = currentYear;
    if (m > 11) {
      m = 0;
      y += 1;
    } else if (m < 0) {
      m = 11;
      y -= 1;
    }
    setCurrentMonth(m);
    setCurrentYear(y);
  };

  // Filter items for currently selected single day
  const itemsForSelectedDay = filteredItemsByOrigin.filter((item) => {
    if (!item.startDate && !item.endDate) return true;
    return (
      item.startDate <= selectedDateISO &&
      item.endDate >= selectedDateISO
    );
  });

  // Calculate day stats
  const completedCountForDay = itemsForSelectedDay.filter((i) => i.status === 'Concluído').length;
  const totalCountForDay = itemsForSelectedDay.length;
  const dayProgressPct = totalCountForDay > 0 ? Math.round((completedCountForDay / totalCountForDay) * 100) : 0;

  // Month Calendar Matrix calculation
  const daysInMonthCount = new Date(currentYear, currentMonth + 1, 0).getDate();
  const firstDayOfWeek = new Date(currentYear, currentMonth, 1).getDay(); // 0 = Sun

  const calendarGridCells: { dateISO: string; dayNum: number; isCurrentMonth: boolean }[] = [];

  // Previous month padding
  const prevMonthDaysCount = new Date(currentYear, currentMonth, 0).getDate();
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const pDay = prevMonthDaysCount - i;
    const prevM = currentMonth === 0 ? 11 : currentMonth - 1;
    const prevY = currentMonth === 0 ? currentYear - 1 : currentYear;
    const iso = `${prevY}-${(prevM + 1).toString().padStart(2, '0')}-${pDay.toString().padStart(2, '0')}`;
    calendarGridCells.push({ dateISO: iso, dayNum: pDay, isCurrentMonth: false });
  }

  // Current month
  for (let d = 1; d <= daysInMonthCount; d++) {
    const iso = `${currentYear}-${(currentMonth + 1).toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}`;
    calendarGridCells.push({ dateISO: iso, dayNum: d, isCurrentMonth: true });
  }

  // Next month padding to complete 35 or 42 cells
  const remainingCells = (7 - (calendarGridCells.length % 7)) % 7;
  for (let n = 1; n <= remainingCells; n++) {
    const nextM = currentMonth === 11 ? 0 : currentMonth + 1;
    const nextY = currentMonth === 11 ? currentYear + 1 : currentYear;
    const iso = `${nextY}-${(nextM + 1).toString().padStart(2, '0')}-${n.toString().padStart(2, '0')}`;
    calendarGridCells.push({ dateISO: iso, dayNum: n, isCurrentMonth: false });
  }

  // Helper for origin badge styles
  const getOriginBadgeClass = (origin: 'FOCOVEST' | 'RITVIDA' | 'MEI') => {
    switch (origin) {
      case 'FOCOVEST':
        return 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30';
      case 'RITVIDA':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'MEI':
        return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
    }
  };

  const getOriginIcon = (origin: 'FOCOVEST' | 'RITVIDA' | 'MEI') => {
    switch (origin) {
      case 'FOCOVEST':
        return <Target className="w-3 h-3 text-indigo-400" />;
      case 'RITVIDA':
        return <Sparkles className="w-3 h-3 text-emerald-400" />;
      case 'MEI':
        return <Building2 className="w-3 h-3 text-amber-400" />;
    }
  };

  return (
    <div className="space-y-6">
      {/* Top Main Navigation & Interlinking Header */}
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-xl space-y-4">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4">
          <div>
            <div className="flex items-center gap-2">
              <span className="text-[11px] font-extrabold text-indigo-600 dark:text-indigo-400 uppercase tracking-wider bg-indigo-500/10 px-3 py-1 rounded-full border border-indigo-500/20 w-fit flex items-center gap-1.5">
                <Layers className="w-3.5 h-3.5" /> Agenda Unificada
              </span>
              <span className="text-xs text-slate-500 dark:text-slate-400">
                • FocoVest + RitVida + MEI Interligados
              </span>
            </div>
            <h2 className="text-xl md:text-2xl font-extrabold text-slate-900 dark:text-slate-100 mt-2 flex items-center gap-2">
              <Calendar className="w-6 h-6 text-indigo-500" />
              Agenda Central & Cronograma Interativo
            </h2>
          </div>

          <div className="flex items-center gap-2 flex-wrap">
            {/* Time Granularity Selector (Dia, Mês, Ano) */}
            <div className="bg-slate-100 dark:bg-slate-950 p-1 rounded-2xl border border-slate-200 dark:border-slate-800 flex items-center gap-1 shadow-inner">
              <button
                onClick={() => setTimeGranularity('DIA')}
                className={`px-3.5 py-1.5 text-xs font-bold rounded-xl transition ${
                  timeGranularity === 'DIA'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                Dia
              </button>
              <button
                onClick={() => setTimeGranularity('MES')}
                className={`px-3.5 py-1.5 text-xs font-bold rounded-xl transition ${
                  timeGranularity === 'MES'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                Mês
              </button>
              <button
                onClick={() => setTimeGranularity('ANO')}
                className={`px-3.5 py-1.5 text-xs font-bold rounded-xl transition ${
                  timeGranularity === 'ANO'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                Ano
              </button>
            </div>

            {/* Layout View Selector */}
            <div className="bg-slate-100 dark:bg-slate-950 p-1 rounded-2xl border border-slate-200 dark:border-slate-800 flex items-center gap-1 shadow-inner">
              <button
                onClick={() => setLayoutView('TIMELINE')}
                className={`px-3 py-1.5 text-xs font-bold rounded-xl flex items-center gap-1.5 transition ${
                  layoutView === 'TIMELINE'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                <Clock className="w-3.5 h-3.5" /> Linha do Tempo
              </button>
              <button
                onClick={() => setLayoutView('GANTT')}
                className={`px-3 py-1.5 text-xs font-bold rounded-xl flex items-center gap-1.5 transition ${
                  layoutView === 'GANTT'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                <BarChart2 className="w-3.5 h-3.5" /> GANTT
              </button>
              <button
                onClick={() => setLayoutView('KANBAN')}
                className={`px-3 py-1.5 text-xs font-bold rounded-xl flex items-center gap-1.5 transition ${
                  layoutView === 'KANBAN'
                    ? 'bg-indigo-600 text-white shadow-md'
                    : 'text-slate-600 dark:text-slate-400 hover:text-slate-200'
                }`}
              >
                <KanbanIcon className="w-3.5 h-3.5" /> Kanban
              </button>
            </div>

            <button
              onClick={() => handleOpenAddModal()}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-bold rounded-xl flex items-center gap-1.5 transition shadow-lg shadow-indigo-600/20"
            >
              <Plus className="w-4 h-4" /> Nova Tarefa
            </button>
          </div>
        </div>

        {/* Origin Filter Bar (Interlinking) */}
        <div className="flex items-center gap-2 pt-3 border-t border-slate-200 dark:border-slate-800 overflow-x-auto pb-1 scrollbar-none">
          <span className="text-xs font-bold text-slate-500 dark:text-slate-400 flex items-center gap-1 shrink-0">
            <Filter className="w-3.5 h-3.5 text-indigo-500" /> Origem:
          </span>
          <button
            onClick={() => setOriginFilter('TODOS')}
            className={`px-3 py-1.5 text-xs font-bold rounded-xl transition shrink-0 ${
              originFilter === 'TODOS'
                ? 'bg-slate-900 text-white dark:bg-slate-100 dark:text-slate-900 shadow-md'
                : 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 hover:bg-slate-200'
            }`}
          >
            Todas as Páginas ({allUnifiedItems.length})
          </button>
          <button
            onClick={() => setOriginFilter('FOCOVEST')}
            className={`px-3 py-1.5 text-xs font-bold rounded-xl transition flex items-center gap-1.5 shrink-0 ${
              originFilter === 'FOCOVEST'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20 hover:bg-indigo-500/20'
            }`}
          >
            <Target className="w-3.5 h-3.5" /> FocoVest ({allUnifiedItems.filter((i) => i.moduleOrigin === 'FOCOVEST').length})
          </button>
          <button
            onClick={() => setOriginFilter('RITVIDA')}
            className={`px-3 py-1.5 text-xs font-bold rounded-xl transition flex items-center gap-1.5 shrink-0 ${
              originFilter === 'RITVIDA'
                ? 'bg-emerald-600 text-white shadow-md'
                : 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20 hover:bg-emerald-500/20'
            }`}
          >
            <Sparkles className="w-3.5 h-3.5" /> RitVida ({allUnifiedItems.filter((i) => i.moduleOrigin === 'RITVIDA').length})
          </button>
          <button
            onClick={() => setOriginFilter('MEI')}
            className={`px-3 py-1.5 text-xs font-bold rounded-xl transition flex items-center gap-1.5 shrink-0 ${
              originFilter === 'MEI'
                ? 'bg-amber-600 text-white shadow-md'
                : 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20 hover:bg-amber-500/20'
            }`}
          >
            <Building2 className="w-3.5 h-3.5" /> MEI ({allUnifiedItems.filter((i) => i.moduleOrigin === 'MEI').length})
          </button>
        </div>
      </div>

      {/* ========================================================================= */}
      {/* 2. GRANULARITY VIEW: DIA (DAY TIMELINE & DETAILS)                        */}
      {/* ========================================================================= */}
      {timeGranularity === 'DIA' && (
        <div className="space-y-6">
          {/* Day Date Stepper & Day Summary Stats */}
          <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-md flex flex-col md:flex-row items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <button
                onClick={() => handleStepDay(-1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
                title="Dia Anterior"
              >
                <ChevronLeft className="w-5 h-5" />
              </button>

              <div className="text-center md:text-left">
                <p className="text-xs text-slate-500 dark:text-slate-400 uppercase font-bold">
                  Agenda do Dia
                </p>
                <div className="flex items-center gap-2">
                  <h3 className="text-lg font-black text-slate-900 dark:text-slate-100">
                    {formatBRDate(selectedDateISO)}
                  </h3>
                  {selectedDateISO === todayISO && (
                    <span className="text-[10px] bg-emerald-500/10 text-emerald-500 border border-emerald-500/30 px-2 py-0.5 rounded-full font-bold">
                      Hoje
                    </span>
                  )}
                </div>
              </div>

              <button
                onClick={() => handleStepDay(1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
                title="Próximo Dia"
              >
                <ChevronRight className="w-5 h-5" />
              </button>

              <button
                onClick={() => setSelectedDateISO(todayISO)}
                className="ml-2 px-3 py-1.5 text-xs bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold rounded-xl transition"
              >
                Ir para Hoje
              </button>
            </div>

            {/* Progress metric for selected day */}
            <div className="flex items-center gap-4 bg-slate-50 dark:bg-slate-950 p-3 rounded-2xl border border-slate-200 dark:border-slate-800 w-full md:w-auto">
              <div className="space-y-1 min-w-[140px]">
                <div className="flex justify-between text-xs font-bold text-slate-700 dark:text-slate-300">
                  <span>Concluídos</span>
                  <span>{completedCountForDay}/{totalCountForDay}</span>
                </div>
                <div className="w-full bg-slate-200 dark:bg-slate-800 h-2 rounded-full overflow-hidden">
                  <div
                    className="bg-indigo-600 h-full transition-all duration-300"
                    style={{ width: `${dayProgressPct}%` }}
                  />
                </div>
              </div>
              <span className="text-lg font-extrabold text-indigo-600 dark:text-indigo-400">
                {dayProgressPct}%
              </span>
            </div>
          </div>

          {/* LAYOUT VIEW: TIMELINE */}
          {layoutView === 'TIMELINE' && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              {/* Daily Interactive Task Cards List */}
              <div className="lg:col-span-2 space-y-4">
                <div className="flex items-center justify-between">
                  <h3 className="text-base font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                    <CheckSquare className="w-5 h-5 text-indigo-500" />
                    Tarefas Agendadas ({itemsForSelectedDay.length})
                  </h3>
                </div>

                {itemsForSelectedDay.length === 0 ? (
                  <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-8 text-center space-y-3">
                    <Calendar className="w-10 h-10 text-slate-400 mx-auto" />
                    <p className="text-sm font-semibold text-slate-600 dark:text-slate-300">
                      Nenhuma tarefa agendada para {formatBRDate(selectedDateISO)}.
                    </p>
                    <button
                      onClick={() => handleOpenAddModal()}
                      className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition inline-flex items-center gap-2"
                    >
                      <Plus className="w-4 h-4" /> Adicionar Tarefa
                    </button>
                  </div>
                ) : (
                  <div className="space-y-3">
                    {itemsForSelectedDay.map((item) => (
                      <div
                        key={item.id}
                        className={`bg-white dark:bg-slate-900 border ${
                          item.status === 'Concluído'
                            ? 'border-emerald-500/30 dark:border-emerald-500/20 opacity-80'
                            : 'border-slate-200 dark:border-slate-800'
                        } rounded-2xl p-4 transition shadow-sm dark:shadow-md space-y-3`}
                      >
                        <div className="flex items-start justify-between gap-3">
                          <div className="flex items-start gap-3">
                            <button
                              onClick={() => handleToggleItemCompletion(item)}
                              className={`mt-0.5 p-1 rounded-lg transition ${
                                item.status === 'Concluído'
                                  ? 'bg-emerald-500 text-white'
                                  : 'border border-slate-300 dark:border-slate-700 text-slate-400 hover:text-emerald-500'
                              }`}
                            >
                              <Check className="w-4 h-4" />
                            </button>

                            <div>
                              <div className="flex items-center gap-2 flex-wrap">
                                <span
                                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full border flex items-center gap-1 ${getOriginBadgeClass(
                                    item.moduleOrigin
                                  )}`}
                                >
                                  {getOriginIcon(item.moduleOrigin)}
                                  {item.moduleOrigin}
                                </span>
                                <span className="text-[10px] font-bold bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-300 px-2 py-0.5 rounded-full">
                                  {item.categoryTag}
                                </span>
                              </div>

                              <h4
                                className={`text-sm font-extrabold text-slate-900 dark:text-slate-100 mt-1 ${
                                  item.status === 'Concluído' ? 'line-through text-slate-400 dark:text-slate-500' : ''
                                }`}
                              >
                                {item.title}
                              </h4>

                              {item.details && (
                                <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">
                                  {item.details}
                                </p>
                              )}
                            </div>
                          </div>

                          <div className="flex items-center gap-1 shrink-0">
                            {/* Switch to originating module page */}
                            <button
                              onClick={() => handleJumpToModule(item.moduleOrigin, item.originalType)}
                              className="p-1.5 text-slate-400 hover:text-indigo-500 rounded-lg transition"
                              title="Ir para a página de origem"
                            >
                              <ExternalLink className="w-4 h-4" />
                            </button>

                            {item.originalType === 'VISUAL_TASK' && (
                              <>
                                <button
                                  onClick={() => handleOpenEditModal(item.rawObject as VisualTask)}
                                  className="p-1.5 text-slate-400 hover:text-amber-500 rounded-lg transition"
                                  title="Editar"
                                >
                                  <Edit3 className="w-4 h-4" />
                                </button>
                                <button
                                  onClick={() => deleteVisualTask(item.rawObject.id)}
                                  className="p-1.5 text-slate-400 hover:text-rose-500 rounded-lg transition"
                                  title="Excluir"
                                >
                                  <Trash2 className="w-4 h-4" />
                                </button>
                              </>
                            )}
                          </div>
                        </div>

                        {/* Date Range & Time Badge */}
                        <div className="flex items-center justify-between text-xs text-slate-500 dark:text-slate-400 pt-2 border-t border-slate-100 dark:border-slate-800/60">
                          <span className="flex items-center gap-1 font-mono text-[11px]">
                            <Clock className="w-3.5 h-3.5 text-indigo-500" />
                            {item.startTime} - {item.endTime} ({formatTaskDuration(item.startTime, item.endTime)})
                          </span>
                          <span className="font-semibold text-[11px]">
                            📅 {formatBRDate(item.startDate)}
                            {item.endDate && item.endDate !== item.startDate && ` a ${formatBRDate(item.endDate)}`}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Hourly Schedule View (00:00 to 23:00) */}
              <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-md space-y-3">
                <h3 className="text-sm font-extrabold text-slate-900 dark:text-slate-100 flex items-center justify-between">
                  <span className="flex items-center gap-1.5">
                    <Clock className="w-4 h-4 text-indigo-500" /> Grade Horária (24h)
                  </span>
                  <span className="text-[10px] text-slate-400 font-normal">Clique para Agendar</span>
                </h3>

                <div className="space-y-1.5 max-h-[500px] overflow-y-auto pr-1">
                  {Array.from({ length: 24 }, (_, i) => i).map((hour) => {
                    const hourTasks = itemsForSelectedDay.filter((item) => {
                      if (item.originalType === 'VISUAL_TASK') {
                        return (item.rawObject as VisualTask).startHour === hour;
                      }
                      const hStr = hour.toString().padStart(2, '0');
                      return item.startTime?.startsWith(hStr);
                    });

                    return (
                      <div
                        key={hour}
                        className="group flex items-start gap-2 p-2 rounded-xl bg-slate-50 dark:bg-slate-950 border border-slate-200/60 dark:border-slate-800/80 hover:border-indigo-500/40 transition"
                      >
                        <span className="text-[10px] font-bold font-mono text-slate-400 w-10 pt-0.5">
                          {hour.toString().padStart(2, '0')}:00
                        </span>

                        <div className="flex-1 min-h-[24px]">
                          {hourTasks.length > 0 ? (
                            <div className="space-y-1">
                              {hourTasks.map((ht) => (
                                <div
                                  key={ht.id}
                                  className={`text-[11px] font-bold p-1.5 rounded-lg border flex items-center justify-between ${getOriginBadgeClass(
                                    ht.moduleOrigin
                                  )}`}
                                >
                                  <span className="truncate">{ht.title}</span>
                                  <span className="text-[9px] opacity-80">{ht.startTime}</span>
                                </div>
                              ))}
                            </div>
                          ) : (
                            <button
                              onClick={() => handleOpenAddModal(hour)}
                              className="w-full text-left text-[10px] text-slate-400 opacity-0 group-hover:opacity-100 transition hover:text-indigo-500 font-semibold py-0.5"
                            >
                              + Agendar às {hour.toString().padStart(2, '0')}:00
                            </button>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          )}

          {/* LAYOUT VIEW: GANTT */}
          {layoutView === 'GANTT' && (
            <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-md space-y-4 overflow-x-auto">
              <h3 className="text-base font-extrabold text-slate-900 dark:text-slate-100 flex items-center gap-2">
                <BarChart2 className="w-5 h-5 text-indigo-500" /> Fluxo de Execução - GANTT (06:00 às 22:00)
              </h3>

              <div className="min-w-[700px] space-y-2">
                <div className="flex items-center text-[10px] font-bold text-slate-400 border-b border-slate-200 dark:border-slate-800 pb-2">
                  <span className="w-48 shrink-0">Tarefa / Módulo</span>
                  <div className="flex-1 grid grid-cols-16 gap-1 text-center font-mono">
                    {Array.from({ length: 16 }, (_, i) => i + 6).map((h) => (
                      <span key={h}>{h}h</span>
                    ))}
                  </div>
                </div>

                {itemsForSelectedDay.map((item) => (
                  <div
                    key={item.id}
                    className="flex items-center gap-2 py-2 border-b border-slate-100 dark:border-slate-800/50 text-xs"
                  >
                    <div className="w-48 shrink-0 truncate font-semibold text-slate-800 dark:text-slate-200 flex items-center gap-1.5">
                      {getOriginIcon(item.moduleOrigin)}
                      <span className="truncate">{item.title}</span>
                    </div>

                    <div className="flex-1 bg-slate-100 dark:bg-slate-950 h-7 rounded-xl relative overflow-hidden border border-slate-200 dark:border-slate-800 flex items-center px-2">
                      <div
                        className={`absolute h-5 rounded-lg text-[10px] font-bold text-white flex items-center px-2 shadow-sm ${
                          item.moduleOrigin === 'FOCOVEST'
                            ? 'bg-indigo-600'
                            : item.moduleOrigin === 'MEI'
                            ? 'bg-amber-600'
                            : 'bg-emerald-600'
                        }`}
                        style={{
                          left: '10%',
                          width: '70%'
                        }}
                      >
                        <span className="truncate">{item.startTime} - {item.endTime}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* LAYOUT VIEW: KANBAN */}
          {layoutView === 'KANBAN' && (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {(['A Fazer', 'Em Progresso', 'Concluído'] as const).map((columnStatus) => {
                const columnItems = itemsForSelectedDay.filter((i) => i.status === columnStatus);

                return (
                  <div
                    key={columnStatus}
                    className="bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-3xl p-4 space-y-3"
                  >
                    <div className="flex items-center justify-between">
                      <h4 className="text-xs font-black uppercase text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
                        <Circle className="w-3.5 h-3.5 text-indigo-500" />
                        {columnStatus} ({columnItems.length})
                      </h4>
                    </div>

                    <div className="space-y-3 min-h-[200px]">
                      {columnItems.map((item) => (
                        <div
                          key={item.id}
                          className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl p-3 shadow-sm space-y-2"
                        >
                          <div className="flex items-center justify-between">
                            <span
                              className={`text-[9px] font-bold px-2 py-0.5 rounded-full border flex items-center gap-1 ${getOriginBadgeClass(
                                item.moduleOrigin
                              )}`}
                            >
                              {getOriginIcon(item.moduleOrigin)}
                              {item.moduleOrigin}
                            </span>

                            <button
                              onClick={() => handleToggleItemCompletion(item)}
                              className="text-[10px] text-indigo-500 hover:underline font-semibold"
                            >
                              Mudar Status
                            </button>
                          </div>

                          <h5 className="text-xs font-bold text-slate-900 dark:text-slate-100">
                            {item.title}
                          </h5>

                          <p className="text-[10px] text-slate-500 dark:text-slate-400 font-mono">
                            🕒 {item.startTime} - {item.endTime}
                          </p>
                        </div>
                      ))}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* 3. GRANULARITY VIEW: MÊS (MONTH CALENDAR GRID)                            */}
      {/* ========================================================================= */}
      {timeGranularity === 'MES' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-xl space-y-5">
          {/* Month Header Stepper */}
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <button
                onClick={() => handleStepMonth(-1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
              >
                <ChevronLeft className="w-5 h-5" />
              </button>

              <h3 className="text-lg md:text-xl font-black text-slate-900 dark:text-slate-100">
                {MONTH_NAMES_PT[currentMonth]} {currentYear}
              </h3>

              <button
                onClick={() => handleStepMonth(1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
              >
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>

            <button
              onClick={() => {
                setCurrentMonth(new Date().getMonth());
                setCurrentYear(new Date().getFullYear());
                setSelectedDateISO(todayISO);
              }}
              className="px-3 py-1.5 text-xs bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 text-slate-700 dark:text-slate-300 font-bold rounded-xl transition"
            >
              Mês Atual
            </button>
          </div>

          {/* Weekday Labels Header */}
          <div className="grid grid-cols-7 gap-2 text-center text-xs font-black text-slate-500 dark:text-slate-400 border-b border-slate-200 dark:border-slate-800 pb-2">
            {WEEKDAYS_PT.map((day) => (
              <span key={day}>{day}</span>
            ))}
          </div>

          {/* Calendar Grid Cells */}
          <div className="grid grid-cols-7 gap-2">
            {calendarGridCells.map((cell, idx) => {
              const dayItems = filteredItemsByOrigin.filter((item) => {
                return item.startDate <= cell.dateISO && item.endDate >= cell.dateISO;
              });

              const isTodayCell = cell.dateISO === todayISO;
              const isSelectedCell = cell.dateISO === selectedDateISO;

              return (
                <div
                  key={idx}
                  onClick={() => {
                    setSelectedDateISO(cell.dateISO);
                    setTimeGranularity('DIA');
                  }}
                  className={`min-h-[90px] md:min-h-[110px] p-2 rounded-2xl border transition cursor-pointer flex flex-col justify-between ${
                    isSelectedCell
                      ? 'border-indigo-600 bg-indigo-500/10 dark:bg-indigo-500/20 shadow-md'
                      : isTodayCell
                      ? 'border-emerald-500/60 bg-emerald-500/5 dark:bg-emerald-500/10'
                      : cell.isCurrentMonth
                      ? 'bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-800/80 hover:border-indigo-400'
                      : 'bg-slate-100/50 dark:bg-slate-950/40 border-slate-200/50 dark:border-slate-800/30 opacity-40'
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span
                      className={`text-xs font-extrabold ${
                        isTodayCell
                          ? 'bg-emerald-500 text-white w-6 h-6 rounded-full flex items-center justify-center'
                          : 'text-slate-700 dark:text-slate-300'
                      }`}
                    >
                      {cell.dayNum}
                    </span>

                    {dayItems.length > 0 && (
                      <span className="text-[10px] font-bold px-1.5 py-0.5 rounded-full bg-slate-200 dark:bg-slate-800 text-slate-600 dark:text-slate-300">
                        {dayItems.length}
                      </span>
                    )}
                  </div>

                  {/* Task Pills Preview */}
                  <div className="space-y-1 mt-1 overflow-hidden">
                    {dayItems.slice(0, 3).map((item) => (
                      <div
                        key={item.id}
                        className={`text-[9px] font-bold px-1.5 py-0.5 rounded truncate border ${getOriginBadgeClass(
                          item.moduleOrigin
                        )}`}
                      >
                        {item.title}
                      </div>
                    ))}
                    {dayItems.length > 3 && (
                      <span className="text-[9px] text-slate-400 font-bold block">
                        +{dayItems.length - 3} mais
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* 4. GRANULARITY VIEW: ANO (12 MONTHS YEARLY OVERVIEW)                     */}
      {/* ========================================================================= */}
      {timeGranularity === 'ANO' && (
        <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl p-5 shadow-sm dark:shadow-xl space-y-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <button
                onClick={() => setCurrentYear(currentYear - 1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
              >
                <ChevronLeft className="w-5 h-5" />
              </button>

              <h3 className="text-xl md:text-2xl font-black text-slate-900 dark:text-slate-100">
                Visão Anual de {currentYear}
              </h3>

              <button
                onClick={() => setCurrentYear(currentYear + 1)}
                className="p-2 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl text-slate-700 dark:text-slate-200 transition"
              >
                <ChevronRight className="w-5 h-5" />
              </button>
            </div>

            <span className="text-xs font-bold text-slate-500 dark:text-slate-400">
              Clique em um mês para abrir o calendário completo
            </span>
          </div>

          {/* 12 Months Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
            {MONTH_NAMES_PT.map((mName, mIdx) => {
              const monthPrefix = `${currentYear}-${(mIdx + 1).toString().padStart(2, '0')}`;
              const monthItems = filteredItemsByOrigin.filter((i) => i.startDate.startsWith(monthPrefix));
              const focovestCount = monthItems.filter((i) => i.moduleOrigin === 'FOCOVEST').length;
              const meiCount = monthItems.filter((i) => i.moduleOrigin === 'MEI').length;
              const ritvidaCount = monthItems.filter((i) => i.moduleOrigin === 'RITVIDA').length;

              return (
                <div
                  key={mName}
                  onClick={() => {
                    setCurrentMonth(mIdx);
                    setTimeGranularity('MES');
                  }}
                  className="bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl p-4 hover:border-indigo-500 transition cursor-pointer space-y-3 shadow-sm hover:shadow-md"
                >
                  <div className="flex items-center justify-between border-b border-slate-200 dark:border-slate-800 pb-2">
                    <h4 className="text-sm font-extrabold text-slate-900 dark:text-slate-100">
                      {mName}
                    </h4>
                    <span className="text-xs font-extrabold text-indigo-600 dark:text-indigo-400">
                      {monthItems.length} tarefas
                    </span>
                  </div>

                  <div className="space-y-1.5 text-xs text-slate-600 dark:text-slate-400">
                    <div className="flex items-center justify-between">
                      <span className="flex items-center gap-1">🎯 FocoVest</span>
                      <span className="font-bold">{focovestCount}</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="flex items-center gap-1">🏢 MEI</span>
                      <span className="font-bold">{meiCount}</span>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="flex items-center gap-1">🌿 RitVida</span>
                      <span className="font-bold">{ritvidaCount}</span>
                    </div>
                  </div>

                  <button className="w-full text-center text-[10px] font-extrabold text-indigo-500 hover:underline pt-1 flex items-center justify-center gap-1">
                    Abrir Mês <ArrowRight className="w-3 h-3" />
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* 5. ADD / EDIT TASK MODAL (Data de Início e Data Final + Horas)            */}
      {/* ========================================================================= */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-3xl max-w-lg w-full p-6 space-y-5 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-lg font-black text-white flex items-center gap-2">
                <Calendar className="w-5 h-5 text-indigo-400" />
                {editingTask ? 'Editar Tarefa' : 'Nova Tarefa na Agenda'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 text-slate-400 hover:text-white rounded-lg transition"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveTask} className="space-y-4 text-xs">
              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  Título da Tarefa
                </label>
                <input
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Ex: Bloco de Exercícios, Envio de NF, Treino..."
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-slate-100 focus:outline-none focus:border-indigo-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                    Função / Categoria
                  </label>
                  <select
                    value={func}
                    onChange={(e) => setFunc(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2.5 text-slate-100 focus:outline-none"
                  >
                    <option value="Trabalho">Trabalho</option>
                    <option value="Estudante">Estudante</option>
                    <option value="Saúde">Saúde / Treino</option>
                    <option value="Administrativo">Administrativo</option>
                    <option value="Pessoal">Pessoal</option>
                  </select>
                </div>

                <div>
                  <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                    Tag / Prioridade
                  </label>
                  <input
                    type="text"
                    value={tag}
                    onChange={(e) => setTag(e.target.value)}
                    placeholder="Ex: Urgente, Foco Total..."
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2.5 text-slate-100 focus:outline-none focus:border-indigo-500"
                  />
                </div>
              </div>

              {/* DATA DE INÍCIO E DATA FINAL */}
              <div className="grid grid-cols-2 gap-3 bg-slate-950/80 border border-slate-800 p-3.5 rounded-2xl">
                <div>
                  <label className="block text-[11px] font-bold text-indigo-400 mb-1">
                    📅 Data de Início
                  </label>
                  <input
                    type="date"
                    value={startDateInput}
                    onChange={(e) => setStartDateInput(e.target.value)}
                    className="w-full bg-slate-900 border border-indigo-500/40 rounded-xl p-2.5 text-slate-100 focus:outline-none focus:border-indigo-400 font-mono"
                    required
                  />
                </div>

                <div>
                  <label className="block text-[11px] font-bold text-indigo-400 mb-1">
                    📅 Data Final
                  </label>
                  <input
                    type="date"
                    value={endDateInput}
                    onChange={(e) => setEndDateInput(e.target.value)}
                    className="w-full bg-slate-900 border border-indigo-500/40 rounded-xl p-2.5 text-slate-100 focus:outline-none focus:border-indigo-400 font-mono"
                    required
                  />
                </div>
              </div>

              {/* HORA DE INÍCIO E HORA DE FIM */}
              <div className="grid grid-cols-2 gap-3 bg-slate-950/80 border border-slate-800 p-3.5 rounded-2xl">
                <div>
                  <label className="block text-[11px] font-bold text-slate-300 mb-1">
                    🕒 Hora de Início
                  </label>
                  <input
                    type="time"
                    value={startTimeInput}
                    onChange={(e) => setStartTimeInput(e.target.value)}
                    className="w-full bg-slate-900 border border-slate-700 rounded-xl p-2.5 text-slate-100 font-mono focus:outline-none focus:border-indigo-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-[11px] font-bold text-indigo-400 mb-1">
                    🕒 Hora de Fim
                  </label>
                  <input
                    type="time"
                    value={endTimeInput}
                    onChange={(e) => setEndTimeInput(e.target.value)}
                    className="w-full bg-slate-900 border border-indigo-500/40 rounded-xl p-2.5 text-slate-100 font-mono focus:outline-none focus:border-indigo-400"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  Status Inicial
                </label>
                <select
                  value={status}
                  onChange={(e) => setStatus(e.target.value as any)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2.5 text-slate-100 focus:outline-none"
                >
                  <option value="A Fazer">A Fazer</option>
                  <option value="Em Progresso">Em Progresso</option>
                  <option value="Concluído">Concluído</option>
                </select>
              </div>

              <div>
                <label className="block text-[11px] font-semibold text-slate-300 mb-1">
                  Checklist de Subtarefas (1 por linha)
                </label>
                <textarea
                  rows={3}
                  value={checklistInput}
                  onChange={(e) => setChecklistInput(e.target.value)}
                  placeholder="Passo 1&#10;Passo 2&#10;Passo 3"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-2.5 text-slate-100 focus:outline-none focus:border-indigo-500 resize-none"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold rounded-xl transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl transition shadow-lg shadow-indigo-600/20"
                >
                  Salvar Tarefa
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
