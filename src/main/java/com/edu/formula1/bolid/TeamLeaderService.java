package com.edu.formula1.bolid;


import com.edu.formula1.ActiveMQConfig;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import java.util.logging.Logger;

@Component
public class TeamLeaderService implements SessionAwareMessageListener<Message> {

    public final static Integer MAX_COUNT_PITSTOPS = 3;
    private static Logger logger;

    public TeamLeaderService(){
        logger = Logger.getLogger(TeamLeaderService.class.getCanonicalName());
    }

    @Override
    @JmsListener(destination = ActiveMQConfig.BOLID_QUEUE_REPLY2)
    public void onMessage(Message message, Session session) throws JMSException {
        logger.info("Team leader service response");
        logger.info(message.toString());
        BolidState bolidState = (BolidState) ((ActiveMQObjectMessage) message).getObject();

        logger.info(bolidState.toString());
        // done handling the request, now create a response message
        final ActiveMQObjectMessage responseMessage = new ActiveMQObjectMessage();
        responseMessage.setJMSCorrelationID(message.getJMSCorrelationID());

        //determine whether pit stop is allowed
        bolidState.setPitStopAllowed(isPitStopAllowed(bolidState));

        responseMessage.setObject(bolidState);

        // Message sent back to the replyTo address of the income message.
        final MessageProducer producer = session.createProducer(message.getJMSReplyTo());
        producer.send(responseMessage);
    }

    /**
     * determine whether pit stop is allowed.
     * Pitstop may be allowed only when the max count of already performed pitstops is not nigher than 3.
     * @param bolidState
     * @return
     */
    private Boolean isPitStopAllowed(BolidState bolidState){
        return (bolidState.getPitStopCount() <= MAX_COUNT_PITSTOPS);
    }
}

