package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Component;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;


public interface ComponentRepository extends JpaRepositoryImplementation<Component, Long> {

}
