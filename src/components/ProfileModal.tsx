import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { X, User, Mail, DollarSign, RefreshCw, Check, ShieldCheck, Sun, Moon } from 'lucide-react';

interface ProfileModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ProfileModal: React.FC<ProfileModalProps> = ({ isOpen, onClose }) => {
  const { userAccount, updateUserAccount, updateFinanceMode, toggleTheme, restoreExampleMeiData, resetVideosToEnemCronograma } = useApp();

  const [name, setName] = useState(userAccount?.name || '');
  const [email, setEmail] = useState(userAccount?.email || '');
  const [financeMode, setFinanceMode] = useState(userAccount?.financeMode || 'MEI + Pessoal');
  const [isSaved, setIsSaved] = useState(false);

  if (!isOpen) return null;

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    updateUserAccount(name, email);
    updateFinanceMode(financeMode);
    setIsSaved(true);
    setTimeout(() => {
      setIsSaved(false);
      onClose();
    }, 1000);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 dark:bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-md p-6 text-slate-900 dark:text-slate-100 shadow-2xl relative transition-colors duration-200">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-white rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-3 mb-6">
          <div className="w-12 h-12 rounded-xl bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 flex items-center justify-center">
            <User className="w-6 h-6" />
          </div>
          <div>
            <h3 className="text-lg font-bold">Perfil do Usuário</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">Gerencie suas preferências locais de usuário</p>
          </div>
        </div>

        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Nome Completo</label>
            <div className="relative">
              <User className="w-4 h-4 absolute left-3 top-3 text-slate-400 dark:text-slate-500" />
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-xl py-2 pl-9 pr-3 text-sm focus:outline-none focus:border-indigo-500 text-slate-900 dark:text-slate-100"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">E-mail</label>
            <div className="relative">
              <Mail className="w-4 h-4 absolute left-3 top-3 text-slate-400 dark:text-slate-500" />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-xl py-2 pl-9 pr-3 text-sm focus:outline-none focus:border-indigo-500 text-slate-900 dark:text-slate-100"
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Tema da Aplicação</label>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => {
                  if (userAccount?.isDarkTheme) toggleTheme();
                }}
                className={`flex-1 flex items-center justify-center gap-2 py-2 px-3 rounded-xl border text-xs font-bold transition ${
                  !userAccount?.isDarkTheme
                    ? 'bg-amber-500/10 border-amber-500 text-amber-600 dark:text-amber-400'
                    : 'bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400'
                }`}
              >
                <Sun className="w-4 h-4" />
                <span>Modo Claro</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  if (!userAccount?.isDarkTheme) toggleTheme();
                }}
                className={`flex-1 flex items-center justify-center gap-2 py-2 px-3 rounded-xl border text-xs font-bold transition ${
                  userAccount?.isDarkTheme
                    ? 'bg-indigo-500/10 border-indigo-500 text-indigo-600 dark:text-indigo-400'
                    : 'bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400'
                }`}
              >
                <Moon className="w-4 h-4" />
                <span>Modo Escuro</span>
              </button>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Modo de Exibição Financeira (MEI)</label>
            <div className="relative">
              <DollarSign className="w-4 h-4 absolute left-3 top-3 text-slate-400 dark:text-slate-500" />
              <select
                value={financeMode}
                onChange={(e) => setFinanceMode(e.target.value as any)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-xl py-2 pl-9 pr-3 text-sm focus:outline-none focus:border-indigo-500 text-slate-900 dark:text-slate-100"
              >
                <option value="MEI + Pessoal">MEI + Finanças Pessoais (Completo)</option>
                <option value="Só MEI">Apenas MEI / PJ</option>
                <option value="Só Pessoal">Apenas Finanças Pessoais</option>
              </select>
            </div>
          </div>

          <div className="p-3 bg-slate-50 dark:bg-slate-950/60 rounded-xl border border-slate-200 dark:border-slate-800/80 space-y-2">
            <h4 className="text-xs font-bold text-slate-700 dark:text-slate-300 flex items-center gap-1.5">
              <RefreshCw className="w-3.5 h-3.5 text-indigo-600 dark:text-indigo-400" />
              Restaurar Dados e Trava de Idade (+13)
            </h4>
            <p className="text-[11px] text-slate-500 dark:text-slate-400 leading-relaxed">
              Recarregue dados de demonstração ou refaça a declaração obrigatória de idade (+13).
            </p>
            <div className="flex flex-wrap gap-2 pt-1">
              <button
                type="button"
                onClick={restoreExampleMeiData}
                className="px-2.5 py-1 text-xs bg-slate-200 hover:bg-slate-300 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 rounded-lg transition font-medium"
              >
                Resetar MEI
              </button>
              <button
                type="button"
                onClick={resetVideosToEnemCronograma}
                className="px-2.5 py-1 text-xs bg-slate-200 hover:bg-slate-300 dark:bg-slate-800 dark:hover:bg-slate-700 text-slate-800 dark:text-slate-200 rounded-lg transition font-medium"
              >
                Resetar Aulas ENEM
              </button>
              <button
                type="button"
                onClick={() => {
                  try {
                    localStorage.removeItem('mnanimat_age_verified');
                    window.location.reload();
                  } catch (e) {
                    console.error(e);
                  }
                }}
                className="px-2.5 py-1 text-xs bg-amber-500/20 hover:bg-amber-500/30 text-amber-600 dark:text-amber-400 border border-amber-500/30 rounded-lg transition font-medium"
              >
                Refazer Trava de Idade (+13)
              </button>
            </div>
          </div>

          <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-2 text-xs text-emerald-600 dark:text-emerald-400">
            <ShieldCheck className="w-4 h-4 shrink-0" />
            <span>Processamento local e privado. Nenhum dado de conta é enviado a servidores externos.</span>
          </div>

          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-500 font-bold py-2.5 rounded-xl text-sm transition flex items-center justify-center gap-2 text-white shadow-lg shadow-indigo-600/20"
          >
            {isSaved ? (
              <>
                <Check className="w-4 h-4 text-emerald-400" />
                <span>Salvo com Sucesso!</span>
              </>
            ) : (
              'Salvar Alterações'
            )}
          </button>
        </form>
      </div>
    </div>
  );
};
