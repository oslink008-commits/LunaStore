package com.lunastore.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class Script(
    val id: String,
    val name: String,
    val shortComment: String,
    val fullComment: String,
    val type: String, // "userscript" or "library"
    val language: String?, // "lua", "python", "java"
    val rawUrl: String,
    val githubUrl: String
)

class ScriptViewModel : ViewModel() {
    private val _scripts = MutableStateFlow<List<Script>>(emptyList())
    val scripts: StateFlow<List<Script>> = _scripts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    var runningScriptId: String? = null

    private val _expandedScriptId = MutableStateFlow<String?>(null)
    val expandedScriptId: StateFlow<String?> = _expandedScriptId

    private val client = OkHttpClient()
    private val githubRepo = "oslink008-commits/UTL"

    init {
        loadScripts()
    }

    fun loadScripts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = Request.Builder()
                    .url("https://api.github.com/repos/$githubRepo/contents")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = JSONArray(response.body?.string() ?: "")
                    val scriptList = mutableListOf<Script>()

                    for (i in 0 until json.length()) {
                        val item = json.getJSONObject(i)
                        val name = item.getString("name")
                        val type = item.getString("type")

                        if (type == "file" && (name.endsWith(".lua") || name.endsWith(".py") || name.endsWith(".jar"))) {
                            val rawUrl = item.getString("download_url")
                            val githubUrl = item.getString("html_url")

                            // Fetch file content to parse tags
                            val contentRequest = Request.Builder()
                                .url(rawUrl)
                                .build()

                            val contentResponse = client.newCall(contentRequest).execute()
                            if (contentResponse.isSuccessful) {
                                val content = contentResponse.body?.string() ?: ""
                                val parsedTags = parseTags(content, name)

                                scriptList.add(
                                    Script(
                                        id = name,
                                        name = parsedTags["name"] ?: name,
                                        shortComment = parsedTags["comment"] ?: "No description",
                                        fullComment = parsedTags["fullComment"] ?: "No description",
                                        type = parsedTags["type"] ?: "userscript",
                                        language = parsedTags["language"],
                                        rawUrl = rawUrl,
                                        githubUrl = githubUrl
                                    )
                                )
                            }
                        }
                    }

                    _scripts.value = scriptList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseTags(content: String, fileName: String): Map<String, String?> {
        val tags = mutableMapOf<String, String?>()
        val lines = content.lines()

        var name: String? = null
        var comment: String? = null
        var fullComment = StringBuilder()
        var inCommentBlock = false
        var scriptType: String? = null
        var language: String? = null

        // Determine language from file extension
        language = when {
            fileName.endsWith(".lua") -> "lua"
            fileName.endsWith(".py") -> "python"
            fileName.endsWith(".jar") -> "java"
            else -> null
        }

        for (line in lines) {
            val trimmed = line.trim()

            // Check for comment start/end
            if (trimmed.startsWith("--[[")) {
                inCommentBlock = true
                continue
            }
            if (trimmed.startsWith("--]]")) {
                inCommentBlock = false
                continue
            }

            // Parse single-line comments
            val commentStart = when {
                trimmed.startsWith("--") -> "--"
                trimmed.startsWith("#") -> "#"
                trimmed.startsWith("//") -> "//"
                else -> null
            }

            if (commentStart != null) {
                val commentContent = trimmed.substring(commentStart.length).trim()

                // Parse //name tag
                if (commentContent.startsWith("name")) {
                    name = commentContent.substring(4).trim()
                }
                // Parse //comment tag
                else if (commentContent.startsWith("comment")) {
                    comment = commentContent.substring(7).trim()
                }
                // Parse //type tag (userscript/library)
                else if (commentContent.startsWith("type")) {
                    scriptType = commentContent.substring(4).trim().lowercase()
                }
                // Add to full comment
                else {
                    if (fullComment.isNotEmpty()) {
                        fullComment.append("\n")
                    }
                    fullComment.append(commentContent)
                }
            } else if (inCommentBlock) {
                if (fullComment.isNotEmpty()) {
                    fullComment.append("\n")
                }
                fullComment.append(trimmed)
            }
        }

        tags["name"] = name
        tags["comment"] = comment
        tags["fullComment"] = fullComment.toString().ifEmpty { comment }
        tags["type"] = scriptType ?: "userscript"
        tags["language"] = language

        return tags
    }

    fun toggleExpand(scriptId: String) {
        _expandedScriptId.value = if (_expandedScriptId.value == scriptId) null else scriptId
    }

    fun downloadScript(script: Script) {
        viewModelScope.launch {
            try {
                val request = Request.Builder()
                    .url(script.rawUrl)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val content = response.body?.string() ?: ""
                    // TODO: Save to local storage - needs context
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun runScript(script: Script) {
        _isRunning.value = true
        runningScriptId = script.id
        // Start background service to run the script
    }

    fun stopScript() {
        _isRunning.value = false
        runningScriptId = null
        _isLocked.value = false
    }

    fun toggleLock() {
        _isLocked.value = !_isLocked.value
    }
}

class ScriptRunnerService : Service() {
    private val CHANNEL_ID = "LunaStoreScriptRunner"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LunaStore Script Runner",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Thông báo khi script đang chạy"
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LunaStore đang chạy ở nền")
            .setContentText("Script đang thực thi")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
