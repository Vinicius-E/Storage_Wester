package com.wester.storage.service;

import com.wester.storage.model.Grade;
import com.wester.storage.model.Nivel;
import com.wester.storage.repository.GradeRepository;
import com.wester.storage.repository.ItemEstoqueRepository; // Needed for deletion check
import com.wester.storage.repository.NivelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;

    @Autowired
    private GradeRepository gradeRepository; // To fetch Grade

    @Autowired
    private ItemEstoqueRepository itemEstoqueRepository; // To check for dependencies

    @Transactional(readOnly = true)
    public List<Nivel> listarNiveisPorGrade(Long gradeId) {
        return nivelRepository.findByGradeIdOrderByOrdemAscIdentificadorAsc(gradeId);
    }

    @Transactional(readOnly = true)
    public Optional<Nivel> buscarNivelPorId(Long id) {
        return nivelRepository.findById(id);
    }

    @Transactional
    public Nivel salvarNivel(Nivel nivel, Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade não encontrada com id: " + gradeId));
        nivel.setGrade(grade);

        // Check for duplicate identifier within the same Grade
        if (nivel.getId() == null &&
            nivelRepository.findByGradeAndIdentificador(grade, nivel.getIdentificador()).isPresent()) {
            throw new IllegalArgumentException("Identificador de Nível já existe nesta Grade: " + nivel.getIdentificador());
        }

        // TODO: Consider logic to generate/update identificadorCompletoLegivel
        // Example (simple concatenation, might need refinement):
        // nivel.setIdentificadorCompletoLegivel(generateIdentifier(nivel));

        return nivelRepository.save(nivel);
    }

    @Transactional
    public Nivel atualizarNivel(Long id, Nivel nivelAtualizado) {
        return nivelRepository.findById(id).map(nivelExistente -> {
            // Ensure Grade isn't changed, or handle it explicitly if allowed
            if (!nivelExistente.getGrade().getId().equals(nivelAtualizado.getGrade().getId())) {
                 throw new IllegalArgumentException("Não é permitido alterar a Grade de um Nível existente.");
            }

            // Check for duplicate identifier within the same Grade
            Optional<Nivel> nivelComMesmoId = nivelRepository.findByGradeAndIdentificador(
                nivelExistente.getGrade(), nivelAtualizado.getIdentificador());

            if (!nivelExistente.getIdentificador().equals(nivelAtualizado.getIdentificador()) &&
                nivelComMesmoId.isPresent() && !nivelComMesmoId.get().getId().equals(id)) {
                throw new IllegalArgumentException("Identificador de Nível já existe nesta Grade: " + nivelAtualizado.getIdentificador());
            }

            nivelExistente.setIdentificador(nivelAtualizado.getIdentificador());
            nivelExistente.setOrdem(nivelAtualizado.getOrdem());

            // TODO: Update identificadorCompletoLegivel if necessary
            // nivelExistente.setIdentificadorCompletoLegivel(generateIdentifier(nivelExistente));

            return nivelRepository.save(nivelExistente);
        }).orElseThrow(() -> new RuntimeException("Nível não encontrado com id: " + id));
    }

    @Transactional
    public void deletarNivel(Long id) {
        Nivel nivel = nivelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nível não encontrado com id: " + id));

        // Check for dependencies (ItemEstoque)
        if (!itemEstoqueRepository.findByNivelId(id).isEmpty()) {
            throw new IllegalStateException("Não é possível deletar o Nível pois ele contém Itens de Estoque associados.");
        }

        nivelRepository.deleteById(id);
    }

    // Helper method (example) - needs access to parent entities
    /*
    private String generateIdentifier(Nivel nivel) {
        // This requires fetching the full hierarchy (Grade -> Fileira -> AreaEstoque)
        // which might be inefficient here. Consider doing this generation
        // either asynchronously, via database trigger, or when reading the data.
        Grade grade = nivel.getGrade();
        Fileira fileira = grade.getFileira(); // Assuming eager fetch or separate query
        AreaEstoque area = fileira.getAreaEstoque(); // Assuming eager fetch or separate query
        return String.format("%s-%s-%s-%s",
                area.getNome(), // Or an identifier
                fileira.getIdentificador(),
                grade.getIdentificador(),
                nivel.getIdentificador());
    }
    */
}

