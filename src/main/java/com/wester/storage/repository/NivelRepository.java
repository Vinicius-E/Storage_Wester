package com.wester.storage.repository;

import com.wester.storage.model.Grade;
import com.wester.storage.model.Nivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NivelRepository extends JpaRepository<Nivel, Long> {

    Optional<Nivel> findByGradeAndIdentificador(Grade grade, String identificador);

    List<Nivel> findByGradeIdOrderByOrdemAscIdentificadorAsc(Long gradeId);

    Optional<Nivel> findByIdentificadorCompletoLegivel(String identificadorCompletoLegivel);

    // Add other custom queries as needed
}

