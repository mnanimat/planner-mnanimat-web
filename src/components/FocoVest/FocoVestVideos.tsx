import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { VideoAula } from '../../types';
import { Youtube, Plus, CheckCircle, Play, ExternalLink, Filter, Edit3, Trash2, X, FolderPlus } from 'lucide-react';

const INITIAL_CATEGORIES = [
  'Matemática',
  'Física',
  'Química',
  'Biologia',
  'Redação',
  'Linguagens & Português',
  'Literatura',
  'História',
  'Geografia',
  'Filosofia',
  'Sociologia',
  'Inglês',
  'Espanhol',
  'Desenho Técnico (ITA)',
  'Computação & Algoritmos (ITA)'
];

export const FocoVestVideos: React.FC = () => {
  const { videos, toggleVideoCompleted, addNewVideo, updateVideo, deleteVideo } = useApp();

  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('Matemática');
  const [url, setUrl] = useState('');
  const [selectedCatFilter, setSelectedCatFilter] = useState('TODAS');
  const [activePlayUrl, setActivePlayUrl] = useState<string | null>(null);

  // Dynamic Categories State
  const [categories, setCategories] = useState<string[]>(INITIAL_CATEGORIES);
  const [isAddCategoryOpen, setIsAddCategoryOpen] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState('');

  // Edit Video Modal State
  const [editingVideo, setEditingVideo] = useState<VideoAula | null>(null);

  const filteredVideos = videos.filter((v) => {
    if (selectedCatFilter === 'TODAS') return true;
    return v.category === selectedCatFilter;
  });

  const handleAddVideo = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !url.trim()) return;
    addNewVideo(title, category, url);
    setTitle('');
    setUrl('');
  };

  const handleCreateNewCategory = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = newCategoryName.trim();
    if (!trimmed) return;

    if (!categories.includes(trimmed)) {
      setCategories((prev) => [...prev, trimmed]);
    }
    setCategory(trimmed);
    setNewCategoryName('');
    setIsAddCategoryOpen(false);
  };

  const handleSaveEdit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!editingVideo || !editingVideo.title.trim()) return;
    updateVideo(editingVideo);
    setEditingVideo(null);
  };

  const getYoutubeEmbedUrl = (rawUrl: string) => {
    try {
      if (rawUrl.includes('watch?v=')) {
        const id = rawUrl.split('watch?v=')[1]?.split('&')[0];
        return `https://www.youtube.com/embed/${id}`;
      }
      if (rawUrl.includes('youtu.be/')) {
        const id = rawUrl.split('youtu.be/')[1]?.split('?')[0];
        return `https://www.youtube.com/embed/${id}`;
      }
      return rawUrl;
    } catch {
      return rawUrl;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Youtube className="w-5 h-5 text-red-500" />
            Vídeo-Aulas e Playlists Recomendadas
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Assista a aulas organizadas por matérias do ENEM e do ITA, ou adicione suas próprias matérias e aulas.
          </p>
        </div>

        <button
          onClick={() => setIsAddCategoryOpen(true)}
          className="px-3.5 py-2 bg-slate-900 hover:bg-slate-800 border border-slate-800 hover:border-slate-700 text-slate-200 font-bold rounded-xl text-xs flex items-center gap-2 transition self-start md:self-auto"
        >
          <FolderPlus className="w-4 h-4 text-amber-400" />
          <span>+ Nova Matéria</span>
        </button>
      </div>

      {/* Embedded Video Player */}
      {activePlayUrl && (
        <div className="bg-slate-900 border border-slate-800 rounded-3xl p-4 space-y-3 shadow-2xl relative">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-amber-400">Player de Vídeo Incorporado</span>
            <button
              onClick={() => setActivePlayUrl(null)}
              className="text-xs text-slate-400 hover:text-white px-2.5 py-1 rounded-lg bg-slate-800"
            >
              Fechar Player
            </button>
          </div>
          <div className="aspect-video w-full rounded-2xl overflow-hidden bg-black">
            <iframe
              src={getYoutubeEmbedUrl(activePlayUrl)}
              title="Vídeo-Aula"
              className="w-full h-full border-0"
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
              allowFullScreen
            />
          </div>
        </div>
      )}

      {/* Add New Video Form */}
      <form onSubmit={handleAddVideo} className="bg-slate-900 border border-slate-800 rounded-2xl p-5 space-y-3 shadow-md">
        <h3 className="text-xs font-bold text-slate-300 uppercase tracking-wider">Adicionar Nova Vídeo-Aula</h3>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
          <div className="md:col-span-2">
            <input
              type="text"
              placeholder="Título da vídeo-aula (ex: Física - Cinemática Vetorial)..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-red-500"
              required
            />
          </div>

          <div>
            <div className="flex items-center gap-1">
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-red-500"
              >
                {categories.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <input
              type="url"
              placeholder="URL do YouTube (https://...)"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2.5 text-xs text-slate-100 focus:outline-none focus:border-red-500"
              required
            />
          </div>
        </div>

        <button
          type="submit"
          className="w-full py-2.5 bg-red-600 hover:bg-red-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition shadow-lg shadow-red-600/20"
        >
          <Plus className="w-4 h-4" />
          Adicionar Vídeo-Aula
        </button>
      </form>

      {/* Filter by Category */}
      <div className="space-y-2">
        <div className="flex items-center justify-between">
          <span className="text-xs font-bold text-slate-400 flex items-center gap-1">
            <Filter className="w-3.5 h-3.5" /> Filtrar por Matéria (ENEM & ITA):
          </span>
          <span className="text-[10px] text-slate-500">{categories.length} Matérias Disponíveis</span>
        </div>

        <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-thin">
          <button
            onClick={() => setSelectedCatFilter('TODAS')}
            className={`px-3 py-1.5 rounded-xl text-xs font-bold shrink-0 transition ${
              selectedCatFilter === 'TODAS'
                ? 'bg-slate-100 text-slate-900 shadow-md'
                : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
            }`}
          >
            Todas ({videos.length})
          </button>

          {categories.map((cat) => {
            const count = videos.filter((v) => v.category === cat).length;
            return (
              <button
                key={cat}
                onClick={() => setSelectedCatFilter(cat)}
                className={`px-3 py-1.5 rounded-xl text-xs font-bold shrink-0 transition flex items-center gap-1.5 ${
                  selectedCatFilter === cat
                    ? 'bg-red-600 text-white shadow-md'
                    : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
                }`}
              >
                <span>{cat}</span>
                {count > 0 && <span className="opacity-75 text-[10px] font-extrabold bg-black/30 px-1.5 py-0.2 rounded-full">{count}</span>}
              </button>
            );
          })}
        </div>
      </div>

      {/* Videos List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {filteredVideos.map((vid) => (
          <div
            key={vid.id}
            className={`bg-slate-900 border rounded-2xl p-4 space-y-3 transition shadow-md ${
              vid.isCompleted ? 'border-emerald-500/30 bg-emerald-500/5' : 'border-slate-800 hover:border-slate-700'
            }`}
          >
            <div className="flex items-start justify-between gap-2">
              <div className="space-y-1">
                <span className="text-[10px] font-extrabold uppercase px-2.5 py-0.5 rounded-full bg-red-500/10 text-red-400 border border-red-500/20">
                  {vid.category}
                </span>
                <h3 className={`text-sm font-bold ${vid.isCompleted ? 'line-through text-slate-400' : 'text-slate-100'}`}>
                  {vid.title}
                </h3>
              </div>

              <div className="flex items-center gap-1 shrink-0">
                <button
                  onClick={() => setEditingVideo(vid)}
                  className="p-1.5 text-slate-400 hover:text-amber-400 rounded-xl hover:bg-slate-800 transition"
                  title="Editar recurso"
                >
                  <Edit3 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => deleteVideo(vid.id)}
                  className="p-1.5 text-slate-500 hover:text-red-400 rounded-xl hover:bg-slate-800 transition"
                  title="Excluir"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => toggleVideoCompleted(vid.id)}
                  className={`p-1.5 rounded-xl border transition ${
                    vid.isCompleted
                      ? 'bg-emerald-500 border-emerald-500 text-slate-950'
                      : 'border-slate-700 hover:border-emerald-500 text-slate-500'
                  }`}
                  title="Marcar como Concluído"
                >
                  <CheckCircle className="w-4 h-4" />
                </button>
              </div>
            </div>

            <div className="flex gap-2 pt-1">
              <button
                onClick={() => setActivePlayUrl(vid.youtubeIdOrUrl)}
                className="flex-1 py-1.5 bg-red-600/10 hover:bg-red-600/20 text-red-400 font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 border border-red-500/20 transition"
              >
                <Play className="w-3.5 h-3.5" />
                <span>Assistir no App</span>
              </button>

              <a
                href={vid.youtubeIdOrUrl}
                target="_blank"
                rel="noreferrer"
                className="p-1.5 bg-slate-950 hover:bg-slate-800 text-slate-400 rounded-xl border border-slate-800 flex items-center justify-center"
                title="Abrir no YouTube"
              >
                <ExternalLink className="w-4 h-4" />
              </a>
            </div>
          </div>
        ))}
      </div>

      {/* MODAL TO ADD NEW CATEGORY / MATÉRIA */}
      {isAddCategoryOpen && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <FolderPlus className="w-5 h-5 text-amber-400" />
                Adicionar Nova Matéria de Estudo
              </h3>
              <button
                onClick={() => setIsAddCategoryOpen(false)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreateNewCategory} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Nome da Nova Matéria</label>
                <input
                  type="text"
                  placeholder="Ex: Atualidades, Raciocínio Lógico, Bioquímica..."
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsAddCategoryOpen(false)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs shadow-lg shadow-amber-500/20 transition"
                >
                  Adicionar Matéria
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* EDIT VIDEO MODAL */}
      {editingVideo && (
        <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 space-y-4 shadow-2xl">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 className="text-base font-bold text-slate-100 flex items-center gap-2">
                <Edit3 className="w-5 h-5 text-red-400" />
                Editar Vídeo-Aula
              </h3>
              <button
                onClick={() => setEditingVideo(null)}
                className="text-slate-400 hover:text-slate-200 p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveEdit} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Título da Vídeo-Aula</label>
                <input
                  type="text"
                  value={editingVideo.title}
                  onChange={(e) => setEditingVideo({ ...editingVideo, title: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">Matéria / Categoria</label>
                <select
                  value={editingVideo.category}
                  onChange={(e) => setEditingVideo({ ...editingVideo, category: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
                >
                  {categories.map((c) => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-400 mb-1">URL do Vídeo no YouTube</label>
                <input
                  type="url"
                  value={editingVideo.youtubeIdOrUrl}
                  onChange={(e) => setEditingVideo({ ...editingVideo, youtubeIdOrUrl: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-red-500"
                  required
                />
              </div>

              <div className="flex items-center justify-end gap-2 pt-2 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setEditingVideo(null)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 font-semibold rounded-xl text-xs transition"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 bg-red-600 hover:bg-red-500 text-white font-bold rounded-xl text-xs shadow-lg shadow-red-600/20 transition"
                >
                  Salvar Alterações
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
