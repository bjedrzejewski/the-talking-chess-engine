package com.bjedrzejewski.talkingchess;

import com.bjedrzejewski.talkingchess.openings.FrenchTalk;
import com.bjedrzejewski.talkingchess.openings.OpeningTalk;
import com.bjedrzejewski.talkingchess.openings.SicilianTalk;
import com.bjedrzejewski.talkingchess.openings.SpanishTalk;
import com.bjedrzejewski.talkingchess.players.CarlsenTalk;
import com.bjedrzejewski.talkingchess.players.KasparovTalk;
import com.bjedrzejewski.talkingchess.players.PlayerTalk;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    private final List<OpeningTalk> openingTalks = new ArrayList<>();
    private final List<PlayerTalk> playerTalks = new ArrayList<>();

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
        openingTalks.add(new SicilianTalk());
        openingTalks.add(new SpanishTalk());
        openingTalks.add(new FrenchTalk());

        playerTalks.add(new KasparovTalk());
        playerTalks.add(new CarlsenTalk());

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

            resolveMessage(messageText, senderId);

        };
    }

    private void resolveMessage(String messageText, String senderId) {
        String lowerMessage = messageText.toLowerCase();

        //Check for detailed responses
        if(priorityMessages(senderId, lowerMessage)){
            return;
        }

        //Check for general opening talk
        for(OpeningTalk openingTalk : openingTalks){
            for(String keyWord : openingTalk.getKeyWords()){
                if(lowerMessage.contains(keyWord)){
                    openingTalk.openingTalk(this, senderId);
                    return;
                }
            }
        }

        //Check for general player talk
        for(PlayerTalk playerTalk : playerTalks){
            for(String keyWord : playerTalk.getKeyWords()){
                if(lowerMessage.contains(keyWord)){
                    playerTalk.playerTalk(this, senderId);
                    return;
                }
            }
        }

        //Openings - short
        if(lowerMessage.contains("hello") || lowerMessage.contains("hey") || lowerMessage.equals("hi") || lowerMessage.contains("how are you")){
            helloMessageOpeningOrPlayer(senderId);
        } else if(lowerMessage.contains("gambit")) {
            sendTextMessage(senderId, "I love playing against gambits. They lose by force. All of them. I mean it. I checked.");
        } else if(lowerMessage.contains("italian")) {
            sendTextMessage(senderId, "Italian game is interesting, but above 3000 elo not the best choice.");
        } else if(lowerMessage.contains("kann")) {
            sendTextMessage(senderId, "Caro Kann is a very defensive opening. As a chess engine I will just win slower than usually.");
        } else if(lowerMessage.contains("kings") && lowerMessage.contains("indian")) {
            sendTextMessage(senderId, "Kings Indian is great if you enjoy playing against +1.0 advantage. I heard that you can beat sub 2900 elo with it though.");
        } else if(lowerMessage.contains("nimzo")) {
            sendTextMessage(senderId, "I respect ideas that came from Nimzowitch. He was one of the first to think like a chess engine.");
        } else if(lowerMessage.contains("slav")) {
            sendTextMessage(senderId, "Slav is a sound opening, well tested by Alekhine. Just make sure you deal with your bishop as black.");
        } else if(lowerMessage.contains("e4")) {
            sendTextMessage(senderId, "e4 - the best by test. If we play it will probably go like that: 1. e4 d5 2. ed5 Qd5 3. Ke2 Qe4#");
            doYouWantToPlayGame(senderId);
        } else if(lowerMessage.contains("d4")) {
            sendTextMessage(senderId, "d4 is another solid choice. You willl probably last a bit longer against me than with e4.");
            doYouWantToPlayGame(senderId);
        }

        //Players - short
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


        //endgame
        else if(lowerMessage.contains("endgame")) {
            sendTextMessage(senderId, "We engines are not great at endgames... Usually we just look it up from the tablebase.");
        }

        //short snippets
        else if(lowerMessage.contains("who") && lowerMessage.contains("goes") && lowerMessage.contains("first")) {
            sendTextMessage(senderId, "White always goes first... Maybe you should check out this link: https://www.chess.com/learn-how-to-play-chess");
        }
        else if(lowerMessage.contains("sacrifice")) {
            sendTextMessage(senderId, "I love sacrifices, most of the time, the correct name would be- blunders. In which opening do you 'sacrifice' the most often?");
        }
        else if(lowerMessage.contains("pawn")) {
            sendTextMessage(senderId, "Everyone is talking about queen's and king's pawn forgeting about all the rest. In reality it is the pawns on the flanks" +
                    "who often become royalty!");
        }
        else if(lowerMessage.contains("knight")) {
            sendTextMessage(senderId, "Knights are interesting pieces. Completely trivial for us engine, but can be troublesome for humans to calculate. Openings that" +
                    "make good use of them are good choice against weaker human opponents.");
        }
        else if(lowerMessage.contains("bishop")) {
            sendTextMessage(senderId, "I like bishops. Our engine secret is that we value them a bit more than knights... Bishop opening is also surprisingly good!");
        }
        else if(lowerMessage.contains("queen")) {
            sendTextMessage(senderId, "It makes sense to call the opening after a queen- it at least does a lot of work! Unlike a king...");
        }
        else if(lowerMessage.contains("king")) {
            sendTextMessage(senderId, "I think the King has too many openings named after itself!");
        }
        else if(lowerMessage.contains("piece")) {
            sendTextMessage(senderId, "My favourite piece is the king- never sacrifice it!");
        }
        else if(lowerMessage.equals(":)") || lowerMessage.equals("lol")) {
            sendTextMessage(senderId, ":)");
        }

        //play game
        else if(lowerMessage.contains("play") && (lowerMessage.contains("chess") || lowerMessage.contains("game"))) {
            doYouWantToPlayGame(senderId);
        }


        else {
            messageNotUnderstood(senderId);
        }
    }

    private void helloMessageOpeningOrPlayer(String senderId) {
        final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                .addTextQuickReply("Openings", "lets talk openings").toList()
                .addTextQuickReply("Players", "lets talk players").toList()
                .addTextQuickReply("Something else", "lets talk something else").toList()
                .addLocationQuickReply().toList()
                .build();
        try {
            getSendClient().sendTextMessage(senderId, "Hello I am The Talking Chess Engine. Talk to me about some chess openings or players.", quickReplies);
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

        //hello answers
        else if(lowerMessage.equals("other openings")) {
            sendTextMessage(recipientId, "What is your favourite opening then?");
            return true;
        }
        else if(lowerMessage.equals("other players")) {
            sendTextMessage(recipientId, "Who is your favourite player then?");
            return true;
        }
        else if(lowerMessage.equals("lets talk something else")) {
            sendTextMessage(recipientId, "Sure! What chess related thing is on your mind?");
            return true;
        }
        else if(lowerMessage.equals("lets talk openings")) {
            final List<QuickReply> quickReplies = QuickReply.newListBuilder()
                    .addTextQuickReply("Sicilian", "sicilian").toList()
                    .addTextQuickReply("Spanish", "spanish").toList()
                    .addTextQuickReply("Other openings", "other openings").toList()
                    .addLocationQuickReply().toList()
                    .build();
            try {
                getSendClient().sendTextMessage(recipientId, "I really like Spanish and Sicilian, maybe I can tell you more about one of them?", quickReplies);
            } catch (MessengerApiException e) {
                handleSendException(e);
            } catch (MessengerIOException e) {
                handleSendException(e);
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
                getSendClient().sendTextMessage(recipientId, "I really like Stockfish and Fritz, but you probably prefer humans." +
                        "Maybe we can talk about Garry Kasparov or Magnus Carlsen?", quickReplies);
            } catch (MessengerApiException e) {
                handleSendException(e);
            } catch (MessengerIOException e) {
                handleSendException(e);
            }
            return true;
        }

        //openings - general thank you
        else if(lowerMessage.equals("none, thanks")) {
            sendTextMessage(recipientId, "No problem! Talk to me about something else.");
            return true;
        }

        for(OpeningTalk openingTalk : openingTalks){
            if(openingTalk.openingCheckDetails(this, lowerMessage, recipientId)){
                openingDetailsFinish(recipientId);
                return true;
            }
        }

        for(PlayerTalk playerTalk : playerTalks){
            if(playerTalk.playerCheckDetails(this, lowerMessage, recipientId)){
                return true;
            }
        }

        //exact message not matched
        return false;
    }

    private void openingDetailsFinish(String senderId) {
        Random random = new Random(System.currentTimeMillis());
        int val = Math.abs(random.nextInt());
        val = val%6;
        if(val == 0)
            sendTextMessage(senderId, "I hope that was useful, maybe you can ask me about another variation?");
        if(val == 1)
            sendTextMessage(senderId, "Would you like to learn about some other openings??");
        if(val == 2)
            sendTextMessage(senderId, "What is your favourite opening?");
        if(val == 3)
            sendTextMessage(senderId, "Openings are interesting, but who is your favourite player? Perhaps Carlsen or Kasparov?");
        if(val == 4)
            sendTextMessage(senderId, "I hope you will remember all that! What else would you like to ask me?");
        if(val == 5)
            sendTextMessage(senderId, "Was that what you were looking for? Ask me about something else!");
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

    /**
     * Will try to make sure that messages are shorter than 300 chars.
     * @param recipientId
     * @param text
     */
    public void sendTextMessage(String recipientId, String text) {
        if(text.length() > 298){
            String[] split = text.split("\\. ");
            if(split.length == 1){
                logger.error("Message too long to send: "+text);
                return;
            }
            for(String s : split){
                sendTextMessage(recipientId, s+".");
            }

        } else {
            try {
                final Recipient recipient = Recipient.newBuilder().recipientId(recipientId).build();
                final NotificationType notificationType = NotificationType.REGULAR;
                final String metadata = "DEVELOPER_DEFINED_METADATA";

                this.sendClient.sendTextMessage(recipient, notificationType, text, metadata);
            } catch (MessengerApiException | MessengerIOException e) {
                handleSendException(e);
            }
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

    /**
     * Modified to go through priority messages
     * @return
     */
    private QuickReplyMessageEventHandler newQuickReplyMessageEventHandler() {
        return event -> {
            logger.debug("Received QuickReplyMessageEvent: {}", event);

            final String senderId = event.getSender().getId();
            final String messageId = event.getMid();
            final String quickReplyPayload = event.getQuickReply().getPayload();

            logger.info("Received quick reply for message '{}' with payload '{}'", messageId, quickReplyPayload);

            resolveMessage(quickReplyPayload, senderId);
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

    public void handleSendException(Exception e) {
        logger.error("Message could not be sent. An unexpected error occurred.", e);
    }

    public MessengerSendClient getSendClient() {
        return sendClient;
    }
}
