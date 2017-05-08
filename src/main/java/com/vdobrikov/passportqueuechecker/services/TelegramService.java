package com.vdobrikov.passportqueuechecker.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: Vladimir Dobrikov (hedin.mail@gmail.com)
 */

@Component
public class TelegramService {
    private static final Logger LOG = LoggerFactory.getLogger(TelegramService.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("#{'${telegram.chat.ids}'.split(',')}")
    private Set<Long> chatIds;

    private TelegramBot bot;

    @PostConstruct
    private void init() {
        bot = TelegramBotAdapter.build(botToken);
    }

    public void sendMessage(String msg) {
        LOG.debug("Sending msg = {} chats = {}", msg, chatIds);
        chatIds.stream().forEach(chatId -> {
            SendMessage request = new SendMessage(chatId, msg);
            SendResponse response = bot.execute(request);
            verify(response);
            LOG.debug("chat {} OK", chatId);
        });
    }

    public List<String> getNewMessages() {
        LOG.debug("Fetching new messages");
        GetUpdates getUpdates = new GetUpdates()
            .limit(10)
            .offset(0)
            .timeout(0);
        GetUpdatesResponse response = bot.execute(getUpdates);
        verify(response);
        LOG.debug("OK");
        return response.updates().stream()
            .map(update -> update.message().text())
            .collect(Collectors.toList());
    }

    private void verify(BaseResponse response) {
        if (response.isOk()) {
            return;
        }
        LOG.error("Error found in response. code = {}; description = {}", response.errorCode(), response.description());
        throw new RuntimeException(String.format("Telegram error: code = %s; description = %s", response.errorCode(), response.description()));
    }
}
