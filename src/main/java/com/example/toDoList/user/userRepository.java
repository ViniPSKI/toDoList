package com.example.toDoList.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

//primeiro a entidade, e depois o tipo do id
public interface userRepository extends JpaRepository<userModel, UUID> {

    userModel findByUsername(String username);

}
