package me.camwalford.backendapiservice.context

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class SecurityContextElement(
    private val securityContext: SecurityContext
) : ThreadContextElement<SecurityContext>, AbstractCoroutineContextElement(SecurityContextElement) {

    companion object Key : CoroutineContext.Key<SecurityContextElement>

    override fun updateThreadContext(context: CoroutineContext): SecurityContext {
        val previousContext = SecurityContextHolder.getContext()
        SecurityContextHolder.setContext(securityContext)
        return previousContext
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: SecurityContext) {
        SecurityContextHolder.setContext(oldState)
    }
}
