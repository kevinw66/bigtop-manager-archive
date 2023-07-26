package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.Setting;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface SettingRepository extends CrudRepository<Setting, Long> {

    Optional<Setting> findFirstByOrderByVersionDesc();
}
