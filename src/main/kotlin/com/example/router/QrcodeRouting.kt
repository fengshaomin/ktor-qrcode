package com.example.router

import com.example.utils.FileUtils
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.QrRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/qrcode") {
            val qrcodeString: String? = call.request.queryParameters["url"]
            val fileName = qrcodeString?.let { it1 -> FileUtils.createQRcode(it1) }
            call.respond(mapOf("data" to fileName))
        }
    }
}