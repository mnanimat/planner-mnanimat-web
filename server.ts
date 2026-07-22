import express from 'express';
import cors from 'cors';
import path from 'path';
import { fileURLToPath } from 'url';
import { GoogleGenAI } from '@google/genai';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const app = express();
const PORT = 3000;

app.use(cors());
app.use(express.json({ limit: '10mb' }));

// Lazy initializer for Gemini Client
let genAI: GoogleGenAI | null = null;
function getGenAIClient(): GoogleGenAI | null {
  const apiKey = process.env.GEMINI_API_KEY;
  if (!apiKey || apiKey.trim() === '' || apiKey === 'MY_GEMINI_API_KEY') {
    return null;
  }
  if (!genAI) {
    genAI = new GoogleGenAI({ apiKey });
  }
  return genAI;
}

// Offline simulated AI responses (mirroring Android GeminiService.kt fallback)
function getOfflineFallbackResponse(prompt: string, systemInstruction?: string): string {
  const query = prompt.toLowerCase();
  
  if (query.includes('redação') || query.includes('corrigir') || query.includes('redacao')) {
    return `[OFFLINE - SIMULADOR IA LOCAL]
Análise Detalhada da Redação (Baseada na Estrutura Oficial do ENEM):

1. Competência 1 (Domínio da norma culta - 0 a 200): 180 pt
   - Excelente domínio da modalidade escrita, com raras falhas gramaticais. Atenção à concordância em períodos longos.

2. Competência 2 (Compreensão do tema e áreas do conhecimento - 0 a 200): 200 pt
   - Tema plenamente compreendido e repertório sociocultural produtivo aplicado com sucesso na introdução e desenvolvimento.

3. Competência 3 (Argumentação e projeto de texto - 0 a 200): 180 pt
   - Tese consistente e encadeamento lógico de ideias. Argumentos bem fundamentados.

4. Competência 4 (Mecanismos linguísticos / Coesão - 0 a 200): 180 pt
   - Boa utilização de conectivos interparágrafos e intraparágrafos sem repetições viciosas.

5. Competência 5 (Proposta de intervenção - 0 a 200): 180 pt
   - Contém Agente, Ação, Meio/Modo e Efeito. Falta detalhar o Meio/Modo com exemplos para nota máxima nesta competência.

--------------------------------------------------
NOTA FINAL ESTIMADA: 920 / 1000

Conselho de Reescrita:
Para alcançar os 1000 pontos, especifique o Agente Governamental (ex: "Ministério da Educação, em parceria com as secretarias estaduais") e detalhe como os recursos serão fiscalizados.`;
  }

  if (query.includes('matemática') || query.includes('equação') || query.includes('função') || query.includes('calcul')) {
    return `[OFFLINE - SIMULADOR IA LOCAL]
Explicação de Matemática para o ENEM/ITA:

Para resolver problemas de Função Quadrática (y = ax² + bx + c):
1. Identifique os coeficientes 'a', 'b' e 'c'.
2. O vértice da parábola representa o ponto máximo (se a < 0) ou mínimo (se a > 0).
   - Xv = -b / (2a)
   - Yv = -Δ / (4a), onde Δ = b² - 4ac.
3. As raízes são encontradas pela Fórmula de Bhaskara:
   - x = (-b ± √Δ) / (2a).

Se quiser respostas dinâmicas em tempo real conectadas aos servidores do Google, insira sua GEMINI_API_KEY no painel de Secrets do AI Studio!`;
  }

  return `[OFFLINE - SIMULADOR IA LOCAL]
Olá! Sou o seu Tutor IA do Planner MNAnimat.

Identifiquei sua dúvida de estudos: "${prompt.slice(0, 70)}${prompt.length > 70 ? '...' : ''}"

Dica de Estudo:
Para fixar este conteúdo na memória de longo prazo, utilize o método dos 7 passos de aprendizagem (Aula -> Resumo -> Autoexplicação -> Exercícios -> Caderno de Erros -> Revisão -> Simulado) na aba Trilhas.

Mantenha a consistência e foco total na sua preparação!`;
}

// Server API Proxy for Gemini
app.post('/api/gemini/generate', async (req, res) => {
  try {
    const { prompt, systemInstruction } = req.body;
    if (!prompt) {
      return res.status(400).json({ error: 'Prompt é obrigatório.' });
    }

    const aiClient = getGenAIClient();
    if (!aiClient) {
      const fallbackText = getOfflineFallbackResponse(prompt, systemInstruction);
      return res.json({ text: fallbackText, isOffline: true });
    }

    try {
      const response = await aiClient.models.generateContent({
        model: 'gemini-2.5-flash',
        contents: prompt,
        config: systemInstruction ? { systemInstruction } : undefined,
      });

      const replyText = response.text || getOfflineFallbackResponse(prompt, systemInstruction);
      return res.json({ text: replyText, isOffline: false });
    } catch (apiErr: any) {
      console.warn('Gemini API call failed, falling back to offline simulator:', apiErr?.message || apiErr);
      const fallbackText = getOfflineFallbackResponse(prompt, systemInstruction);
      return res.json({ text: `[Aviso de Conexão: ${apiErr?.message || 'Falha na API'} - Modo Offline Ativo]\n\n` + fallbackText, isOffline: true });
    }
  } catch (err: any) {
    console.error('Server error handling Gemini request:', err);
    res.status(500).json({ error: err?.message || 'Erro interno no servidor' });
  }
});

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', time: new Date().toISOString() });
});

// Vite Development or Production Server Static Serving
async function startServer() {
  if (process.env.NODE_ENV !== 'production') {
    const { createServer: createViteServer } = await import('vite');
    const vite = await createViteServer({
      server: { middlewareMode: true, host: '0.0.0.0', port: 3000 },
      appType: 'spa',
    });
    app.use(vite.middlewares);
  } else {
    const distPath = path.join(process.cwd(), 'dist');
    app.use(express.static(distPath));
    app.get('*', (req, res) => {
      res.sendFile(path.join(distPath, 'index.html'));
    });
  }

  app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Planner MNAnimat Server running at http://0.0.0.0:${PORT}`);
  });
}

startServer();
