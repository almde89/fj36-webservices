package br.com.caelum.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class TesteUnmarshal {

    public static void main (String[] args) throws Exception {
        final JAXBContext context = JAXBContext.newInstance(Livro.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();

        final Livro livro = (Livro) unmarshaller.unmarshal(new File("livro.xml"));
        System.out.println(livro.getTitulo());
        System.out.println(livro.getCategoria().getNome());
    }

}
