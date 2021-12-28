package com.group1.wallet.service

import com.group1.dto.WalletRequestDTO
import com.group1.dto.WalletResponseDTO
import com.group1.enums.PaymentStatus
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class WalletService {
    private var userBalanceMap: MutableMap<Int, Double>? = null

    @PostConstruct
    private fun init() {
        userBalanceMap = mutableMapOf(
            1 to 15000.0,
            2 to 1000.0,
            3 to 1000.0
        )
    }

    fun debit(requestDTO: WalletRequestDTO): WalletResponseDTO {
        val balance = userBalanceMap!!.getOrDefault(requestDTO.userId, 0.0)

        var status = PaymentStatus.PAYMENT_REJECTED
        if (balance >= requestDTO.amount) {
            userBalanceMap!![requestDTO.userId] = balance - requestDTO.amount
            status = PaymentStatus.PAYMENT_APPROVED
        }

        return WalletResponseDTO(
            requestDTO.userId,
            requestDTO.orderId,
            requestDTO.amount,
            status
        )
    }

    fun credit(requestDTO: WalletRequestDTO) {
        userBalanceMap!!.computeIfPresent(requestDTO.userId) { _: Int?, v: Double -> v + requestDTO.amount }
    }
}