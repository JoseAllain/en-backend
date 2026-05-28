package com.ensolvers.backend.user.service;

import java.util.List;

import com.ensolvers.backend.user.dto.UserUpsertRequest;
import com.ensolvers.backend.user.model.User;

public interface UserService {

    User create(UserUpsertRequest request);

    User update(Long id, UserUpsertRequest request);

    void delete(Long id);

    User findById(Long id);

    List<User> findAll();
}