package com.ecommerce.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName;

    private String phone;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
