package com.suplz.adidas.presentation.screen.home

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.suplz.adidas.presentation.navigation.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // 1. Получаем текущий контекст. Он нужен для проверки разрешений и показа Toast.
    val context = LocalContext.current

    // 2. Создаем и "запоминаем" лаунчер для запроса разрешений.
    // Он будет обрабатывать результат ответа пользователя (разрешил/отклонил).
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Если пользователь дал разрешение, переходим на экран таймлайна.
                navController.navigate(Screen.Timeline.route)
            } else {
                // Если пользователь отклонил, показываем сообщение.
                Toast.makeText(
                    context,
                    "Доступ к календарю необходим для работы функции.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            // 3. Логика при нажатии на кнопку
            when (PackageManager.PERMISSION_GRANTED) {
                // Проверяем, есть ли уже разрешение
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CALENDAR
                ) -> {
                    // Если разрешение уже есть, просто переходим дальше.
                    navController.navigate(Screen.Timeline.route)
                }
                else -> {
                    // Если разрешения нет, запускаем системный диалог запроса.
                    // Результат будет обработан в `onResult` блоке лаунчера выше.
                    permissionLauncher.launch(Manifest.permission.READ_CALENDAR)
                }
            }
        }) {
            Text("Посмотреть мой день")
        }
    }
}