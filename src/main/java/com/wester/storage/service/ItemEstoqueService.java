package com.wester.storage.service;

import com.wester.storage.model.ItemEstoque;
import com.wester.storage.model.Nivel;
import com.wester.storage.model.Produto;
import com.wester.storage.model.Usuario;
import com.wester.storage.repository.ItemEstoqueRepository;
import com.wester.storage.repository.NivelRepository;
import com.wester.storage.repository.ProdutoRepository;
// Import HistoricoMovimentacaoService when created
// import com.wester.storage.service.HistoricoMovimentacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemEstoqueService {

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository;

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Inject HistoricoMovimentacaoService later
    // @Autowired
    // private HistoricoMovimentacaoService historicoService;

    // Inject UsuarioService or get current user from SecurityContext
    // @Autowired
    // private UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<ItemEstoque> listarItensPorNivel(Long nivelId) {
        return itemEstoqueRepository.findByNivelId(nivelId);
    }

    @Transactional(readOnly = true)
    public List<ItemEstoque> listarItensPorProduto(Long produtoId) {
        return itemEstoqueRepository.findByProdutoId(produtoId);
    }

    @Transactional(readOnly = true)
    public Optional<ItemEstoque> buscarItemPorNivelEProduto(Long nivelId, Long produtoId) {
        Nivel nivel = nivelRepository.findById(nivelId).orElse(null);
        Produto produto = produtoRepository.findById(produtoId).orElse(null);
        if (nivel == null || produto == null) {
            return Optional.empty();
        }
        return itemEstoqueRepository.findByNivelAndProduto(nivel, produto);
    }

    @Transactional
    public ItemEstoque adicionarOuAtualizarItem(Long nivelId, Long produtoId, int quantidade, Long usuarioId) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva para adicionar/atualizar.");
        }

        Nivel nivel = nivelRepository.findById(nivelId)
                .orElseThrow(() -> new RuntimeException("Nível não encontrado com id: " + nivelId));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + produtoId));
        // Fetch Usuario performing the action (implementation depends on security context)
        // Usuario usuario = usuarioService.buscarUsuarioPorId(usuarioId).orElseThrow(...);

        Optional<ItemEstoque> itemExistenteOpt = itemEstoqueRepository.findByNivelAndProduto(nivel, produto);

        ItemEstoque itemEstoque;
        int quantidadeAnterior = 0;
        String tipoOperacao;

        if (itemExistenteOpt.isPresent()) {
            // Update existing item
            itemEstoque = itemExistenteOpt.get();
            quantidadeAnterior = itemEstoque.getQuantidade();
            itemEstoque.setQuantidade(itemEstoque.getQuantidade() + quantidade); // Add to existing quantity
            tipoOperacao = "ENTRADA"; // Or "AJUSTE_QUANTIDADE" depending on business logic
        } else {
            // Create new item
            itemEstoque = new ItemEstoque();
            itemEstoque.setNivel(nivel);
            itemEstoque.setProduto(produto);
            itemEstoque.setQuantidade(quantidade);
            tipoOperacao = "ENTRADA";
        }

        ItemEstoque itemSalvo = itemEstoqueRepository.save(itemEstoque);

        // Log history
        // historicoService.logOperacao(...);

        return itemSalvo;
    }

    @Transactional
    public ItemEstoque removerQuantidadeItem(Long nivelId, Long produtoId, int quantidade, Long usuarioId) {
         if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a remover deve ser positiva.");
        }

        Nivel nivel = nivelRepository.findById(nivelId)
                .orElseThrow(() -> new RuntimeException("Nível não encontrado com id: " + nivelId));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + produtoId));
        // Fetch Usuario

        ItemEstoque itemEstoque = itemEstoqueRepository.findByNivelAndProduto(nivel, produto)
                .orElseThrow(() -> new RuntimeException("Item não encontrado neste nível para o produto especificado."));

        int quantidadeAnterior = itemEstoque.getQuantidade();
        if (quantidade > quantidadeAnterior) {
            throw new IllegalArgumentException("Quantidade a remover excede a quantidade em estoque (" + quantidadeAnterior + ").");
        }

        itemEstoque.setQuantidade(quantidadeAnterior - quantidade);

        ItemEstoque itemSalvo;
        if (itemEstoque.getQuantidade() == 0) {
            // Optionally delete the record if quantity becomes zero
            itemEstoqueRepository.delete(itemEstoque);
            itemSalvo = itemEstoque; // Return the state before deletion for logging
        } else {
            itemSalvo = itemEstoqueRepository.save(itemEstoque);
        }

        // Log history
        // historicoService.logOperacao(...);

        return itemSalvo; // Or perhaps return void or a status DTO
    }

     @Transactional
    public void moverItem(Long nivelOrigemId, Long nivelDestinoId, Long produtoId, int quantidade, Long usuarioId) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a mover deve ser positiva.");
        }
        if (nivelOrigemId.equals(nivelDestinoId)) {
             throw new IllegalArgumentException("Nível de origem e destino não podem ser iguais.");
        }

        // Remove from origin
        ItemEstoque itemOrigem = removerQuantidadeItem(nivelOrigemId, produtoId, quantidade, usuarioId);

        try {
            // Add to destination
            adicionarOuAtualizarItem(nivelDestinoId, produtoId, quantidade, usuarioId);
        } catch (Exception e) {
            // Rollback: Add back to origin if adding to destination fails
             adicionarOuAtualizarItem(nivelOrigemId, produtoId, quantidade, usuarioId); // This needs careful handling of history logging
             throw new RuntimeException("Erro ao adicionar item ao nível de destino. Movimentação revertida.", e);
        }

        // Log history for MOVIMENTACAO (might need specific logging in remover/adicionar or a dedicated log here)
    }

    // Service method for the complex search defined in the repository
    @Transactional(readOnly = true)
    public List<ItemEstoque> pesquisarItens(String nomeModelo, String codigoWester, String cor, Integer quantidadeMin, Integer quantidadeMax, Long areaId) {
        return itemEstoqueRepository.searchItems(nomeModelo, codigoWester, cor, quantidadeMin, quantidadeMax, areaId);
    }
}

