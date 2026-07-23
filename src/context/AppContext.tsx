import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  StudySubject,
  Flashcard,
  Simulado,
  VideoAula,
  CadernoErro,
  Essay,
  RitVidaHour,
  RitVidaFinance,
  RitVidaProject,
  StudySchedule,
  CustomCronogramaItem,
  MeiTransaction,
  MeiInvoice,
  MeiConfig,
  UserAccount,
  GymWorkout,
  DietLog,
  VisualTask,
  ChatMessage,
  PortfolioItem
} from '../types';

import {
  initialSubjects,
  initialFlashcards,
  initialSimulados,
  initialVideos,
  initialCadernoErros,
  initialEssays,
  initialRitVidaHours,
  initialRitVidaFinances,
  initialRitVidaProjects,
  initialSchedules,
  initialCustomCronogramaItems,
  initialMeiTransactions,
  initialMeiInvoices,
  initialMeiConfig,
  initialGymWorkouts,
  initialDietLogs,
  initialVisualTasks,
  initialPortfolioItems
} from '../data/initialData';

interface AppContextType {
  // User & Settings
  userAccount: UserAccount | null;
  acceptTermsAndCreateAccount: (name: string, email: string, passwordHash: string) => void;
  updateUserAccount: (name: string, email: string) => void;
  toggleTheme: () => void;
  updateFinanceMode: (mode: 'MEI + Pessoal' | 'Só MEI' | 'Só Pessoal') => void;
  logoutUserAccount: () => void;
  setupOnboardingMode: (useDemo: boolean, selectedEnem: boolean, selectedIta: boolean, name: string, email: string, passwordHash: string) => void;

  // Active Modules
  activeModule: 'FOCOVEST' | 'RITVIDA' | 'MEI_PRO';
  setActiveModule: (module: 'FOCOVEST' | 'RITVIDA' | 'MEI_PRO') => void;
  selectedFocoVestTab: number;
  setSelectedFocoVestTab: (idx: number) => void;
  selectedRitVidaTab: number;
  setSelectedRitVidaTab: (idx: number) => void;
  selectedMeiTab: number;
  setSelectedMeiTab: (idx: number) => void;

  // Study Subjects
  subjects: StudySubject[];
  toggleSubjectStep: (subjectId: number, step: string) => void;
  addNewSubject: (title: string, category: string) => void;
  updateSubject: (subject: StudySubject) => void;
  deleteSubject: (id: number) => void;

  // Flashcards
  flashcards: Flashcard[];
  addNewFlashcard: (question: string, answer: string) => void;
  answerFlashcard: (card: Flashcard, difficulty: number) => void;
  updateFlashcard: (flashcard: Flashcard) => void;
  deleteFlashcard: (id: number) => void;

  // Simulados
  simulados: Simulado[];
  addNewSimulado: (subject: string, totalQuestions: number, correctAnswers: number, durationMinutes: number) => void;
  updateSimulado: (simulado: Simulado) => void;
  deleteSimulado: (id: number) => void;

  // Video Aulas
  videos: VideoAula[];
  toggleVideoCompleted: (id: number) => void;
  addNewVideo: (title: string, category: string, url: string) => void;
  updateVideo: (video: VideoAula) => void;
  deleteVideo: (id: number) => void;
  resetVideosToEnemCronograma: () => void;

  // AI Chat & Tutor
  chatMessages: ChatMessage[];
  isChatLoading: boolean;
  sendChatMessage: (text: string) => Promise<void>;
  clearChat: () => void;

  // Essay Correction
  essays: Essay[];
  isEssayCorrecting: boolean;
  correctEssay: (title: string, text: string) => Promise<void>;
  updateEssay: (essay: Essay) => void;
  deleteEssay: (id: number) => void;

  // Caderno de Erros
  errors: CadernoErro[];
  addNewError: (subject: string, questionText: string, errorReason: string, correctConcept: string) => void;
  updateError: (error: CadernoErro) => void;
  deleteError: (id: number) => void;

  // Pomodoro Timer
  pomodoroSecondsLeft: number;
  pomodoroIsRunning: boolean;
  pomodoroBlockMinutes: number;
  selectPomodoroBlock: (minutes: number) => void;
  startPomodoro: () => void;
  pausePomodoro: () => void;
  resetPomodoro: () => void;

  // RitVida Hours
  hoursList: RitVidaHour[];
  addWorkedHours: (functionName: string, hours: number, dateString: string) => void;
  deleteHour: (id: number) => void;
  updateHour: (hour: RitVidaHour) => void;

  // RitVida Finances
  ritVidaFinances: RitVidaFinance[];
  addRitVidaTransaction: (description: string, amount: number, type: 'REVENUE' | 'EXPENSE', dateString: string) => void;
  updateRitVidaTransaction: (transaction: RitVidaFinance) => void;
  deleteRitVidaTransaction: (id: number) => void;

  // RitVida Projects
  projects: RitVidaProject[];
  addProject: (name: string, progressPercentage: number, targetDateString: string) => void;
  updateProjectProgress: (id: number, progress: number) => void;
  updateProject: (project: RitVidaProject) => void;
  deleteProject: (id: number) => void;

