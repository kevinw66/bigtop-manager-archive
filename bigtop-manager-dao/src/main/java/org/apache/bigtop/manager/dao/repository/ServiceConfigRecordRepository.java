package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.ServiceConfigRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceConfigRecordRepository extends JpaRepository<ServiceConfigRecord, Long> {

    Optional<ServiceConfigRecord> findFirstByClusterIdAndServiceIdOrderByVersionDesc(Long clusterId, Long serviceId);

}
