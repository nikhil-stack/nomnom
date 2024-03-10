package com.example.nomnom.controllers;

import com.example.nomnom.utils.security.JwtHelper;
import com.example.nomnom.utils.security.JwtRequest;
import com.example.nomnom.utils.security.JwtResponse;
import com.example.nomnom.services.UserService;
import com.example.nomnom.models.Users;
import com.example.nomnom.utils.ApiResponse;
import java.util.regex.*;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nomnom/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;


    @Autowired
    private JwtHelper helper;

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

   static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

   static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";


    @PostMapping("/login")
    public ApiResponse<JwtResponse> login(@RequestBody JwtRequest request) {
        try {
            final String email = request.getEmail();
            final String password = request.getPassword();
            validateEmail(email, password);
            doAuthenticate(email, password);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            String token = helper.generateToken(userDetails);

            JwtResponse response = JwtResponse.builder()
                    .jwtToken(token)
                    .username(userDetails.getUsername())
                    .build();

            return ApiResponse.successResponse(response);
        } catch (InvalidEmailException e) {
            return ApiResponse.failureResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (AuthenticationException e) {
            return ApiResponse.failureResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password");
        }
    }

    private void validateEmail(String email, String password) {
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);

        Matcher emailMatcher = emailPattern.matcher(email);
        Matcher passwordMatcher = passwordPattern.matcher(password);

        if (!emailMatcher.matches() || !passwordMatcher.matches()) {
            throw new InvalidEmailException("Invalid email/password");
        }
    }

    static class InvalidEmailException extends RuntimeException {
        public InvalidEmailException(String message) {
            super(message);
        }
    }


    @PostMapping("/signup")
    public ApiResponse<Object> createUser( @RequestBody Users user) {
        final String email = user.getEmail();
        final String password = user.getPassword();

        validateEmail(email, password);

        String response = userService.createUser(user);
        if(response.equals("User successfully created")) {
            return ApiResponse.successResponse(response);
        }
        else{
            return  ApiResponse.failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), response);
        }
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(HttpServletRequest request) {
        System.out.println("here");
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(request, null, auth);
            }
            return ApiResponse.successResponse(null);
        } catch (Exception e) {
            return ApiResponse.failureResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logout failed");
        }
    }


    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }

}