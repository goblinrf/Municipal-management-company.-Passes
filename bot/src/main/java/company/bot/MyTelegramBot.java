package company.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import company.bot.service.UpdateHandler;

public class MyTelegramBot {
    private final TelegramBot bot;
    private final UpdateHandler updateHandler;

    public MyTelegramBot() {
        String token = System.getenv("TELEGRAM_BOT_TOKEN");
        this.bot = new TelegramBot(token);
        this.updateHandler = new UpdateHandler(bot);
    }

    public void start() {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                updateHandler.handle(update);
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
