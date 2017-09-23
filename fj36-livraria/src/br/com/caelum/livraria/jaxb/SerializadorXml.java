package br.com.caelum.livraria.jaxb;


import br.com.caelum.livraria.modelo.Pedido;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;

public class SerializadorXml {

	public String toXml(Pedido pedido) {
	    try {
	        final Marshaller marshaller = JAXBContext.newInstance(Pedido.class).createMarshaller();
	        final StringWriter writer = new StringWriter();
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        marshaller.marshal(pedido, writer);

	        return writer.toString();
        } catch (PropertyException e) {
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
