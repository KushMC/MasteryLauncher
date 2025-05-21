package com.redemastery.launcher.utils

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.system.Os
import android.util.Log
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.IResult.Companion.failed
import com.redemastery.launcher.core.IResult.Companion.loading
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.oldapi.pojav.Architecture
import com.redemastery.oldapi.pojav.Tools
import com.redemastery.oldapi.pojav.multirt.Runtime
import com.redemastery.oldapi.pojav.utils.FileUtils
import com.redemastery.oldapi.pojav.utils.MathUtils
import com.redemastery.oldapi.pojav.utils.MathUtils.RankedValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream
import org.apache.commons.io.FileUtils.deleteDirectory
import org.apache.commons.io.IOUtils.copyLarge
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.getValue

object MultiRTUtils {
    private val sCache: MutableMap<String, Runtime> = mutableMapOf()

    val RUNTIME_FOLDER = File(Tools.MULTIRT_HOME)
    private const val JAVA_VERSION_STR = "JAVA_VERSION=\""
    private const val OS_ARCH_STR = "OS_ARCH=\""

    /**
     * Lista todos os runtimes disponíveis
     */
    fun getRuntimes(): List<Runtime> =
        RUNTIME_FOLDER.takeIf { it.exists() || it.mkdirs() }?.listFiles()
            ?.map { read(it.name) }
            ?: throw RuntimeException("Failed to create or access runtime directory")

    /**
     * Retorna o nome exato de um JRE se existir
     */
    fun getExactJreName(majorVersion: Int): String? =
        getRuntimes().firstOrNull { it.javaVersion == majorVersion }?.name

    /**
     * Retorna o nome do JRE mais próximo da versão desejada
     */
    fun getNearestJreName(majorVersion: Int): String? {
        val nearest = findNearestPositive<Runtime>(
            majorVersion,
            getRuntimes().toMutableList()
        ) { it?.javaVersion ?: -1 }
        return nearest?.value?.name
    }

    fun <T> findNearestPositive(
        targetValue: Int,
        objects: MutableList<T>,
        valueProvider: MathUtils.ValueProvider<T>
    ): RankedValue<T?>? {
        var delta = Int.Companion.MAX_VALUE
        var selectedObject: T? = null
        for (`object` in objects) {
            val objectValue = valueProvider.getValue(`object`)
            if (objectValue < targetValue) continue

            val currentDelta = objectValue - targetValue
            if (currentDelta == 0) return RankedValue<T?>(`object`, 0)
            if (currentDelta >= delta) continue

            selectedObject = `object`
            delta = currentDelta
        }
        if (selectedObject == null) return null
        return RankedValue<T?>(selectedObject, delta)
    }

