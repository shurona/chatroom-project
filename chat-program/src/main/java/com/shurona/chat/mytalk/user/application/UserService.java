package com.shurona.chat.mytalk.user.application;

import com.shurona.chat.mytalk.user.domain.model.User;

public interface UserService {

    public Long saveUser(String loginId, String password, String description, String phoneNumber);

    public boolean checkLoginIdDuplicated(String loginId);

    public boolean checkPhoneNumberDuplicated(String phoneNumber);

    public boolean checkPasswordCorrect(String inputPassword, String dbPassword);

    /**
     * 유저 ID를 기준으로 유저 검색(없으면 자동 에러 처리)
     */
    public User findUserById(Long userId);

    public User findUserByLoginId(String loginId);

    public void findUserList();

    public void deleteUser(Long userId);

}
