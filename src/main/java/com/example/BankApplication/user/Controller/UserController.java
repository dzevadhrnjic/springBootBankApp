package com.example.BankApplication.user.Controller;

import com.example.BankApplication.account.Exception.InvalidTokenException;
import com.example.BankApplication.changePassword.Model.ChangePassword;
import com.example.BankApplication.changePassword.Service.ChangePasswordService;
import com.example.BankApplication.user.Exception.InvalidEmailOrPasswordException;
import com.example.BankApplication.user.Model.AccessToken;
import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Model.UserLogin;
import com.example.BankApplication.user.Service.LoginService;
import com.example.BankApplication.user.Service.UserService;
import com.example.BankApplication.user.Exception.ValidationException;
import com.example.BankApplication.user.Exception.ValidationIdException;
import com.example.BankApplication.verification.Exception.EmailVerificationException;
import com.example.BankApplication.verification.Model.Verification;
import com.example.BankApplication.verification.Service.VerifyEmail;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.mail.MessagingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping(path = "api/users")

public class UserController {

    private final UserService userService;
    private final LoginService loginService;
    private final VerifyEmail verifyEmail;
    private final ChangePasswordService changePasswordService;

    public UserController(UserService userService,
                          LoginService loginService,
                          VerifyEmail verifyEmail,
                          ChangePasswordService changePasswordService) {
        this.userService = userService;
        this.loginService = loginService;
        this.verifyEmail = verifyEmail;
        this.changePasswordService = changePasswordService;
    }

    @GetMapping
    public ResponseEntity<Object> listUsers(@Param("pageNumber") Integer pageNumber, @Param("pageSize") Integer pageSize) throws SQLException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.listUsers(pageNumber, pageSize));
        } catch (ValidationIdException e) {
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

    @GetMapping(path = "list/{userId}")
    public ResponseEntity<Object> listUserByIdList(@PathVariable("userId") Long userId) throws SQLException {
        try {
            List<User> listUserById = userService.listUserByIdList(userId);
            return ResponseEntity.status(HttpStatus.OK).body(listUserById);
        }catch (ValidationIdException e){
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
        } catch (MessagingException | IOException e) {
            return null;
        }
    }

    @PostMapping(path = "login")
    public ResponseEntity<Object> userLogin(@RequestBody UserLogin userLogin) throws SQLException {
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
    public ResponseEntity<Object> passwordChange(@RequestHeader(value = "Authorization") String token) throws Exception {
        try {
            changePasswordService.changePassword(token);
            return ResponseEntity.status(HttpStatus.OK).body(token);
        }catch (ValidationIdException | MessagingException | IOException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
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
        }
    }

    @DeleteMapping(path = "{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("userId") Long userId) throws SQLException {
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
                                             @RequestBody User user) throws SQLException {
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