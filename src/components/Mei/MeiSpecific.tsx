import React from 'react';
import { useApp } from '../../context/AppContext';
import { FileCheck, Shield, TrendingUp, AlertCircle, CheckCircle2 } from 'lucide-react';

export const MeiSpecific: React.FC = () => {
  const { meiTransactions, meiConfig } = useApp();

  const pjRevenues = meiTransactions
    .filter((t) => t.accountType === 'PJ' && t.transactionType === 'RECEITA')
    .reduce((acc, t) => acc + t.amount, 0);

  const annualLimit = meiConfig.annualLimit || 81000;
  const monthlyDas = meiConfig.monthlyDas || 81.9;
  const emergencyGoal = meiConfig.emergencyFundGoal || 6000;

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <FileCheck className="w-5 h-5 text-indigo-400" />
          Contabilidade Específica MEI / PJ
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Acompanhamento de impostos, DAS Simples Nacional e declaração anual de faturamento (DASN-SIMEI).
        </p>
      </div>

      {/* Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-2 shadow-md">
          <span className="text-xs font-bold text-amber-400 uppercase">Guia DAS Mensal</span>
          <p className="text-2xl font-extrabold text-white">R$ {monthlyDas.toFixed(2)}</p>
          <p className="text-[11px] text-slate-400">Vencimento todo dia 20</p>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-2 shadow-md">
          <span className="text-xs font-bold text-emerald-400 uppercase">Faturamento Acumulado</span>
          <p className="text-2xl font-extrabold text-emerald-400">R$ {pjRevenues.toFixed(2)}</p>
          <p className="text-[11px] text-slate-400">Teto Anual: R$ {annualLimit.toLocaleString()}</p>
        </div>

        <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-2 shadow-md">
          <span className="text-xs font-bold text-indigo-400 uppercase">Meta de Reserva PJ</span>
          <p className="text-2xl font-extrabold text-indigo-300">R$ {emergencyGoal.toFixed(2)}</p>
          <p className="text-[11px] text-slate-400">Fundo para imprevistos corporativos</p>
        </div>
      </div>

      {/* DASN SIMEI Checklist */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-lg">
        <h3 className="text-sm font-bold text-slate-100 flex items-center gap-2">
          <Shield className="w-4 h-4 text-emerald-400" />
          Checklist Obrigações Fiscais do MEI
        </h3>

        <div className="space-y-2 text-xs">
          <div className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>Pagamento em dia do DAS SIMEI (INSS + ISS/ICMS)</span>
            </div>
            <span className="text-[10px] text-emerald-400 font-bold">Regular</span>
          </div>

          <div className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
              <span>Emissão de Notas Fiscais para Clientes PJ</span>
            </div>
            <span className="text-[10px] text-emerald-400 font-bold">Ativo</span>
          </div>

          <div className="p-3 bg-slate-950 rounded-xl border border-slate-800/80 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-amber-400 shrink-0" />
              <span>Declaração Anual DASN-SIMEI (Prazo 31 de Maio)</span>
            </div>
            <span className="text-[10px] text-amber-400 font-bold">Pendente Exercício</span>
          </div>
        </div>
      </div>
    </div>
  );
};
