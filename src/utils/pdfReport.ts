import { jsPDF } from 'jspdf';
import { RitVidaHour, StudySubject, CustomCronogramaItem } from '../types';

export function generatePdfReport(
  hoursList: RitVidaHour[],
  subjectsList: StudySubject[],
  customCronogramaList: CustomCronogramaItem[]
) {
  const doc = new jsPDF();

  // Header
  doc.setFillColor(30, 41, 59); // Dark blue / slate
  doc.rect(0, 0, 210, 35, 'F');

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(20);
  doc.setFont('helvetica', 'bold');
  doc.text('PLANNER MNANIMAT', 15, 18);

  doc.setFontSize(11);
  doc.setFont('helvetica', 'normal');
  doc.text('Relatório Executivo Consolidado - Estudos & Rotina', 15, 27);

  const dateStr = new Date().toLocaleDateString('pt-BR');
  doc.setFontSize(9);
  doc.text(`Gerado em: ${dateStr}`, 150, 27);

  let y = 45;

  // Section 1: Resumo de Horas por Função
  doc.setTextColor(30, 41, 59);
  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.text('1. Distribuição de Horas por Função', 15, y);
  y += 8;

  const hoursByFunction: Record<string, number> = {};
  hoursList.forEach((h) => {
    hoursByFunction[h.functionName] = (hoursByFunction[h.functionName] || 0) + h.hours;
  });

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  const entries = Object.entries(hoursByFunction);
  if (entries.length === 0) {
    doc.text('Nenhuma hora registrada ainda.', 20, y);
    y += 8;
  } else {
    entries.forEach(([func, total]) => {
      doc.text(`• ${func}: ${total.toFixed(1)} horas`, 20, y);
      y += 6;
    });
  }

  y += 6;

  // Section 2: Progresso das Trilhas de Estudo
  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.text('2. Trilhas e Progresso nos Estudos', 15, y);
  y += 8;

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  if (subjectsList.length === 0) {
    doc.text('Nenhum assunto cadastrado nas trilhas.', 20, y);
    y += 8;
  } else {
    subjectsList.slice(0, 10).forEach((sub) => {
      const steps = [
        sub.stepAula,
        sub.stepResumo,
        sub.stepAutoexplicacao,
        sub.stepExercicios,
        sub.stepCadernoErros,
        sub.stepRevisao,
        sub.stepSimulado
      ];
      const completedCount = steps.filter(Boolean).length;
      const pct = Math.round((completedCount * 100) / steps.length);

      doc.text(`• [${sub.category}] ${sub.title} - ${pct}% concluído (${completedCount}/7 etapas)`, 20, y);
      y += 6;
      if (y > 270) {
        doc.addPage();
        y = 20;
      }
    });
  }

  y += 6;

  // Section 3: Cronograma Personalizado
  if (y > 250) {
    doc.addPage();
    y = 20;
  }

  doc.setFontSize(14);
  doc.setFont('helvetica', 'bold');
  doc.text('3. Cronograma e Metas Ativas', 15, y);
  y += 8;

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  if (customCronogramaList.length === 0) {
    doc.text('Nenhuma atividade personalizada no cronograma.', 20, y);
    y += 8;
  } else {
    customCronogramaList.slice(0, 8).forEach((item) => {
      const statusStr = item.isCompleted ? '[Concluída]' : '[Pendente]';
      doc.text(`• ${statusStr} ${item.content} (${item.week} - ${item.dateInterval})`, 20, y);
      y += 6;
      if (y > 270) {
        doc.addPage();
        y = 20;
      }
    });
  }

  // Footer
  doc.setFontSize(8);
  doc.setTextColor(100, 116, 139);
  doc.text('Planner MNAnimat • Processamento Local 100% Seguro & Privado', 15, 285);

  doc.save(`Relatorio_MNAnimat_${dateStr.replace(/\//g, '-')}.pdf`);
}
