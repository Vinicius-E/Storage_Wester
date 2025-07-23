package com.wester.storage.repository;

import com.wester.storage.model.AreaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AreaEstoqueRepository extends JpaRepository<AreaEstoque, Long> {

    // Spring Data JPA automatically creates a query based on the method name
    Optional<AreaEstoque> findByNome(String nome);

    // We can add more custom query methods here if needed later
    // Example: List<AreaEstoque> findByDescricaoContaining(String keyword);
}

