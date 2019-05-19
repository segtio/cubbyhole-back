package com.kata.cubbyhole.web.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class SignUpRequest {
    @NotBlank(message = "Name cannot be empty")
    @Size(min = 4, max = 40, message = "Name size must be between 4 and 40")
    private String name;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 15, message = "Username size must be between 3 and 15")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Size(max = 40, message = "Email size cannot be over 40")
    @Email
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 20, message = "Password size must be between 6 and 20")
    private String password;

    public SignUpRequest() {
    }

    public SignUpRequest(@NotBlank @Size(min = 4, max = 40) String name, @NotBlank @Size(min = 3, max = 15) String username, @NotBlank @Size(max = 40) @Email String email, @NotBlank @Size(min = 6, max = 20) String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
