package com.example.sales.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "avgRating", ignore = true)
    UserInfoResponse toUserResponse(User user);

    List<UserInfoResponse> toUserResponse(List<User> users);

    UserSummary toUserSummary(User user);

    List<UserSummary> toUserSummary(List<User> users);
}