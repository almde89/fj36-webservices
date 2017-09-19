package br.com.caelum.estoque.ws;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
@Stateless
public class EstoqueWS {

	public EstoqueWS() {
		repositorio.put("ARQ", new ItemEstoque("ARQ", 2));
		repositorio.put("SOA", new ItemEstoque("SOA", 5));
		repositorio.put("TDD", new ItemEstoque("TDD", 1));
		repositorio.put("RES", new ItemEstoque("RES", 2));
		repositorio.put("LOG", new ItemEstoque("LOG", 4));
		repositorio.put("WEB", new ItemEstoque("WEB", 1));
	}
	
	@WebMethod
	public ItemEstoque getQuantidade(final String codigo) {
		return repositorio.get(codigo);
	}
	
	private Map<String, ItemEstoque> repositorio = new HashMap<>();
	
}