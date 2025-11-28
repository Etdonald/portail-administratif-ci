package ci.gov.gestion_documents.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("konedonald2000@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email envoyé à: " + to);

        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email à " + to + ": " + e.getMessage());
        }
    }

}
