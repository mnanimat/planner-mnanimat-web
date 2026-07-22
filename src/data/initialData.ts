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
  GymWorkout,
  DietLog,
  VisualTask,
  PortfolioItem
} from '../types';

export const initialSubjects: StudySubject[] = [
  {
    id: 1,
    title: 'Matemática ENEM: Função Quadrática e Gráficos',
    category: 'Matemática',
    stepAula: true,
    stepResumo: true,
    stepAutoexplicacao: true,
    stepExercicios: true,
    stepCadernoErros: false,
    stepRevisao: false,
    stepSimulado: false
  },
  {
    id: 2,
    title: 'Biologia ENEM: Citologia e Organelas Celulares',
    category: 'Biologia',
    stepAula: true,
    stepResumo: true,
    stepAutoexplicacao: false,
    stepExercicios: false,
    stepCadernoErros: false,
    stepRevisao: false,
    stepSimulado: false
  },
  {
    id: 3,
    title: 'Física ITA: Leis de Newton em Referenciais Não Inerciais',
    category: 'Física',
    stepAula: true,
    stepResumo: false,
    stepAutoexplicacao: false,
    stepExercicios: false,
    stepCadernoErros: false,
    stepRevisao: false,
    stepSimulado: false
  },
  {
    id: 4,
    title: 'Química ITA: Físico-Química e Equilíbrio Iônico',
    category: 'Química',
    stepAula: false,
    stepResumo: false,
    stepAutoexplicacao: false,
    stepExercicios: false,
    stepCadernoErros: false,
    stepRevisao: false,
    stepSimulado: false
  },
  {
    id: 5,
    title: 'Redação ENEM: Estrutura da Proposta de Intervenção',
    category: 'Redação',
    stepAula: true,
    stepResumo: true,
    stepAutoexplicacao: true,
    stepExercicios: true,
    stepCadernoErros: true,
    stepRevisao: true,
    stepSimulado: false
  }
];

export const initialFlashcards: Flashcard[] = [
  {
    id: 1,
    question: 'Qual a fórmula das raízes de uma Equação do 2º Grau (Fórmula de Bhaskara)?',
    answer: 'x = (-b ± √(b² - 4ac)) / (2a)',
    intervalDays: 1,
    easeFactor: 2.5,
    repetitions: 1,
    dueDate: Date.now() - 10000 // due now
  },
  {
    id: 2,
    question: 'O que é a Primeira Lei de Mendel na Genética?',
    answer: 'Lei da Segregação dos Fatores: cada caráter é determinado por um par de fatores que se separam na formação dos gametas.',
    intervalDays: 3,
    easeFactor: 2.4,
    repetitions: 2,
    dueDate: Date.now() + 86400000 * 2 // future
  },
  {
    id: 3,
    question: 'Qual a Segunda Lei de Newton (Princípio Fundamental da Dinâmica)?',
    answer: 'F_resultante = m * a (a força resultante sobre um corpo é o produto de sua massa pela aceleração).',
    intervalDays: 1,
    easeFactor: 2.5,
    repetitions: 0,
    dueDate: Date.now() - 5000 // due now
  }
];

export const initialSimulados: Simulado[] = [
  {
    id: 1,
    subject: 'ENEM 2025 Dia 1 (Linguagens + Humanas + Redação)',
    totalQuestions: 90,
    correctAnswers: 72,
    durationMinutes: 300,
    timestamp: Date.now() - 86400000 * 10
  },
  {
    id: 2,
    subject: 'ENEM 2025 Dia 2 (Matemática + Natureza)',
    totalQuestions: 90,
    correctAnswers: 68,
    durationMinutes: 290,
    timestamp: Date.now() - 86400000 * 3
  }
];

