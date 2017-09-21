package br.com.caelum.jaxb;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

public class EnviarMensagemParaTopico {
    public static void main(String[] args) throws NamingException {
        final InitialContext ic = new InitialContext();
        final ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");
        final Topic queue = (Topic) ic.lookup("jms/TOPICO.LIVRARIA");
        try (JMSContext context = factory.createContext("jms", "jms2");
             final Scanner scan = new Scanner(System.in);) {
            final JMSProducer prod = context.createProducer();
            while (scan.hasNextLine()) {
                final String line = scan.nextLine();
                prod.send(queue, line);
            }
        }
    }
}
