package com.me.tracking_order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TrackingOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrackingOrderApplication.class, args);
	}

}
