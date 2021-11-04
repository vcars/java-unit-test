package com.example.belajar.unittest.unittest.service;

import com.example.belajar.unittest.unittest.model.entity.Customers;
import com.example.belajar.unittest.unittest.model.request.LoginRequest;
import com.example.belajar.unittest.unittest.model.response.SessionResponse;
import com.example.belajar.unittest.unittest.repository.CustomersRepository;
import com.example.belajar.unittest.unittest.util.CacheUtility;
import com.example.belajar.unittest.unittest.util.CommonUtility;
import com.example.belajar.unittest.unittest.util.Constants;
import org.apache.commons.lang3.ObjectUtils;
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

    @Value("${unittest.session.expired.cooldown}")
    private Integer cooldown;

    @Value("${unittest.config.retry.login}")
    private Integer maxLoginRetryLogin;

    public LoginService(CustomersRepository customersRepository,
                        CacheUtility cacheUtility,
                        CommonUtility commonUtility){
        this.customersRepository = customersRepository;
        this.cacheUtility = cacheUtility;
        this.commonUtility = commonUtility;
    }

    public SessionResponse execute(LoginRequest request){
        this.doValidateRequest(request);
        Customers customers = customersRepository.getUserByUsername(request.getUsername()).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Data tidak ditemukan"));

        this.commonUtility.checkBlockedUser(request.getUsername());

        if (!customers.getPassword().equals(request.getPassword()))
        {
            this.checkRetryLoginCount(customers);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"username atau password yang anda masukkan salah");
        }

        String uuid = commonUtility.getUuid();
        this.cacheUtility.set(Constants.RDS_CUSTOMER_SESSION,uuid,request.getUsername(),sessionExpired);
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

    private void checkRetryLoginCount(Customers customers){
        Integer retryCount = ObjectUtils.isEmpty(customers.getRetryLogin()) ? 0 : customers.getRetryLogin();
        if (retryCount >= maxLoginRetryLogin){
            updateRetryCount(customers,0);
            this.cacheUtility.set(Constants.RDS_CUSTOMER_BLOCKED,customers.getUsername(),"LIMIT RETRY LOGIN",cooldown);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,Constants.CUSTOMER_LIMIT_REACH);
        }
        updateRetryCount(customers,retryCount+1);
    }

    private void updateRetryCount(Customers customers , Integer value){
        customers.setRetryLogin(value);
        customersRepository.save(customers);
    }
}
