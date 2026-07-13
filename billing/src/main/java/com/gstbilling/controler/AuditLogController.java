package com.gstbilling.controler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.gstbilling.models.AuditLog;
import com.gstbilling.service.AuditLogService;

@RestController
@RequestMapping("/audit")
public class AuditLogController {

	@Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public Page<AuditLog> getAllLogs(
    	    		@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size)  {
        return auditLogService.getgetAllLogs(page,size);
    }
}