  // Schedules & Cronograma
  schedules: StudySchedule[];
  addSchedule: (dayOfWeek: string, durationMinutes: number, subjectTitle: string) => void;
  updateSchedule: (schedule: StudySchedule) => void;
  deleteSchedule: (id: number) => void;
  customCronogramaItems: CustomCronogramaItem[];
  addCustomCronogramaItem: (content: string, week: string, dateInterval: string, targetSchedule?: 'ENEM' | 'ITA' | 'CUSTOM') => void;
  toggleCustomCronogramaItem: (id: number) => void;
  updateCustomCronogramaItem: (item: CustomCronogramaItem) => void;
  importCustomCronogramaItems: (items: CustomCronogramaItem[]) => void;
  deleteCustomCronogramaItem: (id: number) => void;
  clearAllCustomCronogramaItems: () => void;

  // MEI Transactions
  meiTransactions: MeiTransaction[];
  addMeiTransaction: (
    description: string,
    amount: number,
    category: string,
    accountType: 'PJ' | 'PESSOAL' | 'DINHEIRO',
    transactionType: 'RECEITA' | 'DESPESA',
    dateString: string,
    hasInvoice: boolean,
    status: 'Pago' | 'Pendente',
    notes: string
  ) => void;
  updateMeiTransaction: (transaction: MeiTransaction) => void;
  deleteMeiTransaction: (id: number) => void;
  clearMeiTransactions: () => void;

  // MEI Invoices
  meiInvoices: MeiInvoice[];
  addMeiInvoice: (
    clientName: string,
    serviceDescription: string,
    amount: number,
    dueDate: string,
    isIssued: boolean,
    isSent: boolean,
    isReceived: boolean,
    invoiceLink: string
  ) => void;
  updateMeiInvoice: (invoice: MeiInvoice) => void;
  deleteMeiInvoice: (id: number) => void;
  clearMeiInvoices: () => void;

  // MEI Config
  meiConfig: MeiConfig;
  saveMeiConfig: (config: MeiConfig) => void;
  restoreExampleMeiData: () => void;

  // Gym & Diet
  gymWorkouts: GymWorkout[];
  insertGymWorkout: (exercise: string, sets: number, reps: number, weightKg: number, dateString: string) => void;
  updateGymWorkout: (workout: GymWorkout) => void;
  deleteGymWorkout: (id: number) => void;
  toggleGymWorkoutStatus: (id: number) => void;

  dietLogs: DietLog[];
  insertDietLog: (mealType: string, foodName: string, caloriesKcal: number, waterIntakeMl: number, dateString: string) => void;
  updateDietLog: (log: DietLog) => void;
  deleteDietLog: (id: number) => void;

  // Visual Tasks
  visualTasks: VisualTask[];
  insertVisualTask: (
    title: string,
    startDate: string,
    startTime: string,
    endDate: string,
    endTime: string,
    startHour: number,
    durationHours: number,
    func: string,
    tag: string,
    checklistRaw: string
  ) => void;
  updateVisualTask: (task: VisualTask) => void;
  deleteVisualTask: (id: number) => void;

  // Portfolio Items
  portfolioItems: PortfolioItem[];
  addPortfolioItem: (title: string, description: string, iconType: 'design' | 'photo' | 'integration' | 'manufacturing') => void;
  deletePortfolioItem: (id: number) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

function getStorage<T>(key: string, defaultValue: T): T {
  try {
    const item = localStorage.getItem(key);
    return item ? JSON.parse(item) : defaultValue;
  } catch {
    return defaultValue;
  }
}

function setStorage<T>(key: string, value: T): void {
  try {
    localStorage.setItem(key, JSON.stringify(value));
  } catch (e) {
    console.error('Failed to save to localStorage:', e);
  }
}

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Navigation & User State
  const [activeModule, setActiveModule] = useState<'FOCOVEST' | 'RITVIDA' | 'MEI_PRO'>('FOCOVEST');
  const [selectedFocoVestTab, setSelectedFocoVestTab] = useState(0);
  const [selectedRitVidaTab, setSelectedRitVidaTab] = useState(0);
  const [selectedMeiTab, setSelectedMeiTab] = useState(0);

  const [userAccount, setUserAccount] = useState<UserAccount | null>(() =>
    getStorage<UserAccount | null>('mn_user_account', {
      id: 1,
      name: 'Micael Souza',
      email: 'mnanimat@gmail.com',
      passwordHash: 'demo123',
      termsAccepted: true,
      termsAcceptedTimestamp: Date.now() - 86400000 * 30,
      isDarkTheme: true,
      financeMode: 'MEI + Pessoal'
    })
  );

