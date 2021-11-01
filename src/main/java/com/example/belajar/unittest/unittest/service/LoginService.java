package com.example.belajar.unittest.unittest.service;

import com.example.belajar.unittest.unittest.model.entity.Customers;
import com.example.belajar.unittest.unittest.model.request.LoginRequest;
import com.example.belajar.unittest.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.unittest.repository.CustomersRepository;
import com.example.belajar.unittest.unittest.util.CacheUtility;
import com.example.belajar.unittest.unittest.util.CommonUtility;
import com.example.belajar.unittest.unittest.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoginService {

    private CustomersRepository customersRepository;
    private CacheUtility cacheUtility;
    private CommonUtility commonUtility;

    @Value("${unittest.session.expired.signing}")
    private Integer sessionExpired;

    public LoginService(CustomersRepository customersRepository,
                        CacheUtility cacheUtility,
                        CommonUtility commonUtility){
        this.customersRepository = customersRepository;
        this.cacheUtility = cacheUtility;
        this.commonUtility = commonUtility;
    }

    public SessionResponse execute(LoginRequest request){
        this.doValidateRequest(request);
        Customers customers = customersRepository.getUserByUsername(request.getUsername()).orElseThrow(()-> new ResponseStatusException(
                HttpStatus.NOT_FOUND,"User tidak ditemukan"
        ));

        if (!customers.getPassword().equals(request.getPassword()))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Password yang anda masukkan salah");
        }
        String uuid = commonUtility.getUuid();
        this.cacheUtility.set(Constants.CUSTOMER_SESSION,uuid,request.getUsername(),sessionExpired);
        return SessionResponse.builder()
                .accessToken(uuid)
                .username(customers.getUsername())
                .fullname(customers.getFullname())
                .build();
    }

    private void doValidateRequest(LoginRequest request){
        if (StringUtils.isEmpty(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"password tidak boleh kosong");
        }
    }
}
