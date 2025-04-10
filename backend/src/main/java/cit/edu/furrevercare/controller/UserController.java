package cit.edu.furrevercare.controller;


import cit.edu.furrevercare.entity.User;
import cit.edu.furrevercare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public String createUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        return userService.saveUser(user);
    }

    @GetMapping("/{userID}")
    public User getUser(@PathVariable String userID) throws ExecutionException, InterruptedException {
        return userService.getUserById(userID);
    }

    @DeleteMapping("/{userID}")
    public String deleteUser(@PathVariable String userID) throws ExecutionException, InterruptedException {
        return userService.deleteUser(userID);
    }
}