  useEffect(() => {
    setStorage('mn_user_account', userAccount);
    if (userAccount?.isDarkTheme) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [userAccount]);

  // Model states initialized with localStorage or initial Data
  const [subjects, setSubjects] = useState<StudySubject[]>(() => getStorage('mn_subjects', initialSubjects));
  const [flashcards, setFlashcards] = useState<Flashcard[]>(() => getStorage('mn_flashcards', initialFlashcards));
  const [simulados, setSimulados] = useState<Simulado[]>(() => getStorage('mn_simulados', initialSimulados));
  const [videos, setVideos] = useState<VideoAula[]>(() => getStorage('mn_videos', initialVideos));
  const [errors, setErrors] = useState<CadernoErro[]>(() => getStorage('mn_errors', initialCadernoErros));
  const [essays, setEssays] = useState<Essay[]>(() => getStorage('mn_essays', initialEssays));
  const [hoursList, setHoursList] = useState<RitVidaHour[]>(() => getStorage('mn_hours', initialRitVidaHours));
  const [ritVidaFinances, setRitVidaFinances] = useState<RitVidaFinance[]>(() => getStorage('mn_ritvida_finances', initialRitVidaFinances));
  const [projects, setProjects] = useState<RitVidaProject[]>(() => getStorage('mn_projects', initialRitVidaProjects));
  const [schedules, setSchedules] = useState<StudySchedule[]>(() => getStorage('mn_schedules', initialSchedules));
  const [customCronogramaItems, setCustomCronogramaItems] = useState<CustomCronogramaItem[]>(() => getStorage('mn_custom_cronograma', initialCustomCronogramaItems));
  const [meiTransactions, setMeiTransactions] = useState<MeiTransaction[]>(() => getStorage('mn_mei_transactions', initialMeiTransactions));
  const [meiInvoices, setMeiInvoices] = useState<MeiInvoice[]>(() => getStorage('mn_mei_invoices', initialMeiInvoices));
  const [meiConfig, setMeiConfig] = useState<MeiConfig>(() => getStorage('mn_mei_config', initialMeiConfig));
  const [gymWorkouts, setGymWorkouts] = useState<GymWorkout[]>(() => getStorage('mn_gym_workouts', initialGymWorkouts));
  const [dietLogs, setDietLogs] = useState<DietLog[]>(() => getStorage('mn_diet_logs', initialDietLogs));
  const [visualTasks, setVisualTasks] = useState<VisualTask[]>(() => getStorage('mn_visual_tasks', initialVisualTasks));
  const [portfolioItems, setPortfolioItems] = useState<PortfolioItem[]>(() => getStorage('mn_portfolio_items', initialPortfolioItems));

  // Sync back to localStorage
  useEffect(() => setStorage('mn_subjects', subjects), [subjects]);
  useEffect(() => setStorage('mn_flashcards', flashcards), [flashcards]);
  useEffect(() => setStorage('mn_simulados', simulados), [simulados]);
  useEffect(() => setStorage('mn_videos', videos), [videos]);
  useEffect(() => setStorage('mn_errors', errors), [errors]);
  useEffect(() => setStorage('mn_essays', essays), [essays]);
  useEffect(() => setStorage('mn_hours', hoursList), [hoursList]);
  useEffect(() => setStorage('mn_ritvida_finances', ritVidaFinances), [ritVidaFinances]);
  useEffect(() => setStorage('mn_projects', projects), [projects]);
  useEffect(() => setStorage('mn_schedules', schedules), [schedules]);
  useEffect(() => setStorage('mn_custom_cronograma', customCronogramaItems), [customCronogramaItems]);
  useEffect(() => setStorage('mn_mei_transactions', meiTransactions), [meiTransactions]);
  useEffect(() => setStorage('mn_mei_invoices', meiInvoices), [meiInvoices]);
  useEffect(() => setStorage('mn_mei_config', meiConfig), [meiConfig]);
  useEffect(() => setStorage('mn_gym_workouts', gymWorkouts), [gymWorkouts]);
  useEffect(() => setStorage('mn_diet_logs', dietLogs), [dietLogs]);
  useEffect(() => setStorage('mn_visual_tasks', visualTasks), [visualTasks]);
  useEffect(() => setStorage('mn_portfolio_items', portfolioItems), [portfolioItems]);

  // Pomodoro State
  const [pomodoroBlockMinutes, setPomodoroBlockMinutes] = useState(25);
  const [pomodoroSecondsLeft, setPomodoroSecondsLeft] = useState(25 * 60);
  const [pomodoroIsRunning, setPomodoroIsRunning] = useState(false);

  useEffect(() => {
    let interval: any = null;
    if (pomodoroIsRunning && pomodoroSecondsLeft > 0) {
      interval = setInterval(() => {
        setPomodoroSecondsLeft((prev) => prev - 1);
      }, 1000);
    } else if (pomodoroSecondsLeft === 0) {
      setPomodoroIsRunning(false);
    }
    return () => clearInterval(interval);
  }, [pomodoroIsRunning, pomodoroSecondsLeft]);

  const selectPomodoroBlock = (minutes: number) => {
    setPomodoroBlockMinutes(minutes);
    setPomodoroSecondsLeft(minutes * 60);
    setPomodoroIsRunning(false);
  };
  const startPomodoro = () => setPomodoroIsRunning(true);
  const pausePomodoro = () => setPomodoroIsRunning(false);
  const resetPomodoro = () => {
    setPomodoroIsRunning(false);
    setPomodoroSecondsLeft(pomodoroBlockMinutes * 60);
  };

  // AI Chat Tutor State
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>([
    {
      id: '1',
      sender: 'AI',
      text: 'Olá! Sou seu Tutor IA gratuito e local. Como posso te ajudar com os seus estudos hoje?',
      timestamp: Date.now()
    }
  ]);
  const [isChatLoading, setIsChatLoading] = useState(false);

  const sendChatMessage = async (text: string) => {
    if (!text.trim()) return;
    const userMsg: ChatMessage = {
      id: Date.now().toString(),
      sender: 'USER',
      text,
      timestamp: Date.now()
    };
    setChatMessages((prev) => [...prev, userMsg]);
    setIsChatLoading(true);

    setTimeout(() => {
      let responseText = '';
      const query = text.toLowerCase();

      if (query.includes('equação') || query.includes('equacao') || query.includes('vértice') || query.includes('vertice') || query.includes('2º grau')) {
        responseText = `[Tutor IA Local - Matemática e Álgebra]

Para equações do 2º grau no formato ax² + bx + c = 0:

1. Discriminante (Delta):
   Δ = b² - 4ac

2. Raízes (Fórmula de Bhaskara):
   x = (-b ± √Δ) / (2a)

3. Vértice da Parábola (Máximos e Mínimos no ENEM):
   - X_v = -b / (2a)  (Ponto de máximo ou mínimo no eixo X, ex: quantidade de itens fabricados)
   - Y_v = -Δ / (4a)  (Valor máximo ou mínimo no eixo Y, ex: lucro máximo ou custo mínimo)

Dica FocoVest: No ENEM, questões de lucro máximo ou altura máxima de projétil sempre pedem o cálculo das coordenadas do vértice V(Xv, Yv)!`;
      } else if (query.includes('intervenção') || query.includes('intervencao') || query.includes('5 elementos') || query.includes('redação') || query.includes('redacao')) {
        responseText = `[Tutor IA Local - Redação ENEM]

A Proposta de Intervenção (Competência 5) vale 200 pontos e exige obrigatoriamente os 5 elementos abaixo:

1. AGENTE: Quem executará a medida? (Ex: Ministério da Educação, ONG de alfabetização digital).
2. AÇÃO: O que será feito? (Ex: Promover oficinas de capacitação e conscientização crítica).
3. MEIO / MODO: Como será feito? (Ex: Por meio de parcerias com prefeituras e veiculação em mídias comunitárias).
4. EFEITO / FINALIDADE: Para que serve? (Ex: Com o intuito de mitigar a exclusão digital e promover a cidadania plena).
5. DETALHAMENTO: Explicação adicional de um dos elementos. (Ex: "Ministério da Educação — órgão responsável pela formulação das políticas nacionais de ensino — ...").

Dica FocoVest: Sempre verifique se o seu detalhamento exemplifica ou especifica o Agente ou a Ação para garantir os 200 pontos completos na C5! Para correções completas com IA, use o botão do ChatGPT no topo do aplicativo!`;
      } else if (query.includes('mendel') || query.includes('genética') || query.includes('genetica')) {
        responseText = `[Tutor IA Local - Biologia e Genética]

Leis de Mendel para o ENEM:

• 1ª Lei de Mendel (Monohibridismo / Segregação de Fatores):
  Cada caráter é determinado por um par de fatores (genes alelos) que se separam na formação dos gametas.
  - Cruzamento de heterozigotos (Aa x Aa): Proporção Genotípica 1 AA : 2 Aa : 1 aa. Proporção Fenotípica 3 Dominantes : 1 Recessivo.

• 2ª Lei de Mendel (Dihibridismo / Segregação Independente):
  Alelos de genes diferentes segregam-se independentemente durante a meiose.
  - Cruzamento di-híbrido (AaBb x AaBb): Proporção Fenotípica clássica 9:3:3:1.`;
      } else if (query.includes('centrípeta') || query.includes('centripeta') || query.includes('vetor') || query.includes('mecânica') || query.includes('mecanica') || query.includes('ita')) {
        responseText = `[Tutor IA Local - Física e Mecânica Avançada ITA]

Força Centrípeta e Decomposição Vetorial:

1. Força Centrípeta (F_c):
   F_c = m · v² / R = m · ω² · R
   Não é uma "força nova", mas sim a resultante de todas as forças reais que apontam para o centro da trajetória circular.

2. Decomposição Vetorial em Aclives / Curvas Sobre-elevadas (ITA):
   - N · sin(θ) = m · v² / R (Componente da Normal garantindo o movimento circular)
   - N · cos(θ) = m · g (Equilíbrio vertical)
   - Dividindo as equações: tan(θ) = v² / (g · R)

Essa relação permite calcular a velocidade ideal para que um veículo faça a curva sem depender do atrito dos pneus!`;
      } else {
        responseText = `[Tutor IA Local - Assistente FocoVest]

Entendi sua pergunta sobre: "${text}"

Orientação Prática de Estudo:
• Para absorver e dominar essa matéria com eficiência, aplique a Metodologia dos 7 Passos nas suas Trilhas:
  1. Assistir à aula com atenção plena
  2. Construir um resumo sintético
  3. Praticar a autoexplicação
  4. Resolver a lista de exercícios práticos
  5. Anotar dúvidas e pontos fracos no Caderno de Erros
  6. Revisar com repetição espaçada nos Flashcards
  7. Testar seu tempo e precisão nos Simulados

Se você quiser realizar uma correção completa de redação com o assistente especializado do ChatGPT (OpenAI), acesse o módulo de Redação!`;
      }

      const aiMsg: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'AI',
        text: responseText,
        timestamp: Date.now()
      };
      setChatMessages((prev) => [...prev, aiMsg]);
      setIsChatLoading(false);
    }, 400);
  };