export const initialVideos: VideoAula[] = [
  {
    id: 1,
    title: 'Redação ENEM: Como fazer uma Introdução Nota 1000',
    category: 'Redação',
    youtubeIdOrUrl: 'https://www.youtube.com/watch?v=Lp7eNOn_E6E',
    isCompleted: true
  },
  {
    id: 2,
    title: 'Matemática ENEM: Introdução à Função Quadrática',
    category: 'Matemática',
    youtubeIdOrUrl: 'https://www.youtube.com/watch?v=0hWcoA7GfGk',
    isCompleted: true
  },
  {
    id: 3,
    title: 'Física ENEM: Cinemática Escalar e Conceitos Iniciais',
    category: 'Física',
    youtubeIdOrUrl: 'https://www.youtube.com/watch?v=Vp2C-Z8wHTo',
    isCompleted: false
  },
  {
    id: 4,
    title: 'Biologia ENEM: Genética Mendeliana e Cruzamentos Básicos',
    category: 'Biologia',
    youtubeIdOrUrl: 'https://www.youtube.com/watch?v=P_Yw8vW71Z8',
    isCompleted: false
  },
  {
    id: 5,
    title: 'História ENEM: Revolução Francesa Resumo Completo',
    category: 'História',
    youtubeIdOrUrl: 'https://www.youtube.com/watch?v=I8q0S_L4_vU',
    isCompleted: false
  }
];

export const initialCadernoErros: CadernoErro[] = [
  {
    id: 1,
    subject: 'Física - Mecânica',
    questionText: 'Questão sobre cálculo de força centrípeta em curva inclinada sem atrito.',
    errorReason: 'Esqueci de decompor a força normal N nas componentes vertical e horizontal.',
    correctConcept: 'Em curva inclinada sem atrito, Fc = N * sin(θ) e m*g = N * cos(θ), logo tan(θ) = v² / (rg).',
    timestamp: Date.now() - 86400000 * 5
  }
];

export const initialEssays: Essay[] = [
  {
    id: 1,
    title: 'Caminhos para combater a desigualdade no acesso às tecnologias educacionais no Brasil',
    text: 'Na obra "Utopia", de Thomas More, é retratada uma sociedade perfeita, livre de mazelas sociais. No entanto, ao analisar a realidade brasileira contemporânea, percebe-se que a democratização do acesso às tecnologias de ensino permanece distante desse ideal, impulsionada pela negligência governamental e pela disparidade socioeconômica entre as regiões do país...',
    feedback: `Análise do Corretor Oficial ENEM:
Comp. 1: 180/200 (Excelente norma culta, pequeno desvio de pontuação).
Comp. 2: 200/200 (Tema compreendido com repertório legítimo e produtivo).
Comp. 3: 180/200 (Argumentação consistente e bem articulada).
Comp. 4: 180/200 (Ótimo uso dos conectivos interparágrafos).
Comp. 5: 180/200 (Proposta de intervenção completa com Agente, Ação, Meio e Efeito).

NOTA FINAL ESTIMADA: 920 / 1000
Dica: Detalhe ainda mais o agente governamental informando a pasta responsável (ex: Ministério da Educação).`,
    timestamp: Date.now() - 86400000 * 2
  }
];

export const initialRitVidaHours: RitVidaHour[] = [
  { id: 1, functionName: 'Estudante', hours: 4.5, dateString: '2026-07-20' },
  { id: 2, functionName: 'Trabalho', hours: 6.0, dateString: '2026-07-20' },
  { id: 3, functionName: 'Saúde', hours: 1.5, dateString: '2026-07-20' },
  { id: 4, functionName: 'Administrativo', hours: 1.0, dateString: '2026-07-20' },
  { id: 5, functionName: 'Estudante', hours: 5.0, dateString: '2026-07-21' },
  { id: 6, functionName: 'Trabalho', hours: 5.5, dateString: '2026-07-21' },
  { id: 7, functionName: 'Saúde', hours: 2.0, dateString: '2026-07-21' }
];

export const initialRitVidaFinances: RitVidaFinance[] = [
  { id: 1, description: 'Serviço de Animação 3D Freelance', amount: 3200, type: 'REVENUE', dateString: '2026-07-10' },
  { id: 2, description: 'Supermercado e Alimentação', amount: 450, type: 'EXPENSE', dateString: '2026-07-12' },
  { id: 3, description: 'Assinatura Plataforma de Estudos', amount: 120, type: 'EXPENSE', dateString: '2026-07-15' },
  { id: 4, description: 'Venda de Projeto de Software', amount: 1800, type: 'REVENUE', dateString: '2026-07-18' }
];

