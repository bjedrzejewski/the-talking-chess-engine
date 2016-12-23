package com.bjedrzejewski.talkingchess;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.send.MessengerSendClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TheTalkingChessEngineApplication {

    private static final Logger logger = LoggerFactory.getLogger(TheTalkingChessEngineApplication.class);

    /**
     * Initializes the {@code MessengerSendClient}.
     */
    @Bean
    public MessengerSendClient messengerSendClient() {
        String pageAccessToken = System.getenv().get("pageAccessToken");
        logger.debug("Initializing MessengerSendClient - pageAccessToken: {}", pageAccessToken);
        return MessengerPlatform.newSendClientBuilder(pageAccessToken).build();
    }

	public static void main(String[] args) {
		SpringApplication.run(TheTalkingChessEngineApplication.class, args);
	}
}
