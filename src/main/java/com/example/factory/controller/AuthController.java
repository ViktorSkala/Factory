package com.example.factory.controller;

import com.example.factory.dto.EmployeeDto;
import com.example.factory.dto.LoginRequest;
import com.example.factory.dto.LoginResponse;
import com.example.factory.dto.TokenDto;
import com.example.factory.georetriver.GeoRetriever;
import com.example.factory.georetriver.Location;
import com.example.factory.jwt.JwtUtils;
import com.example.factory.model.Employee;
import com.example.factory.model.Role;
import com.example.factory.service.EmployeeService;
import com.example.factory.service.RoleService;
import com.example.factory.telegrambot.CustomBot;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class AuthController {

    private AuthenticationManager authenticationManager;
    private EmployeeService employeeService;
    private RoleService roleService;
    private JwtUtils jwtUtils;
    private PasswordEncoder passwordEncoder;
    private GeoRetriever geoRetriever;
    private CustomBot telegramBot;

    public AuthController(AuthenticationManager authenticationManager, EmployeeService employeeService, RoleService roleService, JwtUtils jwtUtils, PasswordEncoder passwordEncoder, GeoRetriever geoRetriever, CustomBot telegramBot) {
        this.authenticationManager = authenticationManager;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.geoRetriever = geoRetriever;
        this.telegramBot = telegramBot;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User principal = (User) authentication.getPrincipal();
        String token = jwtUtils.generateJwtToken(principal.getUsername());
        LoginResponse authResponse = new LoginResponse(principal.getUsername(), token);
        Location location = geoRetriever.getLocation(request.getRemoteAddr());
        System.out.println(request.getRemoteAddr());
        System.out.println(request.getLocalAddr());
        try {
            telegramBot.sendMessage("User " + loginRequest.getEmail() + " loged in from " + location.toString());
        } catch (TelegramApiException e) {
            System.out.println("Telegram bot send message exception " + e.getMessage());
        }
        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody EmployeeDto employeeDto, HttpServletRequest request) {
        Employee employee = Employee.of(employeeDto);
        employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
        List<Role> roles = new ArrayList<>();
        Role worker = roleService.findByName("Worker");
        roles.add(worker);
        employee.setRoles(roles);
        Employee employeeFromDb = employeeService.create(employee);
        Location location = geoRetriever.getLocation(request.getRemoteAddr());
        try {
            telegramBot.sendMessage("User " + employeeFromDb.getEmail() + " registered from " + location.toString());
        } catch (TelegramApiException e) {
            System.out.println("Telegram bot send message exception " + e.getMessage());
        }
        return ResponseEntity.ok(EmployeeDto.of(employeeFromDb));
    }

    @GetMapping("/access-denied")
    public ResponseEntity<?> unauthorized() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.isNull(authUser)) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().body(EmployeeDto.of(employeeService.findByEmail(authUser.getUsername())));
    }

    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
            return ResponseEntity.ok().body("Token not exist");
        }
        String token = header.substring("Bearer ".length());
        Claims claims = jwtUtils.getClaims(token);

        TokenDto tokenDto = TokenDto.of(claims);
        return ResponseEntity.ok().body(tokenDto);
    }
}
