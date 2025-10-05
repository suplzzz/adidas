// presentation/screen/timeline/TimelineComponents.kt

package com.suplz.adidas.presentation.screen.timeline

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.suplz.adidas.domain.entity.*
import androidx.core.net.toUri

// --- Карточка события ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineItemCard(
    item: TimelineItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(16.dp))
                WeatherInfoChip(weather = item.weather)
            }
            Spacer(Modifier.height(16.dp))
            RouteInfo(from = item.userLocation, to = item.eventLocation)
            Spacer(Modifier.height(16.dp))
            HorizontalDivider() // ИСПОЛЬЗУЕМ HorizontalDivider
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = "Время в пути", tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(text = "В пути: ${item.travelDuration}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
internal fun WeatherInfoChip(weather: WeatherInfo) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = getWeatherIcon(weather.condition), contentDescription = weather.condition, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = weather.temperature, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
internal fun RouteInfo(from: String, to: String) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 4.dp)) {
            Icon(imageVector = Icons.Default.Circle, contentDescription = "Начало", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
            VerticalDivider(modifier = Modifier.height(30.dp)) // ДЛЯ ВЕРТИКАЛЬНОЙ ЛИНИИ
            Icon(imageVector = Icons.Default.Circle, contentDescription = "Конец", modifier = Modifier.size(12.dp), tint = Color.Gray)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = from, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(16.dp))
            Text(text = to, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// --- BottomSheet с деталями ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineDetailsSheet(item: TimelineItem, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        TimelineDetailsSheetContent(item)
    }
}

@Composable
private fun TimelineDetailsSheetContent(item: TimelineItem) {
    var selectedOption by remember { mutableStateOf<TransportOption?>(null) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
        Text(text = item.name, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), textAlign = TextAlign.Center)
        Text(text = "Погодные данные", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        WeatherDetails(weather = item.weather)
        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp)) // ИСПОЛЬЗУЕМ HorizontalDivider
        Text("Рекомендованные маршруты", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Column(modifier = Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item.transportOptions.forEach { option ->
                TransportOptionRow(option = option, isSelected = selectedOption == option, onClick = { selectedOption = option })
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                selectedOption?.let {
                    if (item.userLocation.isNotBlank() && item.eventLocation.isNotBlank()) {
                        val transportType = get2GisTransportType(it.type)
                        val uri = "dgis://2gis.ru/routeSearch/rsType/$transportType/from/${item.userLocation}/to/${item.eventLocation}"
                        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                        context.startActivity(intent)
                    }
                }
            },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Построить маршрут в 2ГИС")
        }
    }
}

@Composable
private fun WeatherDetails(weather: WeatherInfo) {
    Row(modifier = Modifier.fillMaxWidth()) {
        WeatherDetailItem(label = "Ощущается как", value = weather.feelsLike, modifier = Modifier.weight(1f))
        WeatherDetailItem(label = "Ветер", value = "${weather.windSpeed}, ${weather.windDirection}", modifier = Modifier.weight(1f))
        WeatherDetailItem(label = "Условия", value = weather.condition, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun WeatherDetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TransportOptionRow(option: TransportOption, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).selectable(selected = isSelected, onClick = onClick, role = Role.RadioButton).padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = getTransportIcon(option.type), contentDescription = getTransportName(option.type), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(text = getTransportName(option.type), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(text = option.duration, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Box(modifier = Modifier.size(14.dp).clip(CircleShape).padding(2.dp)) {
            Icon(imageVector = Icons.Default.Circle, contentDescription = "Статус рекомендации", tint = getStatusColor(option.status))
        }
        Spacer(Modifier.width(16.dp))
        RadioButton(selected = isSelected, onClick = null)
    }
}


// --- Хелперы для иконок, цветов и имен ---

internal fun getWeatherIcon(condition: String): ImageVector = when {
    condition.contains("ясно", ignoreCase = true) -> Icons.Default.WbSunny
    condition.contains("облачно", ignoreCase = true) -> Icons.Default.WbCloudy
    else -> Icons.Default.MoreVert
}

internal fun getTransportIcon(type: TransportType): ImageVector = when (type) {
    TransportType.CAR -> Icons.Default.DirectionsCar
    TransportType.BUS -> Icons.Default.DirectionsBus
    TransportType.FOOT -> Icons.AutoMirrored.Filled.DirectionsWalk
    TransportType.SCOOTER -> Icons.Default.ElectricScooter
    TransportType.TAXI -> Icons.Default.LocalTaxi
    TransportType.UNKNOWN -> Icons.Default.MoreVert
}

@Composable
internal fun getTransportName(type: TransportType): String = when (type) {
    TransportType.CAR -> "Автомобиль"
    TransportType.BUS -> "Автобус"
    TransportType.FOOT -> "Пешком"
    TransportType.SCOOTER -> "Самокат"
    TransportType.TAXI -> "Такси"
    TransportType.UNKNOWN -> "Другое"
}

@Composable
internal fun getStatusColor(status: RecommendationStatus): Color = when (status) {
    RecommendationStatus.GREEN -> Color(0xFF2E7D32)
    RecommendationStatus.RED -> MaterialTheme.colorScheme.error
    RecommendationStatus.GRAY -> Color.Gray
    RecommendationStatus.UNKNOWN -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
}

internal fun get2GisTransportType(type: TransportType): String = when (type) {
    TransportType.CAR -> "car"
    TransportType.BUS -> "ctx"
    TransportType.FOOT -> "pedestrian"
    TransportType.TAXI -> "taxi"
    TransportType.SCOOTER -> "pedestrian"
    TransportType.UNKNOWN -> "car"
}