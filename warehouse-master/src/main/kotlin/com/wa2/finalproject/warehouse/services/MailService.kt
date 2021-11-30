package com.wa2.finalproject.warehouse.services

interface MailService {
    fun sendMessage(toMail: String, subject: String, mailBody: String)
}