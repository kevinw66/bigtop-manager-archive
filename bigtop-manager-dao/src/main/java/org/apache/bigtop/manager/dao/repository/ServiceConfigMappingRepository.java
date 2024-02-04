package org.apache.bigtop.manager.dao.repository;

import org.apache.bigtop.manager.dao.entity.ServiceConfigMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceConfigMappingRepository extends JpaRepository<ServiceConfigMapping, Long> {

    @SuppressWarnings("all")
    /**
     * 获取每个服务最新版本的配置
     */
    @Query(value = """
            SELECT scm.*
            FROM service_config_mapping scm
                     JOIN
                 (SELECT a.id,
                         a.service_id,
                         a.version
                  FROM (SELECT *
                        FROM service_config_record) a
                           JOIN
                       (SELECT max(u.version) max_version,
                               u.service_id
                        FROM service_config_record u
                        WHERE u.cluster_id = ?1
                        GROUP BY u.service_id) b
                       ON a.version = b.max_version
                           AND a.service_id = b.service_id) c
                 ON scm.service_config_record_id = c.id
            """, nativeQuery = true)
    List<ServiceConfigMapping> findAllGroupLastest(Long clusterId);

    List<ServiceConfigMapping> findAllByServiceConfigId(Long serviceConfigId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordId(Long serviceConfigRecordId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordServiceId(Long serviceId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordClusterId(Long clusterId);

}
