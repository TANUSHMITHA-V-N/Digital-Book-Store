package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.CustomUserDetails;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins="http://localhost:3000")
public class AuthController {
	@Autowired
	private AuthenticationManager authManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
		try {
			// Authenticate the user
			Authentication authentication = authManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
			
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	
			String token = jwtUtil.generateToken(userDetails); 
			String role = userDetails.getRole(); 
			
			Map<String, String> response=new HashMap<>();
			response.put("token", token);
			response.put("role", role); 
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Invalid email or password"));
		}
	}
}
