package br.com.caelum.jms;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/TOPICO.LIVRARIA"),
        @ActivationConfigProperty(propertyValue = "javax.jms.Topic", propertyName = "destinationType")
})
public class FinanceiroMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            final TextMessage msg = (TextMessage) message;
            System.out.printf("Gerando notas para %s\n", msg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
