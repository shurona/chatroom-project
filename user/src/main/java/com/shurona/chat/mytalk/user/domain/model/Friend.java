package com.shurona.chat.mytalk.user.domain.model;

import com.shurona.chat.mytalk.common.entity.BaseEntity;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "friend")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User friend;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private FriendRequest request;

    @Column
    private boolean banned;
}
