package edu.esi.ds.esientradas.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.esi.ds.esientradas.model.PDFEntrada;

public interface PDFDAO extends JpaRepository<PDFEntrada, Long> {

	Optional<PDFEntrada> findByEntradaId(Long entradaId);

}
