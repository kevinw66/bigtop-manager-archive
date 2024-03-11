package org.apache.bigtop.manager.dao.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "\"user\"")
@TableGenerator(name = "user_generator", table = "sequence", pkColumnName = "seq_name", valueColumnName = "seq_count")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_generator")
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "status")
    private Boolean status;

}
