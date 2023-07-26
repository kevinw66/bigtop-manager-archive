package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.RepoOS;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface RepoOSRepository extends CrudRepository<RepoOS, Long> {

    @Query(value = "SELECT u FROM RepoOS u WHERE u.repo.stack.id=?1 ")
    List<RepoOS> findAllByStackId(Long stackId);
}
