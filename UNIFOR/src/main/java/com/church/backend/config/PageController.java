package com.church.backend.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

	@GetMapping("/")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage() {
		return "forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage() {
		return "reset-password";
	}

	@GetMapping("/password-reset-success")
	public String successPage() {
		return "success";
	}
}
