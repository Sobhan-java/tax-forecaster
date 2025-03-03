package com.snappay.taxforecaster.repository;

import com.snappay.taxforecaster.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,String> {

    UserEntity findByUsername(String username);

    boolean existsByUsername(String username);
}
