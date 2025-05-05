import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*


fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Buscaminas") {
        BuscaminasApp()
    }
}

@Composable
fun BuscaminasApp() {
    var filas by remember { mutableStateOf("8") }
    var columnas by remember { mutableStateOf("8") }
    var minas by remember { mutableStateOf("10") }
    var tablero by remember { mutableStateOf(arrayOf<Array<Celda>>()) }
    var estadoJuego by remember { mutableStateOf("Jugando...") }
    var juegoIniciado by remember { mutableStateOf(false) }
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }

    LaunchedEffect(Unit) {
        while (true) {
            horaActual = obtenerHoraActual()
            delay(1000L)
        }
    }

    val buscaminas = remember { Busacaminas() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Buscaminas", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Hora: $horaActual")

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            OutlinedTextField(value = filas, onValueChange = { filas = it }, label = { Text("Filas") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp).height(5.dp))
            OutlinedTextField(value = columnas, onValueChange = { columnas = it }, label = { Text("Columnas") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp).height(5.dp))
            OutlinedTextField(value = minas, onValueChange = { minas = it }, label = { Text("Minas") }, modifier = Modifier.weight(1f))
        }
        Button(onClick = {
            val f = filas.toIntOrNull() ?: 8
            val c = columnas.toIntOrNull() ?: 8
            val m = minas.toIntOrNull() ?: 10
            tablero = buscaminas.generarTablero(f, c)
            buscaminas.colocarMinas(tablero, m)
            estadoJuego = "Jugando..."
            juegoIniciado = true
        }) {
            Text("Iniciar Juego")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = estadoJuego, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        if (juegoIniciado) {
            tablero.forEachIndexed { i, fila ->
                Row {
                    fila.forEachIndexed { j, _ ->
                        CeldaUI(
                            celda = tablero[i][j],
                            tablero = tablero,
                            x = i,
                            y = j,
                            onLongClick = {
                                tablero = tablero.map { it.copyOf() }.toTypedArray()
                                if (!tablero[i][j].descubierta) {
                                    tablero[i][j].marcar()
                                }
                            },
                             onClick = {
                                if (tablero[i][j].marcada) return@CeldaUI
                                tablero = tablero.map { it.copyOf() }.toTypedArray()
                                buscaminas.descubrirCelda(tablero, i, j)
                                if (buscaminas.comprobarDerrota(tablero, i, j)) {
                                    estadoJuego = "¡Perdiste!"
                                } else if (buscaminas.comprobarVictoria(tablero)) {
                                    estadoJuego = "¡Ganaste!"
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

    }
}
@Composable
fun CeldaUI(
    celda: Celda,
    tablero: Array<Array<Celda>>,
    x: Int,
    y: Int,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val color = when {
        celda.descubierta && celda.tieneMina() -> Color.Red
        celda.descubierta -> Color.LightGray
        celda.marcada -> Color.Yellow
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color, shape = RoundedCornerShape(4.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (celda.descubierta) {
            Text(if (celda.tieneMina()) "*" else celda.calcularminasAlrededor(tablero, x, y).toString())
        } else if (celda.marcada) {
            Text("M")
        }
    }
}
fun obtenerHoraActual(): String {
    val formato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formato.format(Date())
}
