package com.example.toDoList.task;

import com.example.toDoList.utils.utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class taskContoller {

    @Autowired
    private iTaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody taskModel task, HttpServletRequest request){
        var userId = (UUID) request.getAttribute("userId");
        task.setUserId(userId);

        var currentDate = LocalDateTime.now();

        if (currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt())){

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de inicio/termino é menor que a data atual.");

        } else if (task.getEndAt().isBefore(task.getStartAt())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de termino é maior que a data de inicio.");

        } else {

            var taskCreated = this.taskRepository.save(task);
            return ResponseEntity.status(HttpStatus.OK).body(taskCreated);

        }

    }

    @GetMapping("/list")
    public List<taskModel> list(HttpServletRequest request){
        var userId = (UUID) request.getAttribute("userId");
        var tasks = this.taskRepository.findByUserId(userId);
        return tasks;
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody taskModel taskEdit, HttpServletRequest request, @PathVariable UUID id){
        var userId = (UUID) request.getAttribute("userId");

        var existTask = this.taskRepository.findById(id).orElse(null);

        if(existTask == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tarefa não encontrada.");
        }

        if(!existTask.getUserId().equals(userId)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário sem permissão de alteração!");
        }

        utils.copyNonNullProperties(taskEdit, existTask);

        this.taskRepository.save(existTask);

        return ResponseEntity.status(HttpStatus.OK).body(existTask);
    }

}
