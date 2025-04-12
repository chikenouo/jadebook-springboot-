package com.example.jadebook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.jadebook.mapper")
public class JadebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(JadebookApplication.class, args);
	}

}
