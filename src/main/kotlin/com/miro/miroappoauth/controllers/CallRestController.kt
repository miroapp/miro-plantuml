package com.miro.miroappoauth.controllers

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.miro.miroappoauth.config.AppProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CallRestController(
    private val appProperties: AppProperties
) {

    @GetMapping("/call")
    fun call(
        @RequestParam("id_token") idToken: String
    ): Map<String, Any> {
        val jwt = JWT.decode(idToken)
        try {
            JWT.require(Algorithm.HMAC256(appProperties.clientSecret))
                .build()
                .verify(jwt)
        } catch (e: JWTVerificationException) {
            // todo fix issue with it
            throw IllegalStateException("Wrong JWT signature")
        }

        val map = LinkedHashMap<String, Any>()
        map["user"] = jwt.getClaim("user")
        map["team"] = jwt.getClaim("team")
        return map
    }
}
