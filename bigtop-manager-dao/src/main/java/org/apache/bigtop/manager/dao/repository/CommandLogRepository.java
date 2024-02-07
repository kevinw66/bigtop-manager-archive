package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.CommandLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandLogRepository extends JpaRepository<CommandLog, Long> {


}
