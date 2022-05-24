import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.linux.free
import platform.linux.malloc
import platform.posix.aligned_alloc
import platform.posix.getcwd

actual open class FilePlatform actual constructor() {
    actual val fileSystem: FileSystem
        get() = FileSystem.SYSTEM
}

actual fun currentAbsolutePath(): Path {
    val size = 4096
    val mem = malloc((sizeOf<ByteVar>() * 4096).toULong()) ?: throw OutOfMemoryError()
    val str = mem.reinterpret<ByteVar>()

    getcwd(str, size.toULong())

    val kString = str.toKString()

    free(str)
    return kString.toPath()
}