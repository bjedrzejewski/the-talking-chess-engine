package com.bjedrzejewski.talkingchess;

import com.bjedrzejewski.talkingchess.general.GeneralTalk;
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

            GeneralTalk.resolveMessage(messageText, senderId, openingTalks, playerTalks, this);

        };
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

            GeneralTalk.resolveMessage(quickReplyPayload, senderId, openingTalks, playerTalks, this);
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
