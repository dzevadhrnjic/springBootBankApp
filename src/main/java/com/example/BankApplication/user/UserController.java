package com.example.BankApplication.user;

import com.example.BankApplication.account.InvalidTokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping(path = "api/users")

public class UserController {

    private LoginService loginService = new LoginService();

    private final UserService userService;

    @Autowired

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> listUsers() throws SQLException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.listUsers());
        }catch (ValidationIdException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "{userId}")
    public ResponseEntity<Object> listUserById(@PathVariable("userId") Long userId) throws SQLException {
        try {
            User listUserId = userService.listUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(listUserId);
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> createNewUser(@RequestBody User user) throws SQLException {
        try {
            userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<Object> userLogin(@RequestBody User user)throws SQLException{
        try {
            AccessToken user1 = loginService.loginUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user1);
        }catch (InvalidEmailOrPasswordException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<Object> deleteUser(@RequestHeader(value = "Authorization") String token,
                                             @PathVariable("userId") Long userId) throws SQLException {
        try {
            userService.deleteUser(token, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userId);
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidTokenException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping(path = "{userId}")
    public ResponseEntity<Object> updateUser(@RequestHeader(value = "Authorization") String token,
                                             @PathVariable("userId") Long userId,
                                             @RequestBody User user) throws SQLException {
        try {
            User updatedUser =userService.updateUser(token, userId, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedUser);
        } catch (ValidationException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }  catch (ValidationIdException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}