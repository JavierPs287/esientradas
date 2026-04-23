package edu.esi.ds.esientradas.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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

@Service
public class PDFService {

	private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Autowired
	private PDFDAO pdfDAO;

	@Transactional
	public PDFEntrada generarPdfEntrada(Entrada entrada) {
		try {
			byte[] contenido = crearPdf(entrada);

			PDFEntrada pdfEntrada = new PDFEntrada();
			pdfEntrada.setEntrada(entrada);
			pdfEntrada.setContenido(contenido);
			pdfEntrada.setNombreArchivo(construirNombreArchivo(entrada));
			pdfEntrada.setGeneradoEn(LocalDateTime.now());

			return pdfDAO.save(pdfEntrada);
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo generar el PDF de la entrada", e);
		}
	}

	@Transactional(readOnly = true)
	public Optional<PDFEntrada> obtenerPdfPorEntradaId(Long entradaId) {
		return pdfDAO.findByEntradaId(entradaId);
	}

	private byte[] crearPdf(Entrada entrada) throws IOException {
		try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			PDPage page = new PDPage(PDRectangle.A4);
			document.addPage(page);

			try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
				float startX = 50;
				float currentY = 760;

				contentStream.beginText();
				contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
				contentStream.newLineAtOffset(startX, currentY);
				contentStream.showText("Entrada comprada");
				contentStream.endText();

				currentY -= 40;
				currentY = escribirLinea(contentStream, startX, currentY, "ID entrada: " + entrada.getId(), PDType1Font.HELVETICA_BOLD, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Estado: " + entrada.getEstado(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Precio: " + formatearPrecio(entrada.getPrecio()) + " EUR", PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Espectaculo: " + entrada.getEspectaculo().getArtista(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Fecha: " + entrada.getEspectaculo().getFecha().format(FECHA_FORMATTER), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, "Tipo de entrada: " + entrada.getClass().getSimpleName(), PDType1Font.HELVETICA, 12);
				currentY = escribirLinea(contentStream, startX, currentY, describirInformacionEspecifica(entrada), PDType1Font.HELVETICA, 12);
			}

			document.save(outputStream);
			return outputStream.toByteArray();
		}
	}

	private float escribirLinea(PDPageContentStream contentStream, float startX, float currentY, String texto, PDType1Font font, float size) throws IOException {
		contentStream.beginText();
		contentStream.setFont(font, size);
		contentStream.newLineAtOffset(startX, currentY);
		contentStream.showText(texto);
		contentStream.endText();
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
