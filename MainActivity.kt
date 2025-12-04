package com.example.proyectofinal

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

// --- MODELO DE DATOS ---
data class Producto(
    val id: String,
    val nombre: String,
    val precio: Double,
    val descripcion: String
)

// Datos en PESOS MEXICANOS
val listaProductos = listOf(
    Producto("PS5", "PlayStation 5 Slim", 10999.00, "Juegos en 4K, Ray Tracing, 1TB SSD. Edición Standard."),
    Producto("XBOX", "Xbox Series X", 11699.00, "La Xbox más potente. 12 TFLOPS, 4K nativo y Game Pass Ultimate."),
    Producto("SWITCH", "Nintendo Switch OLED", 6499.00, "Pantalla OLED de 7 pulgadas, audio mejorado y base con puerto LAN."),
    Producto("PC", "PC Gamer RTX 4070", 34500.00, "Intel Core i9 12th, 32GB RAM, NVIDIA RTX 4070, 2TB SSD NVMe.")
)

// --- COLORES GAMER ---
val ColorFondo = Color(0xFF121212)
val ColorCard = Color(0xFF1E1E1E)
val ColorAcento = Color(0xFF00E676)
val ColorTexto = Color.White

fun formatearPrecio(precio: Double): String {
    return String.format(Locale.US, "$%,.2f MXN", precio)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                TiendaGamerApp()
            }
        }
    }
}

// --- NAVEGACIÓN ---
enum class Pantalla { MENU, CARRITO, ENVIO, FIRMA, EXITO }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TiendaGamerApp() {
    var pantallaActual by remember { mutableStateOf(Pantalla.MENU) }
    var carrito by remember { mutableStateOf(listOf<Producto>()) }
    val total by remember { derivedStateOf { carrito.sumOf { it.precio } } }

