import React from 'react';
import { X, Shield, Lock, Cpu, Database, FileText } from 'lucide-react';

interface LegalTermsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const LegalTermsModal: React.FC<LegalTermsModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 dark:bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 rounded-2xl w-full max-w-3xl p-6 text-slate-900 dark:text-slate-100 shadow-2xl relative max-h-[85vh] overflow-y-auto transition-colors duration-200">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 p-2 text-slate-400 hover:text-slate-600 dark:hover:text-white rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 transition"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-3 mb-6">
          <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 flex items-center justify-center">
            <Shield className="w-6 h-6" />
          </div>
          <div>
            <h3 className="text-xl font-bold">Termos de Uso, Licença MIT & Privacidade</h3>
            <p className="text-xs text-slate-500 dark:text-slate-400">Planner MNAnimat • Ecossistema 100% Local & Privado</p>
          </div>
        </div>

        <div className="space-y-4 text-xs text-slate-600 dark:text-slate-300 leading-relaxed">
          {/* MIT License Box */}
          <div className="p-4 bg-slate-100 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl space-y-3 font-mono">
            <h4 className="font-bold text-sm text-amber-600 dark:text-amber-400 flex items-center gap-2 font-sans">
              <FileText className="w-4 h-4" />
              Licença MIT (MIT License) - Termo Oficial
            </h4>
            <div className="bg-white dark:bg-slate-900 p-3 rounded-lg border border-slate-200 dark:border-slate-800 text-[11px] text-slate-700 dark:text-slate-300 space-y-2 select-text">
              <p className="font-bold text-indigo-600 dark:text-indigo-400">
                Copyright (c) 2026 Micael Nildo Oliveira Souza
              </p>
              <p>
                Permission is hereby granted, free of charge, to any person obtaining a copy
                of this software and associated documentation files (the "Software"), to deal
                in the Software without restriction, including without limitation the rights
                to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
                copies of the Software, and to permit persons to whom the Software is
                furnished to do so, subject to the following conditions:
              </p>
              <p>
                The above copyright notice and this permission notice shall be included in all
                copies or substantial portions of the Software.
              </p>
              <p>
                THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
                IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
                FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
                AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
                LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
                OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
                SOFTWARE.
              </p>
            </div>

            <div className="pt-2 font-sans space-y-2">
              <h5 className="font-bold text-xs text-slate-700 dark:text-slate-300 mb-1">
                --- Tradução em Português (Informativo) ---
              </h5>
              <div className="bg-white/60 dark:bg-slate-900/60 p-3 rounded-lg border border-slate-200/80 dark:border-slate-800/80 text-[11px] text-slate-600 dark:text-slate-400 space-y-2 leading-relaxed">
                <p className="font-bold text-indigo-600 dark:text-indigo-400">
                  Direitos Autorais (c) 2026 Micael Nildo Oliveira Souza
                </p>
                <p>
                  É concedida permissão, gratuitamente, a qualquer pessoa que obtenha uma cópia
                  deste software e dos arquivos de documentação associados (o "Software"), para
                  lidar no Software sem restrições, incluindo, sem limitação, os direitos de usar,
                  copiar, modificar, mesclar, publicar, distribuir, sublicenciar e/ou vender cópias
                  do Software, e para permitir que as pessoas a quem o Software é fornecido o façam,
                  sujeito às seguintes condições:
                </p>
                <p>
                  O aviso de direitos autorais acima e este aviso de permissão deverão ser incluídos em
                  todas as cópias ou partes substanciais do Software.
                </p>
                <p className="uppercase text-[10px] tracking-tight">
                  O SOFTWARE É FORNECIDO "COMO ESTÁ", SEM GARANTIA DE QUALQUER TIPO, EXPRESSA OU
                  IMPLÍCITA, INCLUINDO, MAS NÃO SE LIMITANDO ÀS GARANTIAS DE COMERCIABILIDADE,
                  ADEQUAÇÃO A UM DETERMINADO FIM E NÃO VIOLAÇÃO. EM NENHUM CASO OS AUTORES OU
                  DETENTORES DOS DIREITOS AUTORAIS SERÃO RESPONSÁVEIS POR QUALQUER RECLAMAÇÃO, DANOS
                  OU OUTRA RESPONSABILIDADE, SEJA EM UMA AÇÃO DE CONTRATO, DELITO OU DE OUTRA FORMA,
                  DECORRENTE DE, OU EM CONEXÃO COM O SOFTWARE OU O USO OU OUTRAS NEGOCIAÇÕES NO
                  SOFTWARE.
                </p>
              </div>
            </div>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl space-y-2">
            <h4 className="font-bold text-sm text-indigo-600 dark:text-indigo-400 flex items-center gap-2">
              <Lock className="w-4 h-4" />
              1. Armazenamento Local e Soberania de Dados (LGPD / GDPR)
            </h4>
            <p>
              O Planner MNAnimat foi projetado para operar com foco total em privacidade. Todas as suas informações de estudo (FocoVest), rotina e saúde (RitVida), e finanças/MEI (MEI) são salvas exclusivamente no navegador e armazenamento do seu dispositivo.
            </p>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl space-y-2">
            <h4 className="font-bold text-sm text-indigo-600 dark:text-indigo-400 flex items-center gap-2">
              <Cpu className="w-4 h-4" />
              2. Assistência de Estudos & Chat GPT (OpenAI)
            </h4>
            <p>
              O tutor de estudos funciona de forma 100% offline e local no navegador. A correção avançada de redação é realizada através do direcionamento seguro para o GPT especializado no Chat GPT (OpenAI) para o ENEM, FUVEST e ITA, mantendo a privacidade de dados do usuário e sem armazenamento não autorizado em servidores externos.
            </p>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl space-y-2">
            <h4 className="font-bold text-sm text-indigo-600 dark:text-indigo-400 flex items-center gap-2">
              <Database className="w-4 h-4" />
              3. Ausência de Rastreadores & Transparência
            </h4>
            <p>
              Esta aplicação não contém scripts de rastreamento comercial, telemetria de anunciantes ou venda de dados a terceiros. Você possui total autonomia para exportar, redefinir ou apagar seus dados quando desejar.
            </p>
          </div>

          <div className="p-4 bg-slate-50 dark:bg-slate-950 border border-slate-200 dark:border-slate-800 rounded-xl space-y-2">
            <h4 className="font-bold text-sm text-amber-600 dark:text-amber-400 flex items-center gap-2">
              <Shield className="w-4 h-4" />
              4. Idade Mínima e Serviços de Terceiros
            </h4>
            <p>
              O uso deste site e de todos os seus recursos, módulos, ferramentas ou redirecionamentos é destinado a indivíduos com idade mínima de 13 (treze) anos completos. Caso você seja menor de 18 (dezoito) anos, declara possuir a expressa permissão e supervisão de seus pais ou responsáveis legais para navegar pela plataforma, aceitar estes termos e utilizar quaisquer serviços integrados ou de terceiros (como o ChatGPT).
            </p>
          </div>
        </div>

        <div className="mt-6 pt-4 border-t border-slate-200 dark:border-slate-800 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 font-bold rounded-xl text-xs text-white transition shadow-lg shadow-indigo-600/20"
          >
            Entendido e Concordo
          </button>
        </div>
      </div>
    </div>
  );
};

