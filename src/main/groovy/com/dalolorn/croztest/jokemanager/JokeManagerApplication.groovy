package com.dalolorn.croztest.jokemanager

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class JokeManagerApplication {

	private static final Logger log = LoggerFactory.getLogger(JokeManagerApplication)

	static void main(String[] args) {
		SpringApplication.run(JokeManagerApplication, args)
	}
}
