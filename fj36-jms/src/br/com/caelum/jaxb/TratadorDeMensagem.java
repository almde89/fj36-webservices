package br.com.caelum.jaxb;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class TratadorDeMensagem implements MessageListener {
    @Override
    public void onMessage(Message message) {
        final TextMessage msg = (TextMessage) message;
        try {
            System.out.println("Mensagem: " + msg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
