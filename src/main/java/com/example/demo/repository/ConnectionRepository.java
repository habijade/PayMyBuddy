package com.example.demo.repository;

import com.example.demo.entity.Connection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    List<Connection> findAllConnectionByUserId(Long userId);

    @Modifying
    @Query(value = "DELETE FROM connections c WHERE c.userId = :userId AND c.connectedUserId = :connectedUserId")
    void deleteByUserIdAndConnectedUserId(@Param("userId") Long userId, @Param("connectedUserId") Long connectedUserId);

}
