import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { GraduationCap, Activity, Briefcase, CheckCircle2, ShieldCheck, Sparkles, ArrowRight } from 'lucide-react';

export const Onboarding: React.FC = () => {
  const { setupOnboardingMode } = useApp();

  const [useDemo, setUseDemo] = useState(true);
  const [selectedEnem, setSelectedEnem] = useState(true);
  const [selectedIta, setSelectedIta] = useState(true);
  const [name, setName] = useState('Micael Souza');
  const [email, setEmail] = useState('mnanimat@gmail.com');
  const [password, setPassword] = useState('123456');

  const handleStart = (e: React.FormEvent) => {
    e.preventDefault();
    setupOnboardingMode(useDemo, selectedEnem, selectedIta, name, email, password);
  };

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex items-center justify-center p-4 transition-colors duration-200">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-3xl w-full max-w-2xl p-6 md:p-10 shadow-xl dark:shadow-2xl space-y-8">
        <div className="text-center space-y-3">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-amber-500 flex items-center justify-center font-extrabold text-2xl mx-auto shadow-xl shadow-indigo-500/20 text-white">
            MN
          </div>
          <h1 className="text-2xl md:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-100">
            Bem-vindo ao Planner MNAnimat
          </h1>
          <p className="text-sm text-slate-600 dark:text-slate-400 max-w-lg mx-auto">
            Seu ecossistema integrado para alta performance em estudos (FOCOVEST), organização de rotina e saúde (RITVIDA) e finanças inteligentes (MEI).
          </p>
        </div>

        {/* 3 Core Modules Preview Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
            <div className="p-2 bg-amber-500/10 text-amber-600 dark:text-amber-400 rounded-xl w-fit">
              <GraduationCap className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-sm text-amber-600 dark:text-amber-300">FOCOVEST</h3>
            <p className="text-[11px] text-slate-500 dark:text-slate-400">Trilhas de 7 Passos, Flashcards - Repetição Espaçada, Simulados, Redação e Tutor IA.</p>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
            <div className="p-2 bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 rounded-xl w-fit">
              <Activity className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-sm text-indigo-600 dark:text-indigo-300">RITVIDA</h3>
            <p className="text-[11px] text-slate-500 dark:text-slate-400">Distribuição de horas, Treino & Dieta, Tarefas Visuais e Projetos.</p>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-1">
            <div className="p-2 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 rounded-xl w-fit">
              <Briefcase className="w-5 h-5" />
            </div>
            <h3 className="font-bold text-sm text-emerald-600 dark:text-emerald-300">MEI</h3>
            <p className="text-[11px] text-slate-500 dark:text-slate-400">Fluxo PJ + Pessoal, limite de faturamento R$ 81k e Notas Fiscais.</p>
          </div>
        </div>

        <form onSubmit={handleStart} className="space-y-6">
          {/* Preset Setup Option */}
          <div className="space-y-3">
            <label className="block text-xs font-bold text-slate-600 dark:text-slate-300 uppercase tracking-wider">Modo de Inicialização</label>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setUseDemo(true)}
                className={`p-4 rounded-2xl border text-left transition flex flex-col justify-between ${
                  useDemo
                    ? 'bg-indigo-500/10 border-indigo-500 text-slate-900 dark:text-white'
                    : 'bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-sm flex items-center gap-1.5 text-indigo-600 dark:text-indigo-300">
                    <Sparkles className="w-4 h-4" />
                    Com Dados de Exemplo
                  </span>
                  {useDemo && <CheckCircle2 className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />}
                </div>
                <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-2">
                  Carrega matérias, flashcards, simulação de faturamento MEI e plano de estudos do ENEM/ITA.
                </p>
              </button>

              <button
                type="button"
                onClick={() => setUseDemo(false)}
                className={`p-4 rounded-2xl border text-left transition flex flex-col justify-between ${
                  !useDemo
                    ? 'bg-indigo-500/10 border-indigo-500 text-slate-900 dark:text-white'
                    : 'bg-slate-50 dark:bg-slate-950 border-slate-200 dark:border-slate-800 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span className="font-bold text-sm flex items-center gap-1.5 text-indigo-600 dark:text-indigo-300">
                    Começar do Zero
                  </span>
                  {!useDemo && <CheckCircle2 className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />}
                </div>
                <p className="text-[11px] text-slate-500 dark:text-slate-400 mt-2">
                  Inicia com um painel limpo para você cadastrar seus próprios estudos e transações.
                </p>
              </button>
            </div>
          </div>

          {!useDemo && (
            <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-2xl space-y-3">
              <label className="block text-xs font-bold text-slate-700 dark:text-slate-300">Selecione os focos iniciais:</label>
              <div className="flex gap-4">
                <label className="flex items-center gap-2 text-xs text-slate-700 dark:text-slate-200 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={selectedEnem}
                    onChange={(e) => setSelectedEnem(e.target.checked)}
                    className="rounded text-indigo-600 bg-white dark:bg-slate-900 border-slate-300 dark:border-slate-700"
                  />
                  <span>ENEM (Exame Nacional)</span>
                </label>
                <label className="flex items-center gap-2 text-xs text-slate-700 dark:text-slate-200 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={selectedIta}
                    onChange={(e) => setSelectedIta(e.target.checked)}
                    className="rounded text-indigo-600 bg-white dark:bg-slate-900 border-slate-300 dark:border-slate-700"
                  />
                  <span>ITA / Militares</span>
                </label>
              </div>
            </div>
          )}

          {/* User Details */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Seu Nome</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-xl py-2.5 px-3 text-sm focus:outline-none focus:border-indigo-500 text-slate-900 dark:text-slate-100"
                required
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">Seu E-mail</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-slate-50 dark:bg-slate-950 border border-slate-300 dark:border-slate-800 rounded-xl py-2.5 px-3 text-sm focus:outline-none focus:border-indigo-500 text-slate-900 dark:text-slate-100"
                required
              />
            </div>
          </div>

          <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-center gap-2.5 text-xs text-emerald-600 dark:text-emerald-400">
            <ShieldCheck className="w-5 h-5 shrink-0" />
            <span>Processamento local e seguro. Todos os seus dados ficam salvos apenas neste dispositivo.</span>
          </div>

          <button
            type="submit"
            className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-bold py-3.5 rounded-2xl text-base transition flex items-center justify-center gap-2 shadow-xl shadow-indigo-600/25"
          >
            <span>Iniciar Planner MNAnimat</span>
            <ArrowRight className="w-5 h-5" />
          </button>
        </form>
      </div>
    </div>
  );
};
