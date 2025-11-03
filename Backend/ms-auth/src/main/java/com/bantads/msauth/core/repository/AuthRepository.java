package com.bantads.msauth.core.repository;

import com.bantads.msauth.core.document.Usuario;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthRepository extends MongoRepository<Usuario,Long> {

    Optional<Usuario> findByLogin(String login);

}
