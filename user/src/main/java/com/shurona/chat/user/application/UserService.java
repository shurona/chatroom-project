package com.shurona.chat.user.application;

import com.shurona.chat.user.domain.model.User;

public interface UserService {

    public Long saveUser(String username, String password, String description, String phoneNumber);

    public User findUserById(Long userId);

    public void findUserList();


    public void deleteUser(Long userId);

}
