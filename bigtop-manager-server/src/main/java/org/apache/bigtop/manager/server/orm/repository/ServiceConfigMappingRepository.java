package org.apache.bigtop.manager.server.orm.repository;

import org.apache.bigtop.manager.server.orm.entity.ServiceConfigMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServiceConfigMappingRepository extends JpaRepository<ServiceConfigMapping, Long> {

    @SuppressWarnings("all")
    /**
     * 获取每个服务最新版本的配置
     */
    @Query(value = """
            SELECT aa.*
            FROM service_config_mapping aa
                     JOIN
                 (SELECT a.id,
                         a.service_id,
                         a.version
                  FROM (SELECT *
                        FROM service_config_record) a
                           JOIN
                       (SELECT max(u.version) ver,
                               u.service_id
                        FROM service_config_record u
                                 LEFT JOIN cluster c
                                           ON c.id = u.cluster_id
                        WHERE c.cluster_name = ?1
                        GROUP BY u.service_id) b
                       ON a.version = b.ver
                           AND a.service_id = b.service_id) c
                 ON aa.service_config_record_id = c.id
            """, nativeQuery = true)
    List<ServiceConfigMapping> findAllGroupLastest(String clusterName);

    List<ServiceConfigMapping> findAllByServiceConfigId(Long serviceConfigId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordId(Long serviceConfigRecordId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordServiceId(Long serviceId);

    List<ServiceConfigMapping> findAllByServiceConfigRecordClusterClusterName(String clusterName);

}
