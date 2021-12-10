package com.group1.orchestrator.service

class OrderWorkflow(steps: List<WorkflowStep>) : Workflow {
    override val steps: List<WorkflowStep>

    init {
        this.steps = steps
    }
}