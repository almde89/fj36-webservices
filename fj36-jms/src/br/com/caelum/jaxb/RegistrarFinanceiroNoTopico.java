package br.com.caelum.jaxb;

import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Scanner;

public class RegistrarFinanceiroNoTopico {
    public static void main (final String[] args) throws NamingException {
        final InitialContext ic = new InitialContext();
        final ConnectionFactory factory = (ConnectionFactory) ic.lookup("jms/RemoteConnectionFactory");
        final Topic topico = (Topic) ic.lookup("jms/TOPICO.LIVRARIA");
        try (JMSContext context = factory.createContext("jms", "jms2")) {
            context.setClientID("Financeiro");
            final JMSConsumer consumer = context.createDurableConsumer(topico, "AssinaturaNotas");
            consumer.setMessageListener(new TratadorDeMensagem());
            context.start();
            final Scanner teclado = new Scanner(System.in);
            System.out.println("Financeiro esperando as mensagem");
            System.out.println("Aperte enter para fechar a conexão");
            teclado.nextLine();
            teclado.close();
            context.stop();
        }
    }
}
