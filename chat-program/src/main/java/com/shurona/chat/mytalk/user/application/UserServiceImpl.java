package com.shurona.chat.mytalk.user.application;

import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.vo.UserPhoneNumber;
import com.shurona.chat.mytalk.user.infrastructure.UserJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Long saveUser(String loginId, String password, String description, String phoneNumber) {

        User newUser = User.createUser(loginId, description, phoneNumber);
        newUser.settingPassword(passwordEncoder.encode(password));

        User save = userRepository.save(newUser);

        return save.getId();
    }

    @Override
    public User findUserById(Long userId) {
        return findByUserByIdCheckExist(userId);
    }

    @Override
    public User findUserByLoginId(String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);

        return user.orElse(null);
    }

    @Override
    public void findUserList() {
        //TODO: 유저 목록 조회 시 구현
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = findByUserByIdCheckExist(userId);
        user.deleteUser();

    }

    @Override
    public boolean checkLoginIdDuplicated(String loginId) {
        Optional<User> user = userRepository.findByLoginId(loginId);
        return user.isPresent();
    }

    @Override
    public boolean checkPhoneNumberDuplicated(String phoneNumber) {
        Optional<User> user = userRepository.findByPhoneNumber(new UserPhoneNumber(phoneNumber));
        return user.isPresent();
    }

    @Override
    public boolean checkPasswordCorrect(String inputPassword, String dbPassword) {
        return passwordEncoder.matches(inputPassword, dbPassword);
    }

    /*
        private method
     */
    public User findByUserByIdCheckExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new HttpClientErrorException(HttpStatus.BAD_REQUEST)
        );
    }
}
