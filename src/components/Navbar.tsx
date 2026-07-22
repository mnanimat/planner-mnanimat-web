import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import {
  GraduationCap,
  Activity,
  Briefcase,
  User,
  Shield,
  Sun,
  Moon,
  LogOut,
  FileText
} from 'lucide-react';

interface NavbarProps {
  onOpenProfile: () => void;
  onOpenLegalTerms: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenProfile, onOpenLegalTerms }) => {
  const { activeModule, setActiveModule, userAccount, toggleTheme } = useApp();

  return (
    <header className="sticky top-0 z-40 bg-white/90 dark:bg-slate-900/90 backdrop-blur-md border-b border-slate-200 dark:border-slate-800 text-slate-900 dark:text-white px-4 py-3 shadow-sm dark:shadow-lg transition-colors duration-200">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row md:items-center md:justify-between gap-3">
        {/* Brand & Module Switcher */}
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-amber-500 flex items-center justify-center font-bold text-lg shadow-md shadow-indigo-500/20 text-white">
              MN
            </div>
            <div>
              <h1 className="font-extrabold text-base tracking-tight leading-none text-slate-900 dark:text-slate-100 flex items-center gap-2">
                Planner MNAnimat
                <span className="text-[10px] uppercase font-bold tracking-widest px-2 py-0.5 rounded-full bg-indigo-500/10 text-indigo-600 dark:text-indigo-400 border border-indigo-500/20">
                  Local AI
                </span>
              </h1>
              <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Estudos, Rotina & MEI</p>
            </div>
          </div>

          {/* Quick Actions (Mobile) */}
          <div className="flex items-center gap-1 md:hidden">
            <button
              onClick={toggleTheme}
              className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition"
              title="Alternar Tema (Claro / Escuro)"
            >
              {userAccount?.isDarkTheme ? <Sun className="w-4 h-4 text-amber-500" /> : <Moon className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />}
            </button>
            <button
              onClick={onOpenProfile}
              className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition flex items-center gap-1.5"
            >
              <User className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
            </button>
          </div>
        </div>

        {/* Center Pill Module Navigation */}
        <nav className="flex items-center justify-center p-1 bg-slate-100 dark:bg-slate-950/60 rounded-xl border border-slate-200 dark:border-slate-800/80">
          <button
            onClick={() => setActiveModule('FOCOVEST')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              activeModule === 'FOCOVEST'
                ? 'bg-amber-500 text-slate-950 shadow-md shadow-amber-500/20'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200/60 dark:hover:bg-slate-800/50'
            }`}
          >
            <GraduationCap className="w-4 h-4" />
            <span>FOCOVEST</span>
          </button>

          <button
            onClick={() => setActiveModule('RITVIDA')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              activeModule === 'RITVIDA'
                ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/20'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200/60 dark:hover:bg-slate-800/50'
            }`}
          >
            <Activity className="w-4 h-4" />
            <span>RITVIDA</span>
          </button>

          <button
            onClick={() => setActiveModule('MEI_PRO')}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-xs font-bold transition-all ${
              activeModule === 'MEI_PRO'
                ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20'
                : 'text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 hover:bg-slate-200/60 dark:hover:bg-slate-800/50'
            }`}
          >
            <Briefcase className="w-4 h-4" />
            <span>MEI</span>
          </button>
        </nav>

        {/* Right Actions (Desktop) */}
        <div className="hidden md:flex items-center gap-2">
          <button
            onClick={onOpenLegalTerms}
            className="flex items-center gap-1.5 px-3 py-1.5 text-xs text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-slate-200 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition"
          >
            <Shield className="w-3.5 h-3.5 text-emerald-600 dark:text-emerald-400" />
            <span>Privacidade & Termos</span>
          </button>

          <button
            onClick={toggleTheme}
            className="p-2 rounded-lg bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 hover:bg-slate-200 dark:hover:bg-slate-700 transition"
            title="Alternar Tema (Claro / Escuro)"
          >
            {userAccount?.isDarkTheme ? <Sun className="w-4 h-4 text-amber-500" /> : <Moon className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />}
          </button>

          <button
            onClick={onOpenProfile}
            className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 border border-slate-200 dark:border-slate-700 text-xs font-semibold text-slate-800 dark:text-slate-200 transition"
          >
            <div className="w-6 h-6 rounded-full bg-indigo-500/20 text-indigo-600 dark:text-indigo-300 flex items-center justify-center text-xs font-bold">
              {userAccount?.name?.charAt(0) || 'U'}
            </div>
            <span>{userAccount?.name || 'Perfil'}</span>
          </button>
        </div>
      </div>
    </header>
  );
};
