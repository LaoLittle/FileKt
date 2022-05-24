import kotlinx.cinterop.*
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.posix.*

actual open class FilePlatform actual constructor() {
    actual val fileSystem: FileSystem
        get() = FileSystem.SYSTEM
}

actual fun currentAbsolutePath(): Path {
    val size = 4096
    val mem = __mingw_aligned_malloc((sizeOf<ByteVar>() * 4096).toULong(), alignOf<ByteVar>().toULong()) ?: throw OutOfMemoryError()
    val str = mem.reinterpret<ByteVar>()

    getcwd(str, size)

    val kString = str.toKString()
    __mingw_aligned_free(mem)
    return kString.toPath()
}