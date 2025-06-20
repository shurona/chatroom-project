package com.shurona.chat.mytalk.user.presentation.controller;

import com.shurona.chat.mytalk.user.application.UserService;
import com.shurona.chat.mytalk.user.presentation.form.CreateUserForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/ssr/users/v1")
@Controller
public class UserController {

    private final UserService userService;

    @PostMapping("/form")
    public String createUser(
        @Validated @ModelAttribute("createUserForm") CreateUserForm form,
        BindingResult bindingResult,
        Model model
    ) {

        // 아이디 중복 확인
        if (userService.checkLoginIdDuplicated(form.loginId())) {
            bindingResult.rejectValue("loginId", "error.loginId", "이미 사용 중인 아이디입니다");
        }

        if (userService.checkPhoneNumberDuplicated(form.phoneNumber())) {
            bindingResult.rejectValue("phoneNumber", "error.phoneNumber", "이미 등록된 휴대전화입니다");
        }

        // 오류가 있으면 회원가입 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }

        userService.saveUser(
            form.loginId(), form.password(), form.description(), form.phoneNumber());

        return "redirect:" + "/ssr/login/v1";
    }

    @GetMapping("/form")
    public String createUserForm(
        @Validated @ModelAttribute("createUserForm") CreateUserForm form
    ) {

        return "user/signup";
    }

}