export const initialRitVidaProjects: RitVidaProject[] = [
  { id: 1, name: 'Animação 3D Curta-Metragem Blender', progressPercentage: 75, targetDateString: '2026-08-30', isCompleted: false },
  { id: 2, name: 'Revisão Intensiva Módulos ENEM', progressPercentage: 60, targetDateString: '2026-10-15', isCompleted: false },
  { id: 3, name: 'Modelagem de Cenários Low-Poly', progressPercentage: 100, targetDateString: '2026-07-01', isCompleted: true }
];

export const initialSchedules: StudySchedule[] = [
  { id: 1, dayOfWeek: 'Segunda', durationMinutes: 120, subjectTitle: 'Matemática (Funções e Geometria)' },
  { id: 2, dayOfWeek: 'Segunda', durationMinutes: 90, subjectTitle: 'Redação (Prática de Texto)' },
  { id: 3, dayOfWeek: 'Terça', durationMinutes: 120, subjectTitle: 'Física (Mecânica e Termologia)' },
  { id: 4, dayOfWeek: 'Terça', durationMinutes: 90, subjectTitle: 'Biologia (Citologia e Genética)' },
  { id: 5, dayOfWeek: 'Quarta', durationMinutes: 120, subjectTitle: 'Química (Físico-Química)' },
  { id: 6, dayOfWeek: 'Quinta', durationMinutes: 120, subjectTitle: 'História e Geografia (Humanas)' },
  { id: 7, dayOfWeek: 'Sexta', durationMinutes: 180, subjectTitle: 'Simulado de Prova / Resolução de Questões' }
];

export const initialCustomCronogramaItems: CustomCronogramaItem[] = [
  { id: 1, content: 'Resolver 30 exercícios de Geometria Plana', week: 'Semana 1', dateInterval: '15/07 a 21/07', isCompleted: true },
  { id: 2, content: 'Escrever redação sobre Inteligência Artificial na Educação', week: 'Semana 1', dateInterval: '15/07 a 21/07', isCompleted: true },
  { id: 3, content: 'Revisar flashcards de Química Inorgânica no Anki', week: 'Semana 2', dateInterval: '22/07 a 28/07', isCompleted: false }
];

export const initialMeiTransactions: MeiTransaction[] = [
  { id: 1, dateString: '2026-07-10', description: 'Desenvolvimento de App Mobile', amount: 4200, category: 'Serviços', accountType: 'PJ', transactionType: 'RECEITA', hasInvoice: true, status: 'Pago', notes: 'Cliente Estúdio Criativo' },
  { id: 2, dateString: '2026-07-12', description: 'Venda de Placa Eletrônica Articulada', amount: 1250, category: 'Vendas', accountType: 'PJ', transactionType: 'RECEITA', hasInvoice: false, status: 'Pago', notes: 'Venda direta' },
  { id: 3, dateString: '2026-07-13', description: 'Hospedagem Cloud AWS e Servidores', amount: 180, category: 'Serviços de TI', accountType: 'PJ', transactionType: 'DESPESA', hasInvoice: true, status: 'Pago', notes: 'Infraestrutura' },
  { id: 4, dateString: '2026-07-15', description: 'DAS MEI Julho 2026', amount: 81.9, category: 'Impostos', accountType: 'PJ', transactionType: 'DESPESA', hasInvoice: true, status: 'Pago', notes: 'Guia Simples Nacional' },
  { id: 5, dateString: '2026-07-14', description: 'Supermercado Semanal Família', amount: 350, category: 'Alimentação', accountType: 'PESSOAL', transactionType: 'DESPESA', hasInvoice: false, status: 'Pago', notes: 'Gastos de casa' },
  { id: 6, dateString: '2026-07-05', description: 'Salário Emprego CLT / Pró-labore', amount: 3800, category: 'Salário', accountType: 'PESSOAL', transactionType: 'RECEITA', hasInvoice: false, status: 'Pago', notes: 'Renda mensal fixa' },
  { id: 7, dateString: '2026-07-18', description: 'Venda em Dinheiro (Acessórios e Serviços)', amount: 450, category: 'Vendas', accountType: 'DINHEIRO', transactionType: 'RECEITA', hasInvoice: false, status: 'Pago', notes: 'Pagamento em espécie / nota no balcão' },
  { id: 8, dateString: '2026-07-19', description: 'Compra de Material de Escritório (Dinheiro)', amount: 65, category: 'Insumos', accountType: 'DINHEIRO', transactionType: 'DESPESA', hasInvoice: false, status: 'Pago', notes: 'Papelaria local em notas físicas' }
];

