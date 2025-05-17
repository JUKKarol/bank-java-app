package com.github.jukkarol.controller;

import com.github.jukkarol.dto.accountDto.request.CreateAccountRequest;
import com.github.jukkarol.dto.accountDto.response.CreateAccountResponse;
import com.github.jukkarol.models.User;
import com.github.jukkarol.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@AllArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<CreateAccountResponse> createAccount(CreateAccountRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        request.setUser_id(currentUser.getId());

        return new ResponseEntity<>(accountService.createAccount(request), HttpStatus.OK);
    }
}
