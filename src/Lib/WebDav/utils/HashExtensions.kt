package utils

import java.security.MessageDigest

object HashExtensions {
    @OptIn(ExperimentalStdlibApi::class)
    fun ByteArray.md5String(): String {
        return MessageDigest.getInstance("MD5").digest(this).toHexString()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun ByteArray.sha256String(): String {
        return MessageDigest.getInstance("SHA-256").digest(this).toHexString()
    }
}