export const initialMeiInvoices: MeiInvoice[] = [
  { id: 1, clientName: 'Estúdio Criativo LTDA', serviceDescription: 'Modelagem de Cenários 3D', amount: 1200, dueDate: '2026-07-20', isIssued: true, isSent: true, isReceived: false, invoiceLink: 'https://nfe.prefeitura.sp.gov.br/exemplo123' },
  { id: 2, clientName: 'Autopeças Silva', serviceDescription: 'Consultoria de Engenharia Automotiva', amount: 2500, dueDate: '2026-07-25', isIssued: true, isSent: false, isReceived: false, invoiceLink: '' },
  { id: 3, clientName: 'Editora Educacional', serviceDescription: 'Criação de Banco de Questões de Física', amount: 1500, dueDate: '2026-07-30', isIssued: false, isSent: false, isReceived: false, invoiceLink: '' }
];

export const initialMeiConfig: MeiConfig = {
  id: 1,
  annualLimit: 81000,
  monthlyDas: 81.9,
  monthlySavingsGoal: 500,
  emergencyFundGoal: 6000,
  monthlyMeiRevenueGoal: 6750
};

export const initialGymWorkouts: GymWorkout[] = [
  { id: 1, exercise: 'Supino Reto com Barra', sets: 4, reps: 10, weightKg: 70, dateString: '2026-07-21', isCompleted: true },
  { id: 2, exercise: 'Agachamento Livre', sets: 4, reps: 8, weightKg: 90, dateString: '2026-07-21', isCompleted: true },
  { id: 3, exercise: 'Puxada Alta Frente', sets: 3, reps: 12, weightKg: 55, dateString: '2026-07-22', isCompleted: false },
  { id: 4, exercise: 'Desenvolvimento com Halteres', sets: 3, reps: 10, weightKg: 20, dateString: '2026-07-22', isCompleted: false }
];

export const initialDietLogs: DietLog[] = [
  { id: 1, mealType: 'Café da Manhã', foodName: 'Ovos mexidos (3 un) + Pão Integral + Café preto', caloriesKcal: 420, waterIntakeMl: 500, dateString: '2026-07-22' },
  { id: 2, mealType: 'Almoço', foodName: 'Arroz integral, Feijão, Peito de Frango grelhado e Salada', caloriesKcal: 650, waterIntakeMl: 600, dateString: '2026-07-22' },
  { id: 3, mealType: 'Café da Tarde', foodName: 'Whey Protein com Banana e Aveia', caloriesKcal: 320, waterIntakeMl: 400, dateString: '2026-07-22' }
];

export const initialVisualTasks: VisualTask[] = [
  {
    id: 1,
    title: 'Bloco de Estudo Intensivo de Matemática',
    startDate: '2026-07-22',
    startTime: '08:00',
    endDate: '2026-07-22',
    endTime: '10:30',
    startHour: 8,
    durationHours: 2,
    function: 'Estudante',
    tag: 'Foco Total',
    checklistRaw: 'Resumo em vídeo:true|Exercícios da apostila:true|Flashcards no Anki:false'
  },
  {
    id: 2,
    title: 'Desenvolvimento e Entrega de Job 3D',
    startDate: '2026-07-22',
    startTime: '13:00',
    endDate: '2026-07-22',
    endTime: '17:00',
    startHour: 13,
    durationHours: 4,
    function: 'Trabalho',
    tag: 'Projeto MEI',
    checklistRaw: 'Renderização das cenas:true|Envio de nota fiscal:false'
  }
];

export const initialPortfolioItems: PortfolioItem[] = [
  { id: 1, title: 'Animação 3D de Personagem', description: 'Ciclo de caminhada expressivo e animação de diálogo com sincronia labial avançada criada no Blender.', iconType: 'design' },
  { id: 2, title: 'Modelagem de Cenários 3D', description: 'Modelagem de cenários low-poly de alta fidelidade e mapeamento UV detalhado para ambientes virtuais.', iconType: 'photo' },
  { id: 3, title: 'Plataforma Educacional Integrada', description: 'Tecnologia educacional com exercícios dinâmicos, gamificação e trilhas de estudo personalizadas.', iconType: 'integration' },
  { id: 4, title: 'Protótipo Físico Articulado', description: 'Construção e montagem de maquete física automatizada com motores servo e controle por microcontrolador.', iconType: 'manufacturing' }
];

