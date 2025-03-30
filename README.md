# EchoMind

**EchoMind** is a lightweight Android voice assistant powered by ChatGPT.

### Features

- Speech-to-text using Android SpeechRecognizer
- Sends query to OpenAI ChatGPT (GPT-3.5)
- Receives AI response and plays it back via Text-to-Speech

### Setup

1. Clone the repo:
```bash
git clone https://github.com/your-username/EchoMind.git
```

2. Open in Android Studio

3. Add your OpenAI API key in `MainActivity.kt`:
```kotlin
private val openAiKey = "sk-..." // your key here
```

4. Run the app!

### Permissions

- RECORD_AUDIO
- INTERNET

Enjoy your smart, conversational AI companion!
