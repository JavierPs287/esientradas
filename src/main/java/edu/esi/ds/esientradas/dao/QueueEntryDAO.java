package edu.esi.ds.esientradas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.esi.ds.esientradas.model.QueueEntry;

public interface QueueEntryDAO extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByEspectaculoIdOrderByCreadoEn(Long espectaculoId);

    List<QueueEntry> findByEspectaculoIdAndEstadoOrderByCreadoEn(Long espectaculoId, edu.esi.ds.esientradas.model.QueueState estado);

    List<QueueEntry> findByTokenUsuario(String tokenUsuario);

    List<QueueEntry> findByEspectaculoIdAndTokenUsuario(Long espectaculoId, String tokenUsuario);

}
