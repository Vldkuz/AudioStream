package webdav

import io.ktor.http.*

object WebDavHeaders {
    val Sha256 = "Sha256"
}

object WebDavMethods {
    val MkCol = HttpMethod("MKCOL")
}