package com.intr.debt.repository;

import com.intr.debt.model.FailedPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedPayoutRepository extends JpaRepository<FailedPayout, Long> {

    List<FailedPayout> findAllByStatus(String status);

    @Modifying
    @Query("update FailedPayout f set f.status = :status where f.id = :id")
    void updatePayoutStatus(@Param("status") String status, @Param("id") Long id);
}
