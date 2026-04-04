package com.church.backend.config;

import com.church.backend.identity.dto.AuthDtos.ForgotPasswordRequest;
import com.church.backend.identity.dto.AuthDtos.ResetPasswordRequest;
import com.church.backend.identity.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PageController {

	private final PasswordResetService passwordResetService;

	@GetMapping("/")
	public String loginPage() {
		return "login";
	}

	@GetMapping("/forgot-password")
	public String forgotPasswordPage(Model model) {
		model.addAttribute("email", "");
		model.addAttribute("success", null);
		model.addAttribute("error", null);
		return "forgot-password";
	}

	@PostMapping("/forgot-password")
	public String forgotPasswordSubmit(@ModelAttribute @Valid ForgotPasswordRequest request,
									   BindingResult bindingResult,
									   Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("email", request.email());
			model.addAttribute("error", bindingResult.getFieldError().getDefaultMessage());
			return "forgot-password";
		}

		try {
			passwordResetService.requestReset(request);
			model.addAttribute("success", "Código enviado! Verifique seu e-mail.");
			model.addAttribute("email", request.email());
		} catch (Exception e) {
			model.addAttribute("error", "Erro ao enviar. Tente novamente.");
			model.addAttribute("email", request.email());
		}

		return "forgot-password";
	}

	@GetMapping("/reset-password")
	public String resetPasswordPage(Model model) {
		model.addAttribute("token", "");
		model.addAttribute("newPassword", "");
		model.addAttribute("confirmPassword", "");
		model.addAttribute("error", null);
		return "reset-password";
	}

	@PostMapping("/reset-password")
	public String resetPasswordSubmit(@ModelAttribute ResetPasswordForm form,
									  BindingResult bindingResult,
									  Model model) {
		if (!form.newPassword().equals(form.confirmPassword())) {
			model.addAttribute("error", "As senhas não coincidem.");
			model.addAttribute("token", form.token());
			model.addAttribute("newPassword", form.newPassword());
			model.addAttribute("confirmPassword", form.confirmPassword());
			return "reset-password";
		}

		if (form.newPassword().length() < 6) {
			model.addAttribute("error", "A senha deve ter pelo menos 6 caracteres.");
			model.addAttribute("token", form.token());
			model.addAttribute("newPassword", form.newPassword());
			model.addAttribute("confirmPassword", form.confirmPassword());
			return "reset-password";
		}

		try {
			ResetPasswordRequest request = new ResetPasswordRequest(form.token(), form.newPassword());
			passwordResetService.resetPassword(request);
			return "redirect:/password-reset-success";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage() != null ? e.getMessage() : "Código inválido ou expirado.");
			model.addAttribute("token", form.token());
			model.addAttribute("newPassword", "");
			model.addAttribute("confirmPassword", "");
			return "reset-password";
		}
	}

	@GetMapping("/password-reset-success")
	public String successPage() {
		return "success";
	}

	public record ResetPasswordForm(String token, String newPassword, String confirmPassword) {
	}
}
