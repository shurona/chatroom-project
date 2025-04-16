package com.shurona.chat.user.domain.model;

import com.shurona.chat.user.common.entity.BaseEntity;
import com.shurona.chat.user.domain.vo.UserPhoneNumber;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "_User")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String username;

    @Column
    private String description;

    @Column
    private String password;

    @Embedded
    @Column(name = "phone_number", unique = true, nullable = false)
    private UserPhoneNumber phoneNumber;

    @OneToMany(mappedBy = "user")
    List<Friend> friendList = new ArrayList<>();


}
