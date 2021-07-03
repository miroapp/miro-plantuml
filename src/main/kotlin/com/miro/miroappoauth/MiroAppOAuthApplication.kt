package com.miro.miroappoauth

import com.miro.miroappoauth.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent
import org.springframework.context.event.EventListener

@EnableConfigurationProperties(AppProperties::class)
@SpringBootApplication
class MiroAppOAuthApplication(
    private val serverProperties: ServerProperties
) {

    private val log = LoggerFactory.getLogger(MiroAppOAuthApplication::class.java)

    @EventListener
    fun onApplicationEvent(event: ServletWebServerInitializedEvent) {
        val protocol = if (serverProperties.ssl != null && serverProperties.ssl.isEnabled) "https" else "http"
        if (event.applicationContext.serverNamespace == "management") {
            log.info("Management server started at {}://localhost:{}/manage", protocol, event.webServer.port)
        } else {
            log.info("Server started at {}://localhost:{}", protocol, event.webServer.port)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<MiroAppOAuthApplication>(*args)
}
