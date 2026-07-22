import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { MeiInvoice } from '../../types';
import { FileText, Plus, Trash2, ExternalLink, CheckCircle, Clock } from 'lucide-react';

export const MeiInvoices: React.FC = () => {
  const { meiInvoices, addMeiInvoice, updateMeiInvoice, deleteMeiInvoice } = useApp();

  const [clientName, setClientName] = useState('');
  const [serviceDescription, setServiceDescription] = useState('');
  const [amount, setAmount] = useState(1500);
  const [dueDate, setDueDate] = useState('2026-07-30');
  const [invoiceLink, setInvoiceLink] = useState('');

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!clientName.trim() || amount <= 0) return;
    addMeiInvoice(
      clientName,
      serviceDescription,
      Number(amount),
      dueDate,
      true,
      true,
      false,
      invoiceLink
    );
    setClientName('');
    setServiceDescription('');
    setInvoiceLink('');
  };

  const toggleInvoiceReceived = (inv: MeiInvoice) => {
    updateMeiInvoice({ ...inv, isReceived: !inv.isReceived });
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <FileText className="w-5 h-5 text-indigo-400" />
          Gestão de Notas Fiscais (NFe MEI)
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Acompanhe notas de serviço emitidas, links de validação e status de pagamento do cliente.
        </p>
      </div>

      {/* Add Invoice Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-4 shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Nome do Cliente / Empresa</label>
            <input
              type="text"
              placeholder="Ex: Estúdio Criativo LTDA"
              value={clientName}
              onChange={(e) => setClientName(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Descrição do Serviço</label>
            <input
              type="text"
              placeholder="Ex: Modelagem 3D e Animação"
              value={serviceDescription}
              onChange={(e) => setServiceDescription(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Valor da Nota (R$)</label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(Number(e.target.value))}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
              required
            />
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Data de Vencimento</label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
              required
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-400 mb-1">Link de Acesso à NFe (Opcional)</label>
            <input
              type="url"
              placeholder="https://nfe.prefeitura.sp.gov.br/..."
              value={invoiceLink}
              onChange={(e) => setInvoiceLink(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition shadow-lg shadow-indigo-600/20"
        >
          Cadastrar Nota Fiscal
        </button>
      </form>

      {/* Invoices List */}
      <div className="space-y-3">
        {meiInvoices.map((inv) => (
          <div key={inv.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-md">
            <div className="flex items-start justify-between gap-3">
              <div>
                <div className="flex items-center gap-2">
                  <span className={`text-[10px] font-extrabold uppercase px-2.5 py-0.5 rounded-full border ${
                    inv.isReceived
                      ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20'
                      : 'bg-amber-500/10 text-amber-400 border-amber-500/20'
                  }`}>
                    {inv.isReceived ? 'Recebido' : 'Pendente de Pagamento'}
                  </span>
                  <span className="text-xs text-slate-400">Vencimento: {inv.dueDate}</span>
                </div>

                <h3 className="text-base font-bold text-slate-100 mt-1">{inv.clientName}</h3>
                <p className="text-xs text-slate-300">{inv.serviceDescription}</p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-base font-extrabold text-emerald-400">R$ {inv.amount.toFixed(2)}</span>
                <button
                  onClick={() => deleteMeiInvoice(inv.id)}
                  className="text-slate-500 hover:text-red-400 p-1.5 rounded-lg hover:bg-slate-800 transition"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-slate-800 text-xs">
              <button
                onClick={() => toggleInvoiceReceived(inv)}
                className={`px-3 py-1.5 rounded-xl font-bold flex items-center gap-1.5 transition ${
                  inv.isReceived
                    ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                    : 'bg-slate-950 text-slate-400 hover:text-white border border-slate-800'
                }`}
              >
                <CheckCircle className="w-4 h-4" />
                <span>{inv.isReceived ? 'Marcar como Pendente' : 'Marcar como Recebido'}</span>
              </button>

              {inv.invoiceLink && (
                <a
                  href={inv.invoiceLink}
                  target="_blank"
                  rel="noreferrer"
                  className="text-indigo-400 hover:underline flex items-center gap-1 text-xs"
                >
                  <span>Ver PDF da NFe</span>
                  <ExternalLink className="w-3.5 h-3.5" />
                </a>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
