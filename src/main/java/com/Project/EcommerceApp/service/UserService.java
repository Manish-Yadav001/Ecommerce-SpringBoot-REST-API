package com.Project.EcommerceApp.service;

import com.Project.EcommerceApp.entity.User;
import com.Project.EcommerceApp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String registerUser(User user) {

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return "USER ALREADY EXISTS ";
        }
        userRepository.save(user);
        return "USER REGISTER SUCCESSFULLY";
    }

    public String deleteUser(int id) {
        Optional<User> existUser = userRepository.findById(id);
        if(existUser.isEmpty()){
            return "USER DOESN'T EXIST";
        }
        userRepository.delete(existUser.get());
        return "USER REMOVED SUCCESSFULLY";
    }

   public List<User> getAllUsers(){
        return userRepository.findAll();
   }
}
