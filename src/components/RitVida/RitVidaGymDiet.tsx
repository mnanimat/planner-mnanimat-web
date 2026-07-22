import React, { useState } from 'react';
import { useApp } from '../../context/AppContext';
import { Dumbbell, Utensils, Droplets, Plus, Trash2, CheckCircle2, Circle } from 'lucide-react';

export const RitVidaGymDiet: React.FC = () => {
  const {
    gymWorkouts,
    insertGymWorkout,
    deleteGymWorkout,
    toggleGymWorkoutStatus,
    dietLogs,
    insertDietLog,
    deleteDietLog
  } = useApp();

  // Workout state
  const [exercise, setExercise] = useState('');
  const [sets, setSets] = useState(4);
  const [reps, setReps] = useState(10);
  const [weightKg, setWeightKg] = useState(60);

  // Diet state
  const [mealType, setMealType] = useState('Almoço');
  const [foodName, setFoodName] = useState('');
  const [caloriesKcal, setCaloriesKcal] = useState(500);
  const [waterIntakeMl, setWaterIntakeMl] = useState(500);

  const handleAddWorkout = (e: React.FormEvent) => {
    e.preventDefault();
    if (!exercise.trim()) return;
    insertGymWorkout(exercise, Number(sets), Number(reps), Number(weightKg), new Date().toISOString().split('T')[0]);
    setExercise('');
  };

  const handleAddDiet = (e: React.FormEvent) => {
    e.preventDefault();
    if (!foodName.trim()) return;
    insertDietLog(mealType, foodName, Number(caloriesKcal), Number(waterIntakeMl), new Date().toISOString().split('T')[0]);
    setFoodName('');
  };

  const totalCalories = dietLogs.reduce((acc, log) => acc + log.caloriesKcal, 0);
  const totalWaterMl = dietLogs.reduce((acc, log) => acc + log.waterIntakeMl, 0);

  return (
    <div className="space-y-8">
      {/* Gym Section */}
      <div className="space-y-4">
        <div>
          <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
            <Dumbbell className="w-5 h-5 text-amber-400" />
            Registro de Treinos e Exercícios de Academia
          </h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Acompanhe séries, repetições, cargas e verifique sua constância semanal.
          </p>
        </div>

        {/* Add Workout Form */}
        <form onSubmit={handleAddWorkout} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
          <input
            type="text"
            placeholder="Nome do Exercício (ex: Agachamento Livre)..."
            value={exercise}
            onChange={(e) => setExercise(e.target.value)}
            className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-amber-500"
            required
          />

          <input
            type="number"
            placeholder="Séries"
            value={sets}
            onChange={(e) => setSets(Number(e.target.value))}
            className="w-24 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <input
            type="number"
            placeholder="Reps"
            value={reps}
            onChange={(e) => setReps(Number(e.target.value))}
            className="w-24 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <input
            type="number"
            placeholder="Carga (kg)"
            value={weightKg}
            onChange={(e) => setWeightKg(Number(e.target.value))}
            className="w-28 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <button
            type="submit"
            className="px-4 py-2 bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition shadow-lg shadow-amber-500/20"
          >
            <Plus className="w-4 h-4" />
            Adicionar Treino
          </button>
        </form>

        {/* Workouts Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
          {gymWorkouts.map((w) => (
            <div
              key={w.id}
              className={`p-4 rounded-2xl border flex items-center justify-between gap-3 transition shadow-md ${
                w.isCompleted ? 'bg-emerald-500/10 border-emerald-500/20' : 'bg-slate-900 border-slate-800'
              }`}
            >
              <div className="flex items-center gap-3">
                <button
                  onClick={() => toggleGymWorkoutStatus(w.id)}
                  className={`p-1.5 rounded-xl border transition ${
                    w.isCompleted
                      ? 'bg-emerald-500 border-emerald-500 text-slate-950'
                      : 'border-slate-700 hover:border-emerald-500 text-slate-500'
                  }`}
                >
                  <CheckCircle2 className="w-4 h-4" />
                </button>

                <div>
                  <h4 className={`text-sm font-bold ${w.isCompleted ? 'line-through text-slate-400' : 'text-slate-100'}`}>
                    {w.exercise}
                  </h4>
                  <p className="text-xs text-amber-400 font-semibold">
                    {w.sets} séries x {w.reps} reps • {w.weightKg} kg
                  </p>
                </div>
              </div>

              <button
                onClick={() => deleteGymWorkout(w.id)}
                className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          ))}
        </div>
      </div>

      {/* Diet & Water Section */}
      <div className="space-y-4 pt-4 border-t border-slate-800">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
          <div>
            <h2 className="text-xl font-extrabold text-slate-100 flex items-center gap-2">
              <Utensils className="w-5 h-5 text-indigo-400" />
              Dieta, Calorias e Consumo de Água
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">
              Registro nutricional simplificado para manter a energia nos estudos e treinos.
            </p>
          </div>

          <div className="flex gap-3">
            <div className="px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-xl text-center">
              <p className="text-[10px] text-slate-400">Total Calorias</p>
              <p className="text-xs font-bold text-amber-400">{totalCalories} kcal</p>
            </div>
            <div className="px-3 py-1.5 bg-slate-900 border border-slate-800 rounded-xl text-center">
              <p className="text-[10px] text-slate-400">Ingestão de Água</p>
              <p className="text-xs font-bold text-indigo-400">{(totalWaterMl / 1000).toFixed(1)} L</p>
            </div>
          </div>
        </div>

        {/* Add Diet Form */}
        <form onSubmit={handleAddDiet} className="bg-slate-900 border border-slate-800 rounded-2xl p-4 flex flex-col md:flex-row gap-3 shadow-md">
          <select
            value={mealType}
            onChange={(e) => setMealType(e.target.value)}
            className="bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
          >
            <option value="Café da Manhã">Café da Manhã</option>
            <option value="Almoço">Almoço</option>
            <option value="Café da Tarde">Café da Tarde</option>
            <option value="Jantar">Jantar</option>
            <option value="Lanches">Lanches</option>
          </select>

          <input
            type="text"
            placeholder="Descrição dos alimentos..."
            value={foodName}
            onChange={(e) => setFoodName(e.target.value)}
            className="flex-1 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none focus:border-indigo-500"
            required
          />

          <input
            type="number"
            placeholder="Calorias (kcal)"
            value={caloriesKcal}
            onChange={(e) => setCaloriesKcal(Number(e.target.value))}
            className="w-32 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <input
            type="number"
            placeholder="Água (ml)"
            value={waterIntakeMl}
            onChange={(e) => setWaterIntakeMl(Number(e.target.value))}
            className="w-32 bg-slate-950 border border-slate-800 rounded-xl px-3 py-2 text-xs text-slate-100 focus:outline-none"
            required
          />

          <button
            type="submit"
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-bold rounded-xl text-xs flex items-center justify-center gap-1.5 transition shadow-lg shadow-indigo-600/20"
          >
            <Plus className="w-4 h-4" />
            Registrar Refeição
          </button>
        </form>

        {/* Diet List */}
        <div className="space-y-2">
          {dietLogs.map((log) => (
            <div key={log.id} className="p-3 bg-slate-900 border border-slate-800 rounded-xl flex items-center justify-between text-xs shadow-md">
              <div className="space-y-0.5">
                <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-full bg-indigo-500/10 text-indigo-400 border border-indigo-500/20">
                  {log.mealType}
                </span>
                <p className="font-bold text-slate-100 mt-1">{log.foodName}</p>
                <p className="text-[10px] text-slate-400">
                  {log.caloriesKcal} kcal • {log.waterIntakeMl} ml de água
                </p>
              </div>

              <button
                onClick={() => deleteDietLog(log.id)}
                className="text-slate-500 hover:text-red-400 p-1 rounded-lg"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