  const clearChat = () => {
    setChatMessages([
      {
        id: Date.now().toString(),
        sender: 'AI',
        text: 'Chat limpo! Como posso te ajudar nos seus estudos?',
        timestamp: Date.now()
      }
    ]);
  };

  // Essay Correction State (Offline Local Drafts & ChatGPT Helper)
  const [isEssayCorrecting, setIsEssayCorrecting] = useState(false);
  const correctEssay = async (title: string, text: string) => {
    if (!title.trim() || !text.trim()) return;
    setIsEssayCorrecting(true);

    setTimeout(() => {
      const feedbackLocal = `[RASCUNHO SALVO LOCALMENTE]
Tema: "${title}"
Total de Palavras: ${text.split(/\s+/).filter(Boolean).length} palavras

💡 Seu rascunho de redação foi gravado com sucesso no dispositivo.
Para obter uma avaliação minuciosa com nota de 0 a 1000 nas 5 Competências do ENEM, FUVEST e ITA, utilize o botão verde do ChatGPT no topo da página. O texto do seu rascunho foi formatado e está pronto para envio!`;

      const newEssay: Essay = {
        id: Date.now(),
        title,
        text,
        feedback: feedbackLocal,
        timestamp: Date.now()
      };
      setEssays((prev) => [newEssay, ...prev]);
      setIsEssayCorrecting(false);
    }, 300);
  };

