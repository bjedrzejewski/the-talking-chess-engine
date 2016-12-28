package com.bjedrzejewski.talkingchess.players;

import com.bjedrzejewski.talkingchess.MessengerPlatformCallbackHandler;

import java.util.List;

/**
 * Created by bartoszjedrzejewski on 28/12/2016.
 */
public interface PlayerTalk {

    void playerTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId);

    boolean playerCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId);

    List<String> getKeyWords();
}
