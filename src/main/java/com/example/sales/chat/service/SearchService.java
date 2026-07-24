package com.example.sales.chat.service;

import com.example.sales.user.UserRepository;
import com.example.sales.user.model.User;
import com.example.sales.user.UserMapper;
import com.example.sales.user.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Service for searching users by keyword.
 */
@Service
public class SearchService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    /**
     * Searches users by a keyword and returns user info responses.
     *
     * @param keyword search keyword
     * @return matching users
     */
    public List<UserInfoResponse> searchUser(String keyword) {
        List<User> userList = userRepository.searchUsers(keyword);
        return userMapper.toUserResponse(userList);
    }
}
