package br.com.caelum.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.FileOutputStream;
import java.math.BigDecimal;

public class TesteMarshal {

    public static void main(String[] args) throws Exception {
        final Livro livro = new Livro();
        livro.setCodigo("ARQ");
        livro.setTitulo("Arquitetura Java");
        livro.setNomeAutor("Paulo Silveira");
        livro.setValor(new BigDecimal("29.00"));

        final Categoria categoria = new Categoria();
        categoria.setNome("TI");
        livro.setCategoria(categoria);

        final JAXBContext context = JAXBContext.newInstance(Livro.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(livro, new FileOutputStream("livro.xml"));
    }
}
