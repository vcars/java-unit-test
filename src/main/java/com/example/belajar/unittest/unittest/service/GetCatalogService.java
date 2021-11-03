package com.example.belajar.unittest.unittest.service;

import com.example.belajar.unittest.unittest.adaptor.esb.GetCatalogAdaptor;
import com.example.belajar.unittest.unittest.model.request.AccessTokenRequest;
import com.example.belajar.unittest.unittest.model.response.ListCatalogResponse;
import com.example.belajar.unittest.unittest.util.CacheUtility;
import com.example.belajar.unittest.unittest.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class GetCatalogService {

    private GetCatalogAdaptor getCatalogAdaptor;
    private CacheUtility cacheUtility;

    public GetCatalogService(GetCatalogAdaptor getCatalogAdaptor,
                             CacheUtility cacheUtility){
        this.getCatalogAdaptor = getCatalogAdaptor;
        this.cacheUtility = cacheUtility;
    }

    public ListCatalogResponse execute(AccessTokenRequest input){
        String sessionCache = this.cacheUtility.get(Constants.RDS_CUSTOMER_SESSION,input.getAccessToken());
        if (StringUtils.isEmpty(sessionCache)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Anda tidak berhak akses");
        }
        return getCatalogAdaptor.execute(new Object());
    }
}
