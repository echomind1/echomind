
package com.example.echomind

import android.Manifest
import android.content.Intent
import android.os.*
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.coroutines.*
import okhttp3.*
import com.google.gson.*
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private val client = OkHttpClient()
    private val openAiKey = "YOUR_API_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val spokenText = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                if (!spokenText.isNullOrEmpty()) {
                    getChatGPTResponse(spokenText)
                }
            }
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(this@MainActivity, "识别错误: $error", Toast.LENGTH_SHORT).show()
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    private fun getChatGPTResponse(inputText: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Gson().toJson(mapOf(
                "model" to "gpt-3.5-turbo",
                "messages" to listOf(mapOf("role" to "user", "content" to inputText))
            ))
            val body = RequestBody.create("application/json".toMediaType(), json)
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $openAiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseText = response.body?.string()
            val reply = JsonParser.parseString(responseText).asJsonObject
                .getAsJsonArray("choices")[0]
                .asJsonObject["message"]
                .asJsonObject["content"]
                .asString

            withContext(Dispatchers.Main) {
                tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.CHINA
        }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
