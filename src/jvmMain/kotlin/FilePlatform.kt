import okio.FileSystem
import okio.Path

actual open class FilePlatform actual constructor() {
    actual val fileSystem: FileSystem
        get() = TODO("Not yet implemented")
}

actual fun currentAbsolutePath(): Path {
    TODO("Not yet implemented")
}