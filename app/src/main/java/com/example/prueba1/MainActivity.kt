package com.example.prueba1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prueba1.ui.theme.Prueba1Theme
import com.example.prueba1.ui.theme.components.CardAlerta
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

@OptIn(ExperimentalMaterial3Api::class)
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
            // ----- 1. BARRA DE ESTADOS DE LA CASA -----
            item(span = { GridItemSpan(2) }) {
                SeccionEstadoCasa(
                    puertaAbierta = puertaPrincipal,
                    alarmaActiva = alarmaIncendio,
                    cloudConectado = isConnectedToFirebase
                )
            }

            // ----- 2. CONTROL DE LUCES POR ZONAS -----
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

            // ----- 3. PUERTA PRINCIPAL -----
            item(span = { GridItemSpan(2) }) {
                CardPuerta(
                    abierta = puertaPrincipal,
                    onToggle = { nuevoEstado ->
                        database.child("accesos/puerta_principal").setValue(nuevoEstado)
                    }
                )
            }

            // ----- 4. SISTEMA DE ALARMA (Debajo de la Puerta) -----
            item(span = { GridItemSpan(2) }) {
                CardAlerta(
                    alarmaActiva = alarmaIncendio,
                    onToggleAlarma = { nuevoEstado ->
                        database.child("seguridad/alarma_incendio").setValue(nuevoEstado)
                    }
                )
            }
        }
    }
}