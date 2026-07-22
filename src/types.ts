export interface StudySubject {
  id: number;
  title: string;
  category: string;
  stepAula: boolean;
  stepResumo: boolean;
  stepAutoexplicacao: boolean;
  stepExercicios: boolean;
  stepCadernoErros: boolean;
  stepRevisao: boolean;
  stepSimulado: boolean;
}

export interface Flashcard {
  id: number;
  question: string;
  answer: string;
  intervalDays: number;
  easeFactor: number;
  repetitions: number;
  dueDate: number; // timestamp ms
}

export interface Simulado {
  id: number;
  subject: string;
  totalQuestions: number;
  correctAnswers: number;
  durationMinutes: number;
  timestamp: number;
}

export interface VideoAula {
  id: number;
  title: string;
  category: string;
  youtubeIdOrUrl: string;
  isCompleted: boolean;
}

export interface CadernoErro {
  id: number;
  subject: string;
  questionText: string;
  errorReason: string;
  correctConcept: string;
  timestamp: number;
}

export interface Essay {
  id: number;
  title: string;
  text: string;
  feedback: string;
  timestamp: number;
}

export interface RitVidaHour {
  id: number;
  functionName: string; // e.g. "Trabalho", "Saúde", "Estudante", "Administrativo"
  hours: number;
  dateString: string; // yyyy-MM-dd
}

export interface RitVidaFinance {
  id: number;
  description: string;
  amount: number;
  type: 'REVENUE' | 'EXPENSE';
  dateString: string;
}

export interface RitVidaProject {
  id: number;
  name: string;
  progressPercentage: number;
  targetDateString: string;
  isCompleted: boolean;
}

export interface StudySchedule {
  id: number;
  dayOfWeek: string; // "Segunda", "Terça", etc.
  durationMinutes: number;
  subjectTitle: string;
}

export interface CustomCronogramaItem {
  id: number;
  content: string;
  week: string;
  dateInterval: string;
  isCompleted: boolean;
  targetSchedule?: 'ENEM' | 'ITA' | 'CUSTOM';
}

export interface MeiTransaction {
  id: number;
  dateString: string;
  description: string;
  amount: number;
  category: string;
  accountType: 'PJ' | 'PESSOAL' | 'DINHEIRO';
  transactionType: 'RECEITA' | 'DESPESA';
  hasInvoice: boolean;
  status: 'Pago' | 'Pendente';
  notes: string;
}

export interface MeiInvoice {
  id: number;
  clientName: string;
  serviceDescription: string;
  amount: number;
  dueDate: string;
  isIssued: boolean;
  isSent: boolean;
  isReceived: boolean;
  invoiceLink: string;
}

export interface MeiConfig {
  id: number;
  annualLimit: number; // default 81000
  monthlyDas: number; // default 81.9
  monthlySavingsGoal: number; // default 500
  emergencyFundGoal: number; // default 6000
  monthlyMeiRevenueGoal: number; // default 6750
}

export interface UserAccount {
  id: number;
  name: string;
  email: string;
  passwordHash: string;
  termsAccepted: boolean;
  termsAcceptedTimestamp: number;
  isDarkTheme: boolean;
  financeMode: 'MEI + Pessoal' | 'Só MEI' | 'Só Pessoal';
}

export interface GymWorkout {
  id: number;
  exercise: string;
  sets: number;
  reps: number;
  weightKg: number;
  dateString: string;
  isCompleted: boolean;
}

export interface DietLog {
  id: number;
  mealType: string; // "Café da Manhã", "Almoço", "Café da Tarde", "Jantar", "Lanches"
  foodName: string;
  caloriesKcal: number;
  waterIntakeMl: number;
  dateString: string;
}

export interface VisualTask {
  id: number;
  title: string;
  startDate: string; // yyyy-MM-dd
  startTime: string; // HH:mm
  endDate: string; // yyyy-MM-dd
  endTime: string; // HH:mm
  startHour: number; // 0 to 23
  durationHours: number; // 1 to 24
  function: string;
  tag: string;
  checklistRaw: string; // "item1:false|item2:true"
  status?: 'A Fazer' | 'Em Progresso' | 'Concluído';
}

export interface ChatMessage {
  id: string;
  sender: 'USER' | 'AI';
  text: string;
  timestamp: number;
}

export interface PortfolioItem {
  id: number;
  title: string;
  description: string;
  iconType: 'design' | 'photo' | 'integration' | 'manufacturing';
}