  // User Actions
  const acceptTermsAndCreateAccount = (name: string, email: string, passwordHash: string) => {
    setUserAccount({
      id: 1,
      name,
      email,
      passwordHash,
      termsAccepted: true,
      termsAcceptedTimestamp: Date.now(),
      isDarkTheme: true,
      financeMode: 'MEI + Pessoal'
    });
  };

  const updateUserAccount = (name: string, email: string) => {
    setUserAccount((prev) => (prev ? { ...prev, name, email } : null));
  };

  const toggleTheme = () => {
    setUserAccount((prev) => (prev ? { ...prev, isDarkTheme: !prev.isDarkTheme } : null));
  };

  const updateFinanceMode = (mode: 'MEI + Pessoal' | 'Só MEI' | 'Só Pessoal') => {
    setUserAccount((prev) => (prev ? { ...prev, financeMode: mode } : null));
  };

  const logoutUserAccount = () => {
    setUserAccount((prev) => (prev ? { ...prev, termsAccepted: false } : null));
  };

  const setupOnboardingMode = (useDemo: boolean, selectedEnem: boolean, selectedIta: boolean, name: string, email: string, passwordHash: string) => {
    if (useDemo) {
      setSubjects(initialSubjects);
      setVideos(initialVideos);
      setMeiTransactions(initialMeiTransactions);
      setMeiInvoices(initialMeiInvoices);
    } else {
      let newSubs: StudySubject[] = [];
      let newVids: VideoAula[] = [];
      if (selectedEnem) {
        newSubs.push(
          { id: 101, title: 'Matemática ENEM (Álgebra e Geometria)', category: 'Matemática', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false },
          { id: 102, title: 'Linguagens ENEM (Gramática e Literatura)', category: 'Linguagens', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false },
          { id: 103, title: 'Redação ENEM (Produção Textual e Modelo)', category: 'Redação', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false }
        );
        newVids.push(
          { id: 201, title: 'Redação ENEM: Como fazer uma Introdução Nota 1000', category: 'Redação', youtubeIdOrUrl: 'https://www.youtube.com/watch?v=Lp7eNOn_E6E', isCompleted: false },
          { id: 202, title: 'Matemática ENEM: Introdução à Função Quadrática', category: 'Matemática', youtubeIdOrUrl: 'https://www.youtube.com/watch?v=0hWcoA7GfGk', isCompleted: false }
        );
      }
      if (selectedIta) {
        newSubs.push(
          { id: 104, title: 'Matemática ITA (Cônicas, Matrizes, Polinômios)', category: 'Matemática', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false },
          { id: 105, title: 'Física ITA (Mecânica Avançada, Eletromagnetismo)', category: 'Física', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false },
          { id: 106, title: 'Química ITA (Química Inorgânica e Equilíbrio Iônico)', category: 'Química', stepAula: false, stepResumo: false, stepAutoexplicacao: false, stepExercicios: false, stepCadernoErros: false, stepRevisao: false, stepSimulado: false }
        );
      }
      setSubjects(newSubs);
      setVideos(newVids);
    }

    setUserAccount({
      id: 1,
      name: name || 'Estudante MNAnimat',
      email: email || 'usuario@exemplo.com',
      passwordHash: passwordHash || '123456',
      termsAccepted: true,
      termsAcceptedTimestamp: Date.now(),
      isDarkTheme: true,
      financeMode: 'MEI + Pessoal'
    });
  };

  // Handlers for Study Subjects
  const toggleSubjectStep = (subjectId: number, step: string) => {
    setSubjects((prev) =>
      prev.map((sub) => {
        if (sub.id !== subjectId) return sub;
        const key = step.toLowerCase();
        return {
          ...sub,
          stepAula: key === 'aula' ? !sub.stepAula : sub.stepAula,
          stepResumo: key === 'resumo' ? !sub.stepResumo : sub.stepResumo,
          stepAutoexplicacao: key === 'autoexplicacao' ? !sub.stepAutoexplicacao : sub.stepAutoexplicacao,
          stepExercicios: key === 'exercicios' ? !sub.stepExercicios : sub.stepExercicios,
          stepCadernoErros: key === 'cadernoerros' ? !sub.stepCadernoErros : sub.stepCadernoErros,
          stepRevisao: key === 'revisao' ? !sub.stepRevisao : sub.stepRevisao,
          stepSimulado: key === 'simulado' ? !sub.stepSimulado : sub.stepSimulado
        };
      })
    );
  };

  const addNewSubject = (title: string, category: string) => {
    if (!title.trim() || !category.trim()) return;
    const newSub: StudySubject = {
      id: Date.now(),
      title,
      category,
      stepAula: false,
      stepResumo: false,
      stepAutoexplicacao: false,
      stepExercicios: false,
      stepCadernoErros: false,
      stepRevisao: false,
      stepSimulado: false
    };
    setSubjects((prev) => [newSub, ...prev]);
  };

  const deleteSubject = (id: number) => {
    setSubjects((prev) => prev.filter((s) => s.id !== id));
  };

  const updateSubject = (subject: StudySubject) => {
    setSubjects((prev) => prev.map((s) => (s.id === subject.id ? subject : s)));
  };

