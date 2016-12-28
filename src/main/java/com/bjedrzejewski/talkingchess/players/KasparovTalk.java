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
public class KasparovTalk implements PlayerTalk {

    @Override
    public void playerTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId) {
        callbackHandler.sendTextMessage(senderId, "Garry Kasparov is one of the greatest chess players of all time! My friend Deep Blue is not a fan though...");
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Biography", "i want to know more about garry kasparov").toList()
                .addTextQuickReply("Openings", "i want to know garry kasparov openings").toList()
                .addTextQuickReply("Games", "i want to see some garry kasparov games").toList()
                .addTextQuickReply("Nothing, thanks", "None, thanks").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callbackHandler.getSendClient().sendTextMessage(senderId, "What would you like me to tell you about Garry?", quickReplies);
        } catch (MessengerApiException e) {
            callbackHandler.handleSendException(e);
        } catch (MessengerIOException e) {
            callbackHandler.handleSendException(e);
        }
    }

    @Override
    public boolean playerCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId) {
        if (lowerMessage.contains("kasparov") && lowerMessage.contains("more")) {
            callbackHandler.sendTextMessage(recipientId, "Garry Kimovich Kasparov (Russian: Га́рри Ки́мович Каспа́ров, Russian pronunciation: [ˈɡarʲɪ ˈkʲiməvʲɪtɕ kɐˈsparəf]; born Garik Kimovich Weinstein, 13 April 1963) is a Russian chess grandmaster, former World Chess Champion, writer, and political activist, whom many consider to be the greatest chess player of all time. From 1986 until his retirement in 2005, Kasparov was ranked world No. 1 for 225 out of 228 months. His peak rating of 2851, achieved in 1999, was the highest recorded until being surpassed by Magnus Carlsen in 2013. Kasparov also holds records for consecutive professional tournament victories (15) and Chess Oscars (11).");
            return true;
        } else if ((lowerMessage.contains("kasparov") && lowerMessage.contains("openings"))) {
            callbackHandler.sendTextMessage(recipientId, "Kasparov most played openings with White were: Sicilian, Ruy Lopez, Nimzo Indian, Queen's Gambit Declined, Queen's Indian and Slav.");
            callbackHandler.sendTextMessage(recipientId, "His most played openings with black were: Sicilian (especially Najdorf), King's Indian and Grunfeld. ");
            return true;
        } else if ((lowerMessage.contains("kasparov") && lowerMessage.contains("games"))) {
            callbackHandler.sendTextMessage(recipientId, "Kasparov had many amazing duels. I want to show you a few that I consider the most important:");
            callbackHandler.sendTextMessage(recipientId, "Kasparov Immortal game can be seen here: http://www.chessgames.com/perl/chessgame?gid=1011478 ");
            callbackHandler.sendTextMessage(recipientId, "Kasparov Deep Blue Matches: http://www.chessgames.com/perl/chesscollection?cid=1014770 (it is important for us, engines");
            callbackHandler.sendTextMessage(recipientId, "Some more great Kasparov games compiled: http://www.chessgames.com/perl/chesscollection?cid=1006670");
            return true;
        }

        //exact message not matched
        return false;
    }

    @Override
    public List<String> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        keyWords.add("kasparov");
        return keyWords;
    }
}
