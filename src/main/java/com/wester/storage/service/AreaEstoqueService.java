package com.wester.storage.service;

import com.wester.storage.model.AreaEstoque;
import com.wester.storage.repository.AreaEstoqueRepository;
import com.wester.storage.repository.FileiraRepository; // Needed for deletion check
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AreaEstoqueService {

    @Autowired
    private AreaEstoqueRepository areaEstoqueRepository;

    @Autowired
    private FileiraRepository fileiraRepository; // Inject to check dependencies

    @Transactional(readOnly = true)
    public List<AreaEstoque> listarTodasAreas() {
        return areaEstoqueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<AreaEstoque> buscarAreaPorId(Long id) {
        return areaEstoqueRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<AreaEstoque> buscarPorNome(String nome) {
        return areaEstoqueRepository.findByNome(nome);
    }

    @Transactional
    public AreaEstoque salvarArea(AreaEstoque areaEstoque) {
        if (areaEstoque.getId() == null && buscarPorNome(areaEstoque.getNome()).isPresent()) {
            throw new IllegalArgumentException("Nome da Área de Estoque já existe: " + areaEstoque.getNome());
        }
        return areaEstoqueRepository.save(areaEstoque);
    }

    @Transactional
    public AreaEstoque atualizarArea(Long id, AreaEstoque areaAtualizada) {
        return areaEstoqueRepository.findById(id).map(areaExistente -> {
            Optional<AreaEstoque> areaComMesmoNome = buscarPorNome(areaAtualizada.getNome());
            if (!areaExistente.getNome().equals(areaAtualizada.getNome()) &&
                areaComMesmoNome.isPresent() && !areaComMesmoNome.get().getId().equals(id)) {
                throw new IllegalArgumentException("Nome da Área de Estoque já existe: " + areaAtualizada.getNome());
            }
            areaExistente.setNome(areaAtualizada.getNome());
            areaExistente.setDescricao(areaAtualizada.getDescricao());
            return areaEstoqueRepository.save(areaExistente);
        }).orElseThrow(() -> new RuntimeException("Área de Estoque não encontrada com id: " + id));
    }

    @Transactional
    public void deletarArea(Long id) {
        AreaEstoque area = areaEstoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área de Estoque não encontrada com id: " + id));

        // Check for dependencies (Fileiras)
        if (!fileiraRepository.findByAreaEstoqueIdOrderByOrdemAscIdentificadorAsc(id).isEmpty()) {
            throw new IllegalStateException("Não é possível deletar a Área de Estoque pois ela contém Fileiras associadas.");
        }

        areaEstoqueRepository.deleteById(id);
    }
}

