package com.example.sales.user;


import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserInfoResponse getUserByUsername(String username) {
        return
                userMapper.toUserResponse(
                        userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new)
                );
    }
}
