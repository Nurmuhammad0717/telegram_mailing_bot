package uz.pdp.bot;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailBot extends TelegramLongPollingBot {

    private  String email;
    Checker checker = new Checker();

    public MailBot() {
        super("6894811792:AAGuRGNao2t42NPJd5mU6Jz-Kn5FvCzAt9Q");
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            User from = message.getFrom();

            if (message.hasText()) {
                final String text = message.getText();

                if (text.equals("/start")) {
                    SendMessage sendMessage = new SendMessage(
                            message.getChatId().toString(),
                            "Hello " + from.getFirstName() +
                                    "\n Enter your e-mail address.");
                    Users.USER_STATE.put(message.getChatId(), UserState.EMAIL_REGISTER);
                    this.execute(sendMessage);

                } else if (Users.USER_STATE.get(message.getChatId()) == UserState.EMAIL_REGISTER || message.getText().equals("/new_message") ) {

                    if (checker.checkEmail(message.getText())) {

                        email = message.getText();
                        Users.USER_STATE.put(message.getChatId(), UserState.SENDING_RECIPIENT);
                        this.execute(new SendMessage(message.getChatId().toString(),
                                "Enter recipient's email address."));

                    } else {
                        this.execute(new SendMessage(message.getChatId().toString(),
                                "Please enter valid email address. \n Press /start please."));
                        return;
                    }


                } else if (Users.USER_STATE.get(message.getChatId()) == UserState.SENDING_RECIPIENT) {

                    MailingInfo mailingInfo = new MailingInfo();

                    if (checker.checkEmail(message.getText())) {

                        mailingInfo.setRecipient(message.getText());

                        Users.MAIL_INFOS.put(message.getChatId(), mailingInfo);

                        Users.USER_STATE.put(message.getChatId(), UserState.SENDING_SUBJECT);

                        this.execute(new SendMessage(message.getChatId().toString(),
                                "Enter the subject."));

                    } else {
                        this.execute(new SendMessage(message.getChatId().toString(),
                                "Please enter a valid email address."));
                        Users.USER_STATE.put(message.getChatId(), UserState.SENDING_RECIPIENT);
                    }

                } else if (Users.USER_STATE.get(message.getChatId()) == UserState.SENDING_SUBJECT) {

                    String subj = message.getText();
                    Users.MAIL_INFOS.get(message.getChatId()).setSubject(subj);
                    Users.USER_STATE.put(message.getChatId(), UserState.SENDING_MESSAGE);
                    this.execute(new SendMessage(message.getChatId().toString(),
                            "Enter the message."));

                } else if (Users.USER_STATE.get(message.getChatId()) == UserState.SENDING_MESSAGE) {
                    Users.MAIL_INFOS.get(message.getChatId()).setContent(message.getText());
                    Users.USER_STATE.put(message.getChatId(), UserState.MAILING_FINISHED);

                    MimeMessage transport = getTransport();
                    transport.setFrom(new InternetAddress(email));
                    transport.setRecipient(MimeMessage.RecipientType.TO,
                            new InternetAddress(Users.MAIL_INFOS.
                                    get(message.getChatId()).
                                    getRecipient()));
                    transport.setSubject(Users.MAIL_INFOS.get(message.getChatId()).getSubject());
                    transport.setText(Users.MAIL_INFOS.get(message.getChatId()).getContent());
                    Transport.send(transport);

                    this.execute(new SendMessage(message.getChatId().toString(),
                            "Your message is successfully sent to " +
                                    Users.MAIL_INFOS.get(message.getChatId()).getRecipient()));

                } else if (Users.USER_STATE.get(message.getChatId()) == UserState.MAILING_FINISHED) {
                    this.execute(new SendMessage(message.getChatId().toString(), "Enter /new_message to send email again"));
                }

            }
        }

    }

    public MimeMessage getTransport(){
        Properties props = new Properties();

        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smpt.starttls.enable", "true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new  PasswordAuthentication("545f2b4a73a6fb","8c33d675b89940");
            }
        };

        Session session = Session.getInstance( props, authenticator );

        return new MimeMessage(session);
    }

    @Override
    public String getBotUsername() {

        return "My_2nd_mailing_bot";
    }
}
