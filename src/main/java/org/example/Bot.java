package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "einarstestbot";
    }

    @Override
    public String getBotToken() {
        return "6752312659:AAHpVu77vrrcmJoPrY5RRY7O8h6oGW1L058";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if (Objects.equals(msg.getText(), "what")){
            sendText(id, "ok");
        }
    }
    public void sendText(Long who, String str){
        SendMessage sm = SendMessage.builder().chatId(who.toString()).text(str).build();
        sm.setParseMode(ParseMode.HTML);
        sm.disableWebPagePreview();
        try{
            execute(sm);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
