package com.group1.order.service

import com.vinsguru.dto.OrchestratorResponseDTO
import com.vinsguru.order.repository.PurchaseOrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class OrderEventUpdateService {
    @Autowired
    private val repository: PurchaseOrderRepository? = null
    @Transactional
    fun updateOrder(responseDTO: OrchestratorResponseDTO) {
        repository
            .findById(responseDTO.getOrderId())
            .ifPresent { po ->
                po.setStatus(responseDTO.getStatus())
                repository.save(po)
            }
    }
}