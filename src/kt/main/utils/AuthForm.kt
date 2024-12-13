package kt.main.utils

import kotlinx.serialization.Serializable
import kt.main.core.Auth
import java.security.MessageDigest

@Serializable
data class AuthForm (
    val login: String,
    val password: String
)

object CredentialsFormer {
    private val shaEncoder = MessageDigest.getInstance("SHA-512")!!

    private fun ByteArray.toHexString(): String {
        return joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
    }

    // Сделано для того, чтобы хеш пароля создавался только через него для Auth
    @OptIn(ExperimentalStdlibApi::class)
    fun form2Auth(authForm: AuthForm): Auth {
        val salt = (authForm.login + authForm.password.last()).toByteArray()
        val password = authForm.password.toByteArray()
        // Нужно перед каждым хешированием сбрасывать, иначе хеши на одинаковых объектах может не сойтись
        shaEncoder.reset()
        val hashPass = shaEncoder.digest(password + salt).toHexString()

        return Auth(authForm.login, hashPass)
    }

    // Здесь пароль еще не хешированный приходит в Auth
    fun auth2Form(auth: Auth): AuthForm {
        return AuthForm(auth.login, auth.hashPass)
    }
}