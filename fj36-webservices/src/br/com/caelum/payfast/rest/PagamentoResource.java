package br.com.caelum.payfast.rest;

import br.com.caelum.payfast.modelo.Pagamento;
import br.com.caelum.payfast.modelo.Transacao;

import javax.ejb.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Path("/pagamentos")
@Singleton
public class PagamentoResource {

    public PagamentoResource() {
        final Pagamento pagamento = new Pagamento();
        pagamento.setId(idPagamento++);
        pagamento.setValor(BigDecimal.TEN);
        pagamento.comStatusCriado();
        repositorio.put(pagamento.getId(), pagamento);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Pagamento buscaPagamento(@PathParam("id") Integer id) {
        return repositorio.get(id);
    }

    /*
    Content negotiation: melhor deixar o máximo de representações para abarcar o maior número de clientes.
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response criarPagamento(final Transacao transacao) throws URISyntaxException {
        final Pagamento pagamento = new Pagamento();
        pagamento.setId(idPagamento++);
        pagamento.setValor(transacao.getValor());
        pagamento.comStatusCriado();

        repositorio.put(pagamento.getId(), pagamento);

        System.out.println("PAGAMENTO CRIADO " + pagamento);

        return Response.created(new URI("/pagamentos/" + pagamento.getId()))
                /* .type(MediaType.APPLICATION_JSON) melhor deixar sem essa configuração, pois o clinete faz a negociação
                 * do conteúdo da maneira que ele achar melhor. */
                .entity(pagamento)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Pagamento confirmarPagamento(@PathParam("id") Integer pagamentoId) {
        final Pagamento pagamento = repositorio.get(pagamentoId);
        pagamento.comStatusConfirmado();
        System.out.println("Pagamento confirmado: " + pagamento);
        return pagamento;
    }

    private Map<Integer, Pagamento> repositorio = new HashMap<>();
    private Integer idPagamento = 1;
}
