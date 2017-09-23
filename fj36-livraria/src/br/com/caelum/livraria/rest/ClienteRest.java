package br.com.caelum.livraria.rest;

import java.io.Serializable;

import br.com.caelum.livraria.modelo.Link;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;

import br.com.caelum.livraria.modelo.Pagamento;
import br.com.caelum.livraria.modelo.Transacao;

import javax.ws.rs.HEAD;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

@Component
@Scope("request")
public class ClienteRest implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String SERVER_URI = "http://localhost:8080/fj36_webservices";

	private static final String ENTRY_POINT = "/pagamentos";

	public Pagamento criarPagamento(Transacao transacao) {
		final Client client = ClientBuilder.newClient();
		final Pagamento resposta = client.target(SERVER_URI + ENTRY_POINT)
				.request().accept(MediaType.APPLICATION_JSON)
				.buildPost(Entity.json(transacao))
				.invoke(Pagamento.class);
		System.out.println("Pagamento cirado, id: " + resposta.getId());

		return resposta;
	}

	public Pagamento confirmarPagamento(Pagamento pagamento) {
		final Link linkConfirmar = pagamento.getLinkPeloRel("confirmar");
		final Client client = ClientBuilder.newClient();
		final Pagamento resposta = client.target(SERVER_URI + linkConfirmar.getUri())
				.request().accept(MediaType.APPLICATION_JSON)
				.build(linkConfirmar.getMethod())
				.invoke(Pagamento.class);

		return resposta;
	}

}
