package com.example.belajar.unittest.unittest.controller;

import com.example.belajar.unittest.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.unittest.model.request.LoginRequest;
import com.example.belajar.unittest.unittest.model.request.RegisterRequest;
import com.example.belajar.unittest.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.unittest.service.RegisterService;
import com.example.belajar.unittest.unittest.service.LoginService;
import com.example.belajar.unittest.unittest.service.LogoutService;
import com.example.belajar.unittest.unittest.util.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/customers")
public class CustomerController {

    private LoginService loginService;
    private LogoutService logoutService;
    private RegisterService registerService;

    public CustomerController(LoginService loginService,
                              LogoutService logoutService,
                              RegisterService registerService){
        this.loginService = loginService;
        this.logoutService = logoutService;
        this.registerService = registerService;
    }
    @PostMapping("/v1/login")
    public ResponseEntity<Response> signIn(@RequestBody LoginRequest loginRequest){
        SessionResponse sessionResponse = loginService.execute(loginRequest);
        Response response = new Response(sessionResponse, "Login Berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/register")
    public ResponseEntity<Response> register(@RequestBody RegisterRequest registerRequest){
        ValidationResponse validationResponse = registerService.execute(registerRequest);
        Response response = new Response(validationResponse.getResult(), "Pendaftaran Berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/logout")
    public ResponseEntity<Response> changePassword(@RequestBody AccessTokenRequest accessTokenRequest){
        ValidationResponse validationResponse = logoutService.execute(accessTokenRequest);
        Response response = new Response(validationResponse.getResult(), "Logout Berhasil", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
