package com.supportapp;

import com.supportapp.constant.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.io.File;

@SpringBootApplication
@EnableCaching
public class SupportappApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportappApplication.class, args);
		new File(FileConstant.USER_FOLDER).mkdirs();
	}

}
