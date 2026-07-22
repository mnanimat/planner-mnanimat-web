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
  List,
  CheckCircle2,
  X,
  Sparkles,
  Move,
  ChevronLeft,
  ChevronRight,
  ArrowLeftRight
} from 'lucide-react';

export const RitVidaVisual: React.FC = () => {
  const { visualTasks, insertVisualTask, deleteVisualTask, updateVisualTask } = useApp();

  const [activeView, setActiveView] = useState<'TIMELINE' | 'GANTT' | 'KANBAN'>('TIMELINE');
  const [editingTask, setEditingTask] = useState<VisualTask | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Form State
  const [title, setTitle] = useState('');
  const [func, setFunc] = useState('Trabalho');
  const [tag, setTag] = useState('Urgente');
  const [startHour, setStartHour] = useState(8);
  const [endHour, setEndHour] = useState(10);
  const [durationHours, setDurationHours] = useState(2);
  const [status, setStatus] = useState<'A Fazer' | 'Em Progresso' | 'Concluído'>('A Fazer');
  const [checklistInput, setChecklistInput] = useState('');

  // Drag and Drop state
  const [draggedTaskId, setDraggedTaskId] = useState<number | null>(null);

  const handleOpenAddModal = () => {
    setEditingTask(null);
    setTitle('');
    setFunc('Trabalho');
    setTag('Urgente');
    setStartHour(8);
    setEndHour(10);
    setDurationHours(2);
    setStatus('A Fazer');
    setChecklistInput('');
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (task: VisualTask) => {
    setEditingTask(task);
    setTitle(task.title);
    setFunc(task.function);
    setTag(task.tag);
    setStartHour(task.startHour);
    setDurationHours(task.durationHours);
    setEndHour(Math.min(24, task.startHour + task.durationHours));
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

    const computedDuration = Math.max(1, endHour - startHour);
    const startTimeFormatted = `${startHour.toString().padStart(2, '0')}:00`;
    const endTimeFormatted = `${(endHour % 24).toString().padStart(2, '0')}:00`;
    const dateToday = new Date().toISOString().split('T')[0];

    if (editingTask) {
      updateVisualTask({
        ...editingTask,
        title,
        function: func,
        tag,
        startHour,
        durationHours: computedDuration,
        startTime: startTimeFormatted,
        endTime: endTimeFormatted,
        status,
        checklistRaw: checklistFormatted
      });
    } else {
      insertVisualTask(
        title,
        dateToday,
        startTimeFormatted,
        dateToday,
        endTimeFormatted,
        startHour,
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

  // Drag Handlers for Kanban & Time Grid
  const handleDragStart = (e: React.DragEvent, id: number) => {
    setDraggedTaskId(id);
    e.dataTransfer.setData('text/plain', id.toString());
  };

  const handleDropStatus = (newStatus: 'A Fazer' | 'Em Progresso' | 'Concluído') => {
    if (!draggedTaskId) return;
    const task = visualTasks.find((t) => t.id === draggedTaskId);
    if (task) {
      updateVisualTask({ ...task, status: newStatus });
    }
    setDraggedTaskId(null);
  };

  const handleDropHour = (targetHour: number) => {
    if (!draggedTaskId) return;
    const task = visualTasks.find((t) => t.id === draggedTaskId);
    if (task) {
      const startTimeFormatted = `${targetHour.toString().padStart(2, '0')}:00`;
      const endHourCalc = (targetHour + task.durationHours) % 24;
      const endTimeFormatted = `${endHourCalc.toString().padStart(2, '0')}:00`;
      updateVisualTask({
        ...task,
        startHour: targetHour,
        startTime: startTimeFormatted,
        endTime: endTimeFormatted
      });
    }
    setDraggedTaskId(null);
  };

  const handleShiftTaskHour = (task: VisualTask, deltaHours: number) => {
    const newStartHour = Math.max(0, Math.min(23, task.startHour + deltaHours));
    const startTimeFormatted = `${newStartHour.toString().padStart(2, '0')}:00`;
    const endHourCalc = (newStartHour + task.durationHours) % 24;
    const endTimeFormatted = `${endHourCalc.toString().padStart(2, '0')}:00`;

    updateVisualTask({
      ...task,
      startHour: newStartHour,
      startTime: startTimeFormatted,
      endTime: endTimeFormatted
    });
  };

  const handleResizeTaskEndHour = (task: VisualTask, deltaHours: number) => {
    const newDuration = Math.max(1, Math.min(24 - task.startHour, task.durationHours + deltaHours));
    const endHourCalc = (task.startHour + newDuration) % 24;
    const endTimeFormatted = `${endHourCalc.toString().padStart(2, '0')}:00`;

    updateVisualTask({
      ...task,
      durationHours: newDuration,
      endTime: endTimeFormatted
    });
  };

  const ganttMinHour = 6;
  const ganttMaxHour = 22;
  const ganttTotalHours = ganttMaxHour - ganttMinHour + 1; // 17 hours
  const ganttHoursArray = Array.from({ length: ganttTotalHours }, (_, i) => ganttMinHour + i);

  return (
    <div className="space-y-6">
      {/* Header & Controls */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-lg">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Calendar className="w-6 h-6 text-indigo-400" />
            Agenda RitVida & Timeline Interativa
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            Gerencie tarefas por Horários, Gantt e Quadro Kanban com suporte a arrastar e soltar (Drag & Drop).
          </p>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          {/* View Switcher Buttons */}
          <div className="bg-slate-950 p-1 rounded-xl border border-slate-800 flex items-center gap-1">
            <button
              onClick={() => setActiveView('TIMELINE')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg flex items-center gap-1.5 transition ${
                activeView === 'TIMELINE'
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Clock className="w-3.5 h-3.5" /> Horários 24h
            </button>
            <button
              onClick={() => setActiveView('GANTT')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg flex items-center gap-1.5 transition ${
                activeView === 'GANTT'
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <BarChart2 className="w-3.5 h-3.5" /> GANTT
            </button>
            <button
              onClick={() => setActiveView('KANBAN')}
              className={`px-3 py-1.5 text-xs font-bold rounded-lg flex items-center gap-1.5 transition ${
                activeView === 'KANBAN'
                  ? 'bg-indigo-600 text-white shadow-md'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <KanbanIcon className="w-3.5 h-3.5" /> KANBAN
            </button>
          </div>

          <button
            onClick={handleOpenAddModal}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center gap-2 shadow-lg shadow-indigo-600/20 transition"
          >
            <Plus className="w-4 h-4" /> Nova Tarefa
          </button>
        </div>
      </div>

      {/* VIEW 1: TIMELINE 24H (ARRASTAR E SOLTAR PREDOMINANTE) */}
      {activeView === 'TIMELINE' && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-xl">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
              <Clock className="w-4 h-4 text-indigo-400" />
              Grade de Horários (Arraste a tarefa para mudar o horário)
            </h3>
            <span className="text-[10px] font-semibold text-slate-400 bg-slate-800 px-2.5 py-1 rounded-full">
              {visualTasks.length} Tarefa(s) agendada(s)
            </span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-3">
            {Array.from({ length: 16 }, (_, i) => i + 7).map((hour) => {
              const hourTasks = visualTasks.filter((t) => t.startHour === hour);

              return (
                <div
                  key={hour}
                  onDragOver={(e) => e.preventDefault()}
                  onDrop={() => handleDropHour(hour)}
                  className="bg-slate-950 border border-slate-800/80 rounded-xl p-3 min-h-[90px] flex flex-col justify-between hover:border-indigo-500/50 transition group"
                >
                  <div className="flex items-center justify-between border-b border-slate-800/60 pb-1.5 mb-2">
                    <span className="text-xs font-bold text-indigo-400 flex items-center gap-1">
                      <Clock className="w-3 h-3" /> {hour.toString().padStart(2, '0')}:00
                    </span>
                    <button
                      onClick={() => {
                        setStartHour(hour);
                        handleOpenAddModal();
                      }}
                      className="text-[10px] text-slate-500 hover:text-indigo-400 font-semibold opacity-0 group-hover:opacity-100 transition"
                    >
                      + Agendar
                    </button>
                  </div>

                  {hourTasks.length === 0 ? (
                    <p className="text-[11px] text-slate-600 italic">Horário Livre</p>
                  ) : (
                    <div className="space-y-2">
                      {hourTasks.map((task) => {
                        const isDone = task.status === 'Concluído';
                        return (
                          <div
                            key={task.id}
                            draggable
                            onDragStart={(e) => handleDragStart(e, task.id)}
                            className={`p-2.5 rounded-xl border text-xs cursor-grab active:cursor-grabbing transition shadow-sm ${
                              isDone
                                ? 'bg-emerald-950/20 border-emerald-800/40 text-emerald-300'
                                : 'bg-slate-900 border-slate-700/80 text-slate-200 hover:border-indigo-500'
                            }`}
                          >
                            <div className="flex items-center justify-between gap-1 mb-1">
                              <span className="text-[9px] uppercase tracking-wider font-extrabold px-2 py-0.5 rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                                {task.function}
                              </span>

                              <div className="flex items-center gap-1">
                                <button
                                  onClick={() => handleOpenEditModal(task)}
                                  className="p-1 text-slate-400 hover:text-amber-400 rounded transition"
                                  title="Editar tarefa"
                                >
                                  <Edit3 className="w-3.5 h-3.5" />
                                </button>
                                <button
                                  onClick={() => deleteVisualTask(task.id)}
                                  className="p-1 text-slate-400 hover:text-red-400 rounded transition"
                                  title="Excluir"
                                >
                                  <Trash2 className="w-3.5 h-3.5" />
                                </button>
                              </div>
                            </div>

                            <p className={`font-bold text-xs ${isDone ? 'line-through text-slate-400' : 'text-slate-100'}`}>
                              {task.title}
                            </p>

                            <div className="flex items-center justify-between text-[10px] text-slate-400 mt-2 pt-1 border-t border-slate-800">
                              <span>Duração: {task.durationHours}h</span>
                              <span
                                onClick={() => {
                                  const nextStatus =
                                    task.status === 'A Fazer'
                                      ? 'Em Progresso'
                                      : task.status === 'Em Progresso'
                                      ? 'Concluído'
                                      : 'A Fazer';
                                  updateVisualTask({ ...task, status: nextStatus });
                                }}
                                className={`cursor-pointer px-1.5 py-0.5 rounded font-semibold ${
                                  task.status === 'Concluído'
                                    ? 'bg-emerald-500/20 text-emerald-400'
                                    : task.status === 'Em Progresso'
                                    ? 'bg-amber-500/20 text-amber-400'
                                    : 'bg-slate-800 text-slate-400'
                                }`}
                              >
                                {task.status || 'A Fazer'}
                              </span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* VIEW 2: GANTT CHART */}
      {activeView === 'GANTT' && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-xl overflow-x-auto">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 min-w-[800px]">
            <div>
              <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
                <BarChart2 className="w-4 h-4 text-indigo-400" />
                Linha do Tempo GANTT Interativa (06:00 - 22:00)
              </h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Arraste os blocos para os horários na grade, ou ajuste horário de início e final com as setas.
              </p>
            </div>
            <div className="flex items-center gap-2 text-[10px] font-semibold text-slate-400 bg-slate-950 px-3 py-1.5 rounded-lg border border-slate-800">
              <ArrowLeftRight className="w-3.5 h-3.5 text-indigo-400" />
              <span>Arraste a tarefa para mudar o horário</span>
            </div>
          </div>

          <div className="min-w-[900px] border border-slate-800 rounded-xl bg-slate-950 p-4 space-y-3">
            {/* Timeline Header hours */}
            <div className="flex items-center text-[10px] font-bold text-slate-400 border-b border-slate-800/80 pb-2">
              <div className="w-64 pr-3 shrink-0">Tarefa / Ações de Horário</div>
              <div className="flex-1 grid grid-cols-17 gap-0.5 text-center">
                {ganttHoursArray.map((h) => (
                  <div key={h} className="py-1 bg-slate-900/40 rounded text-slate-400">
                    {h.toString().padStart(2, '0')}h
                  </div>
                ))}
              </div>
            </div>

            {/* Gantt Rows */}
            {visualTasks.length === 0 ? (
              <div className="text-center py-10 text-xs text-slate-500 italic">
                Nenhuma tarefa cadastrada na linha do tempo GANTT. Clique em "+ Nova Tarefa" para agendar.
              </div>
            ) : (
              visualTasks.map((task) => {
                const checklistItems = (task.checklistRaw || '').split('|').filter(Boolean);
                const completedItems = checklistItems.filter((i) => i.endsWith(':true')).length;
                const progressPct =
                  checklistItems.length > 0
                    ? Math.round((completedItems / checklistItems.length) * 100)
                    : task.status === 'Concluído'
                    ? 100
                    : 0;

                const currentEndHour = task.startHour + task.durationHours;

                return (
                  <div
                    key={task.id}
                    className="flex items-center text-xs py-2.5 border-b border-slate-800/40 last:border-0 hover:bg-slate-900/40 rounded-lg transition group"
                  >
                    {/* Left Column: Task details & Quick shift controls */}
                    <div className="w-64 pr-3 shrink-0 flex flex-col justify-center space-y-1">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-slate-100 truncate text-xs">{task.title}</span>
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => handleOpenEditModal(task)}
                            className="p-1 text-slate-400 hover:text-amber-400 rounded transition"
                            title="Editar tarefa"
                          >
                            <Edit3 className="w-3 h-3" />
                          </button>
                          <button
                            onClick={() => deleteVisualTask(task.id)}
                            className="p-1 text-slate-400 hover:text-red-400 rounded transition"
                            title="Excluir"
                          >
                            <Trash2 className="w-3 h-3" />
                          </button>
                        </div>
                      </div>

                      <div className="flex items-center justify-between text-[10px] text-slate-400">
                        <span className="text-indigo-400 font-semibold">{task.function}</span>
                        <span className="font-mono bg-slate-900 px-1.5 py-0.5 rounded border border-slate-800 text-slate-300">
                          {task.startTime} - {task.endTime}
                        </span>
                      </div>

                      {/* Moving & Resizing Buttons */}
                      <div className="flex items-center gap-1.5 pt-1">
                        <div className="flex items-center bg-slate-900 border border-slate-800 rounded p-0.5 text-[9px] text-slate-300">
                          <span className="px-1 text-slate-500 font-semibold">Mover:</span>
                          <button
                            onClick={() => handleShiftTaskHour(task, -1)}
                            disabled={task.startHour <= 0}
                            className="px-1 py-0.5 hover:bg-slate-800 hover:text-indigo-400 rounded disabled:opacity-30 transition"
                            title="Voltar 1 hora"
                          >
                            <ChevronLeft className="w-3 h-3" />
                          </button>
                          <button
                            onClick={() => handleShiftTaskHour(task, 1)}
                            disabled={task.startHour >= 23}
                            className="px-1 py-0.5 hover:bg-slate-800 hover:text-indigo-400 rounded disabled:opacity-30 transition"
                            title="Avançar 1 hora"
                          >
                            <ChevronRight className="w-3 h-3" />
                          </button>
                        </div>

                        <div className="flex items-center bg-slate-900 border border-slate-800 rounded p-0.5 text-[9px] text-slate-300">
                          <span className="px-1 text-slate-500 font-semibold">Fim:</span>
                          <button
                            onClick={() => handleResizeTaskEndHour(task, -1)}
                            disabled={task.durationHours <= 1}
                            className="px-1 py-0.5 hover:bg-slate-800 hover:text-amber-400 rounded disabled:opacity-30 font-bold"
                            title="Diminuir horário final (-1h)"
                          >
                            -1h
                          </button>
                          <button
                            onClick={() => handleResizeTaskEndHour(task, 1)}
                            disabled={task.startHour + task.durationHours >= 24}
                            className="px-1 py-0.5 hover:bg-slate-800 hover:text-amber-400 rounded disabled:opacity-30 font-bold"
                            title="Aumentar horário final (+1h)"
                          >
                            +1h
                          </button>
                        </div>
                      </div>
                    </div>

                    {/* Right Column: Drop targets grid and floating GANTT task bar */}
                    <div className="flex-1 relative h-10 bg-slate-900/50 rounded-xl border border-slate-800/80 grid grid-cols-17 gap-0.5 p-0.5 items-center">
                      {/* Hour Drop Targets */}
                      {ganttHoursArray.map((hour) => (
                        <div
                          key={hour}
                          onDragOver={(e) => e.preventDefault()}
                          onDrop={() => handleDropHour(hour)}
                          className="h-full rounded hover:bg-indigo-500/10 border border-transparent hover:border-indigo-500/30 transition cursor-pointer flex items-center justify-center text-[8px] text-slate-700 hover:text-indigo-300"
                          title={`Soltar aqui para mudar início para ${hour.toString().padStart(2, '0')}:00`}
                        >
                          {hour}h
                        </div>
                      ))}

                      {/* Render Draggable Task Bar Overlay */}
                      {(() => {
                        const startOffsetPct = Math.max(0, ((task.startHour - ganttMinHour) / ganttTotalHours) * 100);
                        const durationPct = Math.min(
                          100 - startOffsetPct,
                          (task.durationHours / ganttTotalHours) * 100
                        );

                        // If task is outside viewable window 6-22, display notice
                        if (task.startHour + task.durationHours <= ganttMinHour || task.startHour >= ganttMaxHour + 1) {
                          return (
                            <div className="absolute inset-0 flex items-center justify-center text-[10px] text-slate-500 italic">
                              Agendado fora do horário visual (Início: {task.startTime})
                            </div>
                          );
                        }

                        return (
                          <div
                            draggable
                            onDragStart={(e) => handleDragStart(e, task.id)}
                            style={{
                              left: `${startOffsetPct}%`,
                              width: `${durationPct}%`
                            }}
                            className="absolute h-8 rounded-lg bg-gradient-to-r from-indigo-600 via-indigo-500 to-indigo-600 text-white font-bold text-[10px] flex items-center justify-between px-2.5 shadow-lg border border-indigo-400/40 cursor-grab active:cursor-grabbing hover:scale-[1.01] transition z-10"
                            title={`Arraste para mudar o horário ou clique para editar. (${task.startTime} - ${task.endTime})`}
                          >
                            <div className="flex items-center gap-1.5 truncate">
                              <Move className="w-3 h-3 text-indigo-200 shrink-0" />
                              <span className="truncate">{task.title}</span>
                            </div>

                            <div className="flex items-center gap-1 shrink-0 ml-1">
                              <span className="text-[9px] bg-slate-950/60 px-1.5 py-0.5 rounded text-indigo-200 font-mono">
                                {task.startTime}-{task.endTime}
                              </span>
                              <span className="text-[8px] bg-indigo-900/80 text-white px-1 py-0.5 rounded">
                                {progressPct}%
                              </span>
                            </div>
                          </div>
                        );
                      })()}
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>
      )}

      {/* VIEW 3: KANBAN BOARD */}
      {activeView === 'KANBAN' && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {(['A Fazer', 'Em Progresso', 'Concluído'] as const).map((colStatus) => {
            const colTasks = visualTasks.filter((t) => (t.status || 'A Fazer') === colStatus);

            return (
              <div
                key={colStatus}
                onDragOver={(e) => e.preventDefault()}
                onDrop={() => handleDropStatus(colStatus)}
                className="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-3 min-h-[450px] shadow-lg flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between border-b border-slate-800 pb-2 mb-3">
                    <h3 className="text-xs font-extrabold uppercase tracking-wider text-slate-200 flex items-center gap-2">
                      <span
                        className={`w-2 h-2 rounded-full ${
                          colStatus === 'A Fazer'
                            ? 'bg-slate-400'
                            : colStatus === 'Em Progresso'
                            ? 'bg-amber-400'
                            : 'bg-emerald-400'
                        }`}
                      />
                      {colStatus}
                    </h3>
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300">
                      {colTasks.length}
                    </span>
                  </div>

                  <div className="space-y-3">
                    {colTasks.length === 0 ? (
                      <p className="text-xs text-slate-500 italic text-center py-6">Nenhuma tarefa aqui</p>
                    ) : (
                      colTasks.map((task) => {
                        const checklistItems = (task.checklistRaw || '').split('|').filter(Boolean);

                        return (
                          <div
                            key={task.id}
                            draggable
                            onDragStart={(e) => handleDragStart(e, task.id)}
                            className="bg-slate-950 border border-slate-800 hover:border-indigo-500 rounded-xl p-4 space-y-3 shadow-md cursor-grab active:cursor-grabbing transition group"
                          >
                            <div className="flex items-center justify-between">
                              <span className="text-[9px] font-extrabold uppercase tracking-wider px-2 py-0.5 rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                                {task.function}
                              </span>

                              <div className="flex items-center gap-1">
                                <button
                                  onClick={() => handleOpenEditModal(task)}
                                  className="p-1 text-slate-400 hover:text-amber-400 rounded transition"
                                  title="Editar recurso"
                                >
                                  <Edit3 className="w-3.5 h-3.5" />
                                </button>
                                <button
                                  onClick={() => deleteVisualTask(task.id)}
                                  className="p-1 text-slate-400 hover:text-red-400 rounded transition"
                                  title="Excluir"
                                >
                                  <Trash2 className="w-3.5 h-3.5" />
                                </button>
                              </div>
                            </div>

                            <h4 className="text-sm font-bold text-slate-100">{task.title}</h4>

                            <div className="flex items-center gap-3 text-[10px] text-slate-400">
                              <span className="flex items-center gap-1 font-mono">
                                <Clock className="w-3 h-3 text-amber-400" /> {task.startTime} - {task.endTime}
                              </span>
                              <span className="flex items-center gap-1">
                                <Tag className="w-3 h-3 text-indigo-400" /> {task.tag}
                              </span>
                            </div>

                            {/* Sub-checklist in Kanban */}
                            {checklistItems.length > 0 && (
                              <div className="pt-2 border-t border-slate-800/80 space-y-1.5">
                                <span className="text-[9px] font-bold uppercase text-slate-500">Checklist:</span>
                                {checklistItems.map((rawItem, idx) => {
                                  const [itemName, statusStr] = rawItem.split(':');
                                  const isDone = statusStr === 'true';

                                  return (
                                    <div
                                      key={idx}
                                      onClick={() => toggleChecklistItem(task, idx)}
                                      className="flex items-center gap-2 text-xs text-slate-300 cursor-pointer hover:text-white"
                                    >
                                      <input
                                        type="checkbox"
                                        checked={isDone}
                                        onChange={() => {}}
                                        className="rounded text-indigo-600 bg-slate-900 border-slate-800"
                                      />
                                      <span className={isDone ? 'line-through text-slate-500' : ''}>{itemName}</span>
                                    </div>
                                  );
                                })}
                              </div>
                            )}

                            {/* Quick status move buttons */}
                            <div className="flex items-center justify-end gap-1 pt-1">
                              {colStatus !== 'A Fazer' && (
                                <button
                                  onClick={() => updateVisualTask({ ...task, status: 'A Fazer' })}
                                  className="text-[9px] font-semibold px-2 py-0.5 rounded bg-slate-900 text-slate-400 hover:text-white"
                                >
                                  ← A Fazer
                                </button>
                              )}
                              {colStatus !== 'Em Progresso' && (
                                <button
                                  onClick={() => updateVisualTask({ ...task, status: 'Em Progresso' })}
                                  className="text-[9px] font-semibold px-2 py-0.5 rounded bg-amber-500/10 text-amber-400 hover:bg-amber-500/20"
                                >
                                  Em Progresso
                                </button>
                              )}
                              {colStatus !== 'Concluído' && (
                                <button
                                  onClick={() => updateVisualTask({ ...task, status: 'Concluído' })}
                                  className="text-[9px] font-semibold px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20"
                                >
                                  Concluir ✓
                                </button>
                              )}
                            </div>
                          </div>
                        );
                      })
                    )}
                  </div>
                </div>

                <button
                  onClick={() => {
                    setStatus(colStatus);
                    handleOpenAddModal();
                  }}
                  className="w-full py-2 bg-slate-950 border border-slate-800 hover:border-indigo-500/50 rounded-xl text-xs text-slate-400 hover:text-indigo-300 font-semibold transition mt-2"
                >
                  + Adicionar a {colStatus}
                </button>
              </div>
            );
          })}
        </div>
      )}

      {/* CREATE / EDIT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl animate-in fade-in zoom-in duration-150">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-indigo-400" />
                {editingTask ? 'Editar Tarefa da Agenda' : 'Nova Tarefa na Agenda'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveTask} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Título da Tarefa / Bloco</label>
                <input
                  type="text"
                  placeholder="Ex: Sessão de Estudo Intensivo, Treino de Musculação..."
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Função / Categoria</label>
                  <select
                    value={func}
                    onChange={(e) => setFunc(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                  >
                    <option value="Estudante">Estudante</option>
                    <option value="Trabalho">Trabalho</option>
                    <option value="Saúde">Saúde</option>
                    <option value="Administrativo">Administrativo</option>
                    <option value="Pessoal">Pessoal</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Status (Kanban)</label>
                  <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value as any)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                  >
                    <option value="A Fazer">A Fazer</option>
                    <option value="Em Progresso">Em Progresso</option>
                    <option value="Concluído">Concluído</option>
                  </select>
                </div>
              </div>

              {/* Start Hour & End Hour Selection */}
              <div className="grid grid-cols-3 gap-3 bg-slate-950/60 border border-slate-800/80 p-3 rounded-xl">
                <div>
                  <label className="block text-[11px] font-semibold text-slate-400 mb-1">Horário de Início</label>
                  <select
                    value={startHour}
                    onChange={(e) => {
                      const newStart = Number(e.target.value);
                      setStartHour(newStart);
                      if (newStart >= endHour) {
                        const newE = Math.min(24, newStart + 1);
                        setEndHour(newE);
                        setDurationHours(1);
                      } else {
                        setDurationHours(endHour - newStart);
                      }
                    }}
                    className="w-full bg-slate-900 border border-slate-700 rounded-lg px-2 py-1.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
                  >
                    {Array.from({ length: 24 }, (_, i) => i).map((h) => (
                      <option key={h} value={h}>
                        {h.toString().padStart(2, '0')}:00
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-[11px] font-semibold text-indigo-400 mb-1">Horário Final (Término)</label>
                  <select
                    value={endHour}
                    onChange={(e) => {
                      const newEnd = Number(e.target.value);
                      if (newEnd > startHour) {
                        setEndHour(newEnd);
                        setDurationHours(newEnd - startHour);
                      } else {
                        const forcedEnd = Math.min(24, startHour + 1);
                        setEndHour(forcedEnd);
                        setDurationHours(1);
                      }
                    }}
                    className="w-full bg-slate-900 border border-indigo-500/50 rounded-lg px-2 py-1.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-400"
                  >
                    {Array.from({ length: 24 }, (_, i) => i + 1).map((h) => (
                      <option key={h} value={h} disabled={h <= startHour}>
                        {h.toString().padStart(2, '0')}:00 {h <= startHour ? '(Inválido)' : ''}
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-[11px] font-semibold text-slate-400 mb-1">Duração Calculada</label>
                  <div className="w-full bg-slate-900 border border-slate-800 rounded-lg px-2.5 py-1.5 text-xs font-extrabold text-indigo-300 flex items-center justify-between">
                    <span>{Math.max(1, endHour - startHour)}h</span>
                    <Clock className="w-3 h-3 text-slate-500" />
                  </div>
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Sub-Checklist de Execução (1 item por linha)</label>
                <textarea
                  rows={3}
                  placeholder="Revisar anotações&#10;Resolver 10 exercícios&#10;Anotar dúvidas no Caderno de Erros"
                  value={checklistInput}
                  onChange={(e) => setChecklistInput(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs shadow-lg shadow-indigo-600/20 transition"
                >
                  {editingTask ? 'Salvar Alterações' : 'Criar Tarefa'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
