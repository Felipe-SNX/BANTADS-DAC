package com.bantads.msauth.core.repository;

import com.bantads.msauth.core.document.BlackListTokens;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlackListTokensRepository extends MongoRepository<BlackListTokens,String> {

    boolean existsByToken(String token);
}
