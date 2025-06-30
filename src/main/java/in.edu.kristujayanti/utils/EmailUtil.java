package in.edu.kristujayanti.utils;

import com.google.zxing.common.BitMatrix;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.mail.*;
import com.google.zxing.*;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Hashtable;
import io.vertx.core.AsyncResult;


public class EmailUtil {

    private static MailClient mailClient;

    public static void setupMailer(Vertx vertx) {
        MailConfig config = new MailConfig()
                .setHostname("smtp.gmail.com")
                .setPort(587)
                .setStarttls(StartTLSOptions.REQUIRED)
                .setUsername("jeswintom123@gmail.com")
                .setPassword("example")
                .setTrustAll(true);

        mailClient = MailClient.createShared(vertx, config, "emailClientPool");
    }

    public static void sendPasswordEmail(String to, String password) {
        send(to, "Your Event App Password", "Hello! Your password is: " + password);
    }

    public static void sendTokenEmail(String to, String token) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix matrix = new MultiFormatWriter().encode(
                    token, BarcodeFormat.QR_CODE, 250, 250, hints);
            MatrixToImageWriter.writeToStream(matrix, "PNG", stream);

            Buffer qrBuffer = Buffer.buffer(stream.toByteArray());

            MailAttachment attachment = MailAttachment.create()
                    .setData(qrBuffer)
                    .setName("ticket.png")
                    .setContentType("image/png")
                    .setDisposition("attachment");

            MailMessage message = new MailMessage()
                    .setFrom("jeswintom123@gmail.com")
                    .setTo(to)
                    .setSubject("Your Event Booking Token")
                    .setText("Thanks for booking!\nYour token: " + token + "\nAttached is your QR code.")
                    .setAttachment(Collections.singletonList(attachment));

            mailClient.sendMail(message, (AsyncResult<MailResult> result) -> {
                if (result.succeeded()) {
                    System.out.println("Email sent to " + to);
                } else {
                    System.out.println("Failed to send email:");
                    result.cause().printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void send(String to, String subject, String body) {
        MailMessage message = new MailMessage()
                .setFrom("jeswintom123@gmail.com")
                .setTo(to)
                .setSubject(subject)
                .setText(body);

        mailClient.sendMail(message, result -> {
            if (result.succeeded()) {
                System.out.println("Email sent to " + to);
            } else {
                System.out.println("Failed to send email:");
                result.cause().printStackTrace();
            }
        });
    }
}
