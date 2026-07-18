package com.example.sales.chat.service;

import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import com.example.sales.user.UserMapper;
import com.example.sales.user.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    public List<UserResponse> searchUser(String keyword) {
        List<User> userList = userRepository.searchUsers(keyword);
        return userMapper.toUserResponse(userList);
    }
}
