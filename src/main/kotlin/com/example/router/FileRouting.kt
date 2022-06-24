package com.example.router

import com.example.config.FILE_PATH
import com.example.entity.ResponseResult
import com.example.utils.FileUtils
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URLEncoder

fun Application.fileRouting() {
    routing {
        route("/file") {
            get {
                val fileInfos = FileUtils.getFileList(FILE_PATH)
                call.respond(fileInfos)
            }
            post {

            }
        }
        post("/upload") {
            val multipart = call.receiveMultipart()
            val filename = fileSave(multipart)
            call.respond(ResponseResult.okResult(filename))
        }
        route("/filedown/{file}") {
            get {
                val fileName: String? = call.parameters["file"]
                val file = File("$FILE_PATH/$fileName")
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(
                        ContentDisposition.Parameters.FileName,
                        withContext(Dispatchers.IO) {
                            URLEncoder.encode(fileName, "UTF-8")
                        }
                    )
                        .toString()
                )
                call.respondFile(file)
            }
        }
    }
}


suspend fun fileSave(multipartData: MultiPartData): String {
    var fileDescription = ""
    var fileName = ""
    multipartData.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> {
                fileDescription = part.value
            }
            is PartData.FileItem -> {
                fileName = part.originalFileName as String
                var fileBytes = part.streamProvider().readBytes()
                File("$FILE_PATH/$fileName").writeBytes(fileBytes)
            }
            else -> {}
        }
    }
    return fileName
}
