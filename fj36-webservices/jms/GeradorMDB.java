package br.com.caelum.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/FILA.GERADOR"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class GeradorMDB implements MessageListener {
    @Override
    public void onMessage(Message message) {
        try {
            final TextMessage msg = (TextMessage) message;
            System.out.printf("Gerando ebooks para %s\n", msg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
