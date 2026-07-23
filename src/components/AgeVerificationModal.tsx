import React, { useState, useEffect } from 'react';
import { ShieldAlert, UserCheck, UserX, Lock, AlertTriangle, ArrowLeft, CheckCircle2, ShieldCheck } from 'lucide-react';

export const AgeVerificationModal: React.FC = () => {
  const [ageStatus, setAgeStatus] = useState<string | null>(() => {
    try {
      return localStorage.getItem('mnanimat_age_verified');
    } catch {
      return null;
    }
  });

  const [viewStep, setViewStep] = useState<'SELECT' | 'UNDER_13_ALERT'>('SELECT');

  // If already verified (+13 or parental consent), do not show modal
  if (ageStatus === 'over13' || ageStatus === 'parental_consent') {
    return null;
  }

  const handleConfirmOver13 = () => {
    try {
      localStorage.setItem('mnanimat_age_verified', 'over13');
    } catch (e) {
      console.error(e);
    }
    setAgeStatus('over13');
  };

  const handleSelectUnder13 = () => {
    setViewStep('UNDER_13_ALERT');
  };

  const handleConfirmParentalConsent = () => {
    try {
      localStorage.setItem('mnanimat_age_verified', 'parental_consent');
    } catch (e) {
      console.error(e);
    }
    setAgeStatus('parental_consent');
  };

  return (
    <div className="fixed inset-0 z-[9999] bg-slate-950/95 backdrop-blur-md flex items-center justify-center p-4 sm:p-6 overflow-y-auto">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-xl p-6 sm:p-8 text-slate-100 shadow-2xl relative my-auto animate-in fade-in zoom-in duration-200">
        
        {/* Header Badge */}
        <div className="flex items-center justify-center mb-6">
          <div className="w-16 h-16 rounded-2xl bg-amber-500/10 border border-amber-500/20 text-amber-400 flex items-center justify-center shadow-lg shadow-amber-500/5">
            <Lock className="w-8 h-8" />
          </div>
        </div>

        {viewStep === 'SELECT' ? (
          <div className="space-y-6 text-center">
            <div>
              <span className="text-[11px] font-extrabold uppercase tracking-wider text-amber-400 bg-amber-500/10 px-3 py-1 rounded-full border border-amber-500/20">
                Trava Obrigatória de Acesso
              </span>
              <h2 className="text-xl sm:text-2xl font-black text-white mt-3">
                Declaração Obrigatória de Idade
              </h2>
              <p className="text-xs sm:text-sm text-slate-300 mt-3 leading-relaxed max-w-lg mx-auto bg-slate-950/80 p-3.5 border border-slate-800 rounded-2xl">
                <strong>Idade Mínima e Serviços de Terceiros:</strong> O uso deste site e de todos os seus recursos, módulos, ferramentas ou redirecionamentos é destinado a indivíduos com idade mínima de 13 (treze) anos completos. Caso você seja menor de 18 (dezoito) anos, declara possuir a expressa permissão e supervisão de seus pais ou responsáveis legais para navegar pela plataforma, aceitar estes termos e utilizar quaisquer serviços integrados ou de terceiros (como o ChatGPT).
              </p>
            </div>

            {/* Choice Cards */}
            <div className="grid grid-cols-1 gap-3.5 pt-2 text-left">
              {/* Option 1: 13+ years */}
              <button
                type="button"
                onClick={handleConfirmOver13}
                className="group relative p-4 sm:p-5 bg-slate-950/80 hover:bg-slate-950 border border-slate-800 hover:border-emerald-500/50 rounded-2xl transition-all duration-200 text-left shadow-lg flex items-start gap-4 hover:scale-[1.01]"
              >
                <div className="w-11 h-11 rounded-xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 flex items-center justify-center shrink-0 mt-0.5 group-hover:bg-emerald-500 group-hover:text-slate-950 transition">
                  <UserCheck className="w-6 h-6" />
                </div>
                <div className="space-y-1 flex-1">
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-bold text-white group-hover:text-emerald-400 transition">
                      Tenho 13 anos ou mais (Maior de 13 anos)
                    </h3>
                    <ShieldCheck className="w-4 h-4 text-emerald-400 opacity-60 group-hover:opacity-100" />
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">
                    Declaro que possuo idade mínima de 13 anos completos e estou apto a utilizar a plataforma Planner MNAnimat e seus recursos.
                  </p>
                </div>
              </button>

              {/* Option 2: Under 13 years */}
              <button
                type="button"
                onClick={handleSelectUnder13}
                className="group relative p-4 sm:p-5 bg-slate-950/80 hover:bg-slate-950 border border-slate-800 hover:border-amber-500/50 rounded-2xl transition-all duration-200 text-left shadow-lg flex items-start gap-4 hover:scale-[1.01]"
              >
                <div className="w-11 h-11 rounded-xl bg-amber-500/10 text-amber-400 border border-amber-500/20 flex items-center justify-center shrink-0 mt-0.5 group-hover:bg-amber-500 group-hover:text-slate-950 transition">
                  <UserX className="w-6 h-6" />
                </div>
                <div className="space-y-1 flex-1">
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-bold text-white group-hover:text-amber-400 transition">
                      Tenho menos de 13 anos (Menor de 13 anos)
                    </h3>
                    <AlertTriangle className="w-4 h-4 text-amber-400 opacity-60 group-hover:opacity-100" />
                  </div>
                  <p className="text-xs text-slate-400 leading-relaxed">
                    Sou menor de 13 anos e necessito de orientações sobre supervisão de responsável legal.
                  </p>
                </div>
              </button>
            </div>

            <div className="pt-2 text-[11px] text-slate-500 border-t border-slate-800/80">
              Sua escolha é registrada localmente em seu dispositivo.
            </div>
          </div>
        ) : (
          /* Under 13 Alert View */
          <div className="space-y-6 text-center">
            <div className="w-12 h-12 rounded-2xl bg-amber-500/10 text-amber-400 border border-amber-500/20 flex items-center justify-center mx-auto">
              <ShieldAlert className="w-6 h-6" />
            </div>

            <div>
              <span className="text-[10px] font-extrabold uppercase tracking-wider text-amber-400 bg-amber-500/10 px-3 py-1 rounded-full border border-amber-500/20">
                Acesso Restrito & Proteção ao Menor
              </span>
              <h2 className="text-xl font-extrabold text-white mt-3">
                Orientação para Menores de 13 Anos
              </h2>
              <div className="p-4 bg-slate-950 border border-slate-800 rounded-2xl text-xs text-slate-300 text-left space-y-3 mt-4 leading-relaxed">
                <p>
                  <strong>Idade Mínima e Serviços de Terceiros:</strong> O uso deste site e de todos os seus recursos, módulos, ferramentas ou redirecionamentos é destinado a indivíduos com idade mínima de 13 (treze) anos completos. Caso você seja menor de 18 (dezoito) anos, declara possuir a expressa permissão e supervisão de seus pais ou responsáveis legais para navegar pela plataforma, aceitar estes termos e utilizar quaisquer serviços integrados ou de terceiros (como o ChatGPT).
                </p>
              </div>
            </div>

            <div className="space-y-3 pt-2">
              <button
                type="button"
                onClick={handleConfirmParentalConsent}
                className="w-full py-3.5 bg-amber-500 hover:bg-amber-400 text-slate-950 font-black rounded-xl text-xs flex items-center justify-center gap-2 transition shadow-lg shadow-amber-500/20 hover:scale-[1.01]"
              >
                <CheckCircle2 className="w-4 h-4" />
                <span>Estou acompanhado por pai/responsável (+18) - Acessar</span>
              </button>

              <button
                type="button"
                onClick={() => setViewStep('SELECT')}
                className="w-full py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-300 font-bold rounded-xl text-xs flex items-center justify-center gap-2 transition"
              >
                <ArrowLeft className="w-4 h-4" />
                <span>Voltar e Refazer Declaração</span>
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
