package com.example.prueba1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba1.ui.theme.Prueba1Theme
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prueba1Theme() {
                DashboardDomotico()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDomotico() {
    // 1. Manejo de Estados con remember y mutableStateOf
    var luzSala by remember { mutableStateOf(false) }
    var luzRecamara by remember { mutableStateOf(false) }
    var alarmaIncendio by remember { mutableStateOf(false) }
    var puertaPrincipal by remember { mutableStateOf(false) }

    // 2. Referencia a la raíz de la casa inteligente en Firebase Realtime Database
    val database = remember { Firebase.database.reference.child("casa_inteligente") }

    // 3. Escuchar la base de datos en tiempo real con LaunchedEffect
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                luzSala = snapshot.child("luces/sala").getValue(Boolean::class.java) ?: false
                luzRecamara = snapshot.child("luces/recamara").getValue(Boolean::class.java) ?: false
                alarmaIncendio = snapshot.child("seguridad/alarma_incendio").getValue(Boolean::class.java) ?: false
                puertaPrincipal = snapshot.child("accesos/puerta_principal").getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores de lectura
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Domótico IoT", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ----- PANEL DE ALERTA (Ocupa las 2 columnas) -----
            item(span = { GridItemSpan(2) }) {
                CardAlerta(
                    alarmaActiva = alarmaIncendio,
                    onSilenciar = {
                        database.child("seguridad/alarma_incendio").setValue(false)
                    }
                )
            }

            // ----- CONTROL DE LUCES POR ZONAS -----
            item {
                CardControl(
                    title = "Luz Sala",
                    icon = Icons.Default.Lightbulb,
                    isOn = luzSala,
                    onToggle = { nuevoEstado ->
                        database.child("luces/sala").setValue(nuevoEstado)
                    }
                )
            }

            item {
                CardControl(
                    title = "Luz Recámara",
                    icon = Icons.Default.Bed,
                    isOn = luzRecamara,
                    onToggle = { nuevoEstado ->
                        database.child("luces/recamara").setValue(nuevoEstado)
                    }
                )
            }

            // ----- ACCESO (Puerta Principal - Ocupa 2 columnas) -----
            item(span = { GridItemSpan(2) }) {
                CardPuerta(
                    abierta = puertaPrincipal,
                    onToggle = { nuevoEstado ->
                        database.child("accesos/puerta_principal").setValue(nuevoEstado)
                    }
                )
            }
        }
    }
}

@Composable
fun CardAlerta(alarmaActiva: Boolean, onSilenciar: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (alarmaActiva) Color(0xFFFFCDD2) else Color(0xFFE8F5E9),
        label = "AlertaColor"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (alarmaActiva) Icons.Default.Warning else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (alarmaActiva) Color.Red else Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (alarmaActiva) "¡ALERTA DE INCENDIO!" else "Sistema de Seguridad OK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (alarmaActiva) Color.Red else Color(0xFF2E7D32)
                )
            }

            if (alarmaActiva) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onSilenciar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.NotificationsOff, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SILENCIAR ALARMA")
                }
            }
        }
    }
}

@Composable
fun CardControl(
    title: String,
    icon: ImageVector,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isOn) Color(0xFFFFB300) else Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Switch(
                checked = isOn,
                onCheckedChange = { nuevoValor -> onToggle(nuevoValor) }
            )
        }
    }
}

@Composable
fun CardPuerta(abierta: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (abierta) Icons.Default.DoorSliding else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (abierta) Color(0xFF1976D2) else Color.Gray,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Puerta Principal", fontWeight = FontWeight.Bold)
                    Text(
                        text = if (abierta) "Estado: Abierta" else "Estado: Bloqueada",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Switch(
                checked = abierta,
                onCheckedChange = { nuevoValor -> onToggle(nuevoValor) }
            )
        }
    }
}