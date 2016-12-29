package com.bjedrzejewski.talkingchess.openings;

import com.bjedrzejewski.talkingchess.MessengerPlatformCallbackHandler;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.send.QuickReply;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bartoszjedrzejewski on 28/12/2016.
 */
public class SpanishTalk implements OpeningTalk {

    @Override
    public void openingTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId) {
        callbackHandler.sendTextMessage(senderId, "Ahhhh, the Spanish. It wins by force. Don't listen to those telling you otherwise!");
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Basics", "I want to learn general Spanish").toList()
                .addTextQuickReply("Main Line", "I want to learn Spanish Main Line").toList()
                .addTextQuickReply("Exchange Variation", "I want to learn Spanish Exchange").toList()
                .addTextQuickReply("Berlin Defence", "I want to learn Spanish Berlin Defence").toList()
                .addTextQuickReply("None, thanks", "None, thanks").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callbackHandler.getSendClient().sendTextMessage(senderId, "Which Spanish variation would you like to learn more about?", quickReplies);
        } catch (MessengerApiException e) {
            callbackHandler.handleSendException(e);
        } catch (MessengerIOException e) {
            callbackHandler.handleSendException(e);
        }
    }

    @Override
    public boolean openingCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId) {
        if (lowerMessage.equals("i want to learn general spanish")) {
            callbackHandler.sendTextMessage(recipientId, "Spanish/Ruy Lopez goes: 1.e4 e5 2.Nf3 Nc6 3.Bb5");
            callbackHandler.sendTextMessage(recipientId, "At the most basic level, White's third move attacks the knight which defends the e5-pawn from the attack by the f3 knight. White's apparent threat to win Black's e-pawn with 4.Bxc6 dxc6 5.Nxe5 is illusory—Black can respond with 5...Qd4, forking the knight and e4-pawn, which will win back the material with a good position. White's 3.Bb5 is still a good move; it develops a piece, prepares castling, and sets up a potential pin against Black's king. However, since White's third move carries no immediate threat, Black can respond in a wide variety of ways.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Ruy_Lopez");
            return true;
        } else if ((lowerMessage.contains("spanish") || lowerMessage.contains("lopez")) && lowerMessage.contains("main")) {
            callbackHandler.sendTextMessage(recipientId, "Main line: 4.Ba4 Nf6 5.0-0 Be7 6.Re1 b5 7.Bb3 d6 8.c3 0-0");
            callbackHandler.sendTextMessage(recipientId, "The main lines of the Closed Ruy Lopez continue 6.Re1 b5 7.Bb3 d6 8.c3 0-0. White can now play 9.d3 or 9.d4, but by far the most common move is 9.h3 which prepares d4 while preventing the awkward pin ...Bg4. This can be considered the main line of the opening as a whole and thousands of top-level games have reached this position. White aims to play d4 followed by Nbd2–f1–g3, which would firmly support e4 with the bishops on open diagonals and both knights threatening Black's kingside. Black will try to prevent this knight manoeuver by expanding on the queenside, taking action in the centre, or putting pressure on e4.");
            callbackHandler.sendTextMessage(recipientId, "Learn more about the Spanish Main Line here: https://en.wikipedia.org/wiki/Ruy_Lopez#Main_line:_4.Ba4_Nf6_5.0-0_Be7_6.Re1_b5_7.Bb3_d6_8.c3_0-0");
            return true;
        } else if ((lowerMessage.contains("spanish") || lowerMessage.contains("lopez")) && lowerMessage.contains("exchange")) {
            callbackHandler.sendTextMessage(recipientId, "Exchange Variation happens after 4...dxc6");
            callbackHandler.sendTextMessage(recipientId, "In the Exchange Variation, 4.Bxc6, (ECO C68–C69) White damages Black's pawn structure, giving him a ready-made long-term plan of playing d4 ...exd4 Qxd4, followed by exchanging all the pieces and winning the pure pawn ending. Black gains good compensation, however, in the form of the bishop pair, and the variation is not considered White's most ambitious, though former world champions Emanuel Lasker and Bobby Fischer employed it with success.");
            callbackHandler.sendTextMessage(recipientId, "Learn more here: https://en.wikipedia.org/wiki/Ruy_Lopez#Exchange_Variation:_4.Bxc6");
            return true;
        } else if (lowerMessage.contains("berlin")) {
            callbackHandler.sendTextMessage(recipientId, "The Berlin Defence, 3...Nf6, has long had a reputation for solidity and drawishness and is sometimes called \"the Berlin Wall\".[23] The Berlin Defence was played in the late 19th century and early 20th century by Emanuel Lasker and others, who typically answered 4.0-0 with 4...d6 in the style of the Steinitz Variation. This approach ultimately fell out of favour, as had the old form of the Steinitz, due to its passivity, and the entire variation became rare. Arthur Bisguier played the Berlin for decades, but always chose the variation 4.0-0 Nxe4. Then in 2000, Vladimir Kramnik used the line as a drawing weapon against Garry Kasparov in Classical World Chess Championship 2000, following which the Berlin has experienced a remarkable renaissance: even players with a dynamic style such as Alexei Shirov, Veselin Topalov, and Kasparov himself have tried it, and Magnus Carlsen and Viswanathan Anand both used it (Carlsen extensively so) during the 2013 World Chess Championship and 2014 World Chess Championship.");
            callbackHandler.sendTextMessage(recipientId, "Read more about this famous defense here: https://en.wikipedia.org/wiki/Ruy_Lopez#Berlin_Defence:_3...Nf6");
            return true;
        }

        //exact message not matched
        return false;
    }

    @Override
    public List<String> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        keyWords.add("spanish");
        keyWords.add("lopez");
        return keyWords;
    }


}
