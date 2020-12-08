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
import java.util.ArrayList;
import java.util.logging.Logger;

@Service
public class MonitorService {
    @Autowired
    private JmsTemplate jmsTemplate;

    public final Integer EMERGENCY_LEVEL_SEVERE = 2;

    private Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    public MonitorService(){

    }

    @JmsListener(destination = ActiveMQConfig.BOLID_INFO_QUEUE)
    public void receiveMessage(@Payload com.edu.formula1.bolid.BolidState bolidState,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {
        logger.info("received <" + bolidState + ">");
        ArrayList<String> channels = route(bolidState);
        for(String channel : channels){
            logger.info("message routed to channel: " + channel);
            jmsTemplate.convertAndSend(channel, bolidState);
        }

    }

    protected ArrayList<String> route(com.edu.formula1.bolid.BolidState bolidState) {
        ArrayList<String> channels = new ArrayList<String>();
        try {
            validateBolidState(bolidState);
        } catch (BolidStateParameterException exception) {
            channels.add(ActiveMQConfig.BOLID_SYSTEM);
        } catch (com.edu.formula1.bolid.BolidStateEmergencyException exception) {
            channels.add(ActiveMQConfig.BOLID_SYSTEM);
            channels.add(ActiveMQConfig.BOLID_MECHANIC_TEAM);
        }
        return channels;
    }

    /**
     *
     * @param bolidState
     * @throws BolidStateParameterException
     * @throws com.edu.formula1.bolid.BolidStateEmergencyException
     */
    protected void validateBolidState(BolidState bolidState) throws BolidStateParameterException, BolidStateEmergencyException {

        //validate emergency level
        if (bolidState.getEmergencyLevel() >= EMERGENCY_LEVEL_SEVERE) {
            throw new BolidStateEmergencyException("Emergence level severe: " + bolidState.getEmergencyLevel());
        }

        //validate bolid parameters
        if (!between(bolidState.getTemperature(), BolidState.TEMPERATURE_RANGE[0], BolidState.TEMPERATURE_RANGE[1])) {
            throw new BolidStateParameterException("Temparture of engine is out of range");
        }
        if (!between(bolidState.getOilPressure(), BolidState.OIL_PRESSURE_RANGE[0], BolidState.OIL_PRESSURE_RANGE[1])) {
            throw new BolidStateParameterException("Oil pressure of engine is out of range");
        }
        if (!between(bolidState.getTirePressure(), BolidState.TIRE_PRESSURE_RANGE[0], BolidState.TIRE_PRESSURE_RANGE[1])) {
            throw new BolidStateParameterException("Tire pressure of bolid is out of range");
        }


    }

    private boolean between(Double i, Double min, Double max) {
        return (i >= min && i <= max);
    }
}
