package com.ecommerce.user.mapper;

import com.ecommerce.user.dto.UserProfileDTO;
import com.ecommerce.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDTO toProfileDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(user.getRole().name())
                .enabled(user.isEnabled())
                .build();
    }
}
