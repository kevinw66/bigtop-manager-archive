package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.springframework.data.repository.CrudRepository;

public interface StackRepository extends CrudRepository<Stack, Long> {
}
