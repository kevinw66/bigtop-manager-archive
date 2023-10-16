package org.apache.bigtop.manager.server.orm.entity;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.bigtop.manager.server.enums.JobState;

import java.util.List;

@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(indexes = {@Index(name = "idx_cluster_id", columnList = "cluster_id")})
@TableGenerator(name = "job_generator", table = "sequence")
public class Job extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "job_generator")
    private Long id;

    @ToString.Exclude
    @OneToMany(mappedBy = "job")
    private List<Stage> stages;

    @Enumerated(EnumType.STRING)
    private JobState state;

    private String desc;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cluster cluster;

}
