import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Folder, Plus, Trash2, Camera, Cpu, Layers, Palette } from 'lucide-react';

export const RitVidaPortfolio: React.FC = () => {
  const { portfolioItems, addPortfolioItem, deletePortfolioItem } = useApp();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [iconType, setIconType] = useState<'design' | 'photo' | 'integration' | 'manufacturing'>('design');

  const handleAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !description.trim()) return;
    addPortfolioItem(title, description, iconType);
    setTitle('');
    setDescription('');
  };

  const renderIcon = (type: string) => {
    switch (type) {
      case 'design':
        return <Palette className="w-5 h-5 text-amber-400" />;
      case 'photo':
        return <Camera className="w-5 h-5 text-indigo-400" />;
      case 'integration':
        return <Cpu className="w-5 h-5 text-emerald-400" />;
      case 'manufacturing':
        return <Layers className="w-5 h-5 text-purple-400" />;
      default:
        return <Folder className="w-5 h-5 text-indigo-400" />;
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
          <Folder className="w-5 h-5 text-indigo-400" />
          Vitrine de Trabalhos e Portfólio
        </h2>
        <p className="text-xs text-slate-400 mt-0.5">
          Exiba seus trabalhos de animação 3D, modelagem, software e engenharia.
        </p>
      </div>

      {/* Add Portfolio Form */}
      <form onSubmit={handleAdd} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 space-y-3 shadow-md">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <input
            type="text"
            placeholder="Título do trabalho..."
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
            required
          />

          <select
            value={iconType}
            onChange={(e) => setIconType(e.target.value as any)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          >
            <option value="design">Animação & Design 🎨</option>
            <option value="photo">Modelagem 3D & Cenários 📷</option>
            <option value="integration">Software & Plataforma 💻</option>
            <option value="manufacturing">Engenharia & Protótipo ⚙️</option>
          </select>

          <input
            type="text"
            placeholder="Descrição curta do trabalho..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
            required
          />
        </div>

        <button
          type="submit"
          className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs transition"
        >
          Adicionar ao Portfólio
        </button>
      </form>

      {/* Items Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {portfolioItems.map((item) => (
          <div key={item.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-md">
            <div className="flex items-start justify-between gap-3">
              <div className="flex items-center gap-3">
                <div className="p-2 bg-slate-950 border border-slate-800 rounded-xl">
                  {renderIcon(item.iconType)}
                </div>
                <div>
                  <h3 className="text-base font-bold text-slate-100">{item.title}</h3>
                  <p className="text-xs text-slate-400 mt-1 leading-relaxed">{item.description}</p>
                </div>
              </div>

              <button
                onClick={() => deletePortfolioItem(item.id)}
                className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
