package com.dalolorn.croztest.jokemanager

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Main application class for the joke manager.
 */
@SpringBootApplication
class JokeManagerApplication {

	// Huh. I never did get to use this, after all...
	private static final Logger log = LoggerFactory.getLogger(JokeManagerApplication)

	static void main(String[] args) {
		SpringApplication.run(JokeManagerApplication, args)
	}
}
