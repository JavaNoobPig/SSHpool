package com.javanoob;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.javanoob.util.Availablefgl;
import com.javanoob.util.SessionFactory;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

@EnableEncryptableProperties
@SpringBootApplication
public class SpringBootHelloWorldApplication {
	
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	Availablefgl availablefgl;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootHelloWorldApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() throws Exception {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		String timer=sdf.format(date);
		sessionFactory.makeSession(timer);
		availablefgl.getAvailablefglArray();
	}
}
