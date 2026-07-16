package com.example.sales.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    List<UserResponse> toUserResponse(List<User> users);
}
