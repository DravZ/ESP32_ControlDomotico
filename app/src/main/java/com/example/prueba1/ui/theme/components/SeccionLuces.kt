package com.example.prueba1.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colores personalizados del diseño de las imágenes
private val BluePrimary = Color(0xFF2563EB)
private val LightBlueBg = Color(0xFFEBF2FF)
private val GrayBg = Color(0xFFF8FAFC)
private val IconCircleOff = Color(0xFFE2E8F0)
private val TextGray = Color(0xFF64748B)

@Composable
fun SeccionLuces(
    luzSala: Boolean,
    luzRecamara: Boolean,
    onToggleSala: (Boolean) -> Unit,
    onToggleRecamara: (Boolean) -> Unit
) {
    // Cálculo dinámico del contador para el encabezado (ejemplo: 0/2 o 1/2)
    val lucesEncendidas = listOf(luzSala, luzRecamara).count { it }

    Column(modifier = Modifier.fillMaxWidth()) {
        // ----- ENCABEZADO CON CONTADOR "Lighting Control · X/2" -----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Lighting Control",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = " · $lucesEncendidas/2",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            }
            TextButton(onClick = { /* Acción opcional */ }) {
                Text(
                    text = "See all",
                    color = BluePrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ----- REJILLA DE TARJETAS DE LUCES -----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                LuzCardItem(
                    title = "Living Room",
                    icon = Icons.Default.Lightbulb,
                    isOn = luzSala,
                    onToggle = onToggleSala
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                LuzCardItem(
                    title = "Bedroom",
                    icon = Icons.Default.Bed,
                    isOn = luzRecamara,
                    onToggle = onToggleRecamara
                )
            }
        }
    }
}

@Composable
fun LuzCardItem(
    title: String,
    icon: ImageVector,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOn) LightBlueBg else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isOn) 0.dp else 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Indicador / Punto azul en la esquina superior derecha si está activo
            if (isOn) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(BluePrimary)
                        .align(Alignment.TopEnd)
                )
            }

            Column {
                // Icono dentro de círculo estilizado
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isOn) BluePrimary else IconCircleOff),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isOn) Color.White else TextGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título del cuarto
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isOn) BluePrimary else Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Fila inferior: Estado ("Off" / "On") y el Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOn) "On" else "Off",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isOn) BluePrimary.copy(alpha = 0.8f) else TextGray
                    )

                    Switch(
                        checked = isOn,
                        onCheckedChange = { onToggle(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BluePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1),
                            uncheckedBorderColor = Color.Transparent,
                            checkedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}