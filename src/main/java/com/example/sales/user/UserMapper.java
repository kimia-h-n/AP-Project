package com.example.sales.user;

import com.example.sales.user.dto.UserInfoResponse;
import com.example.sales.user.dto.UserSummary;
import com.example.sales.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting {@link User} entities into response DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    /**
     * Converts a user entity to a detailed response DTO.
     * <p>
     * The average rating is ignored and may be filled later by the service layer.
     * </p>
     *
     * @param user user entity
     * @return detailed user response
     */
    @Mapping(target = "avgRating", ignore = true)
    UserInfoResponse toUserResponse(User user);

    /**
     * Converts a list of user entities to detailed response DTOs.
     *
     * @param users list of user entities
     * @return list of detailed user responses
     */
    List<UserInfoResponse> toUserResponse(List<User> users);

    /**
     * Converts a user entity to a lightweight summary DTO.
     *
     * @param user user entity
     * @return user summary
     */
    UserSummary toUserSummary(User user);

    /**
     * Converts a list of user entities to lightweight summary DTOs.
     *
     * @param users list of user entities
     * @return list of user summaries
     */
    List<UserSummary> toUserSummary(List<User> users);
}
