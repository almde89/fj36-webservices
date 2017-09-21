package br.com.caelum.jaxb;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

public class EnviarParaFila {

    public static void main(String[] args) throws NamingException {
        final InitialContext ic = new InitialContext();
        final ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");
        final Queue queue = (Queue) ic.lookup("jms/FILA.GERADOR");
        try (JMSContext context = factory.createContext("jms", "jms2")) {
            final JMSProducer prod = context.createProducer();
            final Scanner scan = new Scanner(System.in);
            while (scan.hasNextLine()) {
                final String line = scan.nextLine();
                prod.send(queue, line);
            }
        }
    }
}
