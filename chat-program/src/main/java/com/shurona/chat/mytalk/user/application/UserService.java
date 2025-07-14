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

    /**
     * 휴대전화의 중복을 확인한다.
     */
    public boolean checkPhoneNumberDuplicated(String phoneNumber);

    /**
     * 비밀번호가 맞는 지 확인한다.
     */
    public boolean checkPasswordCorrect(String inputPassword, String dbPassword);

    /**
     * 닉네임을 검색하기 위한 것
     */
    public List<User> findUserListByNickname(Long userId, String nickname, Pageable pageable);

    /**
     * 유저 ID를 기준으로 유저 검색(없으면 자동 에러 처리)
     */
    public User findUserById(Long userId);

    /**
     * 로그인 아이디를 기준으로 유저 정보를 검색한다.
     */
    public User findUserByLoginId(String loginId);

    /**
     * 유저 목록을 갖고 온다.
     */
    public List<User> findUserList(List<Long> userIds);

    /**
     * 유저 정보를 삭제한다.
     */
    public void deleteUser(Long userId);

}
