package com.lunastore.app

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ShizukuManager : ViewModel() {
    var isShizukuAvailable by mutableStateOf(false)
        private set
    var isShizukuGranted by mutableStateOf(false)
        private set

    fun checkShizuku(context: Context) {
        viewModelScope.launch {
            try {
                // Check if Shizuku is installed
                val pm = context.packageManager
                try {
                    pm.getPackageInfo("moe.shizuku.privileged.api", 0)
                    isShizukuAvailable = true
                } catch (e: PackageManager.NameNotFoundException) {
                    isShizukuAvailable = false
                }

                // Check if permission is granted
                // This would require actual Shizuku API integration
                // For now, we'll use a placeholder
                isShizukuGranted = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestShizukuPermission() {
        // This would trigger Shizuku permission request
        // Actual implementation requires Shizuku library
    }
}

@Composable
fun ShizukuStatusIndicator(
    isAvailable: Boolean,
    isGranted: Boolean
) {
    if (!isAvailable) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Text(
                    "Cần Shizuku để chạy scripts",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else if (!isGranted) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Text(
                    "Shizuku chưa kết nối",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
