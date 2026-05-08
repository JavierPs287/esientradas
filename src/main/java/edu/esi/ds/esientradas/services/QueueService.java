package edu.esi.ds.esientradas.services;

import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.esi.ds.esientradas.dao.QueueEntryDAO;
import edu.esi.ds.esientradas.model.QueueEntry;
import edu.esi.ds.esientradas.model.QueueState;
import edu.esi.ds.esientradas.dao.EspectaculoDAO;
import edu.esi.ds.esientradas.model.Espectaculo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class QueueService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);

    @Autowired
    private QueueEntryDAO queueEntryDAO;

    @Autowired
    private EspectaculoDAO espectaculoDAO;

    private static final long TURN_DURATION_SEC = 600; // 10 minutes

    @Transactional
    public void joinQueue(Long espectaculoId, String tokenUsuario) {
        try {
            // Check if user is already in queue (waiting or active)
            List<QueueEntry> existing = queueEntryDAO.findByEspectaculoIdAndTokenUsuario(espectaculoId, tokenUsuario);
            for (QueueEntry qe : existing) {
                if (qe.getEstado() == QueueState.WAITING || qe.getEstado() == QueueState.ACTIVE) {
                    logger.warn("Usuario {} ya está en la cola del espectaculo {}", tokenUsuario, espectaculoId);
                    return; // silently ignore duplicate join
                }
            }

            Espectaculo e = espectaculoDAO.findById(espectaculoId).orElseThrow();
            QueueEntry entry = new QueueEntry();
            entry.setEspectaculo(e);
            entry.setTokenUsuario(tokenUsuario);
            entry.setEstado(QueueState.WAITING);
            QueueEntry saved = queueEntryDAO.save(entry);
            logger.info("Usuario {} se ha unido a la cola del espectaculo {} con ID: {}", tokenUsuario, espectaculoId, saved.getId());
        } catch (Exception ex) {
            logger.error("ERROR al unirse a la cola para espectaculo {}: {}", espectaculoId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public void leaveQueue(Long espectaculoId, String tokenUsuario) {
        List<QueueEntry> entries = queueEntryDAO.findByTokenUsuario(tokenUsuario);
        for (QueueEntry qe : entries) {
            if (qe.getEspectaculo().getId().equals(espectaculoId)) {
                queueEntryDAO.delete(qe);
                logger.info("Usuario {} ha salido de la cola del espectaculo {}", tokenUsuario, espectaculoId);
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<QueueEntry> getActiveEntry(Long espectaculoId, String tokenUsuario) {
        List<QueueEntry> actives = queueEntryDAO.findByEspectaculoIdAndEstadoOrderByCreadoEn(espectaculoId, QueueState.ACTIVE);
        return actives.stream().filter(qe -> qe.getTokenUsuario().equals(tokenUsuario)).findFirst();
    }

    @Transactional(readOnly = true)
    public int getPosition(Long espectaculoId, String tokenUsuario) {
        List<QueueEntry> waiting = queueEntryDAO.findByEspectaculoIdAndEstadoOrderByCreadoEn(espectaculoId, QueueState.WAITING);
        for (int i = 0; i < waiting.size(); i++) {
            if (waiting.get(i).getTokenUsuario().equals(tokenUsuario)) return i + 1; // 1-based
        }
        return -1;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processQueues() {
        // Expire active turns
        List<QueueEntry> allActive = queueEntryDAO.findAll();
        Instant now = Instant.now();
        for (QueueEntry q : allActive) {
            if (q.getEstado() == QueueState.ACTIVE && q.getExpiracion() != null && q.getExpiracion().isBefore(now)) {
                q.setEstado(QueueState.EXPIRED);
                queueEntryDAO.save(q);
                logger.info("Turno expirado para usuario {} en espectaculo {}", q.getTokenUsuario(), q.getEspectaculo().getId());
            }
        }

        // For each espectaculo with requiereCola and apertura <= now, ensure one active entry exists
        List<Espectaculo> espectaculos = espectaculoDAO.findAll();
        for (Espectaculo e : espectaculos) {
            try {
                if (!e.isRequiereCola()) continue;
                Instant apertura = e.getApertura() != null ? e.getApertura().atZone(java.time.ZoneId.systemDefault()).toInstant() : null;
                if (apertura == null || apertura.isAfter(now)) continue;

                List<QueueEntry> active = queueEntryDAO.findByEspectaculoIdAndEstadoOrderByCreadoEn(e.getId(), QueueState.ACTIVE);
                if (!active.isEmpty()) continue; // already an active

                List<QueueEntry> waiting = queueEntryDAO.findByEspectaculoIdAndEstadoOrderByCreadoEn(e.getId(), QueueState.WAITING);
                if (waiting.isEmpty()) continue;

                QueueEntry next = waiting.get(0);
                next.setEstado(QueueState.ACTIVE);
                next.setExpiracion(Instant.now().plusSeconds(TURN_DURATION_SEC));
                queueEntryDAO.save(next);
                logger.info("Concedido turno a usuario {} para espectaculo {}", next.getTokenUsuario(), e.getId());

            } catch (Exception ex) {
                logger.error("Error procesando cola para espectaculo {}: {}", e.getId(), ex.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public boolean userHasActiveTurn(Long espectaculoId, String tokenUsuario) {
        Optional<QueueEntry> opt = getActiveEntry(espectaculoId, tokenUsuario);
        return opt.isPresent();
    }

}
