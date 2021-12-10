package com.group1.orchestrator.service

import java.util.List

class OrderWorkflow(steps: List<WorkflowStep>) : Workflow {
    private val steps: List<WorkflowStep>
    @Override
    fun getSteps(): List<WorkflowStep> {
        return steps
    }

    init {
        this.steps = steps
    }
}