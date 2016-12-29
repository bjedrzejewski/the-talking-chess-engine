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
            callbackHandler.sendTextMessage(recipientId, "The Najdorf Variation of the Sicilian Defence is one of the most respected and deeply studied of all chess openings. Modern Chess Openings calls it the \"Cadillac\" or \"Rolls Royce\" of chess openings. The opening is named after the Polish-Argentine grandmaster Miguel Najdorf. Many players have lived by the Najdorf (notably Bobby Fischer and Garry Kasparov, although Kasparov would often transpose into a Scheveningen).");
            callbackHandler.sendTextMessage(recipientId, "This is just the beginning. Najdorf is highly theoretical. Learn more here: https://en.wikipedia.org/wiki/Sicilian_Defence,_Najdorf_Variation");
            return true;
        } else if (lowerMessage.contains("sicilian") && lowerMessage.contains("dragon")) {
            callbackHandler.sendTextMessage(recipientId, "Dragon goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6");
            callbackHandler.sendTextMessage(recipientId, "In the Dragon, Black fianchettoes their bishop on the h8–a1 diagonal, building a home for the king on g8 while aiming the bishop at the center and queenside. White frequently seeks to meet Black's setup with Be3, Qd2 and Bh6, exchanging off the dragon bishop, followed by launching a kingside pawn storm with h4–h5 and g4. To involve the a1 rook in the attack, White usually castles queenside, which however places the White king on the semi-open c-file. The result is often some blood-curdling chess where both sides attack the other's king with all available resources: either Black's king bites the dust, or his counterplay arrives just in time that White gets mated instead. The line is one of the sharpest and most aggressive variations of the Sicilian Defence, making it one of the sharpest of all chess openings.");
            callbackHandler.sendTextMessage(recipientId, "Dragon is very sharp and theoretical. Learn more here: https://en.wikipedia.org/wiki/Sicilian_Defence,_Dragon_Variation");
            return true;
        } else if (lowerMessage.contains("sicilian") && lowerMessage.contains("classical")) {
            callbackHandler.sendTextMessage(recipientId, "This variation can arise from two different move orders: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6, or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 d6. Black simply brings their knight to its most natural square, and defers, for the moment, the development of their king's bishop." +
                    "White's most common reply is 6.Bg5, the Richter–Rauzer Attack (ECO codes B60 et seq). The move 6.Bg5 was Kurt Richter's invention, threatening to double Black's pawns after Bxf6 and forestalling the Dragon by rendering 6...g6 unplayable. After 6...e6, Vsevolod Rauzer introduced the modern plan of Qd2 and 0-0-0 in the 1930s. White's pressure on the d6-pawn often compels Black to respond to Bxf6 with ...gxf6, rather than recapturing with a piece (e.g. the queen on d8) that also has to defend the d-pawn. This weakens their kingside pawn structure, in return for which Black gains the two bishops, plus a central pawn majority, though these assets are difficult to exploit.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("scheveningen")) {
            callbackHandler.sendTextMessage(recipientId, "Scheveningen goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6");
            callbackHandler.sendTextMessage(recipientId, "The seemingly modest d6–e6 pawn centre affords Black a solid defensive barrier, control of the critical d5 and e5 squares, and retains flexibility to break in the centre with either ...e5 or ...d5. Black can proceed with rapid development and the opening provides sound counterchances and considerable scope for creativity.\n" +
                    "The line has been championed by Garry Kasparov, among many other distinguished grandmasters.");
            callbackHandler.sendTextMessage(recipientId, "You can learn more about this broad opening here: https://en.wikipedia.org/wiki/Sicilian_Defence,_Scheveningen_Variation");
            return true;
        } else if (lowerMessage.contains("sveshnikov")) {
            callbackHandler.sendTextMessage(recipientId, "Sveshnikov goes: 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e5");
            callbackHandler.sendTextMessage(recipientId, "The Sveshnikov Variation was pioneered by Evgeny Sveshnikov and Gennadi Timoshchenko in the 1970s. Before their efforts, the variation was called the Lasker–Pelikan Variation. Emanuel Lasker played it once in his world championship match against Carl Schlechter, and Jorge Pelikan played it a few times in the 1950s, but Sveshnikov's treatment of the variation was the key to its revitalization. The move 5...e5 seems anti-positional as it leaves black with a backwards d-pawn and a weakness on d5. Also, black would have to accept the doubled f-pawns in the main line of the opening. The opening was popularised when Sveshnikov saw its dynamic potential for Black in the 1970s and 80s. Today, it is extremely popular among grandmasters and amateurs alike. Though some lines still give Black trouble, it has been established as a first-rate defence.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if (lowerMessage.contains("taimanov")) {
            callbackHandler.sendTextMessage(recipientId, "Named after Mark Taimanov, the Taimanov Variation can be reached through 1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 e6. Black develops the knight to a natural square and keeps his options open regarding the placement of his other pieces. One of the ideas of this system is to develop the king's bishop to b4 or c5. White can prevent this by 5.Nb5 d6, when 6.c4 leads to a version of the Maróczy Bind favoured by Karpov. The resulting position after 6.c4 Nf6 7.N1c3 a6 8.Na3 b6 is a type of Hedgehog.");
            callbackHandler.sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        } else if ((lowerMessage.contains("against") || lowerMessage.contains("beat")) && lowerMessage.contains("sicilian")) {
            callbackHandler.sendTextMessage(recipientId, "To beat Sicilian I recommend trying either Yugoslav or English attack. They are very similar and very aggressive.");
            callbackHandler.sendTextMessage(recipientId, "To learn about Yugoslav check: https://en.wikipedia.org/wiki/Sicilian_Defence,_Dragon_Variation,_Yugoslav_Attack,_9.Bc4");
            callbackHandler.sendTextMessage(recipientId, "To learn about the English attack check: https://en.wikipedia.org/wiki/Sicilian_Defence,_Najdorf_Variation#English_Attack:_6.Be3");

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
