package com.shurona.chat.mytalk.user.application;

import com.shurona.chat.mytalk.user.domain.model.User;

public interface UserService {

    public Long saveUser(String loginId, String password, String description, String phoneNumber);

    public boolean checkLoginIdDuplicated(String loginId);

    public boolean checkPasswordCorrect(String inputPassword, String dbPassword);

    public User findUserById(Long userId);

    public User findUserByLoginId(String loginId);

    public void findUserList();

    public void deleteUser(Long userId);

}
