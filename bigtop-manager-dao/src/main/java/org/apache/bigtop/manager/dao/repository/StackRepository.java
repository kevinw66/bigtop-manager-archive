package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Stack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StackRepository extends JpaRepository<Stack, Long> {

    Stack findByStackNameAndStackVersion(String stackName, String stackVersion);
}
