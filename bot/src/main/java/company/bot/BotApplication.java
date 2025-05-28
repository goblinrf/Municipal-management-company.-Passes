package company.bot;


import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotApplication {
    public static void main(String[] args) {
        new MyTelegramBot().start();
    }
}
