package com.example.demo.repository;

import com.example.demo.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUserId(Long userId);
    Account findByIban(int iban);
    @Modifying
    @Query(value = "DELETE FROM Account a WHERE a.user.id = :userId")
    void deleteByUserIdQuery(@Param("userId") Long userId);


    boolean existsByUserId(Long userId);
    long countByIban(int iban);

}
