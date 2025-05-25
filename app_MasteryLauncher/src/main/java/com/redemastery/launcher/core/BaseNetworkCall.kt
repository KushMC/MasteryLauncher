package com.redemastery.launcher.core

import com.redemastery.launcher.core.IResult.Companion.failed
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.oldapi.pojav.Tools
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

typealias NetworkAPIInvoke<T> = suspend () -> Response<T>
typealias NetworkDownloadInvoke<T> = suspend () -> Response<T>

suspend fun <T : Any> performNetworkCall(
    messageInCaseOfError: String = "Network error",
    allowRetries: Boolean = true,
    numberOfRetries: Int = 2,
    networkAPICall: NetworkAPIInvoke<T>
): Flow<IResult<T>> {
    var delayDuration = 1000L
    val delayFactor = 2
    return flow {
        val response = networkAPICall()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(success(it))
            } ?: emit(failed(IOException()))
            return@flow
        }
        when (response.code()) {
            404 -> {
                throw IOException()
            }
        }
        throw IOException(response.errorBody()?.string() ?: messageInCaseOfError)
    }.retryWhen { cause, attempt ->
        if (!allowRetries || attempt > numberOfRetries || cause !is IOException)
            return@retryWhen false
        delay(delayDuration)
        delayDuration *= delayFactor
        return@retryWhen true
    }.catch { e ->
        if (e is UnknownHostException) {
            emit(failed(UnknownHostException()))
            return@catch
        }
        if (e is SocketTimeoutException) {
            emit(failed(SocketTimeoutException()))
            return@catch
        }
        emit(failed(IOException(e)))
    }
}


suspend fun <T : ResponseBody> performNetworkCallDownloadAndUnzip(
    fileName: String,
    targetDirectory: File,
    allowRetries: Boolean = true,
    numberOfRetries: Int = 2,
    networkAPICall: NetworkDownloadInvoke<T>
): Flow<IResult<Unit>> {
    var delayDuration = 1000L
    val delayFactor = 2

    return flow {
        val response = networkAPICall()

        if (response.isSuccessful) {
            val body = response.body()
            if (body == null) {
                emit(failed(IOException("Resposta vazia")))
                return@flow
            }

            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs()
            }

            val zipFile = File(Tools.DIR_CACHE, fileName)

            try {
                writeResponseBodyToDisk(body, zipFile)
                unzip(zipFile, targetDirectory)
                zipFile.delete()
                emit(success(Unit))
            } catch (e: IOException) {
                emit(failed(IOException("Erro ao salvar ou descompactar o arquivo: ${e.localizedMessage}", e)))
            }
        } else {
            emit(failed(IOException("Erro na requisição: ${response.code()} - ${response.message()}")))
        }
    }.retryWhen { cause, attempt ->
        if (!allowRetries || attempt >= numberOfRetries || cause !is IOException) {
            false
        } else {
            delay(delayDuration)
            delayDuration *= delayFactor
            true
        }
    }.catch { e ->
        emit(failed(IOException("Erro inesperado: ${e.localizedMessage}", e)))
    }
}



fun unzip(zipFile: File, targetDirectory: File) {
    ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { zis ->
        var ze: ZipEntry?
        val buffer = ByteArray(1024)

        while (zis.nextEntry.also { ze = it } != null) {
            val entryName = ze!!.name

            // Remove o primeiro diretório (ex: "mods/")
            val cleanName = entryName.substringAfter("/") // ou: entryName.replaceFirst("mods/", "")

            if (cleanName.isEmpty()) continue // pular se era só a pasta raiz

            val file = File(targetDirectory, cleanName)

            if (ze!!.isDirectory) {
                file.mkdirs()
            } else {
                file.parentFile?.mkdirs()
                FileOutputStream(file).use { fos ->
                    var count: Int
                    while (zis.read(buffer).also { count = it } != -1) {
                        fos.write(buffer, 0, count)
                    }
                }
            }
        }
    }
}

@Throws(IOException::class)
fun writeResponseBodyToDisk(body: ResponseBody, file: File) {
    file.parentFile?.mkdirs()

    body.byteStream().use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(4096)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
        }
    }
}