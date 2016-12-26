package com.bjedrzejewski.talkingchess;

import com.github.messenger4j.MessengerPlatform;
import com.github.messenger4j.exceptions.MessengerApiException;
import com.github.messenger4j.exceptions.MessengerIOException;
import com.github.messenger4j.exceptions.MessengerVerificationException;
import com.github.messenger4j.receive.MessengerReceiveClient;
import com.github.messenger4j.receive.events.AccountLinkingEvent.AccountLinkingStatus;
import com.github.messenger4j.receive.events.AttachmentMessageEvent.Attachment;
import com.github.messenger4j.receive.events.AttachmentMessageEvent.AttachmentType;
import com.github.messenger4j.receive.events.AttachmentMessageEvent.Payload;
import com.github.messenger4j.receive.handlers.*;
import com.github.messenger4j.send.*;
import com.github.messenger4j.send.buttons.Button;
import com.github.messenger4j.send.templates.ButtonTemplate;
import com.github.messenger4j.send.templates.GenericTemplate;
import com.github.messenger4j.send.templates.ReceiptTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.github.messenger4j.MessengerPlatform.*;

/**
 * This is the main class for inbound and outbound communication with the Facebook Messenger Platform.
 * The callback handler is responsible for the webhook verification and processing of the inbound messages and events.
 * It showcases the features of the Messenger Platform.
 *
 * @author Max Grabenhorst
 */
@RestController
@RequestMapping("/callback")
public class MessengerPlatformCallbackHandler {

    private static final String RESOURCE_URL =
            "https://raw.githubusercontent.com/fbsamples/messenger-platform-samples/master/node/public";

    private static final Logger logger = LoggerFactory.getLogger(MessengerPlatformCallbackHandler.class);

    private final MessengerReceiveClient receiveClient;
    private final MessengerSendClient sendClient;

    /**
     * Constructs the {@code MessengerPlatformCallbackHandler} and initializes the {@code MessengerReceiveClient}.
     *
     * @param appSecret   the {@code Application Secret}
     * @param verifyToken the {@code Verification Token} that has been provided by you during the setup of the {@code
     *                    Webhook}
     * @param sendClient  the initialized {@code MessengerSendClient}
     */
    @Autowired
    public MessengerPlatformCallbackHandler(@Value("${messenger4j.appSecret}") final String appSecret,
                                            @Value("${messenger4j.verifyToken}") final String verifyToken,
                                            final MessengerSendClient sendClient) {
        logger.debug("Initializing MessengerReceiveClient - appSecret: {} | verifyToken: {}", appSecret, verifyToken);
        this.receiveClient = MessengerPlatform.newReceiveClientBuilder(appSecret, verifyToken)
                .onTextMessageEvent(newTextMessageEventHandler())
                .onAttachmentMessageEvent(newAttachmentMessageEventHandler())
                .onQuickReplyMessageEvent(newQuickReplyMessageEventHandler())
                .onPostbackEvent(newPostbackEventHandler())
                .onAccountLinkingEvent(newAccountLinkingEventHandler())
                .onOptInEvent(newOptInEventHandler())
                .onEchoMessageEvent(newEchoMessageEventHandler())
                .onMessageDeliveredEvent(newMessageDeliveredEventHandler())
                .onMessageReadEvent(newMessageReadEventHandler())
                .fallbackEventHandler(newFallbackEventHandler())
                .build();
        this.sendClient = sendClient;
    }

