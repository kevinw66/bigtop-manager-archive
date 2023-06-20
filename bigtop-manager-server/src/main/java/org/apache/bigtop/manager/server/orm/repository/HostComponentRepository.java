package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Component;
import org.apache.bigtop.manager.server.orm.entity.HostComponent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface HostComponentRepository extends CrudRepository<HostComponent, Long> {
    List<HostComponent> findByComponent(Component component);
}
