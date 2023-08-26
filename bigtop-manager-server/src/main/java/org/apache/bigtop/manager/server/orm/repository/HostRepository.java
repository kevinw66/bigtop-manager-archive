package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Host;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface HostRepository extends CrudRepository<Host, Long> {

    Optional<Host> findByHostname(String hostname);

    List<Host> findAllByHostnameIn(Iterable<String> hostnames);

}
