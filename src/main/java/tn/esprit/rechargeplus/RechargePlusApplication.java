package tn.esprit.rechargeplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RechargePlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(RechargePlusApplication.class, args);
    }

}
