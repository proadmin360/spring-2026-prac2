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
     *
     * El mètode simula l’enviament de la notificació mitjançant una línia de log,
     * tal com es demana a l’enunciat de la pràctica.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public void notifyCredentialPending(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha notificat als administradors que la microcredencial {} està pendent de validació per a la matrícula {}.",
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getEnrollment());
    }

    /**
     * Notifica a l’estudiant que la seva microcredencial ha estat concedida.
     *
     * El mètode simula l’enviament del correu mitjançant una línia de log
     * amb la informació rebuda des de l’esdeveniment Kafka.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public void notifyCredentialGranted(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha notificat per correu l'usuari {} que la microcredencial {} ha estat concedida per al curs {}.",
                microcredentialMessage.getUserEmail(),
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getCourseId());
    }

    /**
     * Notifica que una microcredencial ha estat rebutjada.
     *
     * El mètode deixa constància mitjançant log del rebuig de la microcredencial
     * associada a una matrícula concreta.
     *
     * Desenvolupat per Jaume Jurado.
     */
    @Override
    public void notifyCredentialRejected(MicrocredentialMessage microcredentialMessage) {
        log.info("S'ha registrat el rebuig de la microcredencial {} associada a la matrícula {}.",
                microcredentialMessage.getMicrocredentialId(),
                microcredentialMessage.getEnrollment());
    }

}
