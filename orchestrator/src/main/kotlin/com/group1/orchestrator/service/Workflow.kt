package com.group1.orchestrator.service

interface Workflow {
    val steps: List<WorkflowStep>
}