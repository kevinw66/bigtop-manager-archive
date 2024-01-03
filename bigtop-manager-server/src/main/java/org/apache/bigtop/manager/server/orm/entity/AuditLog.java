package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_log")
@TableGenerator(name = "audit_log_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "audit_log_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "uri")
    private String uri;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "args", length = 16777216)
    private String args;

    @Column(name = "tag_name")
    private String tagName;

    @Column(name = "tag_desc")
    private String tagDesc;

    @Column(name = "operation_summary")
    private String operationSummary;

    @Column(name = "operation_desc")
    private String operationDesc;

    @Column(name = "user_id")
    private Long userId;

}
