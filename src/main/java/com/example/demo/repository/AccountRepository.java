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

    // rechercher un compte bancaire en fonction de l'ID de l'utilisateur
    Account findByUserId(Long userId);

   /* @Modifying
    @Query(value = "DELETE FROM accounts a WHERE a.userId = :userId ")
    void deleteByUserId(@Param("userId") Long userId);

    /*@Modifying
    @Query(value = "UPDATE accounts a SET a.account_balance = :accountBalance WHERE a.id = :accountId")
    void updateAccountBalance(@Param("accountId") Long accountId, @Param("accountBalance") BigDecimal accountBalance);

     */


}
