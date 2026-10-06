package com.mapaurbano.app.feature.report

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.model.MapPoint
import com.mapaurbano.app.feature.common.NoticeCard
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.SectionTitle

/** Selector visual simulado. La elección solo sale de esta pantalla al confirmar. */
@Composable
fun LocationPickerScreen(
    initialPoint: MapPoint,
    initialLocation: String,
    modifier: Modifier = Modifier,
    onConfirm: (point: MapPoint, location: String) -> Unit,
    onCancel: () -> Unit,
) {
    var selectedPoint by remember(initialPoint) { mutableStateOf(initialPoint) }
    var location by remember(initialLocation) { mutableStateOf(initialLocation) }
    var gpsUnavailable by rememberSaveable { mutableStateOf(false) }
    val options = remember(initialPoint, initialLocation) {
        buildList {
            add(LocationOption(initialLocation.ifBlank { initialPoint.label }, initialPoint))
            addAll(DEMO_LOCATIONS.filterNot { it.point.latitude == initialPoint.latitude })
        }
    }

    ScrollableFeatureScreen(
        title = "Elegir ubicación",
        modifier = modifier,
        onBack = onCancel,
    ) {
        NoticeCard("El mapa y las direcciones son de demostración. No se consulta tu GPS.")
        OutlinedButton(
            onClick = { gpsUnavailable = true },
            modifier = Modifier
                .fillMaxWidth()
                .sizeIn(minHeight = 48.dp),
        ) {
            Icon(Icons.Rounded.MyLocation, contentDescription = null)
            androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
            Text("Usar mi ubicación")
        }
        if (gpsUnavailable) {
            NoticeCard("GPS no disponible en la demostración. Elegí un punto sugerido o escribí una referencia manual.")
        }
        SimulatedMap(point = selectedPoint)

        SectionTitle(
            title = "Puntos sugeridos",
            supportingText = "Seleccioná una referencia cercana y ajustá el texto si hace falta.",
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val selected = selectedPoint.latitude == option.point.latitude &&
                    selectedPoint.longitude == option.point.longitude
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .selectable(
                            selected = selected,
                            onClick = {
                                selectedPoint = option.point
                                location = option.label
                            },
                            role = Role.RadioButton,
                        )
                        .background(
                            if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .sizeIn(minHeight = 52.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = selected, onClick = null)
                    Icon(Icons.Rounded.LocationOn, contentDescription = null)
                    androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                    Text(option.label, modifier = Modifier.weight(1f))
                }
            }
        }

        OutlinedTextField(
            value = location,
            onValueChange = { location = it.take(120) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Referencia de la ubicación") },
            supportingText = { Text("Ejemplo: esquina, plaza o altura aproximada") },
            singleLine = false,
            maxLines = 2,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
            ),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .sizeIn(minHeight = 48.dp),
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val cleanLocation = location.trim()
                    onConfirm(selectedPoint.copy(label = cleanLocation), cleanLocation)
                },
                enabled = location.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .sizeIn(minHeight = 48.dp),
            ) {
                Icon(Icons.Rounded.MyLocation, contentDescription = null)
                androidx.compose.foundation.layout.Spacer(Modifier.width(8.dp))
                Text("Confirmar")
            }
        }
    }
}

@Composable
private fun SimulatedMap(point: MapPoint) {
    val background = MaterialTheme.colorScheme.surfaceVariant
    val street = MaterialTheme.colorScheme.outlineVariant
    val marker = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .semantics {
                contentDescription = "Vista esquemática. Punto seleccionado: ${point.label}"
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(18.dp)) {
            val xs = listOf(.18f, .48f, .76f)
            val ys = listOf(.22f, .54f, .80f)
            xs.forEach { fraction ->
                drawLine(
                    color = street,
                    start = androidx.compose.ui.geometry.Offset(size.width * fraction, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width * fraction, size.height),
                    strokeWidth = 18f,
                )
            }
            ys.forEach { fraction ->
                drawLine(
                    color = street,
                    start = androidx.compose.ui.geometry.Offset(0f, size.height * fraction),
                    end = androidx.compose.ui.geometry.Offset(size.width, size.height * fraction),
                    strokeWidth = 18f,
                )
            }
            drawCircle(
                color = Color.White,
                radius = 20f,
                center = androidx.compose.ui.geometry.Offset(
                    size.width * point.x.coerceIn(0f, 1f),
                    size.height * point.y.coerceIn(0f, 1f),
                ),
            )
            drawCircle(
                color = marker,
                radius = 13f,
                center = androidx.compose.ui.geometry.Offset(
                    size.width * point.x.coerceIn(0f, 1f),
                    size.height * point.y.coerceIn(0f, 1f),
                ),
            )
        }
        Text(
            text = "Mapa simulado",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = .90f),
                    RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private data class LocationOption(val label: String, val point: MapPoint)

private val DEMO_LOCATIONS = listOf(
    LocationOption(
        "San Martín y Rivadavia",
        MapPoint(.34f, .42f, -32.8891, -68.8448, "San Martín y Rivadavia"),
    ),
    LocationOption(
        "Plaza Central, lateral norte",
        MapPoint(.58f, .30f, -32.8882, -68.8462, "Plaza Central, lateral norte"),
    ),
    LocationOption(
        "Parque Belgrano, ingreso oeste",
        MapPoint(.44f, .74f, -32.8911, -68.8473, "Parque Belgrano, ingreso oeste"),
    ),
)
