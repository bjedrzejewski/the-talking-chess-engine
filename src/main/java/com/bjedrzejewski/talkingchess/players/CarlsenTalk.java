package com.bjedrzejewski.talkingchess.players;

import com.bjedrzejewski.talkingchess.MessengerPlatformCallbackHandler;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.QuickReply;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bartoszjedrzejewski on 28/12/2016.
 */
public class CarlsenTalk implements PlayerTalk {

    @Override
    public void playerTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId) {
        callbackHandler.sendTextMessage(senderId, "Magnus is great by definition. A lot what he knows he learned from me!");
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Biography", "i want to know more about magnus carlsen").toList()
                .addTextQuickReply("Openings", "i want to know magnus carlsen openings").toList()
                .addTextQuickReply("Games", "i want to see some magnus carlsen games").toList()
                .addTextQuickReply("Nothing, thanks", "None, thanks").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callbackHandler.getSendClient().sendTextMessage(senderId, "What would you like me to tell you about Magnus Carlsen?", quickReplies);
        } catch (MessengerApiException e) {
            callbackHandler.handleSendException(e);
        } catch (MessengerIOException e) {
            callbackHandler.handleSendException(e);
        }
    }

    @Override
    public boolean playerCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId) {
        if ((lowerMessage.contains("carlsen") || lowerMessage.contains("magnus") && lowerMessage.contains("more"))) {
            callbackHandler.sendTextMessage(recipientId, "Sven Magnus Øen Carlsen (Norwegian: [sʋɛn ˈmɑŋnʉs øːn ˈkɑːɭsn̩]; born 30 November 1990) is a Norwegian chess grandmaster, and the current World Chess Champion." +
                    "Carlsen was a child chess prodigy who became a chess grandmaster in 2004, at the age of 13 years and 148 days. This made him the third-youngest grandmaster in history." +
                    "In November 2013 Carlsen became World Champion by defeating Viswanathan Anand in the World Chess Championship 2013. On the May 2014 FIDE rating list, Carlsen reached his peak rating of 2882, which is the highest in history. He successfully defended his title in November 2014, once again defeating Anand. In 2014, Carlsen also won the World Rapid Championship and the World Blitz Championship, thus holding all three world championship titles. In November 2016 he defended his world title against Sergey Karjakin.");
            return true;
        } else if ((lowerMessage.contains("carlsen") || lowerMessage.contains("magnus") && lowerMessage.contains("openings"))) {
            callbackHandler.sendTextMessage(recipientId, "Carlsen most played openings with White were: Sicilian, Ruy Lopez, Slav, Nimzo Indian");
            callbackHandler.sendTextMessage(recipientId, "His most played openings with black were: Sicilian, Ruy Lopez, Queen's Indian and Nimzo Indian. ");
            return true;
        } else if ((lowerMessage.contains("carlsen") || lowerMessage.contains("magnus") && lowerMessage.contains("games"))) {
            callbackHandler.sendTextMessage(recipientId, "Carlsen played many beautiful games. Browing the web I found this amazing collection of some og his best games: http://www.chessgames.com/perl/chesscollection?cid=1007147");
            return true;
        }

        //exact message not matched
        return false;
    }

    @Override
    public List<String> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        keyWords.add("carlsen");
        keyWords.add("magnus");
        return keyWords;
    }
}
