package com.edu.formula1;

import com.edu.formula1.bolid.BolidState;
import com.edu.formula1.bolid.BolidSystemService;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.logging.Logger;

@RestController
@RequestMapping("/bolid/api/v1/")
public class BolidAPIController {

    public BolidAPIController(){
        bolid = new BolidState();
    }

    @Autowired
    private BolidSystemService bolidSystemService;

//    @Resource(name = "bolidStateInstance")
    BolidState bolid;

    private Logger logger = Logger.getLogger(BolidSystemService.class.getCanonicalName());


    @GetMapping("/sendInfo/temperature/{actualVal}")
    public ResponseEntity sendInfoTemperature(@PathVariable Double actualVal) {
        HttpStatus statusCode = null;
        try {
//            bolidSystemService.sendStateInfo(BolidState.TEMPERATRUE, actualVal);
            statusCode = HttpStatus.OK;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(null, statusCode);
    }

    @PostMapping(path = "/sendInfo/bolidState", consumes = "application/json", produces = "application/json")
    public ResponseEntity sendBolidStateInfo(@RequestBody BolidState bolidState) {
        HttpStatus statusCode = null;
        try {
            logger.info(bolidState.toString());
            bolidSystemService.sendBolidStateInfo(bolidState);
            statusCode = HttpStatus.OK;
        } catch (Exception e) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<String>(bolidState.toString(),null, statusCode);
    }

    @GetMapping("/sendInfo/run")
    public void runSendingBolidStateInfo() throws InterruptedException{
        logger.info("Spring Boot Embedded ActiveMQ Configuration Example");

            BolidState message = new BolidState(100.2, 43.21, 21.21);
//            bolidSystemService.sendBolidStateInfo(message);

        logger.info("Waiting for all ActiveMQ JMS Messages to be consumed");
    }

    @GetMapping(value = "/request/pitstop", produces = "application/json")
    public ResponseEntity requestPitStop(){
        logger.info("Requesting pit-stop");
        String responseText = "Pitstop is rejected.";

        try {
            Message message = bolidSystemService.sendWithReply(bolid);
            bolid = (BolidState) ((ActiveMQObjectMessage) message).getObject();
            logger.info("Requesting pit-stop successfull: "+bolid);
            if (bolid.getPitStopAllowed()) {
                bolid.setPitStopCount(bolid.getPitStopCount() + 1);
                responseText = "Pitstop is accepted";
            }

            return new ResponseEntity<Object>(responseText, null, HttpStatus.OK);
        } catch (JMSException e) {
            logger.severe(e.getMessage());
            logger.severe(e.getStackTrace().toString());
//            logger.throwing(this.getClass().toString(), "requestPitStop", e);
//            logger.severe(e.getMessage());
            return new ResponseEntity<String>("An error occured please check logs for more info", null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
