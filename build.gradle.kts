import java.io.IOException

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hilt.plugin) apply false
}
fun getGitHash(): String {
    val command = Runtime.getRuntime().exec("git rev-parse HEAD")
    val returnCode = command.waitFor()
    if (returnCode != 0) {
        throw IOException("Command 'getGitHash()' exited with $returnCode")
    }
    return command.inputStream.reader().use { it.readText().trim() }
}

fun gitUsed(): Boolean {
    val process = Runtime.getRuntime().exec("git rev-parse --is-inside-work-tree")
    val returnCode = process.waitFor()
    when (returnCode) {
        127 -> {
            println("git not found")
            return false
        }
        128 -> {
            println("not inside a git repository")
            return false
        }
        0 -> return true
        else -> throw IOException("Command 'gitUsed()' exited with $returnCode")
    }
}