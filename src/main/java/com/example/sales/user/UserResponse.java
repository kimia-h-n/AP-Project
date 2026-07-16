package com.example.sales.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
    private boolean enable;
}
