package com.shurona.chat.mytalk.common.aop;

import com.shurona.chat.mytalk.chat.common.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.ChatException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ChatException.class)
    public String handleChatError(ChatException e, Model model) {
        ChatErrorCode code = e.getCode();
        model.addAttribute("errorMessage", code.getMessage());
        model.addAttribute("status", code.getStatus());
        return "error/customError"; //
    }

}
