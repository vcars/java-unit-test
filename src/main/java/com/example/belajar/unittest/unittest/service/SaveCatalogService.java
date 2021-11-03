package com.example.belajar.unittest.unittest.service;

import com.example.belajar.unittest.unittest.adaptor.esb.SaveCatalogAdaptor;
import com.example.belajar.unittest.unittest.model.request.SaveCatalogRequest;
import com.example.belajar.unittest.unittest.model.response.ValidationResponse;
import com.example.belajar.unittest.unittest.util.CacheUtility;
import com.example.belajar.unittest.unittest.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class SaveCatalogService {

    private SaveCatalogAdaptor saveCatalogAdaptor;
    private CacheUtility cacheUtility;

    public SaveCatalogService (SaveCatalogAdaptor saveCatalogAdaptor,
                               CacheUtility cacheUtility){
        this.saveCatalogAdaptor = saveCatalogAdaptor;
        this.cacheUtility = cacheUtility;
    }

    public ValidationResponse execute(SaveCatalogRequest input){
        log.info("SaveCatalogRequest = {}",input);
        String sessionCache = this.cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,input.getAccessTokenRequest().getAccessToken());
        if (StringUtils.isEmpty(sessionCache)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Anda tidak berhak akses");
        }
        return saveCatalogAdaptor.execute(input.getCatalogRequest());
    }

}
