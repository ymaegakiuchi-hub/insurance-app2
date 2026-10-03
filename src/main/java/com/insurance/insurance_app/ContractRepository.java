package com.insurance.insurance_app;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByEndDateBetween(LocalDate start, LocalDate end);
}
