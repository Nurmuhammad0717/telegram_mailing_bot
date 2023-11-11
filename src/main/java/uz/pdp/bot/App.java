package uz.pdp.bot;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.MailBot;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

public class App {
    public static void main( String[] args ) throws TelegramApiException {


        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);

        MailBot bot = new MailBot();

        api.registerBot(bot);








    }
}
