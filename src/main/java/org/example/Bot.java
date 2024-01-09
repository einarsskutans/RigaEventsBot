package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Bot extends TelegramLongPollingBot {
    Advent[] adventList = new Scraper().scrapeEvents();
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
                    .text("All events").callbackData("list").build();
            var closestButton = InlineKeyboardButton.builder()
                    .text("Closest event").callbackData("closest").build();
            var thisyearButton = InlineKeyboardButton.builder()
                    .text("Events this year").callbackData("thisyear").build();
            InlineKeyboardMarkup keyboard1 = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(closestButton ))
                    .keyboardRow(List.of(listButton, thisyearButton)).build();

            if (Objects.equals(msg.getText(), "/events")) {
                sendKeyboard(id, "Menu", keyboard1);
            }
        } else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            long id = update.getCallbackQuery().getMessage().getChatId();

            if (data.equals("list")) {
                sendText(id, adventListString(adventList));
            }
            if (data.equals("closest")) {
                sendText(id, closestEventString(adventList));
            }
            if (data.equals("thisyear")) {
                sendText(id, thisyearEventsString(adventList));
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

    // Formatting
    public String adventListString (Advent[] adventList) {
        StringBuilder string = new StringBuilder();
        string.append("<strong>All events:</strong>\n");
        for (Advent advent : adventList) {
            string.append(String.format("• %s %s %s\n",advent.title, advent.date, advent.link));
        }
        return String.valueOf(string);
    }
    public String closestEventString (Advent[] adventList) {
        String string;
        string = String.format("<strong>Here are the 2 closest events:</strong>\n• %s %s %s\n• %s %s %s", adventList[0].title, adventList[0].date, adventList[0].link, adventList[1].title, adventList[1].date, adventList[1].link);
        return string;
    }
    public String thisyearEventsString (Advent[] adventList) {
        StringBuilder string = new StringBuilder();
        int date = LocalDate.now().getYear();
        string.append(String.format("<strong>List of events this year %d:</strong>\n", date));
        for (Advent advent : adventList) {
            if (advent.date.contains(String.valueOf(date))) {
                string.append(String.format("• %s %s %s\n", advent.title, advent.date, advent.link));
            }
        }
        return String.valueOf(string);
    }
}
