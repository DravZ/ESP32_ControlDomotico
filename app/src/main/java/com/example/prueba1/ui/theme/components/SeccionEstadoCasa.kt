package com.example.prueba1.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.GppBad
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

@Composable
fun SeccionEstadoCasa(
    puertaAbierta: Boolean,
    alarmaActiva: Boolean,
    cloudConectado: Boolean
) {
    val todoOk = !puertaAbierta && !alarmaActiva && cloudConectado

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // ----- ENCABEZADO CON BADGE "All Good" O "Warning" -----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "House Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                // Badge Dinámico
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (todoOk) Color(0xFFDCFCE7) else Color(0xFFFEE2E2))
                        .border(
                            1.dp,
                            if (todoOk) Color(0xFF86EFAC) else Color(0xFFFCA5A5),
                            CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (todoOk) "• All Good" else "• Alert",
                        color = if (todoOk) Color(0xFF166534) else Color(0xFF991B1B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ----- TRES INDICADORES INFERIORES -----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Estado de la Puerta
                EstadoItem(
                    title = "Door",
                    subtitle = if (puertaAbierta) "Unlocked" else "Locked",
                    icon = if (puertaAbierta) Icons.Default.LockOpen else Icons.Default.Lock,
                    iconBgColor = if (puertaAbierta) Color(0xFFFEF3C7) else Color(0xFFF0FDF4),
                    contentColor = if (puertaAbierta) Color(0xFFD97706) else Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )

                // 2. Estado de la Alarma
                EstadoItem(
                    title = "Alarm",
                    subtitle = if (alarmaActiva) "Active" else "Inactive",
                    icon = if (alarmaActiva) Icons.Default.GppBad else Icons.Default.Shield,
                    iconBgColor = if (alarmaActiva) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                    contentColor = if (alarmaActiva) Color(0xFFDC2626) else Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )

                // 3. Estado de la Nube (Firebase Realtime Database)
                EstadoItem(
                    title = "Cloud",
                    subtitle = if (cloudConectado) "Connected" else "Disconnected",
                    icon = if (cloudConectado) Icons.Default.CloudQueue else Icons.Default.CloudOff,
                    iconBgColor = if (cloudConectado) Color(0xFFEFF6FF) else Color(0xFFF1F5F9),
                    contentColor = if (cloudConectado) Color(0xFF2563EB) else Color(0xFF64748B),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EstadoItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBgColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF0F172A)
        )

        Text(
            text = subtitle,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = contentColor
        )
    }
}