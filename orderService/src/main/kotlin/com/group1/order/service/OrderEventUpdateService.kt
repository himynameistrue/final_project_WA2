package com.group1.order.service

import com.group1.order.dto.OrchestratorResponseDTO
import com.group1.order.repository.PurchaseOrderRepository
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class OrderEventUpdateService(val repository: PurchaseOrderRepository) {

    @Transactional
    fun updateOrder(responseDTO: OrchestratorResponseDTO) {
        repository
            .findById(responseDTO.orderId)
            .ifPresent { po ->
                po.status = responseDTO.status
                repository.save(po)
            }
    }
}