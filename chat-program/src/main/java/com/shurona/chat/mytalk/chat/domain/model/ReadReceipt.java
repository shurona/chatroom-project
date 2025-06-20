package com.shurona.chat.mytalk.chat.domain.model;

import com.shurona.chat.mytalk.user.domain.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 읽음 표시를 나타내는 엔티티 클래스입니다.
 */
@Entity
@Table(
    name = "read_receipt",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"user_id", "log_id"}
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReadReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private ChatLog log;

    public static ReadReceipt createReadReceipt(User user, ChatLog log) {
        ReadReceipt readReceipt = new ReadReceipt();
        readReceipt.user = user;
        readReceipt.log = log;

        return readReceipt;
    }

}
