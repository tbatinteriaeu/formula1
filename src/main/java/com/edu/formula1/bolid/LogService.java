package com.edu.formula1.bolid;

import com.edu.formula1.ActiveMQConfig;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.jms.Session;
import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

@Service
public class LogService {

    private Handler fileHandler;

    public LogService (){
    }

    private static Logger logger = Logger.getLogger(LogService.class.getCanonicalName());

    @JmsListener(destination = ActiveMQConfig.BOLID_INFO_QUEUE)
    public void receiveMessage(@Payload com.edu.formula1.bolid.BolidState bolidState,
                               @Headers MessageHeaders headers,
                               Message message, Session session) {

        logger.addHandler(getFileHandler());
        logger.info(new Date(headers.getTimestamp()) + "|" + bolidState.toString());
    }

    Handler getFileHandler(){
        if (fileHandler instanceof FileHandler) {
            return fileHandler;
        }

        try{
            fileHandler = new FileHandler(System.getProperty("user.dir")+"/logs/"+this.getClass().getSimpleName());
            fileHandler.setFormatter(new MySimpleFormatter());
        } catch (IOException exception) {
            logger.log(Level.SEVERE, exception.getMessage(), exception);
        }

        return fileHandler;
    }

    private class MySimpleFormatter extends Formatter {
        @Override
        public String format(LogRecord logRecord) {
            StringBuilder sb = new StringBuilder();
            sb.append(logRecord.getLevel()).append('|');
            sb.append(logRecord.getMessage()).append('\n');
            return sb.toString();
        }
    }
}
