package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoRepository extends JpaRepository<Repo, Long> {

    List<Repo> findAllByStackId(Long stackId);

    Optional<Repo> findByRepoIdAndOsAndArchAndStackId(String repoId, String os, String arch, Long stackId);
}
