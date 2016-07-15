package creator.soft.cygi.com.friendlyloseweighthelper.utility;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import creator.soft.cygi.com.friendlyloseweighthelper.R;

public class EmailSender extends AsyncTask<String, String, Void> {

    private final Properties properties = initializeProperties();
    private Session session = getDefaultSessionInstance();
    private Context context;

    private Properties initializeProperties() {

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        return props;
    }

    private String subject;
    private String sendToEmail;
    private String sendFromEmail;
    private String messageContent;



    private Session getDefaultSessionInstance() {

        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("FrendlyLoseWeightHelper@gmail.com", "frenDlyLama#347");
            }

        });

        return session;
    }

    public void sendEmail() {

        if(sendToEmail == null){
            throw  new RuntimeException("You must supply sender email");
        }

        if(sendToEmail.isEmpty()){
            throw new RuntimeException("You must supply where to send email");
        }


        this.execute();
    }


    @Override
    protected Void doInBackground(String... params) {

        try {
            MimeMessage message = new MimeMessage(session);
            message.setSender(new InternetAddress(sendFromEmail));
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(sendToEmail));
            message.setSubject(subject);
            DataHandler messageHandler = new DataHandler(new ByteArrayDataSource(messageContent.getBytes(),"text/plain"));
            message.setDataHandler(messageHandler);

            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Toast.makeText(context, R.string.email_wast_sent_info_toast, Toast.LENGTH_LONG).show();
    }
    public String getSendToEmail() {
        return sendToEmail;
    }

    public void setSendToEmail(String sendToEmail) {
        this.sendToEmail = sendToEmail;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendFromEmail() {
        return sendFromEmail;
    }

    public void setSendFromEmail(String sendFromEmail) {
        this.sendFromEmail = sendFromEmail;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
