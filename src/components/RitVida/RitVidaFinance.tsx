import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { DollarSign, Plus, Trash2, ArrowUpRight, ArrowDownRight } from 'lucide-react';

export const RitVidaFinance: React.FC = () => {
  const { ritVidaFinances, addRitVidaTransaction, deleteRitVidaTransaction } = useApp();

  const [description, setDescription] = useState('');
  const [amount, setAmount] = useState(100);
  const [type, setType] = useState<'REVENUE' | 'EXPENSE'>('REVENUE');

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!description.trim() || amount <= 0) return;
    addRitVidaTransaction(description, Number(amount), type, new Date().toISOString().split('T')[0]);
    setDescription('');
  };

  const totalRevenue = ritVidaFinances
    .filter((t) => t.type === 'REVENUE')
    .reduce((acc, t) => acc + t.amount, 0);

  const totalExpense = ritVidaFinances
    .filter((t) => t.type === 'EXPENSE')
    .reduce((acc, t) => acc + t.amount, 0);

  const netBalance = totalRevenue - totalExpense;

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <DollarSign className="w-5 h-5 text-emerald-400" />
          Finanças Pessoais RITVIDA
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Controle simplificado de entradas e saídas de caixa.
        </p>
      </div>

      {/* Financial Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-1">
          <p className="text-[10px] font-bold text-slate-400 uppercase">Receitas Totais</p>
          <p className="text-xl font-extrabold text-emerald-400 flex items-center gap-1">
            <ArrowUpRight className="w-4 h-4" /> R$ {totalRevenue.toFixed(2)}
          </p>
        </div>

        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-1">
          <p className="text-[10px] font-bold text-slate-400 uppercase">Despesas Totais</p>
          <p className="text-xl font-extrabold text-red-400 flex items-center gap-1">
            <ArrowDownRight className="w-4 h-4" /> R$ {totalExpense.toFixed(2)}
          </p>
        </div>

        <div className="p-4 bg-slate-900 border border-slate-800 rounded-2xl space-y-1">
          <p className="text-[10px] font-bold text-slate-400 uppercase">Saldo Líquido</p>
          <p className={`text-xl font-extrabold ${netBalance >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
            R$ {netBalance.toFixed(2)}
          </p>
        </div>
      </div>

      {/* Add Transaction Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
        <select
          value={type}
          onChange={(e) => setType(e.target.value as any)}
          className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
        >
          <option value="REVENUE">Receita (+)</option>
          <option value="EXPENSE">Despesa (-)</option>
        </select>

        <input
          type="text"
          placeholder="Descrição da transação..."
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-emerald-500"
          required
        />

        <input
          type="number"
          step="0.01"
          placeholder="Valor (R$)"
          value={amount}
          onChange={(e) => setAmount(Number(e.target.value))}
          className="w-36 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          required
        />

        <button
          type="submit"
          className="px-4 py-2 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition shadow-lg shadow-emerald-500/20"
        >
          <Plus className="w-4 h-4" />
          Registrar
        </button>
      </form>

      {/* Transactions List */}
      <div className="space-y-2">
        {ritVidaFinances.map((t) => (
          <div key={t.id} className="p-3 bg-slate-900 border border-slate-800 rounded-xl flex items-center justify-between text-xs shadow-md">
            <div className="flex items-center gap-3">
              <span className={`p-1.5 rounded-lg ${t.type === 'REVENUE' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-red-500/10 text-red-400'}`}>
                {t.type === 'REVENUE' ? <ArrowUpRight className="w-4 h-4" /> : <ArrowDownRight className="w-4 h-4" />}
              </span>
              <div>
                <p className="font-bold text-slate-100">{t.description}</p>
                <p className="text-[10px] text-slate-400">{t.dateString}</p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <span className={`font-bold ${t.type === 'REVENUE' ? 'text-emerald-400' : 'text-red-400'}`}>
                {t.type === 'REVENUE' ? '+' : '-'} R$ {t.amount.toFixed(2)}
              </span>

              <button
                onClick={() => deleteRitVidaTransaction(t.id)}
                className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
