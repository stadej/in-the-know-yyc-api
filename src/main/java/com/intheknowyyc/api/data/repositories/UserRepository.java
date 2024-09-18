package com.intheknowyyc.api.data.repositories;

import com.intheknowyyc.api.data.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT user FROM User user WHERE user.id.email=?1")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT user FROM User user WHERE user.id.username=?1")
    Optional<User> findUserByUsername(String username);

}
