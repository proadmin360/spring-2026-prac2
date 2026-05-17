package edu.uoc.epcsd.notification.domain.service;

import edu.uoc.epcsd.notification.application.kafka.MicrocredentialMessage;
import edu.uoc.epcsd.notification.application.kafka.ProductMessage;
import edu.uoc.epcsd.notification.application.rest.dtos.GetProductResponse;
import edu.uoc.epcsd.notification.application.rest.dtos.GetUserResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;

@Log4j2
@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${usersService.getUsersToAlert.url}")
    private String userToAlertUrl;

    @Value("${usersService.url}")
    private String usersServiceUrl;

    @Value("${productService.getProductDetails.url}")
    private String productServiceUrl;
    
    @Value("${microcredentialService.getMicrocredentialDetails.url}")
    private String microcredentialServiceUrl;

    @Override
    public void notifyProductAvailable(ProductMessage productMessage) {

        GetProductResponse product = new RestTemplate().getForEntity(productServiceUrl, GetProductResponse.class, productMessage.getProductId()).getBody();

        GetUserResponse[] usersToAlert = new RestTemplate().getForEntity(userToAlertUrl, GetUserResponse[].class, productMessage.getProductId(), LocalDate.now()).getBody();

        for (GetUserResponse user : usersToAlert) {
            log.info("Sending an email to user " + user.getFullName() + " at \"" + user.getEmail() + "\" to notify new units available on product \"" + product.getName() + "\".");
        }
    }
    
    /**
     * Notifica als administradors que existeix una microcredencial pendent de validació.
     * Notifiquem als administradore conforme hi ha una microcredential pendent de validació. Aquest
     * métdo simula l'enviament de la notificación a través d'una línia de log, respectant així la sol·licitut de
     * l'enunciat de la práctica
     */
    @Override
    public void notifyCredentialPending(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha notificat als administradors que la microcredencial {} està pendent de validació per a la matrícula {}.",
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getEnrollment());
    }

    /**
     * Notifiquem que una microcredencial ha estat concedida. Aquest métode simula l’enviament de 
     * la notificació a traveés d'una línia de log on utilitzem la informació rebuda a través de l’esdeveniment Kafka.
     */
    @Override
    public void notifyCredentialGranted(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha notificat que la microcredencial {} associada a la matrícula {} ha estat concedida.",
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getEnrollment());
    }

    /**
     * Notifiquem que una microcredencial ha estat rebutjada. Aquest métode deixa constància mitjançant log 
     * que la microcredencial associada a una matrícula concreta ha estat rebutjada.
     */
   @Override
    public void notifyCredentialRejected(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha notificat que la microcredencial {} associada a la matrícula {} ha estat rebutjada.",
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getEnrollment());
    }

}
