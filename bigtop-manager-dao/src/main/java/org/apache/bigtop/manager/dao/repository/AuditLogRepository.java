package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
