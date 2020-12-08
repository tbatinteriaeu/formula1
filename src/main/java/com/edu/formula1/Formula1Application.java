package com.edu.formula1;

import com.edu.formula1.bolid.BolidState;
import com.edu.formula1.bolid.BolidSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

@SpringBootApplication
public class Formula1Application {

    @Autowired
    private BolidSystemService bolidSystemService;

    private static BolidSystemService bolid = new BolidSystemService();

    public static void main(String[] args) {
        SpringApplication.run(Formula1Application.class, args);
    }

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public BolidState bolidStateInstance() {
        return new BolidState();
    }

}
