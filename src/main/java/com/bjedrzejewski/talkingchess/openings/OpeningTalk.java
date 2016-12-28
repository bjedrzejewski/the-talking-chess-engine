package com.bjedrzejewski.talkingchess.openings;

import com.bjedrzejewski.talkingchess.MessengerPlatformCallbackHandler;

import java.util.List;

/**
 * Created by bartoszjedrzejewski on 28/12/2016.
 */
public interface OpeningTalk {

    void openingTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId);

    boolean openingCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId);

    List<String> getKeyWords();
}
