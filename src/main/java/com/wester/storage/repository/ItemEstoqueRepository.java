package com.wester.storage.repository;

import com.wester.storage.model.ItemEstoque;
import com.wester.storage.model.Nivel;
import com.wester.storage.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemEstoqueRepository extends JpaRepository<ItemEstoque, Long> {

    Optional<ItemEstoque> findByNivelAndProduto(Nivel nivel, Produto produto);

    List<ItemEstoque> findByNivelId(Long nivelId);

    List<ItemEstoque> findByProdutoId(Long produtoId);

    // Query to find items based on product details (name, code, color) and quantity range
    // This joins multiple tables and might be complex. Consider using Specifications or Querydsl for more complex searches.
    @Query("SELECT ie FROM ItemEstoque ie JOIN ie.produto p JOIN ie.nivel n JOIN n.grade g JOIN g.fileira f JOIN f.areaEstoque ae " +
           "WHERE (:nomeModelo IS NULL OR lower(p.nomeModelo) LIKE lower(concat('%', :nomeModelo, '%'))) " +
           "AND (:codigoWester IS NULL OR p.codigoSistemaWester = :codigoWester) " +
           "AND (:cor IS NULL OR lower(p.cor) = lower(:cor)) " +
           "AND (:quantidadeMin IS NULL OR ie.quantidade >= :quantidadeMin) " +
           "AND (:quantidadeMax IS NULL OR ie.quantidade <= :quantidadeMax) " +
           "AND (:areaId IS NULL OR ae.id = :areaId)")
    List<ItemEstoque> searchItems(
            @Param("nomeModelo") String nomeModelo,
            @Param("codigoWester") String codigoWester,
            @Param("cor") String cor,
            @Param("quantidadeMin") Integer quantidadeMin,
            @Param("quantidadeMax") Integer quantidadeMax,
            @Param("areaId") Long areaId
    );

    // Find all items within a specific AreaEstoque
    @Query("SELECT ie FROM ItemEstoque ie JOIN ie.nivel n JOIN n.grade g JOIN g.fileira f WHERE f.areaEstoque.id = :areaId")
    List<ItemEstoque> findByAreaEstoqueId(@Param("areaId") Long areaId);
}

