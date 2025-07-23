package com.wester.storage.repository;

import com.wester.storage.model.Fileira;
import com.wester.storage.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    Optional<Grade> findByFileiraAndIdentificador(Fileira fileira, String identificador);

    List<Grade> findByFileiraIdOrderByOrdemAscIdentificadorAsc(Long fileiraId);

    // Add other custom queries as needed
}

