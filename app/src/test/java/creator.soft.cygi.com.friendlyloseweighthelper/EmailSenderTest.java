package creator.soft.cygi.com.friendlyloseweighthelper;

import android.os.AsyncTask;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AsyncTask.class})
public class EmailSenderTest {


    public static final String PASSWORD_RESET = "Password Reset";
    public static final String MESSAGE_CONTENT = "Twoje haslo bedzie tutaj: ";
    public static final String EMAIL_SENDER_MAIL = "FrendlyLoseWeightHelper@gmail.com";
    EmailSender emailSender = new EmailSender();

    @Test
    public void checkPropertiesCorrect() {


        Properties propertiesExpected = new Properties();

        propertiesExpected.put("mail.smtp.host", "smtp.gmail.com");
        propertiesExpected.put("mail.smtp.socketFactory.port", "465");
        propertiesExpected.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        propertiesExpected.put("mail.smtp.auth", "true");
        propertiesExpected.put("mail.smtp.port", "465");


        PowerMockito.stub(PowerMockito.method(AsyncTask.class, "execute", Object.class)).toReturn(null);

        emailSender.setSubject(PASSWORD_RESET);
        emailSender.setMessageContent(MESSAGE_CONTENT);
        emailSender.setSendFromEmail(EMAIL_SENDER_MAIL);
        emailSender.setSendToEmail("jacek301@gmail.com");

        Properties propertiesReturned = emailSender.getProperties();

        Assert.assertEquals(propertiesExpected, propertiesReturned);


    }

    @Test(expected = RuntimeException.class)
    public void ifNoEmailToSendSetThenExceptions() {

        emailSender.setSubject(PASSWORD_RESET);
        emailSender.setMessageContent(MESSAGE_CONTENT);
        emailSender.setSendFromEmail(EMAIL_SENDER_MAIL);

        emailSender.sendEmail();
    }

    @Test(expected = RuntimeException.class)
    public void ifEmailToSendIsEmptyThenRuntTimeExceptions() {
        emailSender.setSubject(PASSWORD_RESET);
        emailSender.setMessageContent(MESSAGE_CONTENT);
        emailSender.setSendFromEmail(EMAIL_SENDER_MAIL);
        emailSender.setSendToEmail("");

        emailSender.sendEmail();

    }

}
