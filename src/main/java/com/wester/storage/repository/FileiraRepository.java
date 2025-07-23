package com.wester.storage.repository;

import com.wester.storage.model.AreaEstoque;
import com.wester.storage.model.Fileira;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileiraRepository extends JpaRepository<Fileira, Long> {

    Optional<Fileira> findByAreaEstoqueAndIdentificador(AreaEstoque areaEstoque, String identificador);

    List<Fileira> findByAreaEstoqueIdOrderByOrdemAscIdentificadorAsc(Long areaEstoqueId);

    // Add other custom queries as needed
}