export const countdownEventsList = [
  { title: 'ITA 1ª Fase ✈️', date: '27/09/2026', weeksLeft: 10, daysLeft: 74 },
  { title: 'ITA 2ª Fase 🎯', date: '20/10/2026', weeksLeft: 14, daysLeft: 97 },
  { title: 'ENEM 1º Dia 📝', date: '08/11/2026', weeksLeft: 16, daysLeft: 116 },
  { title: 'ENEM 2º Dia 📐', date: '15/11/2026', weeksLeft: 17, daysLeft: 123 }
];

export const cronogramaEnemList = [
  { exam: 'ENEM', subject: 'Mat: Razão, Proporção e Escalas', watchLink: 'https://www.youtube.com/results?search_query=razao+proporcao+escala+enem', week: 'Semana 1', period: '15/07 a 21/07' },
  { exam: 'ENEM', subject: 'Bio: Ecologia (Cadeias Alimentares e Desequilíbrios)', watchLink: 'https://www.youtube.com/results?search_query=ecologia+enem', week: 'Semana 1', period: '15/07 a 21/07' },
  { exam: 'ENEM', subject: 'Quí: Meio Ambiente e Problemas Ambientais', watchLink: 'https://www.youtube.com/results?search_query=quimica+ambiental+enem', week: 'Semana 2', period: '22/07 a 28/07' },
  { exam: 'ENEM', subject: 'Mat: Porcentagem e Juros Simples/Compostos', watchLink: 'https://www.youtube.com/results?search_query=porcentagem+enem', week: 'Semana 2', period: '22/07 a 28/07' },
  { exam: 'ENEM', subject: 'His: Brasil Colônia (Economia e Escravidão)', watchLink: 'https://www.youtube.com/results?search_query=brasil+colonia+enem', week: 'Semana 3', period: '29/07 a 04/08' },
  { exam: 'ENEM', subject: 'Mat: Estatística (Média, Moda, Mediana) e Gráficos', watchLink: 'https://www.youtube.com/results?search_query=estatistica+enem', week: 'Semana 3', period: '29/07 a 04/08' },
  { exam: 'ENEM', subject: 'Fís: Mecânica (Cinemática, Leis de Newton e Energia)', watchLink: 'https://www.youtube.com/results?search_query=mecanica+enem', week: 'Semana 4', period: '05/08 a 11/08' },
  { exam: 'ENEM', subject: 'Bio: Citologia (Organelas e Transporte na Membrana)', watchLink: 'https://www.youtube.com/results?search_query=citologia+enem', week: 'Semana 5', period: '12/08 a 18/08' },
  { exam: 'ENEM', subject: 'Red: Estrutura Dissertativa e Proposta de Intervenção', watchLink: 'https://www.youtube.com/results?search_query=redacao+enem', week: 'Semana 8', period: '02/09 a 08/09' }
];

export const cronogramaItaList = [
  { exam: 'ITA', subject: 'Mat: Álgebra (Princípio de Indução Finita)', watchLink: 'https://www.youtube.com/results?search_query=principio+inducao+finita+ita', week: 'Semana 1', period: '15/07 a 21/07' },
  { exam: 'ITA', subject: 'Fís: Mecânica Vetorial e Lançamentos com Resistência do Ar', watchLink: 'https://www.youtube.com/results?search_query=lancamento+obliquo+ita', week: 'Semana 1', period: '15/07 a 21/07' },
  { exam: 'ITA', subject: 'Quí: Química Quântica e Orbitais', watchLink: 'https://www.youtube.com/results?search_query=quimica+quantica+ita', week: 'Semana 1', period: '15/07 a 21/07' },
  { exam: 'ITA', subject: 'Mat: Números Complexos (Forma Trigonométrica e Moivre)', watchLink: 'https://www.youtube.com/results?search_query=numeros+complexos+ita', week: 'Semana 5', period: '12/08 a 18/08' }
];
