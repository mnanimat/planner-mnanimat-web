import React from 'react';
import { useApp } from '../../context/AppContext';
import {
  Briefcase,
  TrendingUp,
  ArrowUpRight,
  ArrowDownRight,
  AlertCircle,
  FileText,
  DollarSign,
  SyncAlt,
  Wallet
} from 'lucide-react';

export const MeiDashboard: React.FC = () => {
  const { meiTransactions, meiInvoices, meiConfig, userAccount, setSelectedMeiTab } = useApp();

  const financeMode = userAccount?.financeMode || 'MEI + Pessoal';

  // Revenue & Expense Calculations for PJ (MEI) and Pessoal
  const meiRevenues = meiTransactions
    .filter((t) => t.accountType === 'PJ' && t.transactionType === 'RECEITA')
    .reduce((acc, t) => acc + t.amount, 0);

  const meiExpenses = meiTransactions
    .filter((t) => t.accountType === 'PJ' && t.transactionType === 'DESPESA')
    .reduce((acc, t) => acc + t.amount, 0);

  const meiProfit = meiRevenues - meiExpenses;

  const personalRevenues = meiTransactions
    .filter((t) => t.accountType === 'PESSOAL' && t.transactionType === 'RECEITA')
    .reduce((acc, t) => acc + t.amount, 0);

  const personalExpenses = meiTransactions
    .filter((t) => t.accountType === 'PESSOAL' && t.transactionType === 'DESPESA')
    .reduce((acc, t) => acc + t.amount, 0);

  const personalBalance = personalRevenues - personalExpenses;

  // Dinheiro (Caixa Físico / Espécie) Calculations
  const cashRevenues = meiTransactions
    .filter((t) => t.accountType === 'DINHEIRO' && t.transactionType === 'RECEITA')
    .reduce((acc, t) => acc + t.amount, 0);

  const cashExpenses = meiTransactions
    .filter((t) => t.accountType === 'DINHEIRO' && t.transactionType === 'DESPESA')
    .reduce((acc, t) => acc + t.amount, 0);

  const cashBalance = cashRevenues - cashExpenses;

  const annualLimit = meiConfig.annualLimit || 81000;
  const limitPct = Math.min(100, Math.round((meiRevenues / annualLimit) * 100));

  const pendingInvoices = meiInvoices.filter((inv) => !inv.isReceived).length;

  return (
    <div className="space-y-6">
      {/* Welcome Bar */}
      <div className="bg-gradient-to-r from-emerald-600/20 via-slate-900 to-slate-900 border border-emerald-500/20 rounded-3xl p-6 text-slate-100 shadow-xl">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="space-y-1">
            <span className="text-xs font-bold text-emerald-400 uppercase tracking-wider">
              {financeMode === 'Só Pessoal' ? 'Finanças Pessoais 🏦' : 'Empresa MEI / PJ 💼'}
            </span>
            <h2 className="text-2xl font-extrabold tracking-tight text-white">
              Controle Financeiro Executivo
            </h2>
            <p className="text-xs text-slate-300">
              {financeMode === 'Só Pessoal'
                ? 'Controle de despesas e receitas pessoais com inteligência local.'
                : 'Separação transparente de conta PJ e Pessoal com cálculo automático do limite MEI.'}
            </p>
          </div>

          <button
            onClick={() => setSelectedMeiTab(4)} // Importação / Open Finance
            className="px-4 py-2.5 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold rounded-xl text-xs flex items-center gap-2 transition shadow-lg shadow-emerald-500/20 shrink-0"
          >
            <span>Importar Extrato</span>
          </button>
        </div>
      </div>

      {/* Accounting Metric Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {financeMode !== 'Só Pessoal' && (
          <>
            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-1 shadow-md">
              <span className="text-[10px] font-bold text-indigo-400 uppercase">Receita Bruta PJ</span>
              <p className="text-2xl font-extrabold text-indigo-400">R$ {meiRevenues.toFixed(2)}</p>
              <p className="text-[10px] text-slate-400">Entradas na conta PJ</p>
            </div>

            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-1 shadow-md">
              <span className="text-[10px] font-bold text-red-400 uppercase">Despesas PJ</span>
              <p className="text-2xl font-extrabold text-red-400">R$ {meiExpenses.toFixed(2)}</p>
              <p className="text-[10px] text-slate-400">Custos operacionais</p>
            </div>

            <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-1 shadow-md">
              <span className="text-[10px] font-bold text-emerald-400 uppercase">Lucro Líquido PJ</span>
              <p className="text-2xl font-extrabold text-emerald-400">R$ {meiProfit.toFixed(2)}</p>
              <p className="text-[10px] text-slate-400">Disponível para Pró-Labore</p>
            </div>
          </>
        )}

        {(financeMode === 'MEI + Pessoal' || financeMode === 'Só Pessoal') && (
          <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-1 shadow-md">
            <span className="text-[10px] font-bold text-amber-400 uppercase">Saldo Conta Pessoal</span>
            <p className="text-2xl font-extrabold text-amber-400">R$ {personalBalance.toFixed(2)}</p>
            <p className="text-[10px] text-slate-400">Patrimônio físico pessoa física</p>
          </div>
        )}

        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-1 shadow-md">
          <span className="text-[10px] font-bold text-emerald-400 uppercase">Caixa em Dinheiro 💵</span>
          <p className="text-2xl font-extrabold text-emerald-400">R$ {cashBalance.toFixed(2)}</p>
          <p className="text-[10px] text-slate-400">Valores em espécie / nota física</p>
        </div>
      </div>

      {/* MEI Limit Usage Bar */}
      {financeMode !== 'Só Pessoal' && (
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-lg">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-100">Uso do Limite Anual do MEI (R$ {annualLimit.toLocaleString()})</h3>
              <p className="text-xs text-slate-400">Faturamento acumulado no ano vigente</p>
            </div>
            <span className="text-sm font-extrabold text-emerald-400">{limitPct}% Utilizado</span>
          </div>

          <div className="w-full bg-slate-950 rounded-full h-3 overflow-hidden border border-slate-800">
            <div
              className={`h-3 rounded-full transition-all ${
                limitPct > 85 ? 'bg-red-500' : limitPct > 60 ? 'bg-amber-500' : 'bg-emerald-500'
              }`}
              style={{ width: `${limitPct}%` }}
            />
          </div>

          <div className="flex justify-between text-[11px] text-slate-400 pt-1">
            <span>R$ {meiRevenues.toLocaleString()} faturados</span>
            <span>R$ {(annualLimit - meiRevenues).toLocaleString()} restantes até o teto</span>
          </div>
        </div>
      )}

      {/* Pending Invoices Alert */}
      {pendingInvoices > 0 && (
        <div className="p-4 bg-amber-500/10 border border-amber-500/30 rounded-2xl flex items-center justify-between text-amber-300 text-xs shadow-md">
          <div className="flex items-center gap-2">
            <AlertCircle className="w-5 h-5 shrink-0 text-amber-400" />
            <span>Você tem <strong>{pendingInvoices} notas fiscais / pagamentos pendentes</strong> de clientes.</span>
          </div>
          <button
            onClick={() => setSelectedMeiTab(3)} // Invoices Tab
            className="px-3 py-1.5 bg-amber-500 text-slate-950 font-bold rounded-xl hover:bg-amber-400 transition"
          >
            Ver Notas
          </button>
        </div>
      )}
    </div>
  );
};