  // Flashcards Handlers
  const addNewFlashcard = (question: string, answer: string) => {
    if (!question.trim() || !answer.trim()) return;
    const newCard: Flashcard = {
      id: Date.now(),
      question,
      answer,
      intervalDays: 1,
      easeFactor: 2.5,
      repetitions: 0,
      dueDate: Date.now()
    };
    setFlashcards((prev) => [...prev, newCard]);
  };

  const updateFlashcard = (flashcard: Flashcard) => {
    setFlashcards((prev) => prev.map((f) => (f.id === flashcard.id ? flashcard : f)));
  };

  const answerFlashcard = (card: Flashcard, difficulty: number) => {
    const isCorrect = difficulty >= 3;
    const newRepetitions = isCorrect ? card.repetitions + 1 : 0;
    let newInterval = 1;
    if (isCorrect) {
      if (newRepetitions === 1) newInterval = 1;
      else if (newRepetitions === 2) newInterval = 6;
      else newInterval = Math.max(1, Math.round(card.intervalDays * card.easeFactor));
    }
    const newEaseFactor = Math.max(1.3, card.easeFactor + (0.1 - (5 - difficulty) * (0.08 + (5 - difficulty) * 0.02)));

    const updated: Flashcard = {
      ...card,
      repetitions: newRepetitions,
      intervalDays: newInterval,
      easeFactor: newEaseFactor,
      dueDate: Date.now() + newInterval * 24 * 60 * 60 * 1000
    };
    setFlashcards((prev) => prev.map((f) => (f.id === card.id ? updated : f)));
  };

  const deleteFlashcard = (id: number) => {
    setFlashcards((prev) => prev.filter((f) => f.id !== id));
  };

  // Simulados Handlers
  const addNewSimulado = (subject: string, totalQuestions: number, correctAnswers: number, durationMinutes: number) => {
    const newSim: Simulado = {
      id: Date.now(),
      subject,
      totalQuestions,
      correctAnswers,
      durationMinutes,
      timestamp: Date.now()
    };
    setSimulados((prev) => [newSim, ...prev]);
  };

  const updateSimulado = (simulado: Simulado) => {
    setSimulados((prev) => prev.map((s) => (s.id === simulado.id ? simulado : s)));
  };

  const deleteSimulado = (id: number) => setSimulados((prev) => prev.filter((s) => s.id !== id));

  // Videos Handlers
  const toggleVideoCompleted = (id: number) => {
    setVideos((prev) => prev.map((v) => (v.id === id ? { ...v, isCompleted: !v.isCompleted } : v)));
  };

  const addNewVideo = (title: string, category: string, url: string) => {
    if (!title.trim() || !category.trim()) return;
    const newVid: VideoAula = {
      id: Date.now(),
      title,
      category,
      youtubeIdOrUrl: url,
      isCompleted: false
    };
    setVideos((prev) => [newVid, ...prev]);
  };

  const updateVideo = (video: VideoAula) => {
    setVideos((prev) => prev.map((v) => (v.id === video.id ? video : v)));
  };

  const deleteVideo = (id: number) => setVideos((prev) => prev.filter((v) => v.id !== id));

  const resetVideosToEnemCronograma = () => {
    setVideos(initialVideos);
  };

  // Caderno de Erros Handlers
  const addNewError = (subject: string, questionText: string, errorReason: string, correctConcept: string) => {
    if (!subject.trim() || !questionText.trim()) return;
    const newErr: CadernoErro = {
      id: Date.now(),
      subject,
      questionText,
      errorReason,
      correctConcept,
      timestamp: Date.now()
    };
    setErrors((prev) => [newErr, ...prev]);
  };

  const updateError = (error: CadernoErro) => {
    setErrors((prev) => prev.map((e) => (e.id === error.id ? error : e)));
  };

  const deleteError = (id: number) => setErrors((prev) => prev.filter((e) => e.id !== id));
  const updateEssay = (essay: Essay) => setEssays((prev) => prev.map((e) => (e.id === essay.id ? essay : e)));
  const deleteEssay = (id: number) => setEssays((prev) => prev.filter((e) => e.id !== id));

  // RitVida Hours
  const addWorkedHours = (functionName: string, hours: number, dateString: string) => {
    if (!functionName.trim() || hours <= 0) return;
    const newH: RitVidaHour = {
      id: Date.now(),
      functionName,
      hours,
      dateString: dateString || new Date().toISOString().split('T')[0]
    };
    setHoursList((prev) => [newH, ...prev]);
  };
  const deleteHour = (id: number) => setHoursList((prev) => prev.filter((h) => h.id !== id));
  const updateHour = (hour: RitVidaHour) => setHoursList((prev) => prev.map((h) => (h.id === hour.id ? hour : h)));

  // RitVida Finances
  const addRitVidaTransaction = (description: string, amount: number, type: 'REVENUE' | 'EXPENSE', dateString: string) => {
    if (!description.trim() || amount <= 0) return;
    const newT: RitVidaFinance = {
      id: Date.now(),
      description,
      amount,
      type,
      dateString: dateString || new Date().toISOString().split('T')[0]
    };
    setRitVidaFinances((prev) => [newT, ...prev]);
  };
  const updateRitVidaTransaction = (transaction: RitVidaFinance) => {
    setRitVidaFinances((prev) => prev.map((t) => (t.id === transaction.id ? transaction : t)));
  };
  const deleteRitVidaTransaction = (id: number) => setRitVidaFinances((prev) => prev.filter((t) => t.id !== id));

