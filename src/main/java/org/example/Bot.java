package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        try {
            FileInputStream propsInput = new FileInputStream("src/main/config.properties");
            Properties properties = new Properties();
            properties.load(propsInput);
            return properties.getProperty("USERNAME");
        }
        catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
        return "";
    }

    @Override
    public String getBotToken() {
        try {
            FileInputStream propsInput = new FileInputStream("src/main/config.properties");
            Properties properties = new Properties();
            properties.load(propsInput);
            return properties.getProperty("TOKEN");
        }
        catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            // Initialized var
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();

            // Buttons
            var listButton = InlineKeyboardButton.builder()
                    .text("List").callbackData("list").build();
            var rightButton = InlineKeyboardButton.builder()
                    .text(">").callbackData("right").build();
            var leftButton = InlineKeyboardButton.builder()
                    .text("<").callbackData("left").build();
            InlineKeyboardMarkup keyboard1 = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(leftButton, listButton, rightButton))
                    .build();

            if (Objects.equals(msg.getText(), "/events")) {
                sendKeyboard(id, "Menu", keyboard1);
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            long msg = update.getCallbackQuery().getMessage().getMessageId();
            long id = update.getCallbackQuery().getMessage().getChatId();

            if (data.equals("list")) {
                sendText(id, new Scraper().scrapeEvents());
            }
            if (data.equals("right")) {

            }
        }
    }
    public void sendText(Long who, String str) { // Sends text message
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("html").disableWebPagePreview(true).text(str).build();
        try{
            execute(sm);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
    public void sendKeyboard(Long who, String str, InlineKeyboardMarkup kb) { // Sends keyboard [of buttons] message
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("html").disableWebPagePreview(true).text(str)
                .replyMarkup(kb).build();
        try{
            execute(sm);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
