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
public class FrenchTalk implements OpeningTalk {

    @Override
    public void openingTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId) {
        callbackHandler.sendTextMessage(senderId, "French defence is one of those openings that does not look like much at first... 1. e3 e6? There is so much fire there...");
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Classical", "I want to learn classical French").toList()
                .addTextQuickReply("Winawer", "I want to learn winawer french").toList()
                .addTextQuickReply("Exchange Variation", "I want to learn French Exchange").toList()
                .addTextQuickReply("Advance", "I want to learn Advance French").toList()
                .addTextQuickReply("Tarrasch", "I want to learn Tarrasch French").toList()
                .addTextQuickReply("None, thanks", "None, thanks").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callbackHandler.getSendClient().sendTextMessage(senderId, "Which flavour of French Defence would you like to explore?", quickReplies);
        } catch (MessengerApiException e) {
            callbackHandler.handleSendException(e);
        } catch (MessengerIOException e) {
            callbackHandler.handleSendException(e);
        }
    }

    @Override
    public boolean openingCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId) {
        if (lowerMessage.contains("french") && lowerMessage.contains("classic")) {
            callbackHandler.sendTextMessage(recipientId, "French Classical goes: 1.e4 e6 2.d4 d5, 3.Nc3 Nf6");
            callbackHandler.sendTextMessage(recipientId, "This is a major system in the French. White can continue with the following options:");
            callbackHandler.sendTextMessage(recipientId, "4.Bg5 - White threatens 5.e5, attacking the pinned knight. Black has a number of ways to meet this threat:");
            callbackHandler.sendTextMessage(recipientId, "Burn Variation, named after Amos Burn is the most common reply at the top level: 4... dxe4 5. Nxe4 and usually there now follows: 5... Be7 6. Bxf6 Bxf6 7. Nf3 Nd7 or 7... 0-0");
            callbackHandler.sendTextMessage(recipientId, "4... Be7 5. e5 Nfd7 used to be the main line and remains important, even though the Burn Variation has overtaken it in popularity. The usual continuation is 6. Bxe7 Qxe7 7. f4 0-0 8. Nf3 c5, when White has a number of options, including 9.Bd3, 9.Qd2 and 9.dxc5. An alternative for White is the gambit 6. h4, which was devised by Adolf Albin and played by Chatard, but not taken seriously until the game Alekhine–Fahrni, Mannheim 1914. It is known today as the Albin–Chatard Attack or the Alekhine–Chatard Attack.");
            callbackHandler.sendTextMessage(recipientId, "A third choice for Black is to counterattack with the McCutcheon Variation. In this variation, the second player ignores White's threat of e4-e5 and instead plays 4... Bb4. The main line continues: 5. e5 h6 6. Bd2 Bxc3 7. bxc3 Ne4 8. Qg4.");
            callbackHandler.sendTextMessage(recipientId, "4.e5 - The Steinitz Variation (named after Wilhelm Steinitz) is 4. e5 Nfd7 5. f4 (the most common but White has other options: 5.Nce2, the Shirov–Anand Variation), White gets ready to bolster his centre with c2–c3 and f2–f4. Or 5.Nf3 (aiming for piece play) 5... c5 6. Nf3 Nc6 7. Be3 (7.Nce2 transposes to the Shirov–Anand Variation; a trap is 7.Be2 cxd4 8.Nxd4 Ndxe5! 9.fxe5 Qh4+ winning a pawn), Black has several options. He may step up pressure on d4 by playing 7...Qb6 or 7...cxd4 8.Nxd4 Qb6, or choose to complete his development, either beginning with the kingside by playing 7...cxd4 8.Nxd4 Bc5, or with the queenside by playing 7...a6 8.Qd2 b5.");
            return true;
        } else if (lowerMessage.contains("winawer")) {
            callbackHandler.sendTextMessage(recipientId, "French Winawer goes: 1.e4 e6 2.d4 d5 3.Nc3 Bb4");
            callbackHandler.sendTextMessage(recipientId, "This variation, named after Szymon Winawer and pioneered by Nimzowitsch and Botvinnik, is one of the main systems in the French, due chiefly to the latter's efforts in the 1940s, becoming the most often seen rejoinder to 3.Nc3, though in the 1980s, the Classical Variation with 3...Nf6 began a revival, and has since become more popular.\n" +
                    "3... Bb4 pins the knight on c3, forcing White to resolve the central tension. White normally clarifies the central situation for the moment with 4. e5, gaining space and hoping to show that Black's b4-bishop is misplaced. The main line then is: 4... c5 5. a3 Bxc3+ 6. bxc3");
            callbackHandler.sendTextMessage(recipientId, "It is very popular and you can read more on: https://en.wikipedia.org/wiki/French_Defence#Winawer_Variation:_3...Bb4");
        } else if (lowerMessage.contains("french") && lowerMessage.contains("exchange")) {
            callbackHandler.sendTextMessage(recipientId, "French Exchange goes: 1.e4 e6 2.d4 d5 3.exd5 exd5");
            callbackHandler.sendTextMessage(recipientId, "Many players who begin with 1.e4 find that the French Defence is the most difficult opening for them to play against due to the closed structure and unique strategies of the system. Thus, many players choose to play the exchange so that the position becomes simple and clearcut. White makes no effort to exploit the advantage of the first move, and has often chosen this line with expectation of an early draw, and indeed draws often occur if neither side breaks the symmetry.");
        } else if (lowerMessage.contains("french") && lowerMessage.contains("tarrash")) {
            callbackHandler.sendTextMessage(recipientId, "French Tarrasch goes: 1.e4 e6 2.d4 d5 3.Nd2 Nf6");
            callbackHandler.sendTextMessage(recipientId, "The Tarrasch Variation is named after Siegbert Tarrasch. This move became particularly popular during the 1970s and early 1980s when Anatoly Karpov used it to great effect. Though less aggressive than the alternate 3.Nc3, it is still used by top-level players seeking a small, safe advantage.\n" +
                    "Like 3.Nc3, 3.Nd2 protects e4, but is different in several key respects: it does not block White's c-pawn from advancing, which means he can play c3 at some point to support his d4-pawn. Hence, it avoids the Winawer Variation as 3...Bb4 is now readily answered by 4.c3. On the other hand, 3.Nd2 develops the knight to an arguably less active square than 3.Nc3, and in addition, it hems in White's dark-square bishop. Hence, white will typically have to spend an extra tempo moving the knight from d2 at some point before developing said bishop.");
        } else if (lowerMessage.contains("french") && lowerMessage.contains("advance")) {
            callbackHandler.sendTextMessage(recipientId, "French Exchange goes: 1.e4 e6 2.d4 d5 3.e5");
            callbackHandler.sendTextMessage(recipientId, "The main line of the Advance Variation continues 3... c5 4. c3 Nc6 5. Nf3 and then we have a branching point:");
            callbackHandler.sendTextMessage(recipientId, "5...Qb6, the idea is to increase the pressure on d4 and eventually undermine the White centre. The queen also attacks the b2-square, so White's dark-square bishop cannot easily defend the d4-pawn without losing the b2-pawn. White's most common replies are 6.a3 and 6.Be2.");
            callbackHandler.sendTextMessage(recipientId, "5...Bd7 was mentioned by Greco as early as 1620, and was revived and popularised by Viktor Korchnoi in the 1970s. Now a main line, the idea behind the move is that since Black usually plays ...Bd7 sooner or later, he plays it right away and waits for White to show his hand. If White plays 6.a3 in response, modern theory says that Black equalises or is better after 6...f6!");
            callbackHandler.sendTextMessage(recipientId, "5...Nh6 has recently become a popular alternative");

        }


            //exact message not matched
        return false;
    }

    @Override
    public List<String> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        keyWords.add("french");
        return keyWords;
    }


}