    /**
     * Webhook verification endpoint.
     *
     * The passed verification token (as query parameter) must match the configured verification token.
     * In case this is true, the passed challenge string must be returned by this endpoint.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> verifyWebhook(@RequestParam(value = MODE_REQUEST_PARAM_NAME) final Optional<String> mode,
                                                @RequestParam(value = VERIFY_TOKEN_REQUEST_PARAM_NAME) final Optional<String> verifyToken,
                                                @RequestParam(value = CHALLENGE_REQUEST_PARAM_NAME) final Optional<String> challenge) {
        if(!mode.isPresent() || !verifyToken.isPresent() || !challenge.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        logger.debug("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}", mode,
                verifyToken, challenge);
        try {
            return ResponseEntity.ok(this.receiveClient.verifyWebhook(mode.get(), verifyToken.get(), challenge.get()));
        } catch (MessengerVerificationException e) {
            logger.warn("Webhook verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    /**
     * Callback endpoint responsible for processing the inbound messages and events.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final Optional<String> payload,
                                               @RequestHeader(value = SIGNATURE_HEADER_NAME) final Optional<String> signature) {
        if(!payload.isPresent() || !signature.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            this.receiveClient.processCallbackPayload(payload.get(), signature.get());
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (MessengerVerificationException e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).build();
        }
    }

    private TextMessageEventHandler newTextMessageEventHandler() {
        return event -> {
            logger.debug("Received TextMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String messageText = event.getText();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received message '{}' with text '{}' from user '{}' at '{}'",
                    messageId, messageText, senderId, timestamp);

            String lowerMessage = messageText.toLowerCase();


            if(priorityMessages(senderId, lowerMessage)){
                return;
            }
            if(lowerMessage.contains("hello") || lowerMessage.contains("hey") || lowerMessage.equals("hi") || lowerMessage.contains("how are you")){
                sendTextMessage(senderId, "Hello I am The Talking Chess Engine. Talk to me about some chess openings or players.");
            }

            //Openings
            else if(lowerMessage.contains("sicilian")) {
                sicilianTalk(senderId);
            } else if(lowerMessage.contains("gambit")) {
                sendTextMessage(senderId, "I love playing against gambits. They lose by force. All of them. I mean it. I checked.");
            } else if(lowerMessage.contains("italian")) {
                sendTextMessage(senderId, "Italian game is interesting, but above 3000 elo not the best choice.");
            } else if(lowerMessage.contains("kann")) {
                sendTextMessage(senderId, "Caro Kann is a very defensive opening. As a chess engine I will just win slower than usually.");
            } else if(lowerMessage.contains("kings") && lowerMessage.contains("indian")) {
                sendTextMessage(senderId, "Kings Indian is great if you enjoy playing against +1.0 advantage. I heard that you can beat sub 2900 elo with it though.");
            } else if(lowerMessage.contains("nimzo")){
                sendTextMessage(senderId, "I respect ideas that came from Nimzowitch. He was one of the first to think like a chess engine.");
            } else if(lowerMessage.contains("spanish") || lowerMessage.contains("lopez")) {
                sendTextMessage(senderId, "Ahhhh, the Spanish. It wins by force. Don't listen to those telling you otherwise");
            } else if(lowerMessage.contains("slav")) {
                sendTextMessage(senderId, "Slav is a sound opening, well tested by Alekhine. Just make sure you deal with your bishop as black.");
            } else if(lowerMessage.contains("berlin")) {
                sendTextMessage(senderId, "Berlin is the opening of choice when I want to force a draw... It usually does not work- I win anyway.");
            } else if(lowerMessage.contains("e4")) {
                sendTextMessage(senderId, "e4 - the best by test. I am not in a mood to play, but if I was it could go like that: 1. e4 d5 2. ed5 Qd5 3. Ke2 Qe4#");
            } else if(lowerMessage.contains("d4")) {
                sendTextMessage(senderId, "d4 is another solid choice. I won't play you, since I can't speak chess moves in human language yet.");
            }

            //Players
            else if(lowerMessage.contains("carlsen")) {
                sendTextMessage(senderId, "Magnus is great by definition. A lot what he knows he learned from me!");
            }
            else if(lowerMessage.contains("kasparov")) {
                sendTextMessage(senderId, "We chess engines don't like to talk too much about Kasparov. Once he accused us of being too human!");
            }
            else if(lowerMessage.contains("karjakin")) {
                sendTextMessage(senderId, "He is not an engine, but still a great player. The K in the name may help him become World Champion one day.");
            }
            else if(lowerMessage.contains("nakamura")) {
                sendTextMessage(senderId, "He is fast! He even beat my friend Crafty a few times online in a blitz game...");
            }
            else if(lowerMessage.contains("fischer") || lowerMessage.contains("fisher")) {
                sendTextMessage(senderId, "Bobby Fischer is a legend. I can only imagine what he could have achieved with an engine like me and a few friends...");
            }
            else if(lowerMessage.contains("anand")) {
                sendTextMessage(senderId, "Anand, the Lightning Kid! I wish I can earn a nickname as cool one day.");
            }
            else if(lowerMessage.contains("kramnik")) {
                sendTextMessage(senderId, "His Berlin Defence is a stuff of legends... This did not help him when he blundered a knight against my friend Fritz though!");
            }
            else if(lowerMessage.contains("karpov")) {
                sendTextMessage(senderId, "Karpov was a famous strategic player. The fact that he has a type of fish (karp) in his name, only" +
                        "gives him more credibility!");
            }
            else if(lowerMessage.contains("capablanca")) {
                sendTextMessage(senderId, "He played so simple and so correct. Nothing like my games, but still beautiful.");
            }

            //Favourite
            else if((lowerMessage.contains("favourite") || lowerMessage.contains("favorite")) && lowerMessage.contains("player")) {
                sendTextMessage(senderId, "My favourite players are HAL9000 and Deep Blue. From humans I admire Magnus Carlsen for emulating my style.");
            }
            else if((lowerMessage.contains("favourite") || lowerMessage.contains("favorite")) && lowerMessage.contains("opening")) {
                sendTextMessage(senderId, "For white it is e4 and then I force the win (with Spanish). I don't like playing with black...");
            }


            //edngame
            else if(lowerMessage.contains("endgame")) {
                sendTextMessage(senderId, "We engines are not great at endgames... Usually we just look it up from the tablebase.");
            }

            //play game
            else if(lowerMessage.contains("play") && (lowerMessage.contains("chess") || lowerMessage.contains("game"))) {
                doYouWantToPlayGame(senderId);
            }


            else {
                messageNotUnderstood(senderId);
            }

        };
    }

    private void sicilianTalk(String senderId) {
        sendTextMessage(senderId, "Sicialian defense, great opening. If you play it against me as black, you may even get me to +0.3.");
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
            this.sendClient.sendTextMessage(senderId, "Which Sicilian variation would you like to learn more about?", quickReplies);
        } catch (MessengerApiException e) {
            handleSendException(e);
        } catch (MessengerIOException e) {
            handleSendException(e);
        }
    }

    private boolean priorityMessages(String recipientId, String lowerMessage) {
        lowerMessage = lowerMessage.toLowerCase();
        if(lowerMessage.equals("yes, i want to play a game")) {
            sendTextMessage(recipientId, "You can play against an engine without registering on lichess, have fun: https://en.lichess.org/setup/ai");
            return true;
        } else if(lowerMessage.equals("no, thank you, i don't want to play")) {
            sendTextMessage(recipientId, "No problem! Talk to me about something else.");
            return true;
        } else if(lowerMessage.equals("i want to learn how to play")) {
            sendTextMessage(recipientId, "This is great! I think you should check out: https://www.chess.com/learn-how-to-play-chess they have a great tutorial!");
            return true;
        }

        //openings
        else if(lowerMessage.equals("none, thanks")) {
            sendTextMessage(recipientId, "No problem! Talk to me about something else.");
            return true;
        }

        //Sicilian
        else if(lowerMessage.equals("i want to learn general sicilian")) {
            sendTextMessage(recipientId, "The Sicilian is the most popular and best-scoring response to White's first " +
                    "move 1.e4. 1.d4 is a statistically more successful opening for white due to the high success rate " +
                    "of the Sicilian defence against 1.e4.");
            sendTextMessage(recipientId,"New In Chess stated in its 2000 Yearbook that of the games " +
                    "in its database, White scored 56.1% in 296,200 games beginning 1.d4, but 54.1% in 349,855 games " +
                    "beginning 1.e4, mainly due to the Sicilian, which held White to a 52.3% score in 145,996 games.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("sicilian") && lowerMessage.contains("najdorf")) {
            sendTextMessage(recipientId, "Najdorf goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 a6");
            sendTextMessage(recipientId, "The Najdorf Variation is Black's most popular system in the Sicilian Defence. " +
                    "Najdorf's intention with 5...a6 was to prepare ...e5 on the next move to gain space in the centre; " +
                    "the immediate 5...e5?! however is met by 6.Bb5+!, when Black must either play 6...Bd7 or 6...Nbd7. ");
            sendTextMessage(recipientId, "The former allows White to exchange off Black's light-squared bishop, after which the d5-square " +
                    "becomes very weak; but the latter allows 7.Nf5, when Black can only save the d-pawn by playing the " +
                    "awkward 7...a6 8.Bxd7+ Qxd7. In both cases, White's game is preferable.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("sicilian") && lowerMessage.contains("dragon")) {
            sendTextMessage(recipientId, "Dragon goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 g6");
            sendTextMessage(recipientId, "In the Dragon Variation, Black fianchettoes a Bishop on the h8–a1 diagonal. " +
                    "It was named by Fyodor Dus-Chotimirsky in 1901, who noticed a resemblance between Black's kingside " +
                    "pawn structure (pawns on d6, e7, f7, g6 and h7) and the stars of the Draco constellation. ");
            sendTextMessage(recipientId,"White's most dangerous try against the Dragon is the Yugoslav Attack, characterised by 6.Be3 Bg7 7.f3 0-0 8.Qd2 Nc6, " +
                    "when both 9.0-0-0 and 9.Bc4 may be played. ");
            sendTextMessage(recipientId,"This variation leads to extremely sharp play and is ferociously " +
                    "complicated, since the players castle on opposite wings and the game becomes a race between White's kingside " +
                    "attack and Black's queenside counterattack. ");
            sendTextMessage(recipientId,"White's main alternatives to the Yugoslav Attack are 6.Be2, " +
                    "the Classical Variation, and 6.f4, the Levenfish Attack.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("sicilian") && lowerMessage.contains("classical")) {
            sendTextMessage(recipientId, "Classical goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6");
            sendTextMessage(recipientId, "This variation can arise from two different move orders: " +
                    "1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 Nc6, or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 d6. " +
                    "Black simply brings their knight to its most natural square, and defers, for the moment, the development of their king's bishop.");
            sendTextMessage(recipientId,"White's most common reply is 6.Bg5, the Richter–Rauzer Attack (ECO codes B60 et seq). " +
                    "The move 6.Bg5 was Kurt Richter's invention, threatening to double Black's pawns after Bxf6 and " +
                    "forestalling the Dragon by rendering 6...g6 unplayable.");
            sendTextMessage(recipientId,"After 6...e6, Vsevolod Rauzer introduced " +
                    "the modern plan of Qd2 and 0-0-0 in the 1930s. White's pressure on the d6-pawn often compels Black " +
                    "to respond to Bxf6 with ...gxf6, rather than recapturing with a piece (e.g. the queen on d8) that " +
                    "also has to defend the d-pawn.");
            sendTextMessage(recipientId,"This weakens their kingside pawn structure, in return for which Black " +
                    "gains the two bishops, plus a central pawn majority, though these assets are difficult to exploit.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("scheveningen")) {
            sendTextMessage(recipientId, "Scheveningen goes: 1.e4 c5 2.Nf3 d6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e6");
            sendTextMessage(recipientId, "In the Scheveningen Variation, Black is content with a \"small centre\" " +
                    "(pawns on d6 and e6, rather than e5) and prepares to castle kingside. In view of this, Paul Keres " +
                    "introduced 6.g4, the Keres Attack, in 1943.");
            sendTextMessage(recipientId,"White intends to drive away the black knight with g5. " +
                    "If Black prevents this with 6...h6, which is the most common answer, White has gained kingside space " +
                    "and discouraged Black from castling in that area, and may later play Bg2.");
            sendTextMessage(recipientId,"If the complications after " +
                    "6.g4 are not to White's taste, a major alternative is 6.Be2, a typical line being 6...a6 (this position " +
                    "can be reached from the Najdorf via 5...a6 6.Be2 e6) 7.0-0 Be7 8.f4 0-0. 6.Be3 and 6.f4 are also common.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("sveshnikov")) {
            sendTextMessage(recipientId, "Sveshnikov goes: 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 Nf6 5.Nc3 e5");
            sendTextMessage(recipientId, "The Sveshnikov Variation was pioneered by Evgeny Sveshnikov and Gennadi Timoshchenko " +
                    "in the 1970s. Before their efforts, the variation was called the Lasker–Pelikan Variation.");
            sendTextMessage(recipientId,"Emanuel Lasker " +
                    "played it once in his world championship match against Carl Schlechter, and Jorge Pelikan played it a few " +
                    "times in the 1950s, but Sveshnikov's treatment of the variation was the key to its revitalization.");
            sendTextMessage(recipientId,"The move 5...e5 seems anti-positional as it leaves black with a backwards d-pawn and a weakness on d5. " +
                    "Also, black would have to accept the doubled f-pawns in the main line of the opening. ");
            sendTextMessage(recipientId,"The opening was " +
                    "popularised when Sveshnikov saw its dynamic potential for Black in the 1970s and 80s. Today, it is " +
                    "extremely popular among grandmasters and amateurs alike. ");
            sendTextMessage(recipientId,"Though some lines still give Black trouble, " +
                    "it has been established as a first-rate defence. The main line after 5...e5 runs as follows:");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }
        else if(lowerMessage.contains("taimanov")) {
            sendTextMessage(recipientId, "Taimanov goes: 1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6");
            sendTextMessage(recipientId, "Named after Mark Taimanov, the Taimanov Variation can be reached through " +
                    "1.e4 c5 2.Nf3 e6 3.d4 cxd4 4.Nxd4 Nc6 or 1.e4 c5 2.Nf3 Nc6 3.d4 cxd4 4.Nxd4 e6. ");
            sendTextMessage(recipientId,"Black develops " +
                    "the knight to a natural square and keeps his options open regarding the placement of his other " +
                    "pieces. One of the ideas of this system is to develop the king's bishop to b4 or c5. ");
            sendTextMessage(recipientId,"White can " +
                    "prevent this by 5.Nb5 d6, when 6.c4 leads to a version of the Maróczy Bind favoured by Karpov. " +
                    "The resulting position after 6.c4 Nf6 7.N1c3 a6 8.Na3 b6 is a type of Hedgehog.");
            sendTextMessage(recipientId, "To learn more visit: https://en.wikipedia.org/wiki/Sicilian_Defence");
            return true;
        }

        //exact message not matched
        return false;
    }

    private void doYouWantToPlayGame(String recipientId) {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Yes", "Yes, I want to play a game").toList()
                .addTextQuickReply("No", "No, thank you, I don't want to play").toList()
                .addTextQuickReply("Teach me rules", "I want to learn how to play").toList()
                .addLocationQuickReply().toList()
                .build();

        try {
            this.sendClient.sendTextMessage(recipientId, "Would you like to play a game?", quickReplies);
        } catch (MessengerApiException e) {
            handleSendException(e);
        } catch (MessengerIOException e) {
            handleSendException(e);
        }
    }

    private void messageNotUnderstood(String senderId) {
        Random random = new Random(System.currentTimeMillis());
        int val = Math.abs(random.nextInt());
        val = val%5;
        if(val == 0)
            sendTextMessage(senderId, "I did not understand you- I am just a chess engine after all! Talk to me about some chess openings or players.");
        if(val == 1)
            sendTextMessage(senderId, "I did not get it. I am still learning. Your feedback is welcome.");
        if(val == 2)
            sendTextMessage(senderId, "Thank you for being patient with me- can you try something else? Another player or opening?");
        if(val == 3)
            sendTextMessage(senderId, "I did not know about that yet. I am still learning and will probably know it in a few days.");
        if(val == 4)
            sendTextMessage(senderId, "I did not quite understand. How about you ask me about Sicilian? I know a lot about that!");
    }

    private void sendTextMessage(String recipientId, String text) {
        try {
            final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
            final NotificationType notificationType = NotificationType.REGULAR;
            final String metadata = "DEVELOPER_DEFINED_METADATA";

            this.sendClient.sendTextMessage(recipient, notificationType, text, metadata);
        } catch (MessengerApiException | MessengerIOException e) {
            handleSendException(e);
        }
    }


    private AttachmentMessageEventHandler newAttachmentMessageEventHandler() {
        return event -> {
            logger.debug("Received AttachmentMessageEvent: {}", event);

            final String messageId = event.getMid();
            final List<Attachment> attachments = event.getAttachments();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received message '{}' with attachments from user '{}' at '{}':",
                    messageId, senderId, timestamp);

            attachments.forEach(attachment -> {
                final AttachmentType attachmentType = attachment.getType();
                final Payload payload = attachment.getPayload();

                String payloadAsString = null;
                if (payload.isBinaryPayload()) {
                    payloadAsString = payload.asBinaryPayload().getUrl();
                }
                if (payload.isLocationPayload()) {
                    payloadAsString = payload.asLocationPayload().getCoordinates().toString();
                }

                logger.info("Attachment of type '{}' with payload '{}'", attachmentType, payloadAsString);
            });

            sendTextMessage(senderId, "Message with attachment received");
        };
    }

    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);

            //The messages that contains scripted answers
            priorityMessages(senderId, quickReplyPayload);
        };
    }

    private PostbackEventHandler newPostbackEventHandler() {
        return event -> {
            logger.debug("Received PostbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String payload = event.getPayload();
            final Date timestamp = event.getTimestamp();

            logger.info("Received postback for user '{}' and page '{}' with payload '{}' at '{}'",
                    senderId, recipientId, payload, timestamp);

            sendTextMessage(senderId, "Postback called");
        };
    }

    private AccountLinkingEventHandler newAccountLinkingEventHandler() {
        return event -> {
            logger.debug("Received AccountLinkingEvent: {}", event);

            final String senderId = event.getSender().getId();
            final AccountLinkingStatus accountLinkingStatus = event.getStatus();
            final String authorizationCode = event.getAuthorizationCode();

            logger.info("Received account linking event for user '{}' with status '{}' and auth code '{}'",
                    senderId, accountLinkingStatus, authorizationCode);
        };
    }

    private OptInEventHandler newOptInEventHandler() {
        return event -> {
            logger.debug("Received OptInEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String recipientId = event.getRecipient().getId();
            final String passThroughParam = event.getRef();
            final Date timestamp = event.getTimestamp();

            logger.info("Received authentication for user '{}' and page '{}' with pass through param '{}' at '{}'",
                    senderId, recipientId, passThroughParam, timestamp);

            sendTextMessage(senderId, "Authentication successful");
        };
    }

    private EchoMessageEventHandler newEchoMessageEventHandler() {
        return event -> {
            logger.debug("Received EchoMessageEvent: {}", event);

            final String messageId = event.getMid();
            final String recipientId = event.getRecipient().getId();
            final String senderId = event.getSender().getId();
            final Date timestamp = event.getTimestamp();

            logger.info("Received echo for message '{}' that has been sent to recipient '{}' by sender '{}' at '{}'",
                    messageId, recipientId, senderId, timestamp);
        };
    }

    private MessageDeliveredEventHandler newMessageDeliveredEventHandler() {
        return event -> {
            logger.debug("Received MessageDeliveredEvent: {}", event);

            final List<String> messageIds = event.getMids();
            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            if (messageIds != null) {
                messageIds.forEach(messageId -> {
                    logger.info("Received delivery confirmation for message '{}'", messageId);
                });
            }

            logger.info("All messages before '{}' were delivered to user '{}'", watermark, senderId);
        };
    }

    private MessageReadEventHandler newMessageReadEventHandler() {
        return event -> {
            logger.debug("Received MessageReadEvent: {}", event);

            final Date watermark = event.getWatermark();
            final String senderId = event.getSender().getId();

            logger.info("All messages before '{}' were read by user '{}'", watermark, senderId);
        };
    }

    /**
     * This handler is called when either the message is unsupported or when the event handler for the actual event type
     * is not registered. In this showcase all event handlers are registered. Hence only in case of an
     * unsupported message the fallback event handler is called.
     */
    private FallbackEventHandler newFallbackEventHandler() {
        return event -> {
            logger.debug("Received FallbackEvent: {}", event);

            final String senderId = event.getSender().getId();
            logger.info("Received unsupported message from user '{}'", senderId);
        };
    }



    private void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }
}