    /**
     * Versão legada de unpackRuntime adaptada para Kotlin + coroutines, emitindo progresso via Flow<IResult>
     */
    fun unpackRuntime(am: AssetManager): Flow<IResult<Unit>> = flow {
        emit(IResult.Loading("Verificando runtime interno", 0f))
        // 1) Lê versão declarada no APK
        var rtVersion: String? = null
        val current = readInternalRuntimeVersion("Internal")
        try {
            rtVersion = Tools.read(am.open("components/jre/version"))
        } catch (e: IOException) {
            Log.e("JREAuto", "JRE não incluído neste APK.", e)
        }
        emit(IResult.Loading("Comparando versões", 0.1f))
        val exact = getExactJreName(8)
        // 2) Condições de escape
        if (current == null && exact != null && exact != "Internal") {
            emit(success(Unit))
            return@flow
        }

        if (rtVersion == null) {
            emit(success(Unit))
            return@flow
        }
        if (rtVersion == current) {
            emit(success(Unit))
            return@flow
        }

        emit(IResult.Loading("Iniciando instalação interna", 0.2f))
        installRuntimeNamedBinpack(
            am.open("components/jre/universal.tar.xz"),
            am.open("components/jre/bin-${archAsString(Tools.DEVICE_ARCHITECTURE)}.tar.xz"),
            "Internal",
            rtVersion,
        ).collect {
            when (it) {
                is IResult.Success -> {
                    postPrepare("Internal")
                    emit(IResult.Loading("Pós-preparação interna", 0.8f))
                    emit(success(Unit))
                }

                is IResult.Loading -> emit(IResult.Loading(it.message, it.progress))
                is IResult.Failed -> {
                    Log.e("JREAuto", "Falha ao desempacotar JRE interna", it.exception)
                    emit(IResult.Failed(it.exception))
                }

                else -> {}
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Instala um runtime a partir de um URI, emitindo progresso via Flow<IResult>
     */
    fun installRuntime(context: Context, uri: Uri): Flow<IResult<Unit>> = flow {
        emit(IResult.Loading("Iniciando instalação", 0f))
        runCatching {
            val name = Tools.getFileName(context, uri)
            emit(IResult.Loading("Preparando diretório", 0.1f))

            // limpa e prepara
            val dest = File(RUNTIME_FOLDER, name).apply {
                if (exists()) deleteRecursively()
                mkdirs()
            }

            // copia para arquivo temporário
            val temp = File.createTempFile("runtime", ".tar.xz", context.cacheDir)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(temp).use { fos ->
                    input.copyTo(fos)
                }
            } ?: throw IOException("Falha ao abrir URI")

            emit(IResult.Loading("Calculando total de entradas '${temp.name}'", 0.2f))
            val total = estimateTotalEntriesProgress(temp) { entry, pct ->
                emit(loading(entry, pct))
            }

            emit(IResult.Loading("Descompactando XZ", 0.3f))
            FileInputStream(temp).use { fis ->
                uncompressTarXZ(fis, dest, total) { entry, pct ->
                    emit(IResult.Loading("Descompactando $entry", pct))
                }
            }
            temp.delete()

            emit(IResult.Loading("Pós-preparação", 0.7f))
            postPrepare(name)

        }.onSuccess {
            emit(IResult.Loading("Finalizando instalação", 0.9f))
            emit(IResult.Success(Unit))
        }.onFailure { e ->
            emit(IResult.Failed(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Instala binpack gravando 'bit_version'
     */
    @Throws(IOException::class)
    fun installRuntimeNamedBinpack(
        universal: InputStream,
        platform: InputStream,
        name: String,
        version: String
    ): Flow<IResult<Unit>> = flow {
        val dest = File(RUNTIME_FOLDER, name)
        if (dest.exists()) deleteDirectory(dest)

        emit(IResult.Loading("Calculando total de entradas '${dest.name}'", 0.2f))
        installRuntimeNoRemove(
            universal,
            dest,
            universal.available(),
            onProgress = { fileName, progress ->
                emit(loading("${dest.name}/$fileName", progress))
            })
        installRuntimeNoRemove(
            platform,
            dest,
            platform.available(),
            onProgress = { fileName, progress ->
                emit(loading("${dest.name}/$fileName", progress))
            })

        emit(IResult.Loading("Pós-preparação (200)", 0.7f))
        unpack200(Tools.NATIVE_LIB_DIR, dest.absolutePath)

        FileOutputStream(File(dest, "bit_version")).use { fos ->
            fos.write(version.toByteArray())
        }
        forceReread(name)
        emit(success(Unit))
    }.catch { e ->
        emit(failed(e))
    }.flowOn(Dispatchers.IO)

    private suspend fun installRuntimeNoRemove(
        stream: InputStream,
        dest: File,
        total: Int,
        onProgress: suspend (String, Float) -> Unit
    ) {
        uncompressTarXZ(stream, dest, total, onProgress)
        stream.close()
    }

    private fun readInternalRuntimeVersion(name: String): String? =
        File(RUNTIME_FOLDER, "$name/bit_version").takeIf { it.exists() }
            ?.let {
                try {
                    Tools.read(it.absolutePath)
                } catch (e: IOException) {
                    null
                }
            }

    /**
     * Descompacta .tar.xz com callback de progresso
     */
    private suspend fun uncompressTarXZ(
        tarInput: InputStream,
        dest: File,
        total: Int,
        onProgress: suspend (String, Float) -> Unit
    ) {
        val buffer = ByteArray(256 * 1024)
        try {
            FileUtils.ensureDirectory(dest)
            TarArchiveInputStream(XZCompressorInputStream(tarInput)).use { tar ->
                var count = 0f
                var entry = tar.nextTarEntry
                while (entry != null) {
                    val path = File(dest, entry.name)
                    FileUtils.ensureParentDirectory(path)
                    if(entry.isSymbolicLink){
                        try{
                            Os.symlink(entry.name, entry.linkName)
                        }catch (e: Throwable){
                            Log.e("MultiRT", e.toString())
                        }
                    }else if(entry.isDirectory){
                        FileUtils.ensureDirectory(path)
                    }else if((!path.exists() || path.length() != entry.size)){
                        FileOutputStream(path).use { copyLarge(tar, it, buffer) }
                    }
                    count++
                    onProgress(entry.name, (count * 1f/ total).coerceIn(0f, 1f))
                    entry = tar.nextTarEntry
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Versão simplificada de descompactação sem progresso (uso interno)
     */
    private fun uncompressTarXZBasic(tarStream: InputStream, dest: File) {
        FileUtils.ensureDirectory(dest)
        TarArchiveInputStream(XZCompressorInputStream(tarStream)).use { tarIn ->
            val buf = ByteArray(8192)
            while (true) {
                val entry = tarIn.nextTarEntry ?: break
                val path = File(dest, entry.name).apply { parentFile?.mkdirs() }
                when {
                    entry.isSymbolicLink -> runCatching { Os.symlink(entry.name, entry.linkName) }
                    entry.isDirectory -> FileUtils.ensureDirectory(path)
                    else -> FileOutputStream(path).use { copyLarge(tarIn, it, buf) }
                }
            }
        }
    }

    /**
     * Conta entradas no tar.xz
     */
    private fun estimateTotalEntries(file: File): Int =
        FileInputStream(file).use { fis ->
            TarArchiveInputStream(XZCompressorInputStream(fis)).use { tar ->
                var c = 0
                while (tar.nextTarEntry != null) c++
                c.takeIf { it > 0 } ?: 1
            }
        }

    private suspend fun estimateTotalEntriesProgress(
        file: File,
        onProgress: suspend (String, Float) -> Unit
    ): Int {
        // 1) Garante que o diretório pai exista
        file.parentFile
            ?.takeIf { !it.exists() }
            ?.apply { if (!mkdirs()) throw IOException("Falha ao criar diretório ${file.path}") }

        // 3) Agora sim faça a leitura do .tar.xz
        FileInputStream(file).use { fis ->
            TarArchiveInputStream(XZCompressorInputStream(fis)).use { tar ->
                var count = 0
                while (true) {
                    val entry = tar.nextTarEntry ?: break
                    count++
                    val pct = (count / 1000f).coerceIn(0f, 1f)
                    onProgress(entry.name, pct)
                }
                return count.takeIf { it > 0 } ?: 1
            }
        }
    }

    /**
     * Renomeia freetype e copia libs dummy
     */
    private fun postPrepare(name: String) {
        val dest = File(RUNTIME_FOLDER, name)
        if (!dest.exists()) return
        val runtime = read(name)
        var lib = "lib"
        if (File(dest, "lib/${runtime.arch}").exists()) lib = "lib/${runtime.arch}"
        val inF = File(dest, "$lib/libfreetype.so.6")
        val outF = File(dest, "$lib/libfreetype.so")
        if (inF.exists() && (!outF.exists() || inF.length() != outF.length())) {
            if (!inF.renameTo(outF)) throw IOException("Falha ao renomear freetype")
        }
        copyDummyNativeLib("libawt_xawt.so", dest, lib)
    }

    /**
     * Lê informações de um runtime existente
     */
    fun read(name: String): Runtime {
        sCache[name]?.let { return it }
        val rel = File(RUNTIME_FOLDER, "$name/release")
        val rt = if (!rel.exists()) Runtime(name) else try {
            val txt = Tools.read(rel.absolutePath)
            val jv = Tools.extractUntilCharacter(txt, JAVA_VERSION_STR, '"')
            val arch = Tools.extractUntilCharacter(txt, OS_ARCH_STR, '"')
            if (jv != null && arch != null) {
                val parts = jv.split(".", "_")
                val ver =
                    parts.getOrNull(1)?.toIntOrNull() ?: parts.getOrNull(0)?.toIntOrNull() ?: 0
                Runtime(name, jv, arch, ver)
            } else Runtime(name)
        } catch (e: IOException) {
            Runtime(name)
        }
        sCache[name] = rt
        return rt
    }


    /**
     * Força recarregar um runtime
     */
    fun forceReread(name: String): Runtime {
        sCache.remove(name)
        return read(name)
    }

    /**
     * Obtém diretório home do runtime
     */
    fun getRuntimeHome(name: String): File {
        val dest = File(RUNTIME_FOLDER, name)
        Log.i("MultiRTUtils", "Dest exists? ${dest.exists()}")
        if (!dest.exists() || forceReread(name).versionString == null)
            throw RuntimeException("Selected runtime is broken!")
        return dest
    }

    /**
     * Desempacota arquivos .pack para .jar (Java 8)
     */
    private fun unpack200(nativeDir: String, runtimePath: String) {
        val base = File(runtimePath)
        val packs = org.apache.commons.io.FileUtils.listFiles(base, arrayOf("pack"), true)
        val pb = ProcessBuilder().directory(File(nativeDir))
        packs.forEach { pack ->
            try {
                pb.command(
                    "./libunpack200.so",
                    "-r",
                    pack.absolutePath,
                    pack.absolutePath.replace(".pack", "")
                )
                    .start().waitFor()
            } catch (e: Exception) {
                Log.e("MULTIRT", "Failed to unpack the runtime!", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyDummyNativeLib(name: String, dest: File, lib: String) {
        val target = File(dest, "$lib/$name")
        FileInputStream(File(Tools.NATIVE_LIB_DIR, name)).use { fis ->
            FileOutputStream(target).use { os -> copyLarge(fis, os, ByteArray(8192)) }
        }
    }

    private fun archAsString(arch: Int): String = when (arch) {
        Architecture.ARCH_ARM -> "arm"
        Architecture.ARCH_ARM64 -> "arm64"
        Architecture.ARCH_X86 -> "x86"
        Architecture.ARCH_X86_64 -> "x86_64"
        else -> "unknown"
    }
}