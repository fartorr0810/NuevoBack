package com.example.demo.service;

import java.net.PasswordAuthentication;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import org.springframework.stereotype.Service;
/**
 * Servicio para enviar un correo al administrador
 * @author fran
 *
 */
@Service
public class CorreoService {
	/**
	 * Metodo que recibe el destinatario, un asunto y el cuerpo del mensaje.
	 * Declaramos un objeto Properties, en el indicamos el host,user,clave.. etc..
	 * Despues una sesion y un MimeMessage, tras ello, metemos todos los datos, declaramos el transporte
	 * y el protocolo, nos conectamos y tras ello enviamos el mensaje y cerramos el transporte.
	 * en caso de que haya algun error lanzara una excepcion MessagingException
	 * @param destinatario persona a la que va
	 * @param asunto sobre que trata el email
	 * @param cuerpo contenido del comentario
	 */
	public void enviarConGMail(String destinatario, String asunto, String cuerpo) {
	    String remitente = "icedragonempire72@gmail.com";  
	    Properties props = System.getProperties();
	    props.put("mail.smtp.host", "smtp.gmail.com"); 
	    props.put("mail.smtp.user", remitente);
	    props.put("mail.smtp.clave", "Cenation12,");
	    props.put("mail.smtp.auth", "true");   
	    props.put("mail.smtp.starttls.enable", "true"); 
	    props.put("mail.smtp.port", "587");
	    Session session = Session.getDefaultInstance(props);
	    MimeMessage message = new MimeMessage(session);
	    try {
	        message.setFrom(new InternetAddress(remitente));
	        message.addRecipients(Message.RecipientType.TO, destinatario); 
	        message.setSubject(asunto);
	        message.setText(cuerpo);
	        Transport transport = session.getTransport("smtp");
	        transport.connect("smtp.gmail.com", remitente, "Cenation12,");
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	    }
	    catch (MessagingException me) {
	    }
	}
}
