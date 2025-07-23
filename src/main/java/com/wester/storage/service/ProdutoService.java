package com.wester.storage.service;

import com.wester.storage.model.Produto;
import com.wester.storage.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<Produto> listarTodosProdutos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Produto> buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Produto> buscarPorCodigoWester(String codigo) {
        return produtoRepository.findByCodigoSistemaWester(codigo);
    }

    @Transactional
    public Produto salvarProduto(Produto produto) {
        // Add validation or business logic before saving if needed
        // For example, check if codigoSistemaWester already exists
        if (produto.getId() == null && buscarPorCodigoWester(produto.getCodigoSistemaWester()).isPresent()) {
            throw new IllegalArgumentException("Código Wester já existe: " + produto.getCodigoSistemaWester());
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        return produtoRepository.findById(id).map(produtoExistente -> {
            // Check if the updated code conflicts with another existing product
            if (!produtoExistente.getCodigoSistemaWester().equals(produtoAtualizado.getCodigoSistemaWester()) &&
                buscarPorCodigoWester(produtoAtualizado.getCodigoSistemaWester()).isPresent()) {
                throw new IllegalArgumentException("Código Wester já existe: " + produtoAtualizado.getCodigoSistemaWester());
            }

            produtoExistente.setCodigoSistemaWester(produtoAtualizado.getCodigoSistemaWester());
            produtoExistente.setNomeModelo(produtoAtualizado.getNomeModelo());
            produtoExistente.setCor(produtoAtualizado.getCor());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            return produtoRepository.save(produtoExistente);
        }).orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id)); // Consider a custom exception
    }

    @Transactional
    public void deletarProduto(Long id) {
        // Add logic here to check if the product is in use in ItemEstoque before deleting
        // For now, just delete directly
        if (!produtoRepository.existsById(id)) {
             throw new RuntimeException("Produto não encontrado com id: " + id); // Consider a custom exception
        }
        produtoRepository.deleteById(id);
    }

    // Add methods for search functionalities based on repository methods
    @Transactional(readOnly = true)
    public List<Produto> buscarPorNomeModeloContendo(String nomeModelo) {
        return produtoRepository.findByNomeModeloContainingIgnoreCase(nomeModelo);
    }

     @Transactional(readOnly = true)
    public List<Produto> buscarPorCor(String cor) {
        return produtoRepository.findByCorIgnoreCase(cor);
    }

}

