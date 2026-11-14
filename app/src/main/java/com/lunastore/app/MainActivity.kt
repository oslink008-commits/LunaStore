package com.lunastore.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lunastore.app.ui.theme.LunaStoreTheme
import com.lunastore.app.ShizukuManager
import com.lunastore.app.ShizukuStatusIndicator

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Cần quyền thông báo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            LunaStoreTheme {
                LunaStoreApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunaStoreApp(viewModel: ScriptViewModel = viewModel()) {
    val scripts by viewModel.scripts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()
    val expandedScriptId by viewModel.expandedScriptId.collectAsState()

    val shizukuManager: ShizukuManager = viewModel()
    val isShizukuAvailable by shizukuManager.isShizukuAvailable
    val isShizukuGranted by shizukuManager.isShizukuGranted

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LunaStore") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ShizukuStatusIndicator(
                    isAvailable = isShizukuAvailable,
                    isGranted = isShizukuGranted
                )

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                    if (scripts.isEmpty()) {
                        item {
                            Text(
                                text = "Không có scripts nào",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        items(scripts) { script ->
                            ScriptItem(
                                script = script,
                                isExpanded = expandedScriptId == script.id,
                                isRunning = isRunning && viewModel.runningScriptId == script.id,
                                isLocked = isLocked,
                                onExpand = { viewModel.toggleExpand(script.id) },
                                onDownload = { viewModel.downloadScript(script) },
                                onPlay = { viewModel.runScript(script) },
                                onLock = { viewModel.toggleLock() },
                                onExit = { viewModel.stopScript() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptItem(
    script: Script,
    isExpanded: Boolean,
    isRunning: Boolean,
    isLocked: Boolean,
    onExpand: () -> Unit,
    onDownload: () -> Unit,
    onPlay: () -> Unit,
    onLock: () -> Unit,
    onExit: () -> Unit
) {
    var isDownloading by remember { mutableStateOf(false) }
    var downloadDone by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = script.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = script.shortComment,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(script.type) }
                        )
                        script.language?.let {
                            SuggestionChip(
                                onClick = {},
                                label = { Text(it) }
                            )
                        }
                    }
                }

                IconButton(onClick = onExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = script.fullComment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (script.type == "userscript") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                        } else if (downloadDone) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable(onClick = onPlay),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Button(onClick = {
                                isDownloading = true
                                onDownload()
                                // Simulate download completion
                                kotlinx.coroutines.MainScope().launch {
                                    kotlinx.coroutines.delay(2000)
                                    isDownloading = false
                                    downloadDone = true
                                }
                            }) {
                                Text("Download")
                            }
                        }

                        if (isRunning) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(onClick = onLock) {
                                    Icon(
                                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = if (isLocked) "Unlock" else "Lock",
                                        tint = if (isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(onClick = onExit) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Exit",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Download thư viện",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = {
                            isDownloading = true
                            onDownload()
                            kotlinx.coroutines.MainScope().launch {
                                kotlinx.coroutines.delay(2000)
                                isDownloading = false
                                downloadDone = true
                            }
                        })
                    )
                }
            }
        }
    }
}
