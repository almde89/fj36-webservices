package br.com.caelum.jaxb;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.math.BigDecimal;

public class TesteValidacao {

    public static void main(final String... args) throws Exception {
        final Livro livro = new Livro();
        livro.setCodigo("arq");
        livro.setValor(new BigDecimal("-3"));

        final JAXBContext context = JAXBContext.newInstance(Livro.class);
        final JAXBSource source = new JAXBSource(context, livro);

        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = sf.newSchema(new File("schema.xsd"));

        final Validator validator = schema.newValidator();
        validator.setErrorHandler(new ValidationErrorHandler());
        validator.validate(source);
        validator.validate(new StreamSource(new File("livro.xml")));
    }

}
