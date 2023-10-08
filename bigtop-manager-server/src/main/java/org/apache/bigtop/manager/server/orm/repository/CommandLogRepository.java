package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.CommandLog;
import org.springframework.data.repository.CrudRepository;

public interface CommandLogRepository extends CrudRepository<CommandLog, Long> {


}
