package br.com.caelum.livraria.modelo;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Set;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Pedido implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue 
	private Integer id;
	
	@Temporal(TemporalType.DATE)
	private Calendar data;

	@XmlElementWrapper(name= "itens")
	@XmlElement(name = "item")
	@OneToMany(cascade=CascadeType.PERSIST)
	private Set<ItemCompra> itens;

	@OneToOne(cascade=CascadeType.PERSIST)
	@JoinColumn(unique=true)
	private Pagamento pagamento;

	public void setItens(Set<ItemCompra> itens) {
		this.itens = itens;
	}

	public Set<ItemCompra> getItens() {
		return itens;
	}

	public String getFormato() {
		return this.temApenasLivrosImpressos() ? "impresso" : "ebook";
	}
	
	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	private boolean temApenasLivrosImpressos() {
		
		for (ItemCompra itemCompra : this.itens) {
			if(!itemCompra.isImpresso()) {
				return false;
			}
		}
		return true;
	}

	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}

	public String getStatus() {
		return this.pagamento == null ? "INDEFINIDO" : this.pagamento.getStatus();
	}


	@Override
	public String toString() {
		return "Pedido [id=" + id + ", itens=" + itens + ", pagamento=" + pagamento + "]";
	}
}
