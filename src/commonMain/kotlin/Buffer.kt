import okio.Buffer
import okio.Sink
import okio.Source

const val DEFAULT_BUFFER_SIZE: Long = 8 * 1024

fun Source.copyTo(out: Sink, bufferSize: Long = DEFAULT_BUFFER_SIZE): Long {
    var bytesCopied: Long = 0
    val buffer = Buffer()
    var bytes = read(buffer, bufferSize)

    while (bytes >= 0) {
        bytesCopied += bytes
        bytes = read(buffer, bufferSize)
    }

    out.write(buffer, buffer.size)

    return bytesCopied
}