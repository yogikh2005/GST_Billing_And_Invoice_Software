package com.gstbilling.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.gstbilling.dao.AuditLogRepository;
import com.gstbilling.models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@Service
public class AuditLogService {
	
	@Autowired
    private  AuditLogRepository auditLogRepository;

    public void logAction(String action, String details) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        details="OK";
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setPerformedBy(username);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());

        //auditLogRepository.save(log);
    }

	 public Page<AuditLog> getgetAllLogs(int page, int size) {
	        Pageable pageable = PageRequest.of(page, size); 
	        return auditLogRepository.findAll(pageable);
	    }
}

