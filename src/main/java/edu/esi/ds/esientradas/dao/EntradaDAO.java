package edu.esi.ds.esientradas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import jakarta.transaction.Transactional;

public interface EntradaDAO extends JpaRepository<Entrada, Long> {
    List<Entrada> findByEspectaculoId(Long espectaculoId);
    List<Entrada> findByEspectaculoIdAndEstado(Long espectaculoId, Estado disponible);

    @Transactional
    @Query(value = "UPDATE Entrada e SET e.estado = :estado WHERE e.id = :idEntrada")
    @Modifying
    void updateEstado(@Param("idEntrada") Long idEntrada, @Param("estado") Estado estado);

    // TODO Solucionar consulta
    /*@Query("""
        SELECT 
            COUNT(*) AS total,
            SUM(estado='DISPONIBLE') as libres,
            SUM(estado='RESERVADA') as reservadas,
            SUM(estado='VENDIDA') as vendidas
            FROM entrada 
            WHERE espectaculo_id = :espectaculoId""")
    Object[] countEntradasByEstado(@Param("espectaculoId") Long espectaculoId);
        */
}