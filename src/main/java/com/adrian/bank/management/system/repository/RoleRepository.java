package com.adrian.bank.management.system.repository;

import com.adrian.bank.management.system.entity.ERole;
import com.adrian.bank.management.system.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(ERole roleCustomer);
}
