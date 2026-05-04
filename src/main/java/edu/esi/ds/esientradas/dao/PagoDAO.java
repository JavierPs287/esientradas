package edu.esi.ds.esientradas.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.esi.ds.esientradas.model.Pago;

public interface PagoDAO extends JpaRepository<Pago, Long> {

	List<Pago> findByIdUsuarioOrderByFechaPagoDesc(Long idUsuario);

}
