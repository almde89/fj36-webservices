package br.com.caelum.jaxb;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.math.BigDecimal;

public class TesteMarshalJson {

    public static void main(final String... args) throws Exception {
        final Categoria categoria = new Categoria();
        categoria.setNome("Nome");

        final Livro livro = new Livro();
        livro.setCodigo("ARQ");
        livro.setValor(new BigDecimal("29.00"));
        livro.setNomeAutor("Autor");
        livro.setTitulo("Título");
        livro.setCategoria(categoria);

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("livro.json"), livro);
    }

}
