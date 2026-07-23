import React from 'react';
import { useApp } from './context/AppContext';
import { Navbar } from './components/Navbar';
import { Onboarding } from './components/Onboarding';
import { ProfileModal } from './components/ProfileModal';
import { LegalTermsModal } from './components/LegalTermsModal';
import { AgeVerificationModal } from './components/AgeVerificationModal';

// FOCOVEST screens
import { FocoVestDashboard } from './components/FocoVest/FocoVestDashboard';
import { FocoVestCronograma } from './components/FocoVest/FocoVestCronograma';
import { FocoVestTrilhas } from './components/FocoVest/FocoVestTrilhas';
import { FocoVestAnki } from './components/FocoVest/FocoVestAnki';
import { FocoVestSimulados } from './components/FocoVest/FocoVestSimulados';
import { FocoVestVideos } from './components/FocoVest/FocoVestVideos';
import { FocoVestTutor } from './components/FocoVest/FocoVestTutor';
import { FocoVestRedacao } from './components/FocoVest/FocoVestRedacao';
import { FocoVestFerramentas } from './components/FocoVest/FocoVestFerramentas';

// RITVIDA screens
import { RitVidaOverview } from './components/RitVida/RitVidaOverview';
import { RitVidaStudies } from './components/RitVida/RitVidaStudies';
import { RitVidaVisual } from './components/RitVida/RitVidaVisual';
import { RitVidaGymDiet } from './components/RitVida/RitVidaGymDiet';
import { RitVidaFinance } from './components/RitVida/RitVidaFinance';
import { RitVidaProjects } from './components/RitVida/RitVidaProjects';
import { RitVidaPortfolio } from './components/RitVida/RitVidaPortfolio';

// MEI PRO screens
import { MeiDashboard } from './components/Mei/MeiDashboard';
import { MeiTransactions } from './components/Mei/MeiTransactions';
import { MeiSpecific } from './components/Mei/MeiSpecific';
import { MeiInvoices } from './components/Mei/MeiInvoices';
import { MeiManualImport } from './components/Mei/MeiManualImport';
import { MeiConfigView } from './components/Mei/MeiConfigView';

import {
  GraduationCap,
  Calendar,
  Layers,
  Award,
  Youtube,
  Sparkles,
  FileText,
  Timer,
  Activity,
  Clock,
  Dumbbell,
  Wallet,
  Briefcase,
  Folder,
  DollarSign,
  FileCheck,
  Upload,
  Settings
} from 'lucide-react';

