package oliveiradev.inventario.domain.repository;

import oliveiradev.inventario.domain.model.equipamentos.Equipamento;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoRepository extends MongoRepository<Equipamento, String> {
    Optional<Equipamento> findByNumeroDeSerie(String numeroDeSerie);

    List<Equipamento> findByNomeContainingIgnoreCase(String nome);
}