package com.bluehawana.smrtmart.repository;

import com.bluehawana.smrtmart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    boolean existsById(int id);

    void deleteById(int id);

    Optional<User> findById(int id);}