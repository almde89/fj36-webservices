package br.com.caelum.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class TesteGeraSchema {

    public static void main (String[] args) throws Exception {
        final JAXBContext context = JAXBContext.newInstance(Livro.class);
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                final StreamResult result = new StreamResult(new File("schema.xsd"));
                return result;
            }
        });
    }

}
