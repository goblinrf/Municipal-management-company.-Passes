package company.bot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import company.bot.models.Pass;
import company.bot.session.SessionManager;
import company.bot.strategy.Context;
import company.bot.strategy.impl.StrategyDeactivated;
import company.bot.strategy.impl.StrategyEnded;
import company.bot.strategy.impl.StrategyNotEnded;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHandler {
    private final TelegramBot bot;
    private final Map<Long, Stage> userStages = new HashMap<>();
    private final Map<Long, String> tempLoginBuffer = new HashMap<>();

    // Для упрощения хранение состояния диалога продления
    private final Map<Long, ExtendPassState> extendStates = new HashMap<>();

    enum Stage {
        NONE, AWAIT_LOGIN, AWAIT_PASSWORD
    }

    // Состояния для процесса продления пропуска
    private enum ExtendStage {
        NONE, WAITING_DATE, WAITING_CODE
    }

    private static class ExtendPassState {
        ExtendStage stage = ExtendStage.NONE;
        long passId;
        LocalDate newValidUntil;
    }

    public UpdateHandler(TelegramBot bot) {
        this.bot = bot;
    }

    public void handle(Update update) {
        if (update.message() != null && update.message().text() != null) {
            handleMessage(update);
        } else if (update.callbackQuery() != null) {
            handleCallback(update);
        }
    }

    private void handleMessage(Update update) {
        Message message = update.message();
        Long chatId = message.chat().id();
        String text = message.text();

        // Проверка на продление пропуска
        if (extendStates.containsKey(chatId)) {
            ExtendPassState state = extendStates.get(chatId);
            switch (state.stage) {
                case WAITING_DATE:
                    try {
                        LocalDate date = LocalDate.parse(text);
                        state.newValidUntil = date;
                        state.stage = ExtendStage.WAITING_CODE;
                        bot.execute(new SendMessage(chatId, "Введите дополнительный 6-значный код продления:"));

                    } catch (Exception e) {
                        bot.execute(new SendMessage(chatId, "Неверный формат даты. Введите дату в формате ГГГГ-ММ-ДД:"));
                    }
                    return;

                case WAITING_CODE:
                    String code = text;

                    // Попытка продлить пропуск
                    try {
                        if (!code.matches("\\d{6}")) {
                            code = null;
                        }
                        PassService.extendPass(SessionManager.getSession(chatId), state.passId, state.newValidUntil, code);
                        bot.execute(new SendMessage(chatId, "✅ Пропуск успешно продлён!"));
                    } catch (Exception e) {
                        bot.execute(new SendMessage(chatId, "❌ Ошибка при продлении. Проверьте данные и попробуйте снова."));
                    }
                    extendStates.remove(chatId);
                    showPasses(chatId);

                    return;

                default:
                    // Если состояние неожиданное, очистим
                    extendStates.remove(chatId);
            }
        }

        // Основные команды
        if (text.equals("/start")) {
            showMenu(chatId);
            return;
        }

        if (text.equals("/login")) {
            userStages.put(chatId, Stage.AWAIT_LOGIN);
            bot.execute(new SendMessage(chatId, "Введите логин:").replyMarkup(removeKeyboard()));
            return;
        }

        if (text.equals("/logout")) {
            SessionManager.logout(chatId);
            userStages.put(chatId, Stage.NONE);
            bot.execute(new SendMessage(chatId, "Вы вышли из системы.").replyMarkup(unauthenticatedMenu()));
            return;
        }

        if (text.equals("/passes")) {
            if (!SessionManager.isLoggedIn(chatId)) {
                bot.execute(new SendMessage(chatId, "Сначала выполните вход."));
                return;
            }
            showPasses(chatId);
            return;
        }

        if (!SessionManager.isLoggedIn(chatId)) {
            Stage stage = userStages.getOrDefault(chatId, Stage.NONE);
            if (stage == Stage.AWAIT_LOGIN) {
                tempLoginBuffer.put(chatId, text);
                userStages.put(chatId, Stage.AWAIT_PASSWORD);
                bot.execute(new SendMessage(chatId, "Введите пароль:").replyMarkup(removeKeyboard()));
            } else if (stage == Stage.AWAIT_PASSWORD) {
                String login = tempLoginBuffer.get(chatId);
                String password = text;

                String session = AuthService.login(login, password);
                if (session != null) {
                    SessionManager.saveSession(chatId, session);
                    bot.execute(new SendMessage(chatId, "Успешный вход!").replyMarkup(authenticatedMenu()));
                    userStages.put(chatId, Stage.NONE);
                } else {
                    bot.execute(new SendMessage(chatId, "Ошибка входа. Попробуйте снова с /login").replyMarkup(unauthenticatedMenu()));
                    userStages.put(chatId, Stage.NONE);
                }
            } else {
                bot.execute(new SendMessage(chatId, "Сначала выполните вход с помощью /login").replyMarkup(unauthenticatedMenu()));
            }
        } else {
            // Пользователь авторизован и ввёл текст без команды
            bot.execute(new SendMessage(chatId, "Вы уже вошли. Используйте /logout для выхода.").replyMarkup(authenticatedMenu()));
        }
    }

    private void handleCallback(Update update) {
        Long chatId = update.callbackQuery().message().chat().id();
        String data = update.callbackQuery().data();

        if (data.startsWith("pass:")) {
            // Детали пропуска
            if (!SessionManager.isLoggedIn(chatId)) {
                bot.execute(new SendMessage(chatId, "Сначала выполните вход."));
                return;
            }
            long passId = Long.parseLong(data.substring(5));
            Pass pass = PassService.getPassById(passId, SessionManager.getSession(chatId));
            if (pass == null) {
                bot.execute(new SendMessage(chatId, "Пропуск не найден."));
                return;
            }

            System.out.println(pass.getAddress());
            showPassDetails(chatId, pass, pass.getAddress().getStreet() + pass.getAddress().getEntrance());
            return;
        }

        if (data.equals("back_to_passes")) {
            showPasses(chatId);
            return;
        }

        if (data.startsWith("extend_pass:")) {
            long passId = Long.parseLong(data.substring("extend_pass:".length()));

            Pass pass = PassService.getPassById(passId, SessionManager.getSession(chatId));
            if (pass == null) {
                bot.execute(new SendMessage(chatId, "Пропуск не найден."));
                return;
            }

            if (pass.isExpired() == -1) {
                bot.execute(new SendMessage(chatId, "❌ Этот пропуск деактивирован и не может быть продлён."));
                return;
            }

            ExtendPassState state = new ExtendPassState();
            state.passId = passId;
            state.stage = ExtendStage.WAITING_DATE;
            extendStates.put(chatId, state);
            bot.execute(new SendMessage(chatId, "Введите новую дату окончания пропуска (в формате ГГГГ-ММ-ДД):"));
            return;
        }
    }

    private void showPassDetails(Long chatId, Pass pass, String address) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Информация о пропуске\n\n");
        sb.append("ID: ").append(pass.getId()).append("\n");
        sb.append("Название: ").append(pass.getName()).append("\n");
        sb.append("Действителен до: ").append(pass.getLimitation()).append("\n");
        sb.append("Статус: ").append(getStatusText(pass)).append("\n");
        sb.append("Адрес: ").append(address).append("\n");

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(new InlineKeyboardButton("⬅️ Назад").callbackData("back_to_passes"));

        // Добавляем кнопку "Продлить" только если пропуск не деактивирован
        if (pass.isExpired() != -1) {
            buttons.add(new InlineKeyboardButton("🔄 Продлить").callbackData("extend_pass:" + pass.getId()));
        }

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(buttons.toArray(new InlineKeyboardButton[0]));

        bot.execute(new SendMessage(chatId, sb.toString()).replyMarkup(keyboard));
    }

    private void showPasses(Long chatId) {
        String session = SessionManager.getSession(chatId);
        List<Pass> passes = PassService.getAllPasses(session);

        List<InlineKeyboardButton[]> rows = new ArrayList<>();
        for (Pass pass : passes) {
            String status = getStatusText(pass);

            String textBtn = pass.getId() + " | " + pass.getName() + " | " + status;
            InlineKeyboardButton button = new InlineKeyboardButton(textBtn)
                    .callbackData("pass:" + pass.getId());
            rows.add(new InlineKeyboardButton[]{button});
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows.toArray(new InlineKeyboardButton[0][]));
        bot.execute(new SendMessage(chatId, "Ваши пропуска:").replyMarkup(markup));
    }


    private void showMenu(Long chatId) {
        if (SessionManager.isLoggedIn(chatId)) {
            bot.execute(new SendMessage(chatId, "Выберите команду:").replyMarkup(authenticatedMenu()));
        } else {
            bot.execute(new SendMessage(chatId, "Выберите команду:").replyMarkup(unauthenticatedMenu()));
        }
    }

    private ReplyKeyboardMarkup unauthenticatedMenu() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton[]{new KeyboardButton("/login")}
        ).resizeKeyboard(true);
    }

    private ReplyKeyboardMarkup authenticatedMenu() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton[]{new KeyboardButton("/logout"), new KeyboardButton("/passes")}
        ).resizeKeyboard(true);
    }

    private ReplyKeyboardRemove removeKeyboard() {
        return new ReplyKeyboardRemove();
    }

    private String getStatusText(Pass pass) {
        Context context = new Context();
        if (pass.isExpired() == -1) {
            context.setStrategy(new StrategyDeactivated());
        } else if (pass.isExpired() == 1) {
            context.setStrategy(new StrategyEnded());
        } else {
            context.setStrategy(new StrategyNotEnded());
        }
        return context.showMessage(List.of(pass));
    }
}