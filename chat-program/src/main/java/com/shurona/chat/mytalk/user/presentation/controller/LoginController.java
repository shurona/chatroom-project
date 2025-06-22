package com.shurona.chat.mytalk.user.presentation.controller;

import com.shurona.chat.mytalk.common.session.UserSession;
import com.shurona.chat.mytalk.common.variable.StaticVariable;
import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.domain.model.User;
import com.shurona.chat.mytalk.user.presentation.form.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final UserService userService;

    @GetMapping("/home")
    public String home(
        @SessionAttribute(name = StaticVariable.LOGIN_USER, required = false) UserSession userSession,
        Model model
    ) {

        if (userSession == null) {
            return "user/home";
        }

        return "redirect:/ssr/friends/v1";
    }

    @PostMapping("/ssr/login/v1")
    public String login(
        @Validated @ModelAttribute("loginForm") LoginForm form,
        @RequestParam(value = "redirectURL", defaultValue = "/") String redirectURL,
        BindingResult bindingResult,
        HttpServletRequest request
    ) {
        User user = userService.findUserByLoginId(form.loginId());

        // 아이디가 없는 경우
        if (user == null) {
            bindingResult.rejectValue("loginId", "error.loginId", "없는 사용자 입니다.");
            return "user/login";
        }

        // 비밀번호가 틀린경우
        if (!userService.checkPasswordCorrect(form.password(), user.getPassword())) {
            bindingResult.rejectValue("password", "error.password", "잘못된 비밀번호 입니다.");
            return "user/login";
        }

        // 세션 등록
        HttpSession session = request.getSession();
        UserSession userSession = new UserSession(user.getId(), user.getLoginId());
        //TODO: 로그인 시간 외부에서 주입하도록 수정
        session.setMaxInactiveInterval(StaticVariable.LOGIN_SESSION_TIME);
        session.setAttribute(StaticVariable.LOGIN_USER, userSession);

        return "redirect:" + "/ssr/friends/v1";
    }

    @GetMapping("/ssr/login/v1")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {

        return "user/login";
    }

    @PostMapping("/ssr/logout/v1")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/home";
    }

}
