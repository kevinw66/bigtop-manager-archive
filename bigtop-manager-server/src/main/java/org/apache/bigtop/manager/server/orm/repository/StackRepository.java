package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StackRepository extends CrudRepository<Stack, Long> {
    Optional<Stack> findByStackNameAndStackVersion(String stackName, String stackVersion);
}
