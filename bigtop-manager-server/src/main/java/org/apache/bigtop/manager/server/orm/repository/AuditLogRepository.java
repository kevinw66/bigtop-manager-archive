package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
