package edu.esi.ds.esientradas.services;

import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esientradas.dao.PDFDAO;
import edu.esi.ds.esientradas.model.PDFEntrada;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class GmailEmailService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String username;
    private final String appPassword;

    @Autowired
    private PDFDAO pdfDAO;

    public GmailEmailService(
        @Value("${mail.gmail.username}") String username,
        @Value("${mail.gmail.app-password}") String appPassword
    ) {
        this.username = username;
        this.appPassword = appPassword;
    }

    

    public void sendPDFEmail(String to, String subject, String htmlContent, byte[] pdfContent, String pdfFileName) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

        MimeBodyPart pdfPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(pdfContent, "application/pdf");
        pdfPart.setDataHandler(new DataHandler(source));
        pdfPart.setFileName(pdfFileName);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(pdfPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    @Transactional(readOnly = true)
    public void sendPDF(String to, String subject, Long entradaId) {
        PDFEntrada pdfEntrada = pdfDAO.findByEntradaId(entradaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe PDF para la entrada " + entradaId));

        String htmlContent = construirHtmlContenido(pdfEntrada);

        try {
            sendPDFEmail(to, subject, htmlContent, pdfEntrada.getContenido(), pdfEntrada.getNombreArchivo());
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo enviar el email con el PDF", e);
        }
    }

    private String construirHtmlContenido(PDFEntrada pdfEntrada) {
        String fechaGeneracion = pdfEntrada.getGeneradoEn() == null
            ? "N/A"
            : pdfEntrada.getGeneradoEn().format(DATE_FORMATTER);

        return "<html>"
            + "<body style='margin:0; padding:0; background-color:#d8cfbd; font-family: Arial, sans-serif;'>"
            + "<div style='background-color:#1f2a3a; padding:15px; text-align:center;'>"
            + "<h1 style='color:#ffffff; margin:0; font-size:20px;'>Tus entradas de ESIEntradas</h1>"
            + "</div>"
            + "<div style='padding:40px 20px; text-align:center;'>"
            + "<h2 style='color:#000; font-size:28px; margin-bottom:10px;'>Gracias por tu compra</h2>"
            + "<p style='color:#333; font-size:16px;'>La compra se ha realizado correctamente y ya tienes tu entrada en PDF adjunta.</p>"
            + "<p style='color:#333; font-size:16px; margin:8px 0;'><strong>Entrada ID:</strong> " + pdfEntrada.getEntrada().getId() + "</p>"
            + "<p style='color:#333; font-size:16px; margin:8px 0;'><strong>Archivo:</strong> " + pdfEntrada.getNombreArchivo() + "</p>"
            + "<p style='color:#333; font-size:16px; margin:8px 0 30px;'><strong>Generado:</strong> " + fechaGeneracion + "</p>"
            + "<span style='display:inline-block; padding:14px 28px; font-size:16px; font-weight:bold; color:#000; border-radius:30px; background: linear-gradient(90deg, #ff6a2b, #f7a53b); box-shadow: 0px 4px 10px rgba(0,0,0,0.2);'>"
            + "Entrada adjunta en PDF"
            + "</span>"
            + "</div>"
            + "<div style='text-align:center; padding:20px; font-size:12px; color:#555;'>© ESIEntradas - Todos los derechos reservados</div>"
            + "</body>"
            + "</html>";
    }

}
