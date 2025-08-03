package com.shurona.chat.mytalk.user.application;

import static com.shurona.chat.mytalk.user.common.exception.UserErrorCode.USER_NOT_FOUND;

import com.shurona.chat.mytalk.user.common.exception.UserException;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.vo.UserPhoneNumber;
import com.shurona.chat.mytalk.user.infrastructure.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Long saveUser(String loginId, String password, String description, String phoneNumber) {

        User newUser = User.createUser(loginId, loginId, description, phoneNumber);
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
    public List<User> findUserList(List<Long> userIds) {
        return userRepository.findAllById(userIds);
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

    @Override
    public List<User> findUserListByNickname(Long userId, String nickname, Pageable pageable) {

        Page<User> nickNameContaining = userRepository.findNonFriendsByUserIdAndNicknameContaining(
            userId,
            nickname,
            pageable);

        return nickNameContaining.toList();
    }

    /*
            private method
         */
    public User findByUserByIdCheckExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
            new UserException(USER_NOT_FOUND)
        );
    }
}
