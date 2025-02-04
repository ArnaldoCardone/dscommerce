package com.devsuperior.dscommerce.dto;


import com.devsuperior.dscommerce.entities.User;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserDTO {

    private long id;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String password;
    private List<String> roles = new ArrayList<>();

    public UserDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.phone = entity.getPhone();
        this.birthDate = entity.getBirthDate();
        this.password = entity.getPassword();

        for(GrantedAuthority role : entity.getAuthorities()){
            roles.add(role.getAuthority());
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }
}
