import java.net.InetAddress
import java.net.Socket
import java.io.PrintWriter
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

fun main() {
    val host = "localhost"
    val puerto = 6000

    val encodedKey = "123456789123456789123456"
    val decodedKey: ByteArray = encodedKey.toByteArray()
    val originalKey: SecretKey = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

    val cipher = Cipher.getInstance("AES")

    try {
        val cliente = Socket(host, puerto)
        val salidaCliente = PrintWriter(cliente.getOutputStream(), true)
        val entradaCliente = BufferedReader(InputStreamReader(cliente.getInputStream()))

        while (true) {
            println("Escribe un mensaje para el Servidor:")
            val mensajeCliente = readLine()

            cipher.init(Cipher.ENCRYPT_MODE, originalKey)
            val cipherBytes = cipher.doFinal(mensajeCliente!!.toByteArray(StandardCharsets.UTF_8))
            val mensajeCifrado = Base64.getEncoder().encodeToString(cipherBytes)
            salidaCliente.println(mensajeCifrado)

            val mensajeServidorCifrado = entradaCliente.readLine()
            println("Mensaje cifrado del Servidor: $mensajeServidorCifrado")

            val mensajeServidorBytes = Base64.getDecoder().decode(mensajeServidorCifrado)
            cipher.init(Cipher.DECRYPT_MODE, originalKey)
            val mensajeServidor = String(cipher.doFinal(mensajeServidorBytes), StandardCharsets.UTF_8)
            println("Mensaje descifrado del Servidor: $mensajeServidor")
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }
}
