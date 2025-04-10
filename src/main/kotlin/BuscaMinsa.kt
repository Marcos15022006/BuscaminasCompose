import kotlin.random.Random

class MiExcepcion(mensaje: String): Exception(mensaje)

class Celda(val mina: Boolean, var descubierta: Boolean, var marcada: Boolean){
    fun descubrir(){
        descubierta = true
    }
    fun marcar(){
        if(marcada) marcada=false
        else marcada=true
    }
    fun tieneMina(): Boolean{
        return mina
    }

    fun calcularminasAlrededor(tablero: Array<Array<Celda>>,a: Int,b: Int): Int {
        var minas = 0
        for (i in -1..1) {
            for (j in -1..1) {
                if (i != 0 || j != 0) {
                    val x = a+i
                    val y = b+j
                    if (x >= 0 && x < tablero.size && y >= 0 && y < tablero[0].size) {
                        if (tablero[x][y].tieneMina()) {
                            minas++
                        }
                    }
                }
            }
        }
        return minas
    }
}

class Busacaminas {
    fun generarTablero(alto: Int, ancho: Int): Array<Array<Celda>> {
        if(alto<0 || ancho<0) throw MiExcepcion("no se pueden poner valores negativos para el alto o el ancho")
        val tablero = Array(alto) { Array(ancho) { Celda(false, false, false) } }
        return tablero
    }
    fun  colocarMinas(tablero: Array<Array<Celda>>, minas: Int) {
        if(minas>tablero.size*tablero[0].size||minas<0) throw MiExcepcion("Error en número de minas; o son negativas, o hay demasiadas para el tablero")
        var minasColocadas = 0
        while (minasColocadas < minas) {
            val x = Random.nextInt(tablero.size)
            val y = Random.nextInt(tablero[0].size)
            if (!tablero[x][y].tieneMina()) {
                tablero[x][y] = Celda(true, false, false)
                minasColocadas++
            }
        }
    }
    fun imprimirTablero(tablero: Array<Array<Celda>>) {
        for (i in tablero.indices) {
            for (j in tablero[i].indices) {
                if (tablero[i][j].descubierta) {
                    if(tablero[i][j].mina){
                        print("*  ")
                    } else {
                        print(tablero[i][j].calcularminasAlrededor(tablero,i,j).toString() + "  ")
                    }
                }
                else if(tablero[i][j].marcada) {
                    print("M  ")
                }
                else {
                    print("?  ")
                }
            }
            println()
        }
    }
    fun descubrirCelda(tablero: Array<Array<Celda>>, x: Int, y: Int) {
            tablero[x][y].descubrir()
            if (tablero[x][y].calcularminasAlrededor(tablero,x,y) == 0) {
                for (i in -1..1) {
                    for (j in -1..1) {
                        if (i != 0 || j != 0) {
                            val a = x+i
                            val b = y+j
                            if (a >= 0 && a < tablero.size && b >= 0 && b < tablero[0].size) {
                                if (!tablero[a][b].descubierta) {
                                    descubrirCelda(tablero, a, b)
                                }
                            }
                        }
                    }
                }
            }
    }
    fun comprobarVictoria(tablero: Array<Array<Celda>>): Boolean {
        for (i in tablero.indices) {
            for (j in tablero[i].indices) {
                if (!tablero[i][j].descubierta && !tablero[i][j].tieneMina()) {
                    return false
                }
            }
        }
        return true
    }
    fun comprobarDerrota(tablero: Array<Array<Celda>>, x: Int, y: Int): Boolean {
        if (tablero[x][y].tieneMina()) {
            println("¡Has perdido!")
            desvelartablero(tablero)
            imprimirTablero(tablero)
            return true
        }
        return false
    }
    private fun desvelartablero(tablero: Array<Array<Celda>>) {
        for (i in tablero.indices) {
            for (j in tablero[i].indices) {
                tablero[i][j].descubrir()
            }
        }
    }

}