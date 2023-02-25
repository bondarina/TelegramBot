package ru.relex.controller;

import lombok.extern.log4j.Log4j;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
//@Log4j
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;
    private long chatId;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Model model = new Model();
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    startCommandReceived(message, chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "Да":
                    sendMsg(message, chatId,
                            "\n" +
                                    "...☆.´..☆\n" +
                                    ".........´.¸.☆´ ☆\n" +
                                    "...........☆.´..☆´\n" +
                                    ".............(.☆❤ )\n" +
                                    ".................))     \n" +
                                    "...............██Ɔ" +
                                    "\n\nС днём рождения!" +
                            "\n\nПредставь, что у тебя есть телепорт! В какой город"
                    +" ты бы хотел отправиться?"
                    + "\n(но прежде нужно кое-что проверить: "
                    +"напиши этот город)");

                    break;
                case "Не знаю":
                    sendMsg(message, chatId, "Попробуй ещё раз");
                    break;
                default:
                    try {
                        sendMsg(message, chatId, Weather.getWeather(message.getText(), model));
                    } catch (IOException e) {
                        sendMsg(message, chatId, "Город не найден! Попробуй снова");
                    }

            }
        }

    }

    public void startCommandReceived(Message message, long chatId, String name) {
        String answer = name + ", как настроение?\n\nДобавим немного магии в этот день?"
                + "\n(ответы на выбор ниже)";

        sendMsg(message, chatId, answer);
    }


    public void sendMsg(Message message, long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        sendMessage.setChatId(message.getChatId().toString());

        sendMessage.setReplyToMessageId(message.getMessageId());

        sendMessage.setText(text);
        try {

            setButtons(sendMessage);
            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("Да"));
        keyboardFirstRow.add(new KeyboardButton("Не знаю"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

}
