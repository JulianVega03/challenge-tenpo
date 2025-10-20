package com.tempo.challengetempo.repositories;

import com.tempo.challengetempo.entities.CallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallHistoryRepository extends JpaRepository<CallHistory, Long> {

    Page<CallHistory> findAllByOrderByDateDesc(Pageable pageable);

}
