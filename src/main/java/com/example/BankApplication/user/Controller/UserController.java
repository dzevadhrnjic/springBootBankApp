package com.example.BankApplication.user.Controller;

import com.example.BankApplication.account.Exception.InvalidTokenException;
import com.example.BankApplication.blacklist.exception.BlackListTokenException;
import com.example.BankApplication.changePassword.Model.ChangePassword;
import com.example.BankApplication.changePassword.Service.ChangePasswordService;
import com.example.BankApplication.user.Model.AccessToken;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Exception.InvalidEmailOrPasswordException;
import com.example.BankApplication.user.Model.UserLogin;
import com.example.BankApplication.user.Service.LoginService;
import com.example.BankApplication.user.Service.UserService;
import com.example.BankApplication.user.Exception.ValidationException;
import com.example.BankApplication.user.Exception.ValidationIdException;
import com.example.BankApplication.verification.Exception.EmailVerificationException;
import com.example.BankApplication.verification.Model.Verification;
import com.example.BankApplication.verification.Service.VerifyEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "api/users")

public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    LoginService loginService;

    @Autowired
    VerifyEmail verifyEmail;

    @Autowired
    ChangePasswordService changePasswordService;


    @GetMapping
    public ResponseEntity<Object> getUsers(@Param("pageNumber") Integer pageNumber,
                                           @Param("pageSize") Integer pageSize,
                                           @RequestParam(value = "firstname", required = false) String firstname,
                                           @RequestParam(value = "lastname", required = false) String lastname,
                                           @RequestParam(value = "address", required = false) String address,
                                           @RequestParam(value = "email", required = false) String email) {
        try {
            List<User> users = userService.listUsers(pageNumber, pageSize, firstname, lastname, address, email);
            return ResponseEntity.status(HttpStatus.OK).body(users);
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(path = "{userId}")
    public ResponseEntity<Object> listUserById(@PathVariable("userId") Long userId) {
        try {
            User listUserId = userService.getUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(listUserId);
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("addUser")
    public ResponseEntity<Object> createNewUser(@RequestBody User user) {
        try {
            userService.addUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<Object> userLogin(@RequestBody UserLogin userLogin) {
        try {
            AccessToken loginUser = loginService.loginUser(userLogin);
            return ResponseEntity.status(HttpStatus.CREATED).body(loginUser);
        } catch (InvalidEmailOrPasswordException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "verifyEmail")
    public ResponseEntity<Object> verifyYourMail(@RequestBody Verification verification) {
        try {
            Verification email = verifyEmail.emailVerify(verification);
            return ResponseEntity.status(HttpStatus.OK).body(email);
        } catch (EmailVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(path = "change-password")
    public ResponseEntity<Object> passwordChange(@RequestHeader(value = "Authorization") String token)  {
        try {
            changePasswordService.changePassword(token);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }catch (ValidationIdException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (BlackListTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @PostMapping(path = "new-password")
    public ResponseEntity<Object> newPassword(@RequestHeader(value = "Authorization") String token,
                                              @RequestBody ChangePassword changePassword){
        try {
            ChangePassword password = changePasswordService.newPassword(token, changePassword);
            return ResponseEntity.status(HttpStatus.OK).body(password);
        }catch (InvalidEmailOrPasswordException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (BlackListTokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userId);
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping(path = "{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable("userId") Long userId,
                                             @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(userId, user);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedUser);
        } catch (ValidationException | InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ValidationIdException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}