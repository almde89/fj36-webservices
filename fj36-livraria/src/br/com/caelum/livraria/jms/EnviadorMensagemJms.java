package br.com.caelum.livraria.jms;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import br.com.caelum.livraria.jaxb.SerializadorXml;
import br.com.caelum.livraria.modelo.Pedido;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Topic;

@Component
@Lazy(true)
public class EnviadorMensagemJms implements Serializable {

	private static final long serialVersionUID = 1L;

	public void enviar(Pedido pedido) {
		System.out.println("JMS: Enviando pedido:" + pedido);

		try (final JMSContext context = factory.createContext("jms", "jms2")) {
			final JMSProducer producer = context.createProducer();
			producer.setProperty("formato", pedido.getFormato());
			final String xml = new SerializadorXml().toXml(pedido);
			producer.send(topic, xml);
		}
	}

	@Autowired
	private ConnectionFactory factory;

	@Autowired
	private Topic topic;
}
