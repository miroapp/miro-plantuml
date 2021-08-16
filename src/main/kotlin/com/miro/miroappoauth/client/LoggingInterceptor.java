package com.miro.miroappoauth.client;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Interceptor for RestTemplate/TestRestTemplate that logs HTTP request and response (on the level of RestTemplate
 * layer, e.g. it doesn't print "Host" header (added by real HTTP client)).
 */
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger;

    public LoggingInterceptor() {
        this(LoggerFactory.getLogger(LoggingInterceptor.class));
    }

    public LoggingInterceptor(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] requestBody,
                                        ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = null;
        byte[] responseBody = null;
        Throwable exception = null;
        try {
            response = execution.execute(request, requestBody);
            HttpStatus httpStatus = response.getStatusCode();
            responseBody = StreamUtils.copyToByteArray(response.getBody());
            return getClientHttpResponse(httpStatus, response, responseBody);
        } catch (IOException | RuntimeException | Error e) {
            exception = e;
            throw e;
        } finally {
            PrintStringWriter out = new PrintStringWriter();
            out.print(formatRequest(request, requestBody));
            try {
                if (response != null && responseBody != null) {
                    out.println();
                    out.print(formatResponse(response, responseBody));
                }
            } catch (IOException e) {
                e.printStackTrace(out);
            }

            log(out.toString(), exception);
        }
    }

    private void log(String message, @Nullable Throwable exception) {
        logger.info(message, exception);
    }

    private static String formatRequest(HttpRequest request, byte[] requestBody) {
        PrintStringWriter out = new PrintStringWriter();

        out.println("Request");
        out.println(request.getMethod() + " " + request.getURI());
        printHeaders(out, request.getHeaders());
        if (requestBody != null && requestBody.length > 0) {
            String content = getContent(request.getHeaders(), requestBody);
            out.println();
            out.println(content);
        }

        return out.toString();
    }

    private static String formatResponse(ClientHttpResponse response, byte[] responseBody) throws IOException {
        PrintStringWriter out = new PrintStringWriter();

        out.println("Response");
        out.println("HTTP " + response.getStatusCode().value() + " " + response.getStatusCode().getReasonPhrase());
        printHeaders(out, response.getHeaders());
        String content = getContent(response.getHeaders(), responseBody);
        out.println();
        out.println(content);
        out.println();
        out.println("Response body length " + responseBody.length + " bytes");

        return out.toString();
    }

    private static ClientHttpResponse getClientHttpResponse(HttpStatus httpStatus, ClientHttpResponse response,
                                                            byte[] responseBody) {
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() {
                return httpStatus;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return response.getRawStatusCode();
            }

            @Override
            public String getStatusText() throws IOException {
                return response.getStatusText();
            }

            @Override
            public void close() {
                response.close();
            }

            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(responseBody);
            }

            @Override
            public HttpHeaders getHeaders() {
                return response.getHeaders();
            }
        };
    }

    private static void printHeaders(PrintWriter out, Map<String, List<String>> headers) {
        headers.forEach((name, values) -> values.forEach(value -> out.println(name + ": " + value)));
    }

    private static String getContent(HttpHeaders headers, byte[] bytes) {
        return new String(bytes, getContentTypeCharset(headers));
    }

    private static Charset getContentTypeCharset(HttpHeaders headers) {
        return Optional.ofNullable(headers.getContentType())
                .map(MediaType::getCharset)
                .orElse(UTF_8);
    }

    private static class PrintStringWriter extends PrintWriter {

        private PrintStringWriter() {
            super(new StringWriter());
        }

        @Override
        public String toString() {
            return out.toString();
        }

    }

}