export function App() {
  const {
    userAccount,
    activeModule,
    selectedFocoVestTab,
    setSelectedFocoVestTab,
    selectedRitVidaTab,
    setSelectedRitVidaTab,
    selectedMeiTab,
    setSelectedMeiTab
  } = useApp();

  const [isProfileOpen, setIsProfileOpen] = React.useState(false);
  const [isLegalTermsOpen, setIsLegalTermsOpen] = React.useState(false);

  if (!userAccount) {
    return (
      <>
        <AgeVerificationModal />
        <Onboarding />
      </>
    );
  }

  const renderFocoVestContent = () => {
    switch (selectedFocoVestTab) {
      case 0:
        return (
          <div className="space-y-6">
            <FocoVestDashboard />
            <FocoVestCronograma />
          </div>
        );
      case 1:
        return <FocoVestTrilhas />;
      case 2:
        return <FocoVestAnki />;
      case 3:
        return <FocoVestSimulados />;
      case 4:
        return <FocoVestVideos />;
      case 5:
        return <FocoVestTutor />;
      case 6:
        return <FocoVestRedacao />;
      case 7:
        return <FocoVestFerramentas />;
      default:
        return <FocoVestDashboard />;
    }
  };

  const renderRitVidaContent = () => {
    switch (selectedRitVidaTab) {
      case 0:
        return <RitVidaOverview />;
      case 1:
        return <RitVidaStudies />;
      case 2:
        return <RitVidaVisual />;
      case 3:
        return <RitVidaGymDiet />;
      case 4:
        return <RitVidaFinance />;
      case 5:
        return <RitVidaProjects />;
      case 6:
        return <RitVidaPortfolio />;
      default:
        return <RitVidaOverview />;
    }
  };

  const renderMeiContent = () => {
    switch (selectedMeiTab) {
      case 0:
        return <MeiDashboard />;
      case 1:
        return <MeiTransactions />;
      case 2:
        return <MeiSpecific />;
      case 3:
        return <MeiInvoices />;
      case 4:
        return <MeiManualImport />;
      case 5:
        return <MeiConfigView />;
      default:
        return <MeiDashboard />;
    }
  };

  const focoVestTabs = [
    { label: 'Visão Geral & Cronograma', icon: Calendar },
    { label: 'Trilhas 7 Etapas', icon: GraduationCap },
    { label: 'Flashcards - Repetição Espaçada', icon: Layers },
    { label: 'Simulados', icon: Award },
    { label: 'Vídeo-Aulas', icon: Youtube },
    { label: 'Tutor IA', icon: Sparkles },
    { label: 'Redação ENEM', icon: FileText },
    { label: 'Pomodoro & Erros', icon: Timer }
  ];

  const ritVidaTabs = [
    { label: 'Painel Integrado', icon: Activity },
    { label: 'Horário de Estudos', icon: Clock },
    { label: 'Agenda (Timeline, Gantt & Kanban)', icon: Calendar },
    { label: 'Treino & Nutrição', icon: Dumbbell },
    { label: 'Finanças Pessoais', icon: Wallet },
    { label: 'Projetos', icon: Briefcase },
    { label: 'Portfólio', icon: Folder }
  ];

  const meiTabs = [
    { label: 'Dashboard Executivo', icon: DollarSign },
    { label: 'Lançamentos / Extrato', icon: Wallet },
    { label: 'Contabilidade MEI', icon: FileCheck },
    { label: 'Notas Fiscais NFe', icon: FileText },
    { label: 'Importação Manual', icon: Upload },
    { label: 'Configurações', icon: Settings }
  ];

  return (
    <div className="min-h-screen bg-slate-100 dark:bg-slate-950 text-slate-900 dark:text-slate-100 font-sans transition-colors duration-200 selection:bg-amber-500 selection:text-slate-950">
      <Navbar onOpenProfile={() => setIsProfileOpen(true)} onOpenLegalTerms={() => setIsLegalTermsOpen(true)} />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 space-y-6">
        {/* Module Sub-Navigation Tabs */}
        {activeModule === 'FOCOVEST' && (
          <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-none border-b border-slate-200 dark:border-slate-800/80">
            {focoVestTabs.map((tab, idx) => {
              const Icon = tab.icon;
              const isActive = selectedFocoVestTab === idx;
              return (
                <button
                  key={idx}
                  onClick={() => setSelectedFocoVestTab(idx)}
                  className={`px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 shrink-0 transition ${
                    isActive
                      ? 'bg-amber-500 text-slate-950 shadow-lg shadow-amber-500/20'
                      : 'bg-white dark:bg-slate-900/80 border border-slate-200 dark:border-slate-800/80 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>
        )}

        {activeModule === 'RITVIDA' && (
          <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-none border-b border-slate-200 dark:border-slate-800/80">
            {ritVidaTabs.map((tab, idx) => {
              const Icon = tab.icon;
              const isActive = selectedRitVidaTab === idx;
              return (
                <button
                  key={idx}
                  onClick={() => setSelectedRitVidaTab(idx)}
                  className={`px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 shrink-0 transition ${
                    isActive
                      ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/20'
                      : 'bg-white dark:bg-slate-900/80 border border-slate-200 dark:border-slate-800/80 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>
        )}

        {activeModule === 'MEI_PRO' && (
          <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-none border-b border-slate-200 dark:border-slate-800/80">
            {meiTabs.map((tab, idx) => {
              const Icon = tab.icon;
              const isActive = selectedMeiTab === idx;
              return (
                <button
                  key={idx}
                  onClick={() => setSelectedMeiTab(idx)}
                  className={`px-3.5 py-2 rounded-xl text-xs font-bold flex items-center gap-2 shrink-0 transition ${
                    isActive
                      ? 'bg-emerald-500 text-slate-950 shadow-lg shadow-emerald-500/20'
                      : 'bg-white dark:bg-slate-900/80 border border-slate-200 dark:border-slate-800/80 text-slate-600 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white hover:bg-slate-50 dark:hover:bg-slate-800'
                  }`}
                >
                  <Icon className="w-3.5 h-3.5" />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>
        )}

        {/* Active Module Screen Body */}
        <div className="mt-4">
          {activeModule === 'FOCOVEST' && renderFocoVestContent()}
          {activeModule === 'RITVIDA' && renderRitVidaContent()}
          {activeModule === 'MEI_PRO' && renderMeiContent()}
        </div>
      </main>

      {/* Global Modals */}
      <AgeVerificationModal />
      <ProfileModal isOpen={isProfileOpen} onClose={() => setIsProfileOpen(false)} />
      <LegalTermsModal isOpen={isLegalTermsOpen} onClose={() => setIsLegalTermsOpen(false)} />
    </div>
  );
}

export default App;
