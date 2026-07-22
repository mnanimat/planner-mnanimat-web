import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { cronogramaEnemList, cronogramaItaList } from '../../data/initialData';
import { CustomCronogramaItem } from '../../types';
import {
  Youtube,
  ExternalLink,
  Calendar,
  Search,
  Plus,
  Trash2,
  Edit3,
  Upload,
  Download,
  CheckSquare,
  Square,
  FileText,
  X,
  Copy,
  Check
} from 'lucide-react';

export const FocoVestCronograma: React.FC = () => {
  const {
    customCronogramaItems,
    addCustomCronogramaItem,
    toggleCustomCronogramaItem,
    updateCustomCronogramaItem,
    importCustomCronogramaItems,
    deleteCustomCronogramaItem,
    clearAllCustomCronogramaItems
  } = useApp();

  const [activeTab, setActiveTab] = useState<'ENEM' | 'ITA' | 'CUSTOM'>('ENEM');
  const [selectedWeek, setSelectedWeek] = useState<string>('TODAS');
  const [searchQuery, setSearchQuery] = useState('');

  // Single Item Form State
  const [contentInput, setContentInput] = useState('');
  const [weekInput, setWeekInput] = useState('Semana 1');
  const [dateIntervalInput, setDateIntervalInput] = useState('');
  const [targetScheduleInput, setTargetScheduleInput] = useState<'ENEM' | 'ITA' | 'CUSTOM'>('ENEM');

  // Editing state
  const [editingItem, setEditingItem] = useState<CustomCronogramaItem | null>(null);

  // Import Modal State
  const [isImportModalOpen, setIsImportModalOpen] = useState(false);
  const [rawImportText, setRawImportText] = useState('');
  const [copiedMessage, setCopiedMessage] = useState(false);

  // Update form target schedule when active tab changes
  const handleTabChange = (tab: 'ENEM' | 'ITA' | 'CUSTOM') => {
    setActiveTab(tab);
    setTargetScheduleInput(tab);
  };

  const handleCreateOrUpdateItem = (e: React.FormEvent) => {
    e.preventDefault();
    if (!contentInput.trim()) return;

    if (editingItem) {
      updateCustomCronogramaItem({
        ...editingItem,
        content: contentInput,
        week: weekInput || 'Semana 1',
        dateInterval: dateIntervalInput,
        targetSchedule: targetScheduleInput
      });
      setEditingItem(null);
    } else {
      addCustomCronogramaItem(contentInput, weekInput, dateIntervalInput, targetScheduleInput);
    }

    setContentInput('');
    setDateIntervalInput('');
  };

  const handleEditClick = (item: CustomCronogramaItem) => {
    setEditingItem(item);
    setContentInput(item.content);
    setWeekInput(item.week || 'Semana 1');
    setDateIntervalInput(item.dateInterval || '');
    setTargetScheduleInput(item.targetSchedule || activeTab);
  };

  const handleCancelEdit = () => {
    setEditingItem(null);
    setContentInput('');
    setDateIntervalInput('');
    setTargetScheduleInput(activeTab);
  };

  // Bulk Import text parser
  const handleBulkImport = () => {
    if (!rawImportText.trim()) return;

    const lines = rawImportText.split('\n').map((l) => l.trim()).filter(Boolean);
    const parsedItems: CustomCronogramaItem[] = [];

    lines.forEach((line, index) => {
      let weekStr = 'Semana 1';
      let cleanContent = line;

      const weekMatch = line.match(/^(Semana\s*\d+|S\d+|\d+[\.\-\)]\s*)/i);
      if (weekMatch) {
        weekStr = weekMatch[0].replace(/[\.\-\)]/g, '').trim();
        if (!weekStr.toLowerCase().startsWith('semana')) {
          weekStr = `Semana ${weekStr.replace(/\D/g, '') || '1'}`;
        }
        cleanContent = line.replace(/^(Semana\s*\d+|S\d+|\d+[\.\-\)]\s*)\s*[:\-]?\s*/i, '').trim();
      }

      parsedItems.push({
        id: Date.now() + index,
        content: cleanContent || line,
        week: weekStr,
        dateInterval: '',
        isCompleted: false,
        targetSchedule: targetScheduleInput
      });
    });

    importCustomCronogramaItems(parsedItems);
    setRawImportText('');
    setIsImportModalOpen(false);
  };

  const handleExportJSON = () => {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(customCronogramaItems, null, 2));
    const downloadAnchor = document.createElement('a');
    downloadAnchor.setAttribute("href", dataStr);
    downloadAnchor.setAttribute("download", `cronograma_${targetScheduleInput.toLowerCase()}_${new Date().toISOString().split('T')[0]}.json`);
    document.body.appendChild(downloadAnchor);
    downloadAnchor.click();
    downloadAnchor.remove();
  };

  const handleCopyFormattedText = () => {
    const textFormatted = customCronogramaItems
      .filter((item) => activeTab === 'CUSTOM' || item.targetSchedule === activeTab)
      .map((item) => `[${item.isCompleted ? 'X' : ' '}] ${item.week}: ${item.content} ${item.dateInterval ? `(${item.dateInterval})` : ''}`)
      .join('\n');
    navigator.clipboard.writeText(textFormatted);
    setCopiedMessage(true);
    setTimeout(() => setCopiedMessage(false), 2000);
  };

  // Raw lists for official items
  const rawOfficialList = activeTab === 'ENEM' ? cronogramaEnemList : activeTab === 'ITA' ? cronogramaItaList : [];

  const filteredOfficialList = rawOfficialList.filter((item) => {
    const matchesWeek = selectedWeek === 'TODAS' || item.week === selectedWeek;
    const matchesSearch = item.subject.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesWeek && matchesSearch;
  });

  // Custom added items matching activeTab
  const itemsForActiveTab = customCronogramaItems.filter((item) => {
    if (activeTab === 'ENEM') return item.targetSchedule === 'ENEM';
    if (activeTab === 'ITA') return item.targetSchedule === 'ITA';
    return item.targetSchedule === 'CUSTOM' || !item.targetSchedule;
  });

  const filteredCustomList = itemsForActiveTab.filter((item) => {
    const matchesWeek = selectedWeek === 'TODAS' || item.week === selectedWeek;
    const matchesSearch = item.content.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesWeek && matchesSearch;
  });

  return (
    <div className="space-y-6">
      {/* Header & Tabs */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-slate-900 border border-slate-800 rounded-2xl p-5 shadow-lg">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Calendar className="w-6 h-6 text-amber-400" />
            Cronograma de Estudos Inteligente
          </h2>
          <p className="text-xs text-slate-400 mt-1">
            Alterne entre os cronogramas (ENEM, ITA e Meu Cronograma) e adicione novas matérias em qualquer um deles.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="flex bg-slate-950 p-1 rounded-xl border border-slate-800 flex-wrap gap-1">
          <button
            onClick={() => handleTabChange('ENEM')}
            className={`px-3.5 py-2 rounded-lg text-xs font-bold transition flex items-center gap-1.5 ${
              activeTab === 'ENEM'
                ? 'bg-amber-500 text-slate-950 shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            CRONOGRAMA ENEM
          </button>
          <button
            onClick={() => handleTabChange('ITA')}
            className={`px-3.5 py-2 rounded-lg text-xs font-bold transition flex items-center gap-1.5 ${
              activeTab === 'ITA'
                ? 'bg-indigo-600 text-white shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            CRONOGRAMA ITA
          </button>
          <button
            onClick={() => handleTabChange('CUSTOM')}
            className={`px-3.5 py-2 rounded-lg text-xs font-bold transition flex items-center gap-1.5 ${
              activeTab === 'CUSTOM'
                ? 'bg-emerald-500 text-slate-950 shadow-md'
                : 'text-slate-400 hover:text-white'
            }`}
          >
            ⭐ MEU CRONOGRAMA
          </button>
        </div>
      </div>

      {/* FORM TO ADD / EDIT ITEMS INTO ANY CRONOGRAMA */}
      <form onSubmit={handleCreateOrUpdateItem} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
        <div className="flex items-center justify-between flex-wrap gap-2">
          <h3 className="text-sm font-bold text-slate-200 flex items-center gap-2">
            <Plus className="w-4 h-4 text-amber-400" />
            {editingItem ? 'Editar Tópico do Cronograma' : 'Adicionar Novo Conteúdo / Matéria'}
          </h3>

          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={() => setIsImportModalOpen(true)}
              className="px-3 py-1.5 bg-indigo-600/20 hover:bg-indigo-600/30 border border-indigo-500/30 text-indigo-300 font-bold rounded-xl text-xs flex items-center gap-1.5 transition"
            >
              <Upload className="w-3.5 h-3.5" /> Importar Lista
            </button>
            {itemsForActiveTab.length > 0 && (
              <>
                <button
                  type="button"
                  onClick={handleExportJSON}
                  className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold rounded-xl text-xs flex items-center gap-1.5 transition"
                >
                  <Download className="w-3.5 h-3.5" /> Exportar
                </button>
                <button
                  type="button"
                  onClick={handleCopyFormattedText}
                  className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold rounded-xl text-xs flex items-center gap-1.5 transition"
                >
                  {copiedMessage ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
                  {copiedMessage ? 'Copiado!' : 'Copiar Texto'}
                </button>
              </>
            )}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-5 gap-3">
          {/* Target Cronograma Selector */}
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Adicionar no Cronograma</label>
            <select
              value={targetScheduleInput}
              onChange={(e) => setTargetScheduleInput(e.target.value as 'ENEM' | 'ITA' | 'CUSTOM')}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs font-bold text-amber-400 focus:outline-none focus:border-amber-500"
            >
              <option value="ENEM">CRONOGRAMA ENEM</option>
              <option value="ITA">CRONOGRAMA ITA</option>
              <option value="CUSTOM">MEU CRONOGRAMA</option>
            </select>
          </div>

          <div className="md:col-span-2">
            <label className="block text-xs font-semibold text-slate-400 mb-1">Matéria ou Tópico de Estudo</label>
            <input
              type="text"
              placeholder="Ex: Física - Leis de Newton e Dinâmica"
              value={contentInput}
              onChange={(e) => setContentInput(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Semana ou Módulo</label>
            <input
              type="text"
              placeholder="Ex: Semana 1, Bloco A"
              value={weekInput}
              onChange={(e) => setWeekInput(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Intervalo de Datas (Opcional)</label>
            <input
              type="text"
              placeholder="Ex: 01/08 a 07/08"
              value={dateIntervalInput}
              onChange={(e) => setDateIntervalInput(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
            />
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="submit"
            className="flex-1 py-2.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-extrabold rounded-xl text-xs transition shadow-lg shadow-amber-500/20"
          >
            {editingItem
              ? 'Salvar Alterações no Cronograma'
              : `+ Adicionar no CRONOGRAMA ${targetScheduleInput === 'CUSTOM' ? 'MEU CRONOGRAMA' : targetScheduleInput}`}
          </button>

          {editingItem && (
            <button
              type="button"
              onClick={handleCancelEdit}
              className="px-4 py-2.5 bg-slate-800 text-slate-300 font-bold rounded-xl text-xs hover:bg-slate-700 transition"
            >
              Cancelar Edição
            </button>
          )}
        </div>
      </form>

      {/* Filter Bar */}
      <div className="flex flex-col md:flex-row gap-3 bg-slate-900 border border-slate-800 p-3 rounded-2xl">
        <div className="relative flex-1">
          <Search className="w-4 h-4 absolute left-3 top-3 text-slate-500" />
          <input
            type="text"
            placeholder={`Filtrar matérias no Cronograma ${activeTab}...`}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-xl py-2 pl-9 pr-3 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
          />
        </div>

        <select
          value={selectedWeek}
          onChange={(e) => setSelectedWeek(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
        >
          <option value="TODAS">Todas as Semanas</option>
          <option value="Semana 1">Semana 1</option>
          <option value="Semana 2">Semana 2</option>
          <option value="Semana 3">Semana 3</option>
          <option value="Semana 4">Semana 4</option>
          <option value="Semana 5">Semana 5</option>
          <option value="Semana 8">Semana 8</option>
        </select>
      </div>

      {/* USER ADDED / CUSTOM ITEMS FOR THE ACTIVE TAB */}
      {filteredCustomList.length > 0 && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <h3 className="text-xs font-bold text-amber-400 uppercase tracking-wider">
              Tópicos Adicionados por Você ({filteredCustomList.length})
            </h3>
          </div>

          <div className="space-y-3">
            {filteredCustomList.map((item) => (
              <div
                key={item.id}
                className={`bg-slate-900 border rounded-2xl p-4 flex flex-col md:flex-row md:items-center justify-between gap-3 transition shadow-md ${
                  item.isCompleted ? 'border-emerald-800/40 opacity-75' : 'border-amber-500/30 bg-amber-500/5'
                }`}
              >
                <div className="flex items-start gap-3">
                  <button
                    onClick={() => toggleCustomCronogramaItem(item.id)}
                    className="mt-0.5 text-emerald-400 hover:text-emerald-300 transition"
                    title={item.isCompleted ? 'Marcar como não concluído' : 'Marcar como concluído'}
                  >
                    {item.isCompleted ? (
                      <CheckSquare className="w-5 h-5 text-emerald-400" />
                    ) : (
                      <Square className="w-5 h-5 text-slate-600 hover:text-slate-400" />
                    )}
                  </button>

                  <div className="space-y-1">
                    <div className="flex items-center gap-2 flex-wrap">
                      <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                        {item.week || 'Semana 1'}
                      </span>
                      {item.dateInterval && (
                        <span className="text-[10px] font-semibold text-slate-400 bg-slate-950 px-2 py-0.5 rounded-full border border-slate-800">
                          {item.dateInterval}
                        </span>
                      )}
                      <span className="text-[10px] font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full border border-emerald-500/20">
                        {item.targetSchedule ? `Adicionado ao ${item.targetSchedule}` : 'Personalizado'}
                      </span>
                    </div>
                    <h4 className={`text-sm font-bold ${item.isCompleted ? 'line-through text-slate-400' : 'text-slate-100'}`}>
                      {item.content}
                    </h4>
                  </div>
                </div>

                <div className="flex items-center justify-end gap-2 pt-2 md:pt-0 border-t md:border-t-0 border-slate-800">
                  <a
                    href={`https://www.youtube.com/results?search_query=${encodeURIComponent(item.content)}`}
                    target="_blank"
                    rel="noreferrer"
                    className="px-2.5 py-1.5 bg-red-600/10 hover:bg-red-600/20 text-red-400 font-bold rounded-xl text-[11px] flex items-center gap-1.5 transition"
                  >
                    <Youtube className="w-3.5 h-3.5" /> Aula no YouTube
                  </a>

                  <button
                    onClick={() => handleEditClick(item)}
                    className="p-1.5 bg-slate-800 hover:bg-amber-500/20 text-slate-300 hover:text-amber-400 border border-slate-700 hover:border-amber-500/40 rounded-xl text-xs transition flex items-center gap-1"
                    title="Editar recurso"
                  >
                    <Edit3 className="w-3.5 h-3.5" />
                  </button>

                  <button
                    onClick={() => deleteCustomCronogramaItem(item.id)}
                    className="p-1.5 bg-slate-800 hover:bg-red-500/20 text-slate-400 hover:text-red-400 border border-slate-700 hover:border-red-500/40 rounded-xl text-xs transition"
                    title="Excluir"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* OFFICIAL ENEM / ITA LIST (FOR ENEM AND ITA TABS) */}
      {activeTab !== 'CUSTOM' && (
        <div className="space-y-3">
          <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider">
            Matérias Recomendadas do Cronograma Oficial {activeTab}
          </h3>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {filteredOfficialList.map((item, idx) => (
              <div
                key={idx}
                className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col justify-between space-y-3 hover:border-slate-700 transition shadow-md"
              >
                <div className="space-y-1.5">
                  <div className="flex items-center justify-between">
                    <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                      {item.week} ({item.period})
                    </span>
                    <span className="text-[10px] font-bold text-slate-400">{item.exam}</span>
                  </div>
                  <h3 className="text-sm font-bold text-slate-100 leading-snug">{item.subject}</h3>
                </div>

                <div className="flex items-center gap-2">
                  <a
                    href={item.watchLink}
                    target="_blank"
                    rel="noreferrer"
                    className="flex-1 px-3 py-2 bg-red-600/10 hover:bg-red-600/20 border border-red-500/30 text-red-400 font-bold rounded-xl text-xs flex items-center justify-center gap-2 transition"
                  >
                    <Youtube className="w-4 h-4 text-red-500" />
                    <span>Buscar Aulas de Apoio no YouTube</span>
                    <ExternalLink className="w-3 h-3 ml-auto opacity-60" />
                  </a>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* MEU CRONOGRAMA EMPTY STATE */}
      {activeTab === 'CUSTOM' && filteredCustomList.length === 0 && (
        <div className="text-center py-12 bg-slate-900 border border-slate-800 rounded-2xl p-6">
          <FileText className="w-8 h-8 text-slate-600 mx-auto mb-2" />
          <p className="text-sm font-bold text-slate-300">Seu Cronograma Personalizado está vazio</p>
          <p className="text-xs text-slate-500 max-w-md mx-auto mt-1">
            Adicione matérias pelo formulário acima ou clique em <strong>"Importar Lista"</strong> para colar todo o seu plano de estudos.
          </p>
        </div>
      )}

      {/* IMPORT MODAL FOR SCHEDULE */}
      {isImportModalOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl animate-in fade-in zoom-in duration-150">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Upload className="w-5 h-5 text-indigo-400" />
                Importar no Cronograma {targetScheduleInput}
              </h3>
              <button
                onClick={() => setIsImportModalOpen(false)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <p className="text-xs text-slate-400 leading-relaxed">
              Cole sua lista de matérias abaixo (1 item por linha). Exemplo:
              <br />
              <code className="text-emerald-400 font-mono">Semana 1: Função Afim e Quadrática</code>
              <br />
              <code className="text-emerald-400 font-mono">Semana 2 - Leis de Newton</code>
            </p>

            <textarea
              rows={8}
              placeholder="Semana 1: Matéria 1&#10;Semana 1: Matéria 2&#10;Semana 2: Matéria 3"
              value={rawImportText}
              onChange={(e) => setRawImportText(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-xs text-slate-100 focus:outline-none focus:border-indigo-500 font-mono"
            />

            <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
              <button
                type="button"
                onClick={() => setIsImportModalOpen(false)}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
              >
                Cancelar
              </button>
              <button
                type="button"
                onClick={handleBulkImport}
                className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs shadow-lg shadow-indigo-600/20 transition"
              >
                Processar e Importar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
