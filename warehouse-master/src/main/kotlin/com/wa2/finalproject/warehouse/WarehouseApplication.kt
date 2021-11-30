package com.wa2.finalproject.warehouse

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class WarehouseApplication {

    @Value("\${spring.mail.host}")
    private val mailHost: String = ""

    @Value("\${spring.mail.port}")
    private val mailPort: Int = 0

    @Value("\${spring.mail.username}")
    private val mailUsername: String = ""

    @Value("\${spring.mail.password}")
    private val mailPassword: String = ""

    @Value("\${spring.mail.properties.mail.smtp.auth}")
    private val mailAuth: String = ""

    @Value("\${spring.mail.properties.mail.smtp.starttls.enable}")
    private val mailUsesStartTls: Boolean = true

    @Value("\${spring.mail.properties.mail.debug}")
    private val mailDebug: Boolean = false

    @Bean
    fun getMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()

        mailSender.host = this.mailHost
        mailSender.port = this.mailPort
        mailSender.username = this.mailUsername
        mailSender.password = this.mailPassword
        mailSender.javaMailProperties["mail.smtp.auth"] = this.mailAuth
        mailSender.javaMailProperties["mail.smtp.starttls.enable"] = this.mailUsesStartTls
        mailSender.javaMailProperties["mail.debug"] = this.mailDebug
        return mailSender
    }
}


fun main(args: Array<String>) {
    runApplication<WarehouseApplication>(*args)
}