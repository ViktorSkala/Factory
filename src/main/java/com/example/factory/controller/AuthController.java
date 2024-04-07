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
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        System.out.println("login controller begin");
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

    @GetMapping("/login/oauth2/code/google")
    public void oauthLoginFinish(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken auth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User auth2User = auth2Token.getPrincipal();

        Map<String, Object> attributes = auth2User.getAttributes();

        Employee authEmployee = employeeService.findByEmail((String) attributes.get("email"));
        if (Objects.isNull(authEmployee)) {
            Employee employee = Employee.builder()
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("given_name"))
                    .lastName((String) attributes.get("family_name"))
                    .roles(roleService.getAll())
                    .build();
            authEmployee = employeeService.create(employee);
            Location location = geoRetriever.getLocation(request.getRemoteAddr());
            try {
                telegramBot.sendMessage("User " + authEmployee.getEmail() + " registered from " + location.toString());
            } catch (TelegramApiException e) {
                System.out.println("Telegram bot send message exception " + e.getMessage());
            }
        }
        String token = jwtUtils.generateJwtToken(authEmployee.getEmail());
        try {
            response.sendRedirect("/oauth2.html?token=" + token);
        } catch (IOException e) {
            System.out.println("response fault");;
        }
        Location location = geoRetriever.getLocation(request.getRemoteAddr());
        try {
            telegramBot.sendMessage("User " + authEmployee.getEmail() + " loged in from " + location.toString());
        } catch (TelegramApiException e) {
            System.out.println("Telegram bot send message exception " + e.getMessage());
        }
    }

    @GetMapping("/unauthorized")
    public ResponseEntity<?> unauthorized() {
        User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.isNull(authUser)) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().body(EmployeeDto.of(employeeService.findByEmail(authUser.getUsername())));
    }

    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(HttpServletRequest request) {
        System.out.println("getTokenInfo method starts");
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
