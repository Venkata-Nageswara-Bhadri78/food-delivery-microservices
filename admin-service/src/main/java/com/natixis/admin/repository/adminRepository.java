package com.natixis.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.admin.entity.Admin;

public interface adminRepository extends JpaRepository<Admin, Long>{
    boolean existsByUserId(Long userId);
}
