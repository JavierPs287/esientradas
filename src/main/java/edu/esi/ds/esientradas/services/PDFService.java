package edu.esi.ds.esientradas.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.dao.PDFDAO;
import edu.esi.ds.esientradas.model.DeZona;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.PDFEntrada;
import edu.esi.ds.esientradas.model.Precisa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PDFService {

	private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
	private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Autowired
	private PDFDAO pdfDAO;

	@Transactional
	public PDFEntrada generarPdfEntrada(Entrada entrada) {
		try {
			logger.info("Generando PDF para la entrada con ID: {}", entrada.getId());
			byte[] contenido = crearPdf(entrada);

			PDFEntrada pdfEntrada = new PDFEntrada();
			pdfEntrada.setEntrada(entrada);
			pdfEntrada.setContenido(contenido);
			pdfEntrada.setNombreArchivo(construirNombreArchivo(entrada));
			pdfEntrada.setGeneradoEn(LocalDateTime.now());

			logger.info("PDF generado para la entrada con ID: {}", entrada.getId());
			return pdfDAO.save(pdfEntrada);
		} catch (IOException e) {
			logger.error("Error al generar PDF para la entrada con ID: {}", entrada.getId(), e);
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo generar el PDF de la entrada", e);
		}
	}

	@Transactional(readOnly = true)
	public Optional<PDFEntrada> obtenerPdfPorEntradaId(Long entradaId) {
		logger.info("Obteniendo PDF para la entrada con ID: {}", entradaId);
		return pdfDAO.findByEntradaId(entradaId);
	}

	private byte[] crearPdf(Entrada entrada) throws IOException {
		Entrada entradaReal = (Entrada) Hibernate.unproxy(entrada);

		try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);
			logger.info("Creando contenido del PDF para la entrada con ID: {}", entradaReal.getId());

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				float startX = 50;
				float currentY = 760;

				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
				contentStream.newLineAtOffset(startX, currentY);
				contentStream.showText("Entrada comprada");
				contentStream.endText();

				currentY -= 40;
				currentY = escribirLinea(contentStream, startX, currentY, "ID entrada: " + entradaReal.getId(), PDType1Font.HELVETICA_BOLD, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Estado: " + entradaReal.getEstado(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Precio: " + formatearPrecio(entradaReal.getPrecio()) + " EUR", PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Escenario: " + entradaReal.getEspectaculo().getArtista(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Espectaculo: " + entradaReal.getEspectaculo().getArtista(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Fecha: " + entradaReal.getEspectaculo().getFecha().format(FECHA_FORMATTER), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Tipo de entrada: " + obtenerTipoEntrada(entradaReal), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, describirInformacionEspecifica(entradaReal), PDType1Font.HELVETICA, 12);
			}

			document.save(outputStream);
			logger.info("PDF creado exitosamente para la entrada con ID: {}", entradaReal.getId());
			return outputStream.toByteArray();
		}
	}

	private float escribirLinea(PDPageContentStream contentStream, float startX, float currentY, String texto, PDType1Font font, float size) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, size);
		contentStream.newLineAtOffset(startX, currentY);
		contentStream.showText(texto);
		contentStream.endText();
		logger.debug("Escribiendo línea en PDF: {}", texto);
		return currentY - 24;
	}

	private String describirInformacionEspecifica(Entrada entrada) {
		if (entrada instanceof Precisa precisa) {
			return "Asiento: planta " + precisa.getPlanta() + ", fila " + precisa.getFila() + ", columna " + precisa.getColumna();
		}

		if (entrada instanceof DeZona deZona) {
			return "Zona: " + deZona.getZona();
		}

		return "";
	}

	private String obtenerTipoEntrada(Entrada entrada) {
		if (entrada instanceof Precisa) {
			return "Precisa";
		}

		if (entrada instanceof DeZona) {
			return "DeZona";
		}

		return entrada.getClass().getSimpleName();
	}

	private String formatearPrecio(Long precioCentimos) {
		if (precioCentimos == null) {
			return "0,00";
		}

		long euros = precioCentimos / 100;
		long centimos = Math.abs(precioCentimos % 100);
		return euros + "," + String.format("%02d", centimos);
	}

	private String construirNombreArchivo(Entrada entrada) {
		return "entrada-" + entrada.getId() + ".pdf";
	}

}
