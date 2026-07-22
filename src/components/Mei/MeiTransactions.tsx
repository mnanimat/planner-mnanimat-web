import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { MeiTransaction } from '../../types';
import { Plus, Trash2, ArrowUpRight, ArrowDownRight, Filter, Edit3, X } from 'lucide-react';

export const MeiTransactions: React.FC = () => {
  const { meiTransactions, addMeiTransaction, updateMeiTransaction, deleteMeiTransaction, clearMeiTransactions } = useApp();

  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState(250);
  const [category, setCategory] = useState('Serviços');
  const [accountType, setAccountType] = useState<'PJ' | 'PESSOAL' | 'DINHEIRO'>('PJ');
  const [transactionType, setTransactionType] = useState<'RECEITA' | 'DESPESA'>('RECEITA');
  const [dateString, setDateString] = useState(new Date().toISOString().split('T')[0]);
  const [hasInvoice, setHasInvoice] = useState(true);
  const [notes, setNotes] = useState('');

  const [filterAccount, setFilterAccount] = useState<'TODOS' | 'PJ' | 'PESSOAL' | 'DINHEIRO'>('TODOS');

  // Edit Modal State
  const [editingTransaction, setEditingTransaction] = useState<MeiTransaction | null>(null);

  const filteredTransactions = meiTransactions.filter((t) => {
    if (filterAccount === 'TODOS') return true;
    return t.accountType === filterAccount;
  });

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!description.trim() || amount <= 0) return;
    addMeiTransaction(
      description,
      Number(amount),
      category,
      accountType,
      transactionType,
      dateString,
      hasInvoice,
      'Pago',
      notes
    );
    setDescription('');
    setNotes('');
  };

  const handleOpenEdit = (t: MeiTransaction) => {
    setEditingTransaction(t);
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingTransaction || !editingTransaction.description.trim()) return;
    updateMeiTransaction(editingTransaction);
    setEditingTransaction(null);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100">Lançamentos Financeiros (PJ, Pessoal & Dinheiro)</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Mantenha o registro transparente de entradas e saídas separando pessoa jurídica, física e caixa em dinheiro.
          </p>
        </div>

        <button
          onClick={clearMeiTransactions}
          className="px-3 py-1.5 bg-slate-900 border border-slate-800 text-slate-400 hover:text-red-400 rounded-xl text-xs transition"
        >
          Limpar Todos os Lançamentos
        </button>
      </div>

      {/* Add Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Descrição</label>
            <input
              type="text"
              placeholder="Ex: Consultoria Técnica..."
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Valor (R$)</label>
            <input
              type="number"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Conta / Origem</label>
            <select
              value={accountType}
              onChange={(e) => setAccountType(e.target.value as any)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            >
              <option value="PJ">PJ (Empresa MEI)</option>
              <option value="PESSOAL">PESSOAL (Física)</option>
              <option value="DINHEIRO">DINHEIRO (Caixa / Espécie)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Tipo de Operação</label>
            <select
              value={transactionType}
              onChange={(e) => setTransactionType(e.target.value as any)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            >
              <option value="RECEITA">RECEITA (+)</option>
              <option value="DESPESA">DESPESA (-)</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Categoria</label>
            <input
              type="text"
              placeholder="Ex: Serviços, Impostos, Vendas..."
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Data</label>
            <input
              type="date"
              value={dateString}
              onChange={(e) => setDateString(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Observação / Cliente</label>
            <input
              type="text"
              placeholder="Notas adicionais..."
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-xs transition shadow-lg shadow-emerald-500/20"
        >
          Salvar Transação
        </button>
      </form>

      {/* Account Filters */}
      <div className="flex flex-wrap items-center gap-2">
        <span className="text-xs font-bold text-slate-400 flex items-center gap-1">
          <Filter className="w-3.5 h-3.5" /> Exibir:
        </span>
        <button
          onClick={() => setFilterAccount('TODOS')}
          className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
            filterAccount === 'TODOS'
              ? 'bg-slate-100 text-slate-900'
              : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
          }`}
        >
          Todas
        </button>
        <button
          onClick={() => setFilterAccount('PJ')}
          className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
            filterAccount === 'PJ'
              ? 'bg-indigo-600 text-white'
              : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
          }`}
        >
          Apenas PJ
        </button>
        <button
          onClick={() => setFilterAccount('PESSOAL')}
          className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
            filterAccount === 'PESSOAL'
              ? 'bg-amber-500 text-slate-950'
              : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
          }`}
        >
          Apenas Pessoal
        </button>
        <button
          onClick={() => setFilterAccount('DINHEIRO')}
          className={`px-3 py-1 rounded-lg text-xs font-bold transition ${
            filterAccount === 'DINHEIRO'
              ? 'bg-emerald-600 text-white'
              : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
          }`}
        >
          Apenas Dinheiro (Caixa)
        </button>
      </div>

      {/* Transactions List */}
      <div className="space-y-2">
        {filteredTransactions.map((t) => (
          <div key={t.id} className="p-3.5 bg-slate-900 border border-slate-800 rounded-2xl flex items-center justify-between text-xs shadow-md">
            <div className="flex items-center gap-3">
              <span className={`p-2 rounded-xl ${t.transactionType === 'RECEITA' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'}`}>
                {t.transactionType === 'RECEITA' ? <ArrowUpRight className="w-4 h-4" /> : <ArrowDownRight className="w-4 h-4" />}
              </span>

              <div>
                <div className="flex items-center gap-2">
                  <span className={`text-[9px] font-extrabold uppercase px-2 py-0.5 rounded-full ${
                    t.accountType === 'PJ'
                      ? 'bg-indigo-500/10 text-indigo-400 border border-indigo-500/20'
                      : t.accountType === 'PESSOAL'
                      ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                      : 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                  }`}>
                    {t.accountType === 'DINHEIRO' ? 'DINHEIRO 💵' : t.accountType}
                  </span>
                  <span className="text-[10px] text-slate-400">{t.category}</span>
                </div>
                <p className="font-bold text-slate-100 mt-0.5">{t.description}</p>
                {t.notes && <p className="text-[10px] text-slate-500">{t.notes}</p>}
              </div>
            </div>

            <div className="flex items-center gap-3">
              <span className={`font-extrabold text-sm ${t.transactionType === 'RECEITA' ? 'text-emerald-400' : 'text-red-400'}`}>
                {t.transactionType === 'RECEITA' ? '+' : '-'} R$ {t.amount.toFixed(2)}
              </span>

              <div className="flex items-center gap-1">
                <button
                  onClick={() => handleOpenEdit(t)}
                  className="text-slate-400 hover:text-amber-400 p-1.5 rounded-lg hover:bg-slate-800 transition"
                  title="Editar transação"
                >
                  <Edit3 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => deleteMeiTransaction(t.id)}
                  className="text-slate-500 hover:text-red-400 p-1.5 rounded-lg hover:bg-slate-800 transition"
                  title="Excluir"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* EDIT MODAL FOR TRANSACTIONS */}
      {editingTransaction && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-lg p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-amber-400" />
                Editar Lançamento Financeiro
              </h3>
              <button
                onClick={() => setEditingTransaction(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Descrição</label>
                <input
                  type="text"
                  value={editingTransaction.description}
                  onChange={(e) => setEditingTransaction({ ...editingTransaction, description: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Valor (R$)</label>
                  <input
                    type="number"
                    step="0.01"
                    value={editingTransaction.amount}
                    onChange={(e) => setEditingTransaction({ ...editingTransaction, amount: Number(e.target.value) })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Conta / Origem</label>
                  <select
                    value={editingTransaction.accountType}
                    onChange={(e) => setEditingTransaction({ ...editingTransaction, accountType: e.target.value as any })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                  >
                    <option value="PJ">PJ (Empresa MEI)</option>
                    <option value="PESSOAL">PESSOAL (Física)</option>
                    <option value="DINHEIRO">DINHEIRO (Caixa / Espécie)</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Tipo de Operação</label>
                  <select
                    value={editingTransaction.transactionType}
                    onChange={(e) => setEditingTransaction({ ...editingTransaction, transactionType: e.target.value as any })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                  >
                    <option value="RECEITA">RECEITA (+)</option>
                    <option value="DESPESA">DESPESA (-)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-400 mb-1">Categoria</label>
                  <input
                    type="text"
                    value={editingTransaction.category}
                    onChange={(e) => setEditingTransaction({ ...editingTransaction, category: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Observação / Cliente</label>
                <input
                  type="text"
                  value={editingTransaction.notes || ''}
                  onChange={(e) => setEditingTransaction({ ...editingTransaction, notes: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingTransaction(null)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs transition shadow-lg shadow-amber-500/20"
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
