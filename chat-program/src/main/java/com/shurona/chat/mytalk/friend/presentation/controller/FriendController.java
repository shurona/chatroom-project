package com.shurona.chat.mytalk.friend.presentation.controller;

import static com.shurona.chat.mytalk.common.variable.StaticVariable.LOGIN_USER;

import com.shurona.chat.mytalk.common.session.UserSession;
import com.shurona.chat.mytalk.friend.domain.model.Friend;
import com.shurona.chat.mytalk.friend.presentation.dto.AddFriendForm;
import com.shurona.chat.mytalk.friend.presentation.dto.FriendRequestDto;
import com.shurona.chat.mytalk.friend.service.FriendService;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest;
import com.shurona.chat.mytalk.user.domain.type.FriendRequest.RequestStatus;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/friends/v1")
@Controller
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @GetMapping
    public String FriendListPage(HttpSession session, Model model,
        @ModelAttribute("addFriendForm") AddFriendForm form
        ) {

        // 세션에서 사용자 정보 가져오기
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        User user = userService.findUserById(currentUser.userId());


        // 친구 목록 조회
        List<FriendRequestDto> friendList = FriendRequestDto.from(
            friendService.findAcceptedFriendListByUser(user)
        );
        List<FriendRequestDto> friendRequestDtoList = FriendRequestDto.from(
            friendService.findRequestedFriendListByUser(user)
        );

        // 모델에 친구 목록 추가
        model.addAttribute("friends", friendList);
        model.addAttribute("userInfo", user);
        model.addAttribute("friendRequests", friendRequestDtoList);

        return "friend/home";
    }

    @GetMapping("/requests")
    public String requestFriendList(
        HttpSession session,
        Model model
    ) {
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        User user = userService.findUserById(currentUser.userId());

        List<FriendRequestDto> friendRequestDtoList = FriendRequestDto.from(
            friendService.findRequestedFriendListByUser(user)
        );

        model.addAttribute("friendRequests", friendRequestDtoList);

        return "friend/request";
    }

    @PostMapping("/new")
    public String addNewFriend(
        HttpSession session,
        Model model,
        @Validated @ModelAttribute("addFriendForm") AddFriendForm form,
        BindingResult bindingResult

    ) {
        // 세션에서 사용자 정보 가져오기
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        // 인터셉터에서 확인하지만 2중으로 로그인 상태 확인
        if (currentUser == null) {
            return "redirect:/user/login";
        }

        User user = userService.findUserById(currentUser.userId());
        User friend = userService.findUserByLoginId(form.friendId());

        if (friend == null) {
            bindingResult.rejectValue("friendId", "error.emptyFriend", "존재하지 않는 친구아이디 입니다");
        }

        // 에러 발생 확인
        if (bindingResult.hasErrors()) {
            // 에러 발생 시 다시 리턴해 줘야 하므로 필요한 데이터를 넣어준다.
            List<FriendRequestDto> friendList = FriendRequestDto.from(
                friendService.findAcceptedFriendListByUser(user)
            );
            model.addAttribute("friends", friendList);
            model.addAttribute("showAddFriendModal", true);
            return "friend/home";
        }


        friendService.saveFriend(user, friend);

        return "redirect:/friends/v1";
    }

    /*
        유저들의 상태 변경을 위한 요청들
     */
    @PostMapping("/accept")
    public String acceptFriendRequest(
        HttpSession session,
        @RequestParam("requestId") Long requestId
    ) {
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        Friend friendById = friendService.findFriendById(requestId);
        friendService.changeStatusById(friendById.getId(), FriendRequest.ACCEPTED);

        return "redirect:/friends/v1/requests";
    }

    @PostMapping("/refuse")
    public String refuseFriendRequest(
        HttpSession session,
        @RequestParam("requestId") Long requestId
    ) {
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        Friend friendById = friendService.findFriendById(requestId);
        friendService.changeStatusById(friendById.getId(), FriendRequest.REFUSED);

        return "redirect:/friends/v1/requests";

    }

    @PostMapping("/banned")
    public String bannedFriend(
        HttpSession session,
        @RequestParam("requestId") Long requestId
    ) {
        UserSession currentUser = (UserSession) session.getAttribute(LOGIN_USER);

        Friend friendById = friendService.findFriendById(requestId);
        friendService.changeStatusById(friendById.getId(), FriendRequest.BANNED);

        return "redirect:/friends/v1";
    }
}
