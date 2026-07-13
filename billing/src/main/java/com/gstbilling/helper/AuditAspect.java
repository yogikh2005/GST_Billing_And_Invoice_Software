package com.gstbilling.helper;

import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import com.gstbilling.service.AuditLogService;

@Aspect
@Component
public class AuditAspect {

    private final AuditLogService auditLogService;

    public AuditAspect(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @AfterReturning("execution(* com.gstbilling.controler..*(..))")
    public void logUserAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();

       
        String args = Arrays.toString(joinPoint.getArgs());

        auditLogService.logAction(
            "Executed: " + methodName,
            "Parameters: " + args
        );
    }
}

