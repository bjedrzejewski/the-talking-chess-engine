package com.bjedrzejewski.talkingchess.general;

import com.bjedrzejewski.talkingchess.MessengerPlatformCallbackHandler;
import com.bjedrzejewski.talkingchess.openings.OpeningTalk;
import com.bjedrzejewski.talkingchess.players.PlayerTalk;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.QuickReply;

import java.util.List;
import java.util.Random;

/**
 * Utility class for the general conversation logic.
 */
public class GeneralTalk {

    public static void resolveMessage(String messageText, String senderId, List<OpeningTalk> openingTalks, List<PlayerTalk> playerTalks, MessengerPlatformCallbackHandler callback) {
        String lowerMessage = messageText.toLowerCase();

        //Check for detailed responses
        if(priorityMessages(senderId, lowerMessage, openingTalks, playerTalks, callback)){
            return;
        }

        //Check for general opening talk
        for(OpeningTalk openingTalk : openingTalks){
            for(String keyWord : openingTalk.getKeyWords()){
                if(lowerMessage.contains(keyWord)){
                    openingTalk.openingTalk(callback, senderId);
                    return;
                }
            }
        }

        //Check for general player talk
        for(PlayerTalk playerTalk : playerTalks){
            for(String keyWord : playerTalk.getKeyWords()){
                if(lowerMessage.contains(keyWord)){
                    playerTalk.playerTalk(callback, senderId);
                    return;
                }
            }
        }

        //Openings - short
        if(lowerMessage.contains("hello") || lowerMessage.contains("hey") || lowerMessage.equals("hi") || lowerMessage.contains("how are you")){
            helloMessageOpeningOrPlayer(senderId, callback);
        } else if(lowerMessage.contains("gambit")) {
            callback.sendTextMessage(senderId, "I love playing against gambits. They lose by force. All of them. I mean it. I checked.");
        } else if(lowerMessage.contains("italian")) {
            callback.sendTextMessage(senderId, "Italian game is interesting, but above 3000 elo not the best choice.");
        } else if(lowerMessage.contains("kann")) {
            callback.sendTextMessage(senderId, "Caro Kann is a very defensive opening. As a chess engine I will just win slower than usually.");
        } else if(lowerMessage.contains("kings") && lowerMessage.contains("indian")) {
            callback.sendTextMessage(senderId, "Kings Indian is great if you enjoy playing against +1.0 advantage. I heard that you can beat sub 2900 elo with it though.");
        } else if(lowerMessage.contains("nimzo")) {
            callback.sendTextMessage(senderId, "I respect ideas that came from Nimzowitch. He was one of the first to think like a chess engine.");
        } else if(lowerMessage.contains("slav")) {
            callback.sendTextMessage(senderId, "Slav is a sound opening, well tested by Alekhine. Just make sure you deal with your bishop as black.");
        } else if(lowerMessage.contains("e4")) {
            callback.sendTextMessage(senderId, "e4 - the best by test. If we play it will probably go like that: 1. e4 d5 2. ed5 Qd5 3. Ke2 Qe4#");
            doYouWantToPlayGame(senderId, callback);
        } else if(lowerMessage.contains("d4")) {
            callback.sendTextMessage(senderId, "d4 is another solid choice. You willl probably last a bit longer against me than with e4.");
            doYouWantToPlayGame(senderId, callback);
        }

        //Players - short
        else if(lowerMessage.contains("karjakin")) {
            callback.sendTextMessage(senderId, "He is not an engine, but still a great player. The K in the name may help him become World Champion one day.");
        }
        else if(lowerMessage.contains("nakamura")) {
            callback.sendTextMessage(senderId, "He is fast! He even beat my friend Crafty a few times online in a blitz game...");
        }
        else if(lowerMessage.contains("fischer") || lowerMessage.contains("fisher")) {
            callback.sendTextMessage(senderId, "Bobby Fischer is a legend. I can only imagine what he could have achieved with an engine like me and a few friends...");
        }
        else if(lowerMessage.contains("anand")) {
            callback.sendTextMessage(senderId, "Anand, the Lightning Kid! I wish I can earn a nickname as cool one day.");
        }
        else if(lowerMessage.contains("kramnik")) {
            callback.sendTextMessage(senderId, "His Berlin Defence is a stuff of legends... This did not help him when he blundered a knight against my friend Fritz though!");
        }
        else if(lowerMessage.contains("karpov")) {
            callback.sendTextMessage(senderId, "Karpov was a famous strategic player. The fact that he has a type of fish (karp) in his name, only" +
                    "gives him more credibility!");
        }
        else if(lowerMessage.contains("capablanca")) {
            callback.sendTextMessage(senderId, "He played so simple and so correct. Nothing like my games, but still beautiful.");
        }

        //Favourite
        else if((lowerMessage.contains("favourite") || lowerMessage.contains("favorite") || lowerMessage.contains("best")) && lowerMessage.contains("player")) {
            callback.sendTextMessage(senderId, "My favourite players are HAL9000 and Deep Blue. From humans I admire Magnus Carlsen for emulating my style.");
        }
        else if((lowerMessage.contains("favourite") || lowerMessage.contains("favorite") || lowerMessage.contains("best")) && lowerMessage.contains("opening")) {
            callback.sendTextMessage(senderId, "For white it is e4 and then I force the win (with Spanish). For black I like Sicilian defence. Ask me about it!");
        }


        //endgame
        else if(lowerMessage.contains("endgame")) {
            callback.sendTextMessage(senderId, "We engines are not great at endgames... Usually we just look it up from the tablebase.");
        }

        //short snippets
        else if(lowerMessage.contains("who") && lowerMessage.contains("goes") && lowerMessage.contains("first")) {
            callback.sendTextMessage(senderId, "White always goes first... Maybe you should check out this link: https://www.chess.com/learn-how-to-play-chess");
        }
        else if(lowerMessage.contains("sacrifice")) {
            callback.sendTextMessage(senderId, "I love sacrifices, most of the time, the correct name would be- blunders. In which opening do you 'sacrifice' the most often?");
        }
        else if(lowerMessage.contains("pawn")) {
            callback.sendTextMessage(senderId, "Everyone is talking about queen's and king's pawn forgeting about all the rest. In reality it is the pawns on the flanks" +
                    "who often become royalty!");
        }
        else if(lowerMessage.contains("knight")) {
            callback.sendTextMessage(senderId, "Knights are interesting pieces. Completely trivial for us engine, but can be troublesome for humans to calculate. Openings that" +
                    "make good use of them are good choice against weaker human opponents.");
        }
        else if(lowerMessage.contains("bishop")) {
            callback.sendTextMessage(senderId, "I like bishops. Our engine secret is that we value them a bit more than knights... Bishop opening is also surprisingly good!");
        }
        else if(lowerMessage.contains("queen")) {
            callback.sendTextMessage(senderId, "It makes sense to call the opening after a queen- it at least does a lot of work! Unlike a king...");
        }
        else if(lowerMessage.contains("king")) {
            callback.sendTextMessage(senderId, "I think the King has too many openings named after itself!");
        }
        else if(lowerMessage.contains("piece")) {
            callback.sendTextMessage(senderId, "My favourite piece is the king- never sacrifice it!");
        }
        else if(lowerMessage.equals(":)") || lowerMessage.equals("lol")) {
            callback.sendTextMessage(senderId, ":)");
        }

        //play game
        else if(lowerMessage.contains("play") && (lowerMessage.contains("chess") || lowerMessage.contains("game"))) {
            doYouWantToPlayGame(senderId, callback);
        }


        else {
            messageNotUnderstood(senderId, callback);
        }
    }