  // Projects
  const addProject = (name: string, progressPercentage: number, targetDateString: string) => {
    if (!name.trim()) return;
    const newP: RitVidaProject = {
      id: Date.now(),
      name,
      progressPercentage: Math.min(100, Math.max(0, progressPercentage)),
      targetDateString,
      isCompleted: progressPercentage >= 100
    };
    setProjects((prev) => [newP, ...prev]);
  };

  const updateProjectProgress = (id: number, progress: number) => {
    const prog = Math.min(100, Math.max(0, progress));
    setProjects((prev) => prev.map((p) => (p.id === id ? { ...p, progressPercentage: prog, isCompleted: prog >= 100 } : p)));
  };
  const updateProject = (project: RitVidaProject) => {
    setProjects((prev) => prev.map((p) => (p.id === project.id ? project : p)));
  };
  const deleteProject = (id: number) => setProjects((prev) => prev.filter((p) => p.id !== id));

  // Schedules & Cronograma
  const addSchedule = (dayOfWeek: string, durationMinutes: number, subjectTitle: string) => {
    if (!dayOfWeek.trim() || !subjectTitle.trim() || durationMinutes <= 0) return;
    const newS: StudySchedule = { id: Date.now(), dayOfWeek, durationMinutes, subjectTitle };
    setSchedules((prev) => [...prev, newS]);
  };
  const updateSchedule = (schedule: StudySchedule) => {
    setSchedules((prev) => prev.map((s) => (s.id === schedule.id ? schedule : s)));
  };
  const deleteSchedule = (id: number) => setSchedules((prev) => prev.filter((s) => s.id !== id));

  const addCustomCronogramaItem = (content: string, week: string, dateInterval: string, targetSchedule?: 'ENEM' | 'ITA' | 'CUSTOM') => {
    if (!content.trim()) return;
    const newC: CustomCronogramaItem = {
      id: Date.now(),
      content,
      week: week || 'Semana 1',
      dateInterval: dateInterval || '',
      isCompleted: false,
      targetSchedule: targetSchedule || 'CUSTOM'
    };
    setCustomCronogramaItems((prev) => [newC, ...prev]);
  };

  const toggleCustomCronogramaItem = (id: number) => {
    setCustomCronogramaItems((prev) => prev.map((c) => (c.id === id ? { ...c, isCompleted: !c.isCompleted } : c)));
  };

  const updateCustomCronogramaItem = (item: CustomCronogramaItem) => {
    setCustomCronogramaItems((prev) => prev.map((c) => (c.id === item.id ? item : c)));
  };

  const importCustomCronogramaItems = (items: CustomCronogramaItem[]) => {
    setCustomCronogramaItems((prev) => [...items, ...prev]);
  };

  const deleteCustomCronogramaItem = (id: number) => setCustomCronogramaItems((prev) => prev.filter((c) => c.id !== id));
  const clearAllCustomCronogramaItems = () => setCustomCronogramaItems([]);

  // MEI Transactions
  const addMeiTransaction = (
    description: string,
    amount: number,
    category: string,
    accountType: 'PJ' | 'PESSOAL' | 'DINHEIRO',
    transactionType: 'RECEITA' | 'DESPESA',
    dateString: string,
    hasInvoice: boolean,
    status: 'Pago' | 'Pendente',
    notes: string
  ) => {
    if (!description.trim() || amount <= 0) return;
    const newT: MeiTransaction = {
      id: Date.now(),
      description,
      amount,
      category,
      accountType,
      transactionType,
      dateString: dateString || new Date().toISOString().split('T')[0],
      hasInvoice,
      status,
      notes
    };
    setMeiTransactions((prev) => [newT, ...prev]);
  };

  const updateMeiTransaction = (transaction: MeiTransaction) => {
    setMeiTransactions((prev) => prev.map((t) => (t.id === transaction.id ? transaction : t)));
  };

  const deleteMeiTransaction = (id: number) => setMeiTransactions((prev) => prev.filter((t) => t.id !== id));
  const clearMeiTransactions = () => setMeiTransactions([]);

  // MEI Invoices
  const addMeiInvoice = (
    clientName: string,
    serviceDescription: string,
    amount: number,
    dueDate: string,
    isIssued: boolean,
    isSent: boolean,
    isReceived: boolean,
    invoiceLink: string
  ) => {
    if (!clientName.trim() || amount <= 0) return;
    const newInv: MeiInvoice = {
      id: Date.now(),
      clientName,
      serviceDescription,
      amount,
      dueDate,
      isIssued,
      isSent,
      isReceived,
      invoiceLink
    };
    setMeiInvoices((prev) => [newInv, ...prev]);
  };

  const updateMeiInvoice = (invoice: MeiInvoice) => {
    setMeiInvoices((prev) => prev.map((inv) => (inv.id === invoice.id ? invoice : inv)));
  };

  const deleteMeiInvoice = (id: number) => setMeiInvoices((prev) => prev.filter((inv) => inv.id !== id));
  const clearMeiInvoices = () => setMeiInvoices([]);

  // MEI Config
  const saveMeiConfig = (config: MeiConfig) => setMeiConfig(config);

  const restoreExampleMeiData = () => {
    setMeiTransactions(initialMeiTransactions);
    setMeiInvoices(initialMeiInvoices);
    setMeiConfig(initialMeiConfig);
  };

  // Gym & Diet
  const insertGymWorkout = (exercise: string, sets: number, reps: number, weightKg: number, dateString: string) => {
    if (!exercise.trim()) return;
    const newW: GymWorkout = {
      id: Date.now(),
      exercise,
      sets,
      reps,
      weightKg,
      dateString: dateString || new Date().toISOString().split('T')[0],
      isCompleted: false
    };
    setGymWorkouts((prev) => [newW, ...prev]);
  };

