package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
