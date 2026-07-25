package com.example.prueba1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prueba1.ui.theme.Prueba1Theme
import com.example.prueba1.ui.theme.components.CardAlerta
import com.example.prueba1.ui.theme.components.CardBienvenida
import com.example.prueba1.ui.theme.components.CardPuerta
import com.example.prueba1.ui.theme.components.SeccionEstadoCasa
import com.example.prueba1.ui.theme.components.SeccionLuces
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
            Prueba1Theme {
                DashboardDomotico()
            }
        }
    }
}

@Composable
fun DashboardDomotico() {
    var luzSala by remember { mutableStateOf(false) }
    var luzRecamara by remember { mutableStateOf(false) }
    var alarmaIncendio by remember { mutableStateOf(false) }
    var puertaPrincipal by remember { mutableStateOf(false) }
    var isConnectedToFirebase by remember { mutableStateOf(false) }

    val database = remember { Firebase.database.reference.child("casa_inteligente") }
    val connectedRef = remember { Firebase.database.reference.child(".info/connected") }

    LaunchedEffect(Unit) {
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isConnectedToFirebase = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                isConnectedToFirebase = false
            }
        })

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                luzSala = snapshot.child("luces/sala").getValue(Boolean::class.java) ?: false
                luzRecamara = snapshot.child("luces/recamara").getValue(Boolean::class.java) ?: false
                alarmaIncendio = snapshot.child("seguridad/alarma_incendio").getValue(Boolean::class.java) ?: false
                puertaPrincipal = snapshot.child("accesos/puerta_principal").getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC) // Fondo gris claro moderno
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ----- NAVBAR PERSONALIZADO CON INICIALES "CD" -----
            HeaderNav(isConnected = isConnectedToFirebase, iniciales = "CD")

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ----- 1. TARJETA DE BIENVENIDA -----
                item(span = { GridItemSpan(2) }) {
                    CardBienvenida(
                        nombreUsuario = "Carlos",
                        todoOk = !alarmaIncendio && !puertaPrincipal
                    )
                }

                // ----- 2. BARRA DE ESTADOS DE LA CASA -----
                item(span = { GridItemSpan(2) }) {
                    SeccionEstadoCasa(
                        puertaAbierta = puertaPrincipal,
                        alarmaActiva = alarmaIncendio,
                        cloudConectado = isConnectedToFirebase
                    )
                }

                // ----- 3. CONTROL DE LUCES POR ZONAS -----
                item(span = { GridItemSpan(2) }) {
                    SeccionLuces(
                        luzSala = luzSala,
                        luzRecamara = luzRecamara,
                        onToggleSala = { nuevoEstado ->
                            database.child("luces/sala").setValue(nuevoEstado)
                        },
                        onToggleRecamara = { nuevoEstado ->
                            database.child("luces/recamara").setValue(nuevoEstado)
                        }
                    )
                }

                // ----- 4. PUERTA PRINCIPAL -----
                item(span = { GridItemSpan(2) }) {
                    CardPuerta(
                        abierta = puertaPrincipal,
                        onToggle = { nuevoEstado ->
                            database.child("accesos/puerta_principal").setValue(nuevoEstado)
                        }
                    )
                }

                // ----- 5. SISTEMA DE ALARMA -----
                item(span = { GridItemSpan(2) }) {
                    CardAlerta(
                        alarmaActiva = alarmaIncendio,
                        onToggleAlarma = { nuevoEstado ->
                            database.child("seguridad/alarma_incendio").setValue(nuevoEstado)
                        }
                    )
                }

                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ----- COMPONENTE DEL NAVBAR ESTILIZADO -----
@Composable
fun HeaderNav(isConnected: Boolean, iniciales: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado Izquierdo: Icono Azul + Título + Subtítulo de Conexión
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "My Smart Home",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = if (isConnected) "• Connected" else "• Disconnected",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isConnected) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            }
        }

        // Lado Derecho: Avatar con las iniciales (CD)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = iniciales,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}