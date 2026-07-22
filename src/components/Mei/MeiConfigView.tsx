import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Settings, Save, CheckCircle } from 'lucide-react';

export const MeiConfigView: React.FC = () => {
  const { meiConfig, updateMeiConfig, userAccount, setUserAccount } = useApp();

  const [annualLimit, setAnnualLimit] = useState(meiConfig.annualLimit || 81000);
  const [monthlyDas, setMonthlyDas] = useState(meiConfig.monthlyDas || 81.9);
  const [monthlyRevenueGoal, setMonthlyRevenueGoal] = useState(meiConfig.monthlyRevenueGoal || 6000);
  const [emergencyFundGoal, setEmergencyFundGoal] = useState(meiConfig.emergencyFundGoal || 6000);
  const [financeMode, setFinanceMode] = useState<'MEI + Pessoal' | 'Só Pessoal'>(userAccount?.financeMode || 'MEI + Pessoal');

  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    updateMeiConfig({
      annualLimit: Number(annualLimit),
      monthlyDas: Number(monthlyDas),
      monthlyRevenueGoal: Number(monthlyRevenueGoal),
      emergencyFundGoal: Number(emergencyFundGoal)
    });

    if (userAccount) {
      setUserAccount({
        ...userAccount,
        financeMode
      });
    }

    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <Settings className="w-5 h-5 text-indigo-400" />
          Configurações e Parâmetros Financeiros
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Ajuste os valores de teto de faturamento, guia DAS e metas financeiras corporativas.
        </p>
      </div>

      <form onSubmit={handleSave} className="bg-slate-900 border border-slate-800 rounded-2xl p-6 space-y-5 shadow-xl">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Modo de Operação Financeira</label>
            <select
              value={financeMode}
              onChange={(e) => setFinanceMode(e.target.value as any)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none"
            >
              <option value="MEI + Pessoal">MEI + Finanças Pessoais (Completo)</option>
              <option value="Só Pessoal">Apenas Finanças Pessoais</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Teto de Faturamento Anual MEI (R$)</label>
            <input
              type="number"
              value={annualLimit}
              onChange={(e) => setAnnualLimit(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Valor da Guia Mensal DAS (R$)</label>
            <input
              type="number"
              step="0.01"
              value={monthlyDas}
              onChange={(e) => setMonthlyDas(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Meta de Faturamento Mensal (R$)</label>
            <input
              type="number"
              value={monthlyRevenueGoal}
              onChange={(e) => setMonthlyRevenueGoal(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Meta de Reserva de Emergência PJ (R$)</label>
            <input
              type="number"
              value={emergencyFundGoal}
              onChange={(e) => setEmergencyFundGoal(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-3 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition flex items-center justify-center gap-2 shadow-lg shadow-indigo-600/20"
        >
          <Save className="w-4 h-4" />
          <span>Salvar Parâmetros</span>
        </button>

        {savedSuccess && (
          <div className="p-3 bg-emerald-500/15 border border-emerald-500/30 rounded-xl text-emerald-400 text-xs font-bold text-center flex items-center justify-center gap-1.5">
            <CheckCircle className="w-4 h-4" />
            <span>Configurações salvas com sucesso!</span>
          </div>
        )}
      </form>
    </div>
  );
};
