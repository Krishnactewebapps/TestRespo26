package com.example.productservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * AuditLogger for logging product additions using Logback.
 */
@Component
public class AuditLogger {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    /**
     * Log product addition event.
     *
     * @param productId   the ID of the added product
     * @param productName the name of the added product
     * @param username    the user who added the product
     */
    public void logProductAddition(Long productId, String productName, String username) {
        auditLogger.info("Product added: id={}, name='{}', by user='{}'", productId, productName, username);
    }
}
