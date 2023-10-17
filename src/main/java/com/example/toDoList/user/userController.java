package com.example.toDoList.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.example.toDoList.task.taskModel;
import com.example.toDoList.utils.utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class userController  {

    @Autowired
    private userRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody userModel user){

        var usuario = this.userRepository.findByUsername(user.getUsername());

        if(usuario != null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já cadastrado!");
        }

        var passwordHashred = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());

        user.setPassword(passwordHashred);

        var createdUser = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody userModel userEdit, HttpServletRequest request){

        var userId = (UUID) request.getAttribute("userId");

        var userOn = this.userRepository.findById(userId).orElse(null);

        String username = userEdit.getUsername();

        if (this.userRepository.findByUsername(username) != null){
            userEdit.setUsername(null);
        }

        var passwordHashred = BCrypt.withDefaults().hashToString(12, userEdit.getPassword().toCharArray());
        userEdit.setPassword(passwordHashred);

        utils.copyNonNullProperties(userEdit, userOn);

        this.userRepository.save(userOn);

        return ResponseEntity.status(HttpStatus.OK).body(userOn);
    }

    @DeleteMapping("/delete")
    public void delete(HttpServletRequest request){

        var userId = (UUID) request.getAttribute("userId");

        this.userRepository.deleteById(userId);
    }

}
