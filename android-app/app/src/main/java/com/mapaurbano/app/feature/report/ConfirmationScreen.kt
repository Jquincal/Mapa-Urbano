package com.mapaurbano.app.feature.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapaurbano.app.core.designsystem.MuniColors
import com.mapaurbano.app.core.model.Report
import com.mapaurbano.app.core.model.SubmissionMode
import com.mapaurbano.app.feature.common.MiniMapIllustration
import com.mapaurbano.app.feature.common.ScrollableFeatureScreen
import com.mapaurbano.app.feature.common.StitchCard
import com.mapaurbano.app.feature.common.StitchPrimaryButton

@Composable
fun ConfirmationScreen(
    report: Report,
    modifier: Modifier = Modifier,
    onCopyTrackingCode: (String) -> Unit,
    onShareTrackingCode: (String) -> Unit,
    onOpenReport: () -> Unit,
    onCreateAnother: () -> Unit,
    onGoToMap: () -> Unit,
) {
    val anonymousCode = report.trackingCode.takeIf {
        report.submissionMode == SubmissionMode.Anonymous && !it.isNullOrBlank()
    }

    ScrollableFeatureScreen(
        title = "Confirmación",
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(MuniColors.Green100, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(46.dp),
                    tint = ColorTokens.SuccessInk,
                )
            }
            Text(
                text = "¡Reporte enviado con éxito!",
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "La incidencia quedo registrada como ${report.status.label.lowercase()}.",
                style = MaterialTheme.typography.bodyMedium,
                color = MuniColors.OnSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        StitchCard(contentPadding = PaddingValues(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("COMPROBANTE MUNICIPAL", style = MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
                Text(report.createdAtLabel, style = MaterialTheme.typography.labelSmall, color = MuniColors.OnSurfaceVariant)
            }
            if (anonymousCode != null) {
                StitchCard(containerColor = MuniColors.SurfaceContainerLow, contentPadding = PaddingValues(14.dp)) {
                    Text(
                        "Código de seguimiento anónimo",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        style = MaterialTheme.typography.labelMedium,
                        color = MuniColors.OnSurfaceVariant,
                    )
                    SelectionContainer {
                        Text(
                            text = anonymousCode,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MuniColors.Primary,
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { onCopyTrackingCode(anonymousCode) },
                            modifier = Modifier.weight(1f).sizeIn(minHeight = 44.dp),
                        ) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copiar")
                        }
                        OutlinedButton(
                            onClick = { onShareTrackingCode(anonymousCode) },
                            modifier = Modifier.weight(1f).sizeIn(minHeight = 44.dp),
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Compartir")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MuniColors.Orange100, MaterialTheme.shapes.medium)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(Icons.Rounded.Warning, contentDescription = null, tint = MuniColors.Orange700, modifier = Modifier.size(18.dp))
                        Text(
                            "Guardá este código. Es la llave para consultar avances del reporte anónimo.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MuniColors.Orange700,
                        )
                    }
                }
            } else {
                StitchCard(containerColor = MuniColors.PrimaryFixed, contentPadding = PaddingValues(12.dp)) {
                    Text("Vinculado a tu perfil civico", style = MaterialTheme.typography.labelLarge, color = MuniColors.Primary)
                    Text("Podras consultar los avances desde Mis reportes.", style = MaterialTheme.typography.bodySmall)
                }
            }
            DetailLine("Numero de reporte", report.id)
            DetailLine("Categoría", report.category.label)
            DetailLine("Ubicacion", report.location)
            MiniMapIllustration(modifier = Modifier.height(106.dp))
        }

        StitchPrimaryButton(
            text = if (anonymousCode != null) "Ver seguimiento" else "Ver detalle del reporte",
            onClick = onOpenReport,
            icon = Icons.Rounded.OpenInNew,
        )
        OutlinedButton(
            onClick = onGoToMap,
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp),
        ) {
            Icon(Icons.Rounded.Home, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Volver al mapa")
        }
        TextButton(
            onClick = onCreateAnother,
            modifier = Modifier.fillMaxWidth().sizeIn(minHeight = 48.dp),
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Crear otro reporte")
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MuniColors.OnSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

private object ColorTokens {
    val SuccessInk = androidx.compose.ui.graphics.Color(0xFF005323)
}
