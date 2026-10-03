package com.insurance.insurance_app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Controller
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("customer", new Customer());
        return "index";
    }

    @PostMapping("/customers")
    public String createCustomer(@ModelAttribute Customer customer, Model model) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            model.addAttribute("error", "氏名は必須です。");
            return "index";
        }
        if (customer.getName().length() > 50) {
            model.addAttribute("error", "氏名は50文字以内で入力してください。");
            return "index";
        }
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            if (!customer.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                model.addAttribute("error", "メールアドレスの形式が正しくありません。");
                return "index";
            }
            if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
                model.addAttribute("error", "このメールアドレスは既に登録されています。");
                return "index";
            }
        }
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            if (!customer.getPhone().matches("^[0-9\\-]+$")) {
                model.addAttribute("error", "電話番号は数字とハイフンのみ入力してください。");
                return "index";
            }
        }
        if (customer.getBirthdate() != null) {
            int age = Period.between(customer.getBirthdate(), LocalDate.now()).getYears();
            if (age > 70) {
                model.addAttribute("error", "契約上限年齢（70歳）を超えています。");
                return "index";
            }
        }
        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/customers")
    public String listCustomers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "customers";
    }
}
