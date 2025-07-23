package com.wester.storage.repository;

import com.wester.storage.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoSistemaWester(String codigoSistemaWester);

    List<Produto> findByNomeModeloContainingIgnoreCase(String nomeModelo);

    List<Produto> findByCorIgnoreCase(String cor);

    // Add other custom queries as needed, e.g., combining filters
    List<Produto> findByNomeModeloContainingIgnoreCaseAndCorIgnoreCase(String nomeModelo, String cor);
}

