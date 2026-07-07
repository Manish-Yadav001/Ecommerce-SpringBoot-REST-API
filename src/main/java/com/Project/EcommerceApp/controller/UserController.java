package com.Project.EcommerceApp.controller;

import com.Project.EcommerceApp.entity.User;
import com.Project.EcommerceApp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        String status = userService.registerUser(user);
        if (status.equals("USER ALREADY EXISTS")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("USER ALREADY EXISTS");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("REGISTRATION SUCCESSFUL");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        String status= userService.deleteUser(id);
        if(status.equals("USER DOESN'T EXIST")){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("USER NOT FOUND");
        }
        return ResponseEntity.status(HttpStatus.OK).body("USER DELETED SUCCESSFULLY");
    }

    @GetMapping("/AllUsers")
    public List<User> getAllUsers(){
        List <User> users=userService.getAllUsers();
        return users;
    }
}