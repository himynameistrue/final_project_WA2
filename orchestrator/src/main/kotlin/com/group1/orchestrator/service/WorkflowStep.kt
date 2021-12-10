package com.group1.orchestrator.service

import reactor.core.publisher.Mono

interface WorkflowStep {
    var status: WorkflowStepStatus

    fun process(): Mono<Boolean>
    fun revert(): Mono<Boolean>
}