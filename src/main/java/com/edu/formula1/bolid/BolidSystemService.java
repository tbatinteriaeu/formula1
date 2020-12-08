package com.edu.formula1.bolid;


import com.edu.formula1.ActiveMQConfig;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import static com.edu.formula1.ActiveMQConfig.BOLID_QUEUE_REPLY2;

@Service
@Configuration
@EnableScheduling
public class BolidSystemService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    private static Logger logger;

    public BolidSystemService(){
        logger = Logger.getLogger(BolidSystemService.class.getCanonicalName());
    }


    /**
     * send info fo actual state of bolid
     * e.g sendStateInfo(BolidState.TEMPERATRUE, 100.12)
     *
     * @param bolidState {@link com.edu.formula1.bolid.BolidState}
     */
    public void sendBolidStateInfo(BolidState bolidState) {
        String queue = ActiveMQConfig.BOLID_INFO_QUEUE;
        logger.info(String.format("sending with convertAndSend() to queue '%s' message <%s>", queue, bolidState.toString()));
        jmsTemplate.convertAndSend(queue, bolidState);
    }

    @Scheduled(fixedRate = 15000)
    public void runSendingStateInfo(){
        this.sendBolidStateInfo(
                new com.edu.formula1.bolid.BolidState(
                        generateRandomDouble(BolidState.TEMPERATURE_RANGE[0], BolidState.TEMPERATURE_RANGE[1]),
                        generateRandomDouble(BolidState.TIRE_PRESSURE_RANGE[0], BolidState.TIRE_PRESSURE_RANGE[1]),
                        generateRandomDouble(BolidState.OIL_PRESSURE_RANGE[0], BolidState.OIL_PRESSURE_RANGE[1])
                ));
    }

    public Message sendWithReply(com.edu.formula1.bolid.BolidState bolidState) throws JMSException {
        jmsTemplate.setReceiveTimeout(1000L);
        jmsMessagingTemplate.setJmsTemplate(jmsTemplate);
        jmsMessagingTemplate.setMessageConverter(new MappingJackson2MessageConverter());
        Session session = jmsMessagingTemplate.getConnectionFactory().createConnection()
                .createSession(false, Session.AUTO_ACKNOWLEDGE);

        ActiveMQObjectMessage objectMessage = new ActiveMQObjectMessage();
        objectMessage.setJMSCorrelationID(UUID.randomUUID().toString());
        objectMessage.setJMSReplyTo(new ActiveMQQueue(BOLID_QUEUE_REPLY2));
        objectMessage.setJMSCorrelationID(UUID.randomUUID().toString());
        objectMessage.setJMSExpiration(1000L);
        objectMessage.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        objectMessage.setResponseRequired(true);
        objectMessage.setObject(bolidState);

        return jmsTemplate.sendAndReceive(new ActiveMQQueue(BOLID_QUEUE_REPLY2), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return objectMessage;
            }
        });
    }

    @JmsListener(destination = ActiveMQConfig.BOLID_SYSTEM)
    public void receiveMessage(@Payload BolidState bolidState,
                               @Headers MessageHeaders headers,
                               org.springframework.messaging.Message message, Session session) {

        logger.info("received <" + bolidState + ">");
    }

    private Double generateRandomDouble(double min, double max){
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }
}
