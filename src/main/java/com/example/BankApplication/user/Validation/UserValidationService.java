package com.example.BankApplication.user.Validation;

import com.example.BankApplication.user.Model.User;
import com.example.BankApplication.user.Exception.ValidationException;

public class UserValidationService {

    public static void userFieldsValidation(User user) {

        userFirstNameValidation(user);
        userLastNameValidation(user);
        userAddressValidation(user);
        userPhoneNumberValidation(user);
        userEmailValidation(user);
    }

    public static void userFirstNameValidation(User user) {

        if (user.getFirstname() == null) {
            throw new ValidationException("First name field required");
        } else if (user.getFirstname().equals("")) {
            throw new ValidationException("Can't be empty, first name field required");
        } else if (user.getFirstname().length() > 50) {
            throw new ValidationException("First name is too long, maximum is 50 characters");
        }
    }

    public static void userLastNameValidation(User user) {
        if (user.getLastname() == null) {
            throw new ValidationException("Last name field required");
        } else if (user.getLastname().equals("")) {
            throw new ValidationException("Can't be empty, last name field required");
        } else if (user.getLastname().length() > 50) {
            throw new ValidationException("Last name is too long, maximum is 50 characters");
        }
    }

    public static void userAddressValidation(User user) {
        if (user.getAddress() == null) {
            throw new ValidationException("Address field required");
        } else if (user.getAddress().equals("")) {
            throw new ValidationException("Can't be empty, field address is required");
        } else if (user.getAddress().length() > 100) {
            throw new ValidationException("Address is too long, maximum is 100 characters");
        }
    }

    public static void userPhoneNumberValidation(User user) {
        if (user.getPhoneNumber() == null) {
            throw new ValidationException("Phone number field is required");
        } else if (user.getPhoneNumber().equals("")) {
            throw new ValidationException("Can't be empty, phone number field is required");
        } else if (user.getPhoneNumber().length() != 11) {
            throw new ValidationException("Enter field phone number with +, and 10 numbers");
        } else if (!user.getPhoneNumber().startsWith("+")) {
            throw new ValidationException("Enter field phone number with +");
        }
    }

    public static void userEmailValidation(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Email field is required");
        } else if (user.getEmail().equals("")) {
            throw new ValidationException("Can't be empty, email field is required");
        } else if (user.getEmail().length() > 30) {
            throw new ValidationException("Email is too long, maximum is 30 characters");
        }
    }
}
