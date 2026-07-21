package com.example.sales.chat.service;

import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserMapper;
import com.example.sales.user.UserInfoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<UserInfoResponse> searchUser(String keyword) {
        List<User> userList = userRepository.searchUsers(keyword);
        return userMapper.toUserResponse(userList);
    }
}
