package com.running.you_run.user.repository;

import com.running.you_run.user.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    Optional<PointTransaction> findTopBySenderIdAndReceiverIdOrderBySentAtDesc(Long senderId, Long receiverId);
}
