package com.bancoxyz.bank_batch_system.repository;

import com.bancoxyz.bank_batch_system.model.Interes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteresRepository extends JpaRepository<Interes, Long> {
    List<Interes> findByCuentaId(Long cuentaId);
}