    Scaffold(
        containerColor = ColorFondo,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CYBER STORE MX", color = ColorAcento, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ColorFondo),
                actions = {
                    if (pantallaActual == Pantalla.MENU) {
                        BadgedBox(
                            badge = { if (carrito.isNotEmpty()) Badge { Text("${carrito.size}") } }
                        ) {
                            IconButton(onClick = { pantallaActual = Pantalla.CARRITO }) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito", tint = ColorTexto)
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (pantallaActual) {
                Pantalla.MENU -> PantallaMenu(
                    onAgregar = { carrito = carrito + it },
                    productos = listaProductos
                )
                Pantalla.CARRITO -> PantallaCarrito(
                    carrito = carrito,
                    total = total,
                    onEliminar = { prod -> carrito = carrito - prod },
                    onPagar = { if (carrito.isNotEmpty()) pantallaActual = Pantalla.ENVIO },
                    onVolver = { pantallaActual = Pantalla.MENU }
                )
                Pantalla.ENVIO -> PantallaEnvio(
                    onContinuar = { pantallaActual = Pantalla.FIRMA },
                    onVolver = { pantallaActual = Pantalla.CARRITO }
                )
                Pantalla.FIRMA -> PantallaFirma(
                    total = total,
                    onConfirmarFirma = {
                        pantallaActual = Pantalla.EXITO
                        carrito = emptyList()
                    },
                    onCancelar = { pantallaActual = Pantalla.ENVIO }
                )
                Pantalla.EXITO -> PantallaExito(
                    onInicio = { pantallaActual = Pantalla.MENU }
                )
            }
        }
    }
}

//  PANTALLA 1: MENÚ
@Composable
fun PantallaMenu(onAgregar: (Producto) -> Unit, productos: List<Producto>) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(productos) { producto ->
            Card(
                colors = CardDefaults.cardColors(containerColor = ColorCard),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(producto.nombre, style = MaterialTheme.typography.titleMedium, color = ColorTexto, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f))
                        Text(formatearPrecio(producto.precio), style = MaterialTheme.typography.titleMedium, color = ColorAcento, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.3f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(producto.descripcion, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onAgregar(producto) }, colors = ButtonDefaults.buttonColors(containerColor = ColorAcento), modifier = Modifier.align(Alignment.End)) {
                        Text("AGREGAR +", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

//  PANTALLA 2: CARRITO
@Composable
fun PantallaCarrito(carrito: List<Producto>, total: Double, onEliminar: (Producto) -> Unit, onPagar: () -> Unit, onVolver: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tu Carrito", style = MaterialTheme.typography.headlineMedium, color = ColorTexto)
        Spacer(modifier = Modifier.height(16.dp))
        if (carrito.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { Text("Vacío", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(carrito) { producto ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(ColorCard, RoundedCornerShape(8.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(producto.nombre, color = ColorTexto, fontWeight = FontWeight.Bold)
                            Text(formatearPrecio(producto.precio), color = ColorAcento)
                        }
                        IconButton(onClick = { onEliminar(producto) }) { Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red) }
                    }
                }
            }
        }
        Text("Total: ${formatearPrecio(total)}", style = MaterialTheme.typography.headlineMedium, color = ColorAcento, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onVolver) { Text("Atrás", color = ColorTexto) }
            Button(onClick = onPagar, colors = ButtonDefaults.buttonColors(containerColor = ColorAcento), enabled = carrito.isNotEmpty()) {
                Text("DATOS DE ENVÍO", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

//  PANTALLA 3: DATOS DE ENVÍO (GPS + GEOLOCALIZACION)
@SuppressLint("MissingPermission")
@Composable
fun PantallaEnvio(onContinuar: () -> Unit, onVolver: () -> Unit) {
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var numExt by remember { mutableStateOf("") }
    var numInt by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var ubicacionGPS by remember { mutableStateOf("Sin ubicación detectada") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            Toast.makeText(context, "Permiso concedido. Presiona el botón de nuevo.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Datos de Envío", style = MaterialTheme.typography.headlineMedium, color = ColorTexto)
        Spacer(modifier = Modifier.height(20.dp))

        CampoTextoGamer("Nombre Completo", nombre) { nombre = it }
        CampoTextoGamer("Dirección (Se llena con GPS)", direccion) { direccion = it }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CampoTextoGamer("Num. Ext", numExt, Modifier.weight(1f)) { numExt = it }
            Spacer(modifier = Modifier.width(8.dp))
            CampoTextoGamer("Num. Int", numInt, Modifier.weight(1f)) { numInt = it }
        }
        CampoTextoGamer("Teléfono", telefono, isNumber = true) { telefono = it }
        CampoTextoGamer("Correo Electrónico", correo) { correo = it }

        Spacer(modifier = Modifier.height(16.dp))

        // Tarjeta GPS
        Card(
            colors = CardDefaults.cardColors(containerColor = ColorCard),
            modifier = Modifier.fillMaxWidth().border(1.dp, ColorAcento, RoundedCornerShape(8.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = ColorAcento)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ubicación de entrega", color = ColorTexto, fontWeight = FontWeight.Bold)
                }

                // Muestra la dirección exacta
                Text(text = ubicacionGPS, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))

                Button(
                    onClick = {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            ubicacionGPS = "Localizando..."

                            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener { location ->
                                if (location != null) {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            val geocoder = Geocoder(context, Locale.getDefault())
                                            @Suppress("DEPRECATION")
                                            val direcciones = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                                            withContext(Dispatchers.Main) {
                                                if (!direcciones.isNullOrEmpty()) {
                                                    val dir = direcciones[0]
                                                    val direccionCompleta = dir.getAddressLine(0) ?: "Dirección encontrada sin texto"

                                                    // Actualiza los campos
                                                    direccion = direccionCompleta
                                                    ubicacionGPS = "📍 $direccionCompleta"
                                                } else {
                                                    ubicacionGPS = "Coord: ${location.latitude}, ${location.longitude}"
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                ubicacionGPS = "Error: Verifica tu conexión a internet"
                                            }
                                        }
                                    }
                                } else {
                                    ubicacionGPS = "No se pudo obtener ubicación. Activa el GPS."
                                }
                            }
                        } else {
                            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("📍 USAR MI UBICACIÓN ACTUAL", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onVolver) { Text("Cancelar", color = ColorTexto) }
            Button(
                onClick = onContinuar,
                colors = ButtonDefaults.buttonColors(containerColor = ColorAcento),
                enabled = nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty()
            ) {
                Text("IR A FIRMAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CampoTextoGamer(label: String, value: String, modifier: Modifier = Modifier, isNumber: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAcento,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = ColorTexto,
            unfocusedTextColor = ColorTexto,
            cursorColor = ColorAcento
        ),
        keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        singleLine = true
    )
}

//  PANTALLA 4: FIRMA DIGITAL
@Composable
fun PantallaFirma(total: Double, onConfirmarFirma: () -> Unit, onCancelar: () -> Unit) {
    val puntos = remember { mutableStateListOf<Offset>() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Autorizar Compra", style = MaterialTheme.typography.headlineMedium, color = ColorTexto)
        Text("Total: ${formatearPrecio(total)}", color = ColorAcento, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Firma de recibido:", color = Color.Gray)

        Box(
            modifier = Modifier.fillMaxWidth().height(400.dp)
                .background(Color.Black, RoundedCornerShape(12.dp))
                .border(2.dp, ColorAcento, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, _ -> puntos.add(change.position) }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawPoints(points = puntos, pointMode = PointMode.Polygon, color = Color.White, strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
            }
            TextButton(onClick = { puntos.clear() }, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                Text("Borrar", color = Color.Red)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlinedButton(onClick = onCancelar) { Text("Atrás", color = ColorTexto) }
            Button(onClick = onConfirmarFirma, colors = ButtonDefaults.buttonColors(containerColor = ColorAcento), enabled = puntos.size > 10) {
                Text("FINALIZAR PEDIDO", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

//  PANTALLA 5: ÉXITO
@Composable
fun PantallaExito(onInicio: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("¡PEDIDO ENVIADO!", style = MaterialTheme.typography.headlineLarge, color = ColorAcento, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tu consola llegará pronto.", color = ColorTexto)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onInicio, colors = ButtonDefaults.buttonColors(containerColor = Color.White)) {
            Text("VOLVER AL MENÚ", color = Color.Black)
        }
    }
}