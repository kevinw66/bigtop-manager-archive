package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<Setting, Long> {

}
