package com.bantads.msauth.core.repository;

import com.bantads.msauth.core.document.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthRepository extends MongoRepository<Usuario,Long> {
}
