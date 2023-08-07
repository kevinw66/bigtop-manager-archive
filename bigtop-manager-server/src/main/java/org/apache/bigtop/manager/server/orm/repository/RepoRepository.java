package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RepoRepository extends CrudRepository<Repo, Long> {

    @Query(value = "SELECT r FROM Repo r WHERE r.stack.stackName = ?1 AND r.stack.stackVersion = ?2")
    List<Repo> findAllByStackNameAndStackVersion(String stackName, String stackVersion);

    @Query(value = "SELECT r FROM Repo r WHERE r.stack.id = ?1")
    List<Repo> findAllByStackId(Long stackId);

    Optional<Repo> findByRepoIdAndOsAndArchAndStackId(String repoId, String os, String arch, Long stackId);
}
