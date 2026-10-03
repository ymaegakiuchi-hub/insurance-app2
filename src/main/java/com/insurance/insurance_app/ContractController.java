package com.insurance.insurance_app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ContractController {

    private final ContractRepository contractRepository;
    private final CustomerRepository customerRepository;

    public ContractController(ContractRepository contractRepository, CustomerRepository customerRepository) {
        this.contractRepository = contractRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/contracts/new")
    public String newContract(Model model) {
        model.addAttribute("customers", customerRepository.findAll());
        model.addAttribute("contract", new Contract());
        return "contracts/new";
    }

    @PostMapping("/contracts")
    public String createContract(@RequestParam Long customerId,
                                  @RequestParam String insuranceType,
                                  @RequestParam Integer age,
                                  @RequestParam Integer grade,
                                  @RequestParam String startDate,
                                  @RequestParam String endDate,
                                  Model model) {

        // 年齢上限チェック（70歳超えたらエラー）
        if (age > 70) {
            model.addAttribute("error", "契約可能年齢は70歳以下です。");
            model.addAttribute("customers", customerRepository.findAll());
            model.addAttribute("inputAge", age);
            return "contracts/new";
        }

        // 等級範囲チェック（1〜20）
        if (grade < 1 || grade > 20) {
            model.addAttribute("error", "等級は1〜20の範囲で入力してください。");
            model.addAttribute("customers", customerRepository.findAll());
            model.addAttribute("inputAge", age);
            return "contracts/new";
        }

        // 満期日が開始日より前のチェック
        if (LocalDate.parse(endDate).isBefore(LocalDate.parse(startDate))) {
            model.addAttribute("error", "満期日は開始日より後の日付を入力してください。");
            model.addAttribute("customers", customerRepository.findAll());
            model.addAttribute("inputAge", age);
            return "contracts/new";
        }

        // 年齢による基本保険料（細分化）
        BigDecimal basePremium;
        if (age <= 20) {
            basePremium = new BigDecimal("60000");
        } else if (age <= 25) {
            basePremium = new BigDecimal("45000");
        } else if (age <= 29) {
            basePremium = new BigDecimal("35000");
        } else if (age <= 39) {
            basePremium = new BigDecimal("28000");
        } else if (age <= 49) {
            basePremium = new BigDecimal("22000");
        } else if (age <= 59) {
            basePremium = new BigDecimal("20000");
        } else if (age <= 69) {
            basePremium = new BigDecimal("25000");
        } else {
            basePremium = new BigDecimal("30000");
        }

        // 等級による割引率
        BigDecimal discountRate;
        if (grade <= 5) {
            discountRate = new BigDecimal("0");
        } else if (grade <= 10) {
            discountRate = new BigDecimal("0.20");
        } else if (grade <= 15) {
            discountRate = new BigDecimal("0.30");
        } else {
            discountRate = new BigDecimal("0.40");
        }

        // 保険料計算（1円単位切り捨て）
        BigDecimal premium = basePremium
                .multiply(BigDecimal.ONE.subtract(discountRate))
                .setScale(0, RoundingMode.DOWN);

        Contract contract = new Contract();
        contract.setCustomer(customerRepository.findById(customerId).orElseThrow());
        contract.setInsuranceType(insuranceType);
        contract.setPremium(premium.intValue());
        contract.setGrade(grade);
        contract.setStartDate(LocalDate.parse(startDate));
        contract.setEndDate(LocalDate.parse(endDate));
        contractRepository.save(contract);
        return "redirect:/contracts";
    }

    @GetMapping("/contracts")
    public String listContracts(Model model) {
        model.addAttribute("contracts", contractRepository.findAll());
        return "contracts/list";
    }

    @GetMapping("/contracts/expiring")
    public String expiringContracts(Model model) {
        LocalDate today = LocalDate.now();
        LocalDate in30days = today.plusDays(30);
        model.addAttribute("contracts", contractRepository.findByEndDateBetween(today, in30days));
        return "contracts/expiring";
    }
}
