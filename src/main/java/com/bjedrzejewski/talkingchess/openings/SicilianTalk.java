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
public class SicilianTalk implements OpeningTalk {

    @Override
    public void openingTalk(MessengerPlatformCallbackHandler callbackHandler, String senderId) {
        callbackHandler.sendTextMessage(senderId, "Sicialian defense, great opening. If you play it against me as black, you may even get me to +0.3.");
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("General Ideas", "I want to learn general Sicilian").toList()
                .addTextQuickReply("Najdorf", "I want to learn Sicilian Najdorf").toList()
                .addTextQuickReply("Dragon", "I want to learn Sicilian Dragon").toList()
                .addTextQuickReply("Classical", "I want to learn Sicilian Classical").toList()
                .addTextQuickReply("Scheveningen", "I want to learn Sicilian Scheveningen").toList()
                .addTextQuickReply("Sveshnikov", "I want to learn Sicilian Sveshnikov").toList()
                .addTextQuickReply("Taimanov", "I want to learn Sicilian Taimanov").toList()
                .addTextQuickReply("None, thanks", "None, thanks").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            callbackHandler.getSendClient().sendTextMessage(senderId, "Which Sicilian variation would you like to learn more about?", quickReplies);
        } catch (MessengerApiException e) {
            callbackHandler.handleSendException(e);
        } catch (MessengerIOException e) {
            callbackHandler.handleSendException(e);
        }
    }

    @Override
    public boolean openingCheckDetails(MessengerPlatformCallbackHandler callbackHandler, String lowerMessage, String recipientId) {
        if (lowerMessage.equals("i want to learn general sicilian")) {
            callbackHandler.sendTextMessage(recipientId, "Sicilian goes: 1.e4 c5");
            callbackHandler.sendTextMessage(recipientId, "By advancing the c-pawn two squares, Black asserts control over the d4-square and begins the fight for the centre of the board. The move resembles 1…e5, the next most common response to 1.e4, in that respect. Unlike 1...e5, however, 1...c5 breaks the symmetry of the position, which strongly influences both players' future actions. White, having pushed a kingside pawn, tends to hold the initiative on that side of the board. Moreover, 1...c5 does little for Black's development, unlike moves such as 1...e5, 1...g6, or 1...Nc6, which either develop a minor piece or prepare to do so. In many variations of the Sicilian, Black makes a number of further pawn moves in the opening (for example, ...d6, ...e6, ...a6, and ...b5). Consequently, White often obtains a substantial lead in development and dangerous attacking chances.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("sicilian") && lowerMessage.contains("najdorf")) {
            callbackHandler.sendTextMessage(recipientId, "Najdorf goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6");
            callbackHandler.sendTextMessage(recipientId, "The Najdorf Variation is Black's most popular system in the Sicilian Defence. Najdorf's intention with 5...a6 was to prepare ...e5 on the next move to gain space in the centre; the immediate 5...e5?! however is met by 6.Bb5+!, when Black must either play 6...Bd7 or 6...Nbd7. The former allows White to exchange off Black's light-squared bishop, after which the d5-square becomes very weak; but the latter allows 7.Nf5, when Black can only save the d-pawn by playing the awkward 7...a6 8.Bxd7+ Qxd7. In both cases, White's game is preferable.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("sicilian") && lowerMessage.contains("dragon")) {
            callbackHandler.sendTextMessage(recipientId, "Dragon goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6");
            callbackHandler.sendTextMessage(recipientId, "In the Dragon Variation, Black fianchettoes a Bishop on the h8–a1 diagonal. " +
                    "It was named by Fyodor Dus-Chotimirsky in 1901, who noticed a resemblance between Black's kingside " +
                    "pawn structure (pawns on d6, e7, f7, g6 and h7) and the stars of the Draco constellation. ");
            callbackHandler.sendTextMessage(recipientId, "White's most dangerous try against the Dragon is the Yugoslav Attack, characterised by 6.Be3 Bg7 7.f3 0-0 8.Qd2 Nc6, " +
                    "when both 9.0-0-0 and 9.Bc4 may be played. ");
            callbackHandler.sendTextMessage(recipientId, "This variation leads to extremely sharp play and is ferociously " +
                    "complicated, since the players castle on opposite wings and the game becomes a race between White's kingside " +
                    "attack and Black's queenside counterattack. ");
            callbackHandler.sendTextMessage(recipientId, "White's main alternatives to the Yugoslav Attack are 6.Be2, " +
                    "the Classical Variation, and 6.f4, the Levenfish Attack.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("sicilian") && lowerMessage.contains("classical")) {
            callbackHandler.sendTextMessage(recipientId, "Classical goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6");
            callbackHandler.sendTextMessage(recipientId, "This variation can arise from two different move orders: " +
                    "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6, or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 d6. " +
                    "Black simply brings their knight to its most natural square, and defers, for the moment, the development of their king's bishop.");
            callbackHandler.sendTextMessage(recipientId, "White's most common reply is 6.Bg5, the Richter–Rauzer Attack (ECO codes B60 et seq). " +
                    "The move 6.Bg5 was Kurt Richter's invention, threatening to double Black's pawns after Bxf6 and " +
                    "forestalling the Dragon by rendering 6...g6 unplayable.");
            callbackHandler.sendTextMessage(recipientId, "After 6...e6, Vsevolod Rauzer introduced " +
                    "the modern plan of Qd2 and 0-0-0 in the 1930s. White's pressure on the d6-pawn often compels Black " +
                    "to respond to Bxf6 with ...gxf6, rather than recapturing with a piece (e.g. the queen on d8) that " +
                    "also has to defend the d-pawn.");
            callbackHandler.sendTextMessage(recipientId, "This weakens their kingside pawn structure, in return for which Black " +
                    "gains the two bishops, plus a central pawn majority, though these assets are difficult to exploit.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("scheveningen")) {
            callbackHandler.sendTextMessage(recipientId, "Scheveningen goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6");
            callbackHandler.sendTextMessage(recipientId, "In the Scheveningen Variation, Black is content with a \"small centre\" " +
                    "(pawns on d6 and e6, rather than e5) and prepares to castle kingside. In view of this, Paul Keres " +
                    "introduced 6.g4, the Keres Attack, in 1943.");
            callbackHandler.sendTextMessage(recipientId, "White intends to drive away the black knight with g5. " +
                    "If Black prevents this with 6...h6, which is the most common answer, White has gained kingside space " +
                    "and discouraged Black from castling in that area, and may later play Bg2.");
            callbackHandler.sendTextMessage(recipientId, "If the complications after " +
                    "6.g4 are not to White's taste, a major alternative is 6.Be2, a typical line being 6...a6 (this position " +
                    "can be reached from the Najdorf via 5...a6 6.Be2 e6) 7.0-0 Be7 8.f4 0-0. 6.Be3 and 6.f4 are also common.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("sveshnikov")) {
            callbackHandler.sendTextMessage(recipientId, "Sveshnikov goes: 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e5");
            callbackHandler.sendTextMessage(recipientId, "The Sveshnikov Variation was pioneered by Evgeny Sveshnikov and Gennadi Timoshchenko " +
                    "in the 1970s. Before their efforts, the variation was called the Lasker–Pelikan Variation.");
            callbackHandler.sendTextMessage(recipientId, "Emanuel Lasker " +
                    "played it once in his world championship match against Carl Schlechter, and Jorge Pelikan played it a few " +
                    "times in the 1950s, but Sveshnikov's treatment of the variation was the key to its revitalization.");
            callbackHandler.sendTextMessage(recipientId, "The move 5...e5 seems anti-positional as it leaves black with a backwards d-pawn and a weakness on d5. " +
                    "Also, black would have to accept the doubled f-pawns in the main line of the opening. ");
            callbackHandler.sendTextMessage(recipientId, "The opening was " +
                    "popularised when Sveshnikov saw its dynamic potential for Black in the 1970s and 80s. Today, it is " +
                    "extremely popular among grandmasters and amateurs alike. ");
            callbackHandler.sendTextMessage(recipientId, "Though some lines still give Black trouble, " +
                    "it has been established as a first-rate defence. The main line after 5...e5 runs as follows:");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("taimanov")) {
            callbackHandler.sendTextMessage(recipientId, "Taimanov goes: 1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6");
            callbackHandler.sendTextMessage(recipientId, "Named after Mark Taimanov, the Taimanov Variation can be reached through " +
                    "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 e6. ");
            callbackHandler.sendTextMessage(recipientId, "Black develops " +
                    "the knight to a natural square and keeps his options open regarding the placement of his other " +
                    "pieces. One of the ideas of this system is to develop the king's bishop to b4 or c5. ");
            callbackHandler.sendTextMessage(recipientId, "White can " +
                    "prevent this by 5.Nb5 d6, when 6.c4 leads to a version of the Maróczy Bind favoured by Karpov. " +
                    "The resulting position after 6.c4 Nf6 7.N1c3 a6 8.Na3 b6 is a type of Hedgehog.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }

        //exact message not matched
        return false;
    }

    @Override
    public List<String> getKeyWords() {
        List<String> keyWords = new ArrayList<>();
        keyWords.add("sicilian");
        return keyWords;
    }


}
