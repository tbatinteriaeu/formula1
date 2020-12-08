package com.edu.formula1.bolid;

import com.edu.formula1.ActiveMQConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.Session;
import java.util.logging.Logger;

@Service
public class BolidMechanicService {

    @Autowired
    private JmsTemplate jmsTemplate;

    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    @JmsListener(destination = ActiveMQConfig.BOLID_MECHANIC_TEAM)
    public void receiveMessage(@Payload com.edu.formula1.bolid.BolidState bolidState,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {

        logger.info("received <" + bolidState + ">");
    }
}
