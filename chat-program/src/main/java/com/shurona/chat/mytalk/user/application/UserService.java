package com.shurona.chat.mytalk.user.application;

import com.shurona.chat.mytalk.user.domain.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {

    /**
     * 유저를 저장한다.
     */
    public Long saveUser(String loginId, String password, String description, String phoneNumber);

    /**
     * 로그인 아이디 중복 확인
     */
    public boolean checkLoginIdDuplicated(String loginId);

    public boolean checkPhoneNumberDuplicated(String phoneNumber);

    public boolean checkPasswordCorrect(String inputPassword, String dbPassword);

    /*
        닉네임을 검색하기 위한 것
     */
    public List<User> findUserListByNickname(Long userId, String nickname, Pageable pageable);

    /**
     * 유저 ID를 기준으로 유저 검색(없으면 자동 에러 처리)
     */
    public User findUserById(Long userId);

    public User findUserByLoginId(String loginId);

    public void findUserList();

    public void deleteUser(Long userId);

}
