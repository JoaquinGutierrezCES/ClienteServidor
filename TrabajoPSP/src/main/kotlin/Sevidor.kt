import java.net.ServerSocket
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val puerto = 6000
    val listaMensajes = mutableListOf<String>()
    val executor = Executors.newSingleThreadScheduledExecutor()

    val encodedKey = "123456789123456789123456"
    val decodedKey: ByteArray = encodedKey.toByteArray()
    val originalKey: SecretKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
    val cipher = Cipher.getInstance("AES")

    try {
        val servidor = ServerSocket(puerto)
        println("Escuchando en ${servidor.localPort}")

        val hiloClientes = Executors.newFixedThreadPool(2)

        executor.scheduleAtFixedRate({
            println("Mensaje del cliente:")
            listaMensajes.forEach { println(it) }
        }, 0, 1, TimeUnit.SECONDS)

        while (true) {
            val cliente = servidor.accept()
            println("Nuevo cliente conectado desde ${cliente.inetAddress.hostAddress}")

            hiloClientes.execute {
                val entrada = BufferedReader(InputStreamReader(cliente.getInputStream()))

                while (true) {
                    val mensajeCifrado = entrada.readLine() ?: continue
                    println("Mensaje cifrado recibido: $mensajeCifrado")

                    val mensajeCifradoBytes = Base64.getDecoder().decode(mensajeCifrado)
                    cipher.init(Cipher.DECRYPT_MODE, originalKey)
                    val mensaje = String(cipher.doFinal(mensajeCifradoBytes), StandardCharsets.UTF_8)
                    println("Mensaje descifrado: $mensaje")

                    listaMensajes.add(mensaje)
                }
            }
        }

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        executor.shutdown()
    }
}
