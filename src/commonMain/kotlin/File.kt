import okio.*
import okio.Path.Companion.toPath

expect open class FilePlatform() {
    val fileSystem: FileSystem
}

class File(val path: Path) : FilePlatform() {
    constructor(name: String, normalize: Boolean) : this(name.toPath(normalize))
    constructor(name: String) : this(name, false)

    val name: String
        get() {
            val pathString = path.toString()
            val index = pathString.lastIndexOf(Path.DIRECTORY_SEPARATOR)
            return if (index < 0) pathString
            else pathString.substring(index + 1)
        }

    inline val absoluteFile get() = File(absolutePath)

    inline val absolutePath get() = currentAbsolutePath().resolve(name)

    /**
     * Description of a file or another object referenced by a path.
     *
     * In simple use a file system is a mechanism for organizing files and directories on a local
     * storage device. In practice file systems are more capable and their contents more varied. For
     * example, a path may refer to:
     *
     *  * An operating system process that consumes data, produces data, or both. For example, reading
     *    from the `/dev/urandom` file on Linux returns a unique sequence of pseudorandom bytes to each
     *    reader.
     *
     *  * A stream that connects a pair of programs together. A pipe is a special file that a producing
     *    program writes to and a consuming program reads from. Both programs operate concurrently. The
     *    size of a pipe is not well defined: the writer can write as much data as the reader is able to
     *    read.
     *
     *  * A file on a remote file system. The performance and availability of remote files may be quite
     *    different from that of local files!
     *
     *  * A symbolic link (symlink) to another path. When attempting to access this path the file system
     *    will follow the link and return data from the target path.
     *
     *  * The same content as another path without a symlink. On UNIX file systems an inode is an
     *    anonymous handle to a file's content, and multiple paths may target the same inode without any
     *    other relationship to one another. A consequence of this design is that a directory with three
     *    1 GiB files may only need 1 GiB on the storage device.
     *
     * This class does not attempt to model these rich file system features! It exposes a limited view
     * useful for programs with only basic file system needs. Be cautious of the potential consequences
     * of special files when writing programs that operate on a file system.
     *
     * File metadata is subject to change, and code that operates on file systems should defend against
     * changes to the file that occur between reading metadata and subsequent operations.
     * @see FileMetadata
     */
    inline val metadata get() = fileSystem.metadata(path)

    /**
     * True if the path refers to a directory that contains 0 or more child paths.
     *
     * Note that a path does not need to be a directory for [FileSystem.list] to return successfully.
     * For example, mounted storage devices may have child files, but do not identify themselves as
     * directories.
     * @see FileMetadata.isDirectory
     */
    inline val isDirectory get() = metadata.isDirectory

    /**
     * True if this file is a container of bytes. If this is true, then [FileMetadata.size] is non-null.
     * @see FileMetadata.isRegularFile
     */
    inline val isRegularFile get() = metadata.isRegularFile

    inline val isFile get() = exists && !isDirectory

    inline fun sink() = fileSystem.sink(path)

    fun source(): Source {
        if (!exists) throw FileNotFoundException("File $path not found")
        return fileSystem.source(path)
    }

    inline val exists get() = fileSystem.exists(path)

    inline fun resolve(child: String, normalize: Boolean = false) = File(path.resolve(child, normalize))

    inline operator fun div(child: String) = resolve(child)

    override fun toString(): String = path.toString()
}

inline fun File.writeText(text: String) = writeBytes(text.encodeToByteArray())

fun File.writeBytes(bytes: ByteArray) {
    val buffer = Buffer().write(bytes)
    buffer.use { buf ->
        sink().use { sink ->
            sink.write(buf, buf.size)
        }
    }
}

fun File.readAllBytes(): ByteArray {
    val buffer = Buffer()

    source().use {
        it.read(buffer, metadata.size!!)
    }

    return buffer.readByteArray()
}

fun File.readBytes(): ByteArray {
    val buffer = Buffer()

    source().use {
        it.copyTo(buffer)
    }

    return buffer.readByteArray()
}

expect fun currentAbsolutePath(): Path