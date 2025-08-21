package ai.hadirsa.examples.jpa.startup

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupTestRunner {
    
    private val logger = LoggerFactory.getLogger(StartupTestRunner::class.java)
    
    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        logger.info("=== STARTUP TEST RUNNER TRIGGERED ===")
        logger.info("Application is ready and startup events are firing")
        logger.info("This confirms that the ApplicationReadyEvent is working")
    }
}
