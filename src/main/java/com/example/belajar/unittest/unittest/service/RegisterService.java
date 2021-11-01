package com.example.belajar.unittest.unittest.service;

import com.example.belajar.unittest.unittest.model.entity.Customers;
import com.example.belajar.unittest.unittest.model.request.RegisterRequest;
import com.example.belajar.unittest.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.unittest.repository.CustomersRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterService {

    private CustomersRepository customersRepository;

    public RegisterService (CustomersRepository customersRepository){
        this.customersRepository = customersRepository;
    }

    public ValidationResponse execute(RegisterRequest request){
        this.doValidate(request);
        if (ObjectUtils.isNotEmpty(customersRepository.getUserByUsername(request.getUsername()).get())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Username telah terpakai");
        }
        Customers customers = new Customers();
        customers.setEmail(request.getEmail());
        customers.setFullname(request.getFullname());
        customers.setPassword(request.getPassword());
        customers.setEmail(request.getEmail());
        customers.setIsDeleted(false);
        try {
            customersRepository.save(customers);
        }
        catch (JDBCException je){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Gangguan Sistem");
        }
        return ValidationResponse.builder().result(true).build();
    }

    private void doValidate(RegisterRequest request){
        if (StringUtils.isEmpty(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"password tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getFullname())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"fullname tidak boleh kosong");
        }
        if (StringUtils.isEmpty(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"email tidak boleh kosong");
        }
    }
}
