package com.emintolgahanpolat.api.configuration.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long > {

    User findByUsername(String username);
    boolean existsUsersByEmail(String email);

    boolean existsUsersByUsername(String username);

    User findByEmail(String email);
}
    