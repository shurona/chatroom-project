package com.shurona.chat.mytalk.user.domain.model;

import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.user.domain.vo.UserPhoneNumber;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "_User")
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, name = "username")
    private String loginId;

    @Column
    private String description;

    @Column
    private String password;

    @Embedded
    @Column(name = "phone_number", unique = true, nullable = false)
    private UserPhoneNumber phoneNumber;

    @OneToMany(mappedBy = "user")
    List<Friend> friendList = new ArrayList<>();

    public static User createUser(String loginId, String description, String phoneNumber) {
        User user = new User();
        user.loginId = loginId;
        user.description = description;
        user.phoneNumber = new UserPhoneNumber(phoneNumber);

        return user;
    }

    public void settingPassword(String password) {
        this.password = password;
    }

    public void deleteUser() {
        this.isDeleted = true;
    }
}
