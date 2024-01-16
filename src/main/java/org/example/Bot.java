package org.example;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

import static java.lang.Math.toIntExact;

public class Bot extends TelegramLongPollingBot {
    Logger logger = LogManager.getLogger(Main.class);
    Advent[] adventList = new Scraper().scrapeEvents(); // Does it once as the bot initializes
    @Override
    public String getBotUsername() {
        try {
            FileInputStream propsInput = new FileInputStream("src/main/config.properties");
            Properties properties = new Properties();
            properties.load(propsInput);
            return properties.getProperty("USERNAME");
        }
        catch (FileNotFoundException e) {
            logger.error("Config file not found");
        } catch (IOException e) {
            logger.error("IOException");
        } catch (Exception e) { // Doesn't log/catch !!
            logger.error("getBotUsername error; USERNAME might be empty");
        }
        return null;
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
            logger.error("Config file not found");
        } catch (IOException e) {
            logger.error("IOException");
        } catch (Exception e) { // Doesn't log/catch !!
            logger.error("getBotToken error; TOKEN might be empty");
        }
        return null;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Buttons
        var listButton = InlineKeyboardButton.builder()
                .text("All events").callbackData("list").build();
        var closestButton = InlineKeyboardButton.builder()
                .text("Closest event").callbackData("closest").build();
        var thisyearButton = InlineKeyboardButton.builder()
                .text("Events this year").callbackData("thisyear").build();
        InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(closestButton))
                .keyboardRow(List.of(listButton, thisyearButton)).build();

        if (update.hasMessage()) {
            // Initialized var
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();

            // Commands
            if (Objects.equals(msg.getText(), "/events")) {
                sendKeyboard(id, "<strong>Check events</strong>", keyboard);
                logger.info("Command /events issued");
            }
        } else if (update.hasCallbackQuery()) { // Handles the keyboard queries
            String data = update.getCallbackQuery().getData();
            long id = update.getCallbackQuery().getMessage().getChatId();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            logger.info("Callback query issued");
            if (data.equals("closest")) {
                editMessage(id, message_id, closestEventString(adventList), keyboard);
            }
            if (data.equals("list")) {
                editMessage(id, message_id, adventListString(adventList), keyboard);
            }
            if (data.equals("thisyear")) {
                editMessage(id, message_id, thisyearEventsString(adventList), keyboard);
            }
        }
    }
    public void editMessage(Long chat_id, long message_id, String updated_text, InlineKeyboardMarkup keyboard) {
        EditMessageText sm = EditMessageText.builder()
                .chatId(chat_id.toString())
                .messageId(toIntExact(message_id))
                .text(updated_text)
                .replyMarkup(keyboard)
                .parseMode("html").disableWebPagePreview(true).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
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
            string.append(String.format("• <a href='%s'>%s</a> %s\n", advent.link, advent.title, advent.date));
        }
        return String.valueOf(string);
    }
    public String closestEventString (Advent[] adventList) {
        String string;
        string = String.format("<strong>Here are the 2 closest events:</strong>\n• <a href='%s'>%s</a> %s\n• <a href='%s'>%s</a> %s", adventList[0].link, adventList[0].title, adventList[0].date, adventList[1].link, adventList[1].title, adventList[1].date);
        return string;
    }
    public String thisyearEventsString (Advent[] adventList) {
        StringBuilder string = new StringBuilder();
        int date = LocalDate.now().getYear();
        string.append(String.format("<strong>List of events this year %d:</strong>\n", date));
        for (Advent advent : adventList) {
            if (advent.date.contains(String.valueOf(date))) {
                string.append(String.format("• <a href='%s'>%s</a> %s\n", advent.link, advent.title, advent.date));
            }
        }
        return String.valueOf(string);
    }
}
