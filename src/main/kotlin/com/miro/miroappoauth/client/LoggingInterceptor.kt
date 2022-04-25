package com.miro.miroappoauth.client

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StreamUtils
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*
import java.util.function.Consumer

/**
 * Interceptor for RestTemplate/TestRestTemplate that logs HTTP request and response (on the level of RestTemplate
 * layer, e.g. it doesn't print "Host" header (added by real HTTP client)).
 */
class LoggingInterceptor constructor(
    private val logger: Logger = LoggerFactory.getLogger(
        LoggingInterceptor::class.java
    )
) : ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest, requestBody: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        var response: ClientHttpResponse? = null
        var responseBody: ByteArray? = null
        var exception: Throwable? = null
        return try {
            response = execution.execute(request, requestBody)
            val httpStatus = response.statusCode
            responseBody = StreamUtils.copyToByteArray(response.body)
            getClientHttpResponse(httpStatus, response, responseBody)
        } catch (e: IOException) {
            exception = e
            throw e
        } catch (e: RuntimeException) {
            exception = e
            throw e
        } catch (e: Error) {
            exception = e
            throw e
        } finally {
            val out = PrintStringWriter()
            out.print(formatRequest(request, requestBody))
            try {
                if (response != null && responseBody != null) {
                    out.println()
                    out.print(formatResponse(response, responseBody))
                }
            } catch (e: IOException) {
                e.printStackTrace(out)
            }
            log(out.toString(), exception)
        }
    }

    private fun log(message: String, exception: Throwable?) {
        logger.info(message, exception)
    }

    private class PrintStringWriter : PrintWriter(StringWriter()) {
        override fun toString(): String {
            return out.toString()
        }
    }

    companion object {
        private fun formatRequest(request: HttpRequest, requestBody: ByteArray?): String {
            val out = PrintStringWriter()
            out.println("Request")
            out.println(request.method.toString() + " " + request.uri)
            printHeaders(out, request.headers)
            if (requestBody != null && requestBody.isNotEmpty()) {
                val content = getContent(request.headers, requestBody)
                out.println()
                out.println(content)
            }
            return out.toString()
        }

        private fun formatResponse(response: ClientHttpResponse, responseBody: ByteArray): String {
            val out = PrintStringWriter()
            out.println("Response")
            out.println("HTTP " + response.statusCode.value() + " " + response.statusCode.reasonPhrase)
            printHeaders(out, response.headers)
            val content = getContent(response.headers, responseBody)
            out.println()
            out.println(content)
            out.println()
            out.println("Response body length " + responseBody.size + " bytes")
            return out.toString()
        }

        private fun getClientHttpResponse(
            httpStatus: HttpStatus, response: ClientHttpResponse,
            responseBody: ByteArray
        ): ClientHttpResponse {
            return object : ClientHttpResponse {
                override fun getStatusCode(): HttpStatus {
                    return httpStatus
                }

                override fun getRawStatusCode(): Int {
                    return response.rawStatusCode
                }

                override fun getStatusText(): String {
                    return response.statusText
                }

                override fun close() {
                    response.close()
                }

                override fun getBody(): InputStream {
                    return ByteArrayInputStream(responseBody)
                }

                override fun getHeaders(): HttpHeaders {
                    return response.headers
                }
            }
        }

        private fun printHeaders(out: PrintWriter, headers: Map<String, List<String>>) {
            headers.forEach { (name: String, values: List<String>) ->
                values.forEach(
                    Consumer { value: String -> out.println("$name: $value") })
            }
        }

        private fun getContent(headers: HttpHeaders, bytes: ByteArray): String {
            return String(bytes, getContentTypeCharset(headers)!!)
        }

        private fun getContentTypeCharset(headers: HttpHeaders): Charset? {
            return Optional.ofNullable(headers.contentType)
                .map { obj: MediaType -> obj.charset }
                .orElse(UTF_8)
        }
    }
}
