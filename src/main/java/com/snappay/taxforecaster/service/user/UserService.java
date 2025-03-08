package com.snappay.taxforecaster.service.user;

import com.snappay.taxforecaster.common.exception.NotAcceptableException;
import com.snappay.taxforecaster.controller.model.TaxUserDto;
import com.snappay.taxforecaster.controller.model.TokenModel;
import com.snappay.taxforecaster.entity.UserEntity;
import com.snappay.taxforecaster.repository.UserRepository;
import com.snappay.taxforecaster.service.oauth.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    private final UserRepository repository;
    private final JwtService jwtService;

    public UserService(UserRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public UserEntity getOne(String username) {
        return repository.findByUsername(username);
    }

    private String encodePassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    private boolean checkPassword(String rawPassword, String hashPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(rawPassword, hashPassword);
    }

    public UserEntity save(TaxUserDto dto) {
        if (null == dto) {
            throw new NotAcceptableException(Collections.singletonList("dto.is.null"));
        }
        if (repository.existsByUsername(dto.getUsername())) {
            throw new NotAcceptableException(Collections.singletonList("user.is.exist"));
        }
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        entity.setPassword(this.encodePassword(dto.getPassword()));
        return repository.save(entity);
    }

    public TokenModel login(TaxUserDto dto) {
        UserEntity entity = repository.findByUsername(dto.getUsername());
        if (null == entity) {
            throw new NotAcceptableException(Collections.singletonList("user.not.found"));
        }
        if (!this.checkPassword(dto.getPassword(), entity.getPassword())) {
            throw new NotAcceptableException(Collections.singletonList("password.not.correct"));
        }
        return jwtService.getAccessToken(entity);
    }
}
