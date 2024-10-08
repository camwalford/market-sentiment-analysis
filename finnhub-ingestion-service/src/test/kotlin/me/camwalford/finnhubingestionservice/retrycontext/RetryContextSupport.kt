package me.camwalford.finnhubingestionservice.retrycontext

import org.springframework.retry.RetryContext

/**
 * A simple implementation of [RetryContext] that can be used for testing.
 */
class RetryContextSupport : RetryContext {
    override fun getRetryCount(): Int = 3
    override fun getLastThrowable(): Throwable? = null
    override fun isExhaustedOnly(): Boolean = false
    override fun setExhaustedOnly() {}
    override fun setAttribute(name: String, value: Any?) {}
    override fun getParent(): RetryContext? = null

    override fun getAttribute(name: String): Any? {
        TODO("Not yet implemented")
    }

    override fun removeAttribute(name: String): Any? {
        TODO("Not yet implemented")
    }

    override fun hasAttribute(name: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun attributeNames(): Array<out String?> {
        TODO("Not yet implemented")
    }
}