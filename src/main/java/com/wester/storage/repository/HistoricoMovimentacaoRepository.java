package com.wester.storage.repository;

import com.wester.storage.model.HistoricoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface HistoricoMovimentacaoRepository extends JpaRepository<HistoricoMovimentacao, Long> {

    List<HistoricoMovimentacao> findByUsuarioIdOrderByTimestampDesc(Long usuarioId);

    List<HistoricoMovimentacao> findByProdutoIdOrderByTimestampDesc(Long produtoId);

    List<HistoricoMovimentacao> findByNivelIdOrderByTimestampDesc(Long nivelId);

    List<HistoricoMovimentacao> findByTipoOperacaoOrderByTimestampDesc(String tipoOperacao);

    List<HistoricoMovimentacao> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

    // Add other custom queries as needed
}

