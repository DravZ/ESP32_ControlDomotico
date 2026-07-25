package com.example.prueba1.ui.theme.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardAlerta(
    alarmaActiva: Boolean,
    onToggleAlarma: (Boolean) -> Unit
) {
    // Animación y paleta de colores dinámica según el estado de la alarma
    val containerBgColor by animateColorAsState(
        targetValue = if (alarmaActiva) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
        label = "AlertaBgColor"
    )
    val cardBorderColor = if (alarmaActiva) Color(0xFFFCA5A5) else Color(0xFFBBF7D0)
    val iconBgColor = if (alarmaActiva) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
    val mainColor = if (alarmaActiva) Color(0xFFDC2626) else Color(0xFF16A34A)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerBgColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, cardBorderColor, RoundedCornerShape(24.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ----- PARTE SUPERIOR: ICONO, TÍTULOS Y BADGE -----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Contenedor del Icono
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(iconBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (alarmaActiva) Icons.Default.GppBad else Icons.Default.Shield,
                            contentDescription = null,
                            tint = mainColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Textos del Sistema
                    Column {
                        Text(
                            text = "Security System",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (alarmaActiva) "Alarm Active!" else "Alarm Disabled",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = mainColor
                        )

                        // Mensaje de detalle cuando está activa
                        if (alarmaActiva) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = mainColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Motion detected",
                                    fontSize = 12.sp,
                                    color = mainColor,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Badge Dinámico ("• Safe" / "• ALERT")
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (alarmaActiva) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                        .border(
                            1.dp,
                            if (alarmaActiva) Color(0xFFFCA5A5) else Color(0xFF86EFAC),
                            CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (alarmaActiva) "• ALERT" else "• Safe",
                        color = mainColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ----- BOTÓN INFERIOR DE ACCIÓN (Arm / Silence) -----
            if (alarmaActiva) {
                Button(
                    onClick = { onToggleAlarma(false) }, // Silence Alarm (Pasa a false)
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Silence Alarm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { onToggleAlarma(true) }, // Arm Alarm (Pasa a true)
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF16A34A)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF86EFAC))
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF16A34A)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Arm Alarm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        }
    }
}