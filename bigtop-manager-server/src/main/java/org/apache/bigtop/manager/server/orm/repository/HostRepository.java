package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Host;
import org.springframework.data.repository.CrudRepository;

public interface HostRepository extends CrudRepository<Host, Long> {
}