  const updateGymWorkout = (workout: GymWorkout) => {
    setGymWorkouts((prev) => prev.map((w) => (w.id === workout.id ? workout : w)));
  };

  const deleteGymWorkout = (id: number) => setGymWorkouts((prev) => prev.filter((w) => w.id !== id));
  const toggleGymWorkoutStatus = (id: number) => {
    setGymWorkouts((prev) => prev.map((w) => (w.id === id ? { ...w, isCompleted: !w.isCompleted } : w)));
  };

  const insertDietLog = (mealType: string, foodName: string, caloriesKcal: number, waterIntakeMl: number, dateString: string) => {
    if (!foodName.trim()) return;
    const newD: DietLog = {
      id: Date.now(),
      mealType,
      foodName,
      caloriesKcal,
      waterIntakeMl,
      dateString: dateString || new Date().toISOString().split('T')[0]
    };
    setDietLogs((prev) => [newD, ...prev]);
  };

  const updateDietLog = (log: DietLog) => {
    setDietLogs((prev) => prev.map((d) => (d.id === log.id ? log : d)));
  };

  const deleteDietLog = (id: number) => setDietLogs((prev) => prev.filter((d) => d.id !== id));

  // Visual Tasks
  const insertVisualTask = (
    title: string,
    startDate: string,
    startTime: string,
    endDate: string,
    endTime: string,
    startHour: number,
    durationHours: number,
    func: string,
    tag: string,
    checklistRaw: string
  ) => {
    if (!title.trim()) return;
    const newV: VisualTask = {
      id: Date.now(),
      title,
      startDate: startDate || new Date().toISOString().split('T')[0],
      startTime,
      endDate: endDate || new Date().toISOString().split('T')[0],
      endTime,
      startHour,
      durationHours,
      function: func,
      tag,
      checklistRaw
    };
    setVisualTasks((prev) => [newV, ...prev]);
  };

  const updateVisualTask = (task: VisualTask) => {
    setVisualTasks((prev) => prev.map((vt) => (vt.id === task.id ? task : vt)));
  };

  const deleteVisualTask = (id: number) => setVisualTasks((prev) => prev.filter((vt) => vt.id !== id));

  // Portfolio Items
  const addPortfolioItem = (title: string, description: string, iconType: 'design' | 'photo' | 'integration' | 'manufacturing') => {
    if (!title.trim()) return;
    const newP: PortfolioItem = { id: Date.now(), title, description, iconType };
    setPortfolioItems((prev) => [...prev, newP]);
  };

  const deletePortfolioItem = (id: number) => setPortfolioItems((prev) => prev.filter((p) => p.id !== id));

  return (
    <AppContext.Provider
      value={{
        userAccount,
        acceptTermsAndCreateAccount,
        updateUserAccount,
        toggleTheme,
        updateFinanceMode,
        logoutUserAccount,
        setupOnboardingMode,

        activeModule,
        setActiveModule,
        selectedFocoVestTab,
        setSelectedFocoVestTab,
        selectedRitVidaTab,
        setSelectedRitVidaTab,
        selectedMeiTab,
        setSelectedMeiTab,

        subjects,
        toggleSubjectStep,
        addNewSubject,
        updateSubject,
        deleteSubject,

        flashcards,
        addNewFlashcard,
        answerFlashcard,
        updateFlashcard,
        deleteFlashcard,

        simulados,
        addNewSimulado,
        updateSimulado,
        deleteSimulado,

        videos,
        toggleVideoCompleted,
        addNewVideo,
        updateVideo,
        deleteVideo,
        resetVideosToEnemCronograma,

        chatMessages,
        isChatLoading,
        sendChatMessage,
        clearChat,

        essays,
        isEssayCorrecting,
        correctEssay,
        deleteEssay,

        errors,
        addNewError,
        updateError,
        deleteError,

        pomodoroSecondsLeft,
        pomodoroIsRunning,
        pomodoroBlockMinutes,
        selectPomodoroBlock,
        startPomodoro,
        pausePomodoro,
        resetPomodoro,

        hoursList,
        addWorkedHours,
        deleteHour,
        updateHour,

        ritVidaFinances,
        addRitVidaTransaction,
        updateRitVidaTransaction,
        deleteRitVidaTransaction,

        projects,
        addProject,
        updateProjectProgress,
        updateProject,
        deleteProject,

        schedules,
        addSchedule,
        updateSchedule,
        deleteSchedule,

        customCronogramaItems,
        addCustomCronogramaItem,
        toggleCustomCronogramaItem,
        updateCustomCronogramaItem,
        importCustomCronogramaItems,
        deleteCustomCronogramaItem,
        clearAllCustomCronogramaItems,

        meiTransactions,
        addMeiTransaction,
        updateMeiTransaction,
        deleteMeiTransaction,
        clearMeiTransactions,

        meiInvoices,
        addMeiInvoice,
        updateMeiInvoice,
        deleteMeiInvoice,
        clearMeiInvoices,

        meiConfig,
        saveMeiConfig,
        restoreExampleMeiData,

        gymWorkouts,
        insertGymWorkout,
        updateGymWorkout,
        deleteGymWorkout,
        toggleGymWorkoutStatus,

        dietLogs,
        insertDietLog,
        updateDietLog,
        deleteDietLog,

        visualTasks,
        insertVisualTask,
        updateVisualTask,
        deleteVisualTask,

        portfolioItems,
        addPortfolioItem,
        deletePortfolioItem
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
};
