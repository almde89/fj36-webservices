package br.com.caelum.jaxb;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

public class RegistraTratadorNaFila {

    public static void main(String[] args) throws NamingException {
        final InitialContext ic = new InitialContext();
        final ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");
        final Queue queue = (Queue) ic.lookup("jms/FILA.GERADOR");
        try (JMSContext context = factory.createContext("jms", "jms2");
             final Scanner teclado = new Scanner(System.in)) {
            final JMSConsumer consumer = context.createConsumer(queue);
            consumer.setMessageListener(new TratadorDeMensagem());
            context.start();

            System.out.println("Tratador esperando as mensagens na fila JMS.");

            teclado.nextLine();
            context.stop();

        }
    }
}
