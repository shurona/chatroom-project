package com.shurona.chat.mytalk.common.aop.advice;

import com.shurona.chat.mytalk.chat.common.exception.ChatErrorCode;
import com.shurona.chat.mytalk.chat.common.exception.ChatException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    //    @ExceptionHandler(ChatException.class)
    public String handleChatError(ChatException e, Model model) {
        ChatErrorCode code = e.getCode();
        model.addAttribute("errorMessage", code.getMessage());
        model.addAttribute("status", code.getStatus());
        return "error/customError"; //
    }

}
