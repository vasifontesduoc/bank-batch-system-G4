package com.bancoxyz.bank_batch_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bancoxyz.bank_batch_system.model.Transaccion;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
}