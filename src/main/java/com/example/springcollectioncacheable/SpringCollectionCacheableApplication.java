package com.example.springcollectioncacheable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringCollectionCacheableApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCollectionCacheableApplication.class, args);
	}

}
