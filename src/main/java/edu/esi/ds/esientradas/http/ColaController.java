package edu.esi.ds.esientradas.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esientradas.services.QueueService;
import edu.esi.ds.esientradas.dto.DtoQueueStatus;

@RestController
@RequestMapping("/cola")
public class ColaController {

    @Autowired
    private QueueService queueService;

    @PostMapping("/join")
    public DtoQueueStatus join(@RequestParam Long espectaculoId, @RequestParam String token) {
        queueService.joinQueue(espectaculoId, token);
        // Devolver el estado inmediatamente después de unirse
        DtoQueueStatus dto = new DtoQueueStatus();
        dto.setEspectaculoId(espectaculoId);
        dto.setTokenUsuario(token);
        int pos = queueService.getPosition(espectaculoId, token);
        dto.setEstado(pos > 0 ? "WAITING" : "NOT_JOINED");
        dto.setPosicion(pos > 0 ? pos : null);
        return dto;
    }

    @PostMapping("/leave")
    public String leave(@RequestParam Long espectaculoId, @RequestParam String token) {
        queueService.leaveQueue(espectaculoId, token);
        return "ok";
    }

    @GetMapping("/status")
    public DtoQueueStatus status(@RequestParam Long espectaculoId, @RequestParam String token) {
        DtoQueueStatus dto = new DtoQueueStatus();
        dto.setEspectaculoId(espectaculoId);
        dto.setTokenUsuario(token);
        boolean active = queueService.userHasActiveTurn(espectaculoId, token);
        if (active) {
            dto.setEstado("ACTIVE");
            // fetch active entry to get expiration
            var opt = queueService.getActiveEntry(espectaculoId, token);
            opt.ifPresent(qe -> dto.setExpiracion(qe.getExpiracion()));
            dto.setPosicion(1);
        } else {
            int pos = queueService.getPosition(espectaculoId, token);
            dto.setEstado(pos > 0 ? "WAITING" : "NOT_JOINED");
            dto.setPosicion(pos > 0 ? pos : null);
        }
        return dto;
    }

}
