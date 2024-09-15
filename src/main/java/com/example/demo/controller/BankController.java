package com.example.demo.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.Account;
import com.example.demo.service.AccountService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class BankController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/dashboard")
	public String dashboard(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.findAccountByUsername(username);
		model.addAttribute("account", account);
		return "dashboard";
	}

	@GetMapping("/register")
	public String showRegistrationForm() {
		return "register";
	}

	@PostMapping("/register")
	public String registerAccount(@RequestParam String username, @RequestParam String password, Model model) {
		try {
			accountService.registerAccount(username, password);
			return "redirect:/login";
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			return "register";
		}
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping(value = "/deposit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String deposit(BigDecimal amount) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.findAccountByUsername(username);
		accountService.deposit(account, amount);
		return "redirect:/dashboard";
	}

	@PostMapping(value = "/withdraw", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String withdraw(BigDecimal amount, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.findAccountByUsername(username);

		try {
			accountService.withdraw(account, amount);
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("account", account);
			return "dashboard";
		}

		return "redirect:/dashboard";
	}

	@GetMapping("/transactions")
	public String transactionsHistory(Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountService.findAccountByUsername(username);
		model.addAttribute("transactions", accountService.getTransactionHistory(account));

		return "transactions";
	}

	@PostMapping(value = "/transfer", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String transferAmount(String toUsername, BigDecimal amount, Model model) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account fromAccount = accountService.findAccountByUsername(username);

		try {
			accountService.transferAmount(fromAccount, toUsername, amount);
		} catch (RuntimeException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("account", fromAccount);
			return "dashboard";
		}

		return "redirect:/dashboard";
	}

}
