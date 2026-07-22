package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    /**
     * Call Gemini API to generate content.
     */
    suspend fun getCompletion(prompt: String, systemInstruction: String? = null): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getLocalFallbackResponse(prompt, systemInstruction)
        }

        val contents = listOf(Content(parts = listOf(Part(text = prompt))))
        val sysInst = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
        val request = GeminiRequest(
            contents = contents,
            systemInstruction = sysInst,
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Desculpe, não consegui obter uma resposta válida da Inteligência Artificial."
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to offline engine if network fails
            "Erro de Conectividade (${e.message}). Retornando resposta local off-line:\n\n" + 
                getLocalFallbackResponse(prompt, systemInstruction)
        }
    }

    private fun getLocalFallbackResponse(prompt: String, systemInstruction: String?): String {
        val query = prompt.lowercase()
        return when {
            query.contains("redação") || query.contains("corrigir") || query.contains("redacao") -> {
                """
                [OFFLINE - SIMULADOR IA]
                Análise da Redação (Baseada na Estrutura do ENEM):
                
                1. Competência 1 (Domínio da norma culta): Bom domínio da modalidade escrita, com poucos desvios gramaticais. Atenção à concordância em períodos longos.
                2. Competência 2 (Compreensão do tema): O texto aborda o tema principal de maneira satisfatória, desenvolvendo a tese central na introdução.
                3. Competência 3 (Seleção e organização de informações): Apresenta repertório sociocultural produtivo. Argumentação sólida e consistente.
                4. Competência 4 (Demonstração de conhecimento dos mecanismos linguísticos): Boa coesão interparágrafos e intraparágrafos.
                5. Competência 5 (Proposta de intervenção): Contém Agente, Ação, Meio/Modo e Efeito, mas falta detalhar um desses elementos para obter nota máxima nesta competência.
                
                Nota Estimada: 880 / 1000
                Conselho: Faça o detalhamento do Meio/Modo de execução inserindo exemplos concretos de como a ação será posta em prática.
                """.trimIndent()
            }
            query.contains("matemática") || query.contains("equação") || query.contains("função") || query.contains("calcul") -> {
                """
                [OFFLINE - SIMULADOR IA]
                Explicação de Matemática:
                
                Para resolver problemas de Função Quadrática (y = ax² + bx + c):
                1. Identifique os coeficientes 'a', 'b' e 'c'.
                2. O vértice da parábola representa o ponto máximo (se a < 0) ou mínimo (se a > 0).
                   - Xv = -b / (2a)
                   - Yv = -Δ / (4a), onde Δ = b² - 4ac.
                3. As raízes são encontradas pela Fórmula de Bhaskara:
                   - x = (-b ± √Δ) / (2a).
                
                Se tiver um exercício específico, configure a chave de API do Gemini nas configurações do AI Studio para resolver passo a passo!
                """.trimIndent()
            }
            else -> {
                """
                [OFFLINE - SIMULADOR IA]
                Olá! Sou seu Tutor IA do FocoVest. 
                
                Identifiquei sua pergunta relacionada aos estudos: "${prompt.take(60)}..."
                
                Para perguntas de física, química, biologia ou história, utilize o menu correspondente de Trilhas para estudar os tópicos em 7 passos de aprendizagem. Se desejar respostas dinâmicas em tempo real conectadas aos servidores do Google, insira sua chave do Gemini no painel de Secrets do Google AI Studio.
                
                Bons estudos e mantenha o foco!
                """.trimIndent()
            }
        }
    }
}
