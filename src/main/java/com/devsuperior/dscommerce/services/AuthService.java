package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    public void validateSelforAdmin(long userId){
        User user = userService.authenticate();
        //Verifica se o usuário é Admin ou é ele mesmo
        if(!user.hasRole("ROLE_ADMIN") && user.getId()  != userId ){
            throw new ForbiddenException("Access Denied");
        }

    }
}
