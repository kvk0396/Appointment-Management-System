package com.cognizant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.cognizant.service.INotificationService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class NotificationServiceApplication {

	public static void main(String[] args) {
	//	var context =
				SpringApplication.run(NotificationServiceApplication.class, args);
//
//        INotificationService iNotificationService = context.getBean(INotificationService.class);
//        iNotificationService.sendMail("prithvirajperla062@gmail.com", "subject", "body");
//
//        System.out.println("Mail sent successfully");
	}
}
