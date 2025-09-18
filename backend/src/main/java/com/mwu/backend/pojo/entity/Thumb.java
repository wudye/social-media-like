package com.mwu.backend.pojo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Table(name = "thumb",
        uniqueConstraints = @UniqueConstraint(name = "idx_userId_blogId", columnNames = {"userId", "blogId"})
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
public class Thumb {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;


    private Long userId;
    private Long blogId;
    @CreatedDate
    private Date createTime;


    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

}
