package org.elmifdali.chatbotservice.telegram;

import org.elmifdali.chatbotservice.agents.BankAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@ConditionalOnExpression("'${telegram.bot.token:}'.matches('^[0-9]+:.*')")
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    private static final int MAX_MESSAGE_LENGTH = 4096;

    private final String botToken;
    private final BankAgent bankAgent;
    private final TelegramClient telegramClient;

    public TelegramBot(@Value("${telegram.bot.token:}") String botToken, BankAgent bankAgent) {
        this.botToken = botToken;
        this.bankAgent = bankAgent;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            log.info("Received message from chatId {}", chatId);

            try {
                // Each Telegram chat gets its own conversation memory
                String response = bankAgent.chat(messageText, String.valueOf(chatId));
                send(chatId, response != null ? response : "Désolé, aucune réponse générée.");
            } catch (TelegramApiException e) {
                log.error("Telegram API error while sending message to chatId {}", chatId, e);
            } catch (Exception e) {
                log.error("Error processing update for chatId {}", chatId, e);
                try {
                    send(chatId, "Une erreur est survenue lors du traitement de votre demande.");
                } catch (TelegramApiException ex) {
                    log.error("Failed to send error notification to chatId {}", chatId, ex);
                }
            }
        }
    }

    private void send(long chatId, String text) throws TelegramApiException {
        for (int start = 0; start < text.length(); start += MAX_MESSAGE_LENGTH) {
            String part = text.substring(start, Math.min(start + MAX_MESSAGE_LENGTH, text.length()));
            telegramClient.execute(SendMessage.builder().chatId(chatId).text(part).build());
        }
    }
}