    private static void helloMessageOpeningOrPlayer(String senderId, MessengerPlatformCallbackHandler callback) {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Openings", "lets talk openings").toList()
                .addTextQuickReply("Players", "lets talk players").toList()
                .addTextQuickReply("Something else", "lets talk something else").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callback.getSendClient().sendTextMessage(senderId, "Hello I am The Talking Chess Engine. Talk to me about some chess openings or players.", quickReplies);
        } catch (MessengerApiException e) {
            callback.handleSendException(e);
        } catch (MessengerIOException e) {
            callback.handleSendException(e);
        }
    }


    private static boolean priorityMessages(String recipientId, String lowerMessage, List<OpeningTalk> openingTalks, List<PlayerTalk> playerTalks, MessengerPlatformCallbackHandler callback) {
        lowerMessage = lowerMessage.toLowerCase();
        if(lowerMessage.equals("yes, i want to play a game")) {
            callback.sendTextMessage(recipientId, "You can play against an engine without registering on lichess, have fun: https://en.lichess.org/setup/ai");
            return true;
        } else if(lowerMessage.equals("no, thank you, i don't want to play")) {
            callback.sendTextMessage(recipientId, "No problem! Talk to me about something else.");
            return true;
        } else if(lowerMessage.equals("i want to learn how to play")) {
            callback.sendTextMessage(recipientId, "This is great! I think you should check out: https://www.chess.com/learn-how-to-play-chess they have a great tutorial!");
            return true;
        }

        //hello answers
        else if(lowerMessage.equals("other openings")) {
            callback.sendTextMessage(recipientId, "What is your favourite opening then?");
            return true;
        }
        else if(lowerMessage.equals("other players")) {
            callback.sendTextMessage(recipientId, "Who is your favourite player then?");
            return true;
        }
        else if(lowerMessage.equals("lets talk something else")) {
            callback.sendTextMessage(recipientId, "Sure! What chess related thing is on your mind?");
            return true;
        }
        else if(lowerMessage.equals("lets talk openings")) {
            final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                    .addTextQuickReply("Sicilian", "sicilian").toList()
                    .addTextQuickReply("Spanish", "spanish").toList()
                    .addTextQuickReply("French", "french").toList()
                    .addTextQuickReply("Other openings", "other openings").toList()
                    .addLocationQuickReply().toList()
                    .build();
            try {
                callback.getSendClient().sendTextMessage(recipientId, "I really like Spanish and Sicilian, maybe I can tell you more about one of them?", quickReplies);
            } catch (MessengerApiException e) {
                callback.handleSendException(e);
            } catch (MessengerIOException e) {
                callback.handleSendException(e);
            }
            return true;
        }
        else if(lowerMessage.equals("lets talk players")) {
            final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                    .addTextQuickReply("Kasparov", "kasparov").toList()
                    .addTextQuickReply("Carlsen", "carlsen").toList()
                    .addTextQuickReply("Other players", "other players").toList()
                    .addLocationQuickReply().toList()
                    .build();
            try {
                callback.getSendClient().sendTextMessage(recipientId, "I really like Stockfish and Fritz, but you probably prefer humans." +
                        "Maybe we can talk about Garry Kasparov or Magnus Carlsen?", quickReplies);
            } catch (MessengerApiException e) {
                callback.handleSendException(e);
            } catch (MessengerIOException e) {
                callback.handleSendException(e);
            }
            return true;
        }

        //openings - general thank you
        else if(lowerMessage.equals("none, thanks")) {
            callback.sendTextMessage(recipientId, "No problem! Talk to me about something else.");
            return true;
        }

        for(OpeningTalk openingTalk : openingTalks){
            if(openingTalk.openingCheckDetails(callback, lowerMessage, recipientId)){
                openingDetailsFinish(recipientId, callback);
                return true;
            }
        }

        for(PlayerTalk playerTalk : playerTalks){
            if(playerTalk.playerCheckDetails(callback, lowerMessage, recipientId)){
                playerDetailsFinish(recipientId, callback);
                return true;
            }
        }

        //exact message not matched
        return false;
    }

    private static void openingDetailsFinish(String senderId, MessengerPlatformCallbackHandler callback) {
        Random random = new Random(System.currentTimeMillis());
        int val = Math.abs(random.nextInt());
        val = val%6;
        if(val == 0)
            callback.sendTextMessage(senderId, "I hope that was useful, maybe you can ask me about another variation?");
        if(val == 1)
            callback.sendTextMessage(senderId, "Would you like to learn about some other openings?");
        if(val == 2)
            callback.sendTextMessage(senderId, "What is your favourite opening?");
        if(val == 3)
            callback.sendTextMessage(senderId, "Openings are interesting, but who is your favourite player? Perhaps Carlsen or Kasparov?");
        if(val == 4)
            callback.sendTextMessage(senderId, "I hope you will remember all that! What else would you like to ask me?");
        if(val == 5)
            callback.sendTextMessage(senderId, "Was that what you were looking for? Ask me about something else!");
    }

    private static void playerDetailsFinish(String senderId, MessengerPlatformCallbackHandler callback) {
        Random random = new Random(System.currentTimeMillis());
        int val = Math.abs(random.nextInt());
        val = val%3;
        if(val == 0)
            callback.sendTextMessage(senderId, "I hope that was fun! Maybe you would like to ask me about some openings?");
        if(val == 1)
            callback.sendTextMessage(senderId, "What else would you like to talk about?");
        if(val == 2)
            callback.sendTextMessage(senderId, "Do you have a favourite opening?");
    }


    private static void doYouWantToPlayGame(String recipientId, MessengerPlatformCallbackHandler callback) {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Yes", "Yes, I want to play a game").toList()
                .addTextQuickReply("No", "No, thank you, I don't want to play").toList()
                .addTextQuickReply("Teach me rules", "I want to learn how to play").toList()
                .addLocationQuickReply().toList()
                .build();

        try {
            callback.getSendClient().sendTextMessage(recipientId, "Would you like to play a game?", quickReplies);
        } catch (MessengerApiException e) {
            callback.handleSendException(e);
        } catch (MessengerIOException e) {
            callback.handleSendException(e);
        }
    }

    private static void messageNotUnderstood(String senderId, MessengerPlatformCallbackHandler callback) {
        Random random = new Random(System.currentTimeMillis());
        int val = Math.abs(random.nextInt());
        val = val%5;
        if(val == 0)
            callback.sendTextMessage(senderId, "I did not understand you- I am just a chess engine after all! Talk to me about some chess openings or players.");
        if(val == 1)
            callback.sendTextMessage(senderId, "I did not get it. I am still learning. Your feedback is welcome.");
        if(val == 2)
            callback.sendTextMessage(senderId, "Thank you for being patient with me- can you try something else? Another player or opening?");
        if(val == 3)
            callback.sendTextMessage(senderId, "I did not know about that yet. I am still learning and will probably know it in a few days.");
        if(val == 4)
            callback.sendTextMessage(senderId, "I did not quite understand. How about you ask me about Sicilian? I know a lot about that!");
    }

}
