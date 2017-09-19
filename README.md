# Aula 1

## Reconstruindo as técnicas de RPC

Fala desde pipes entre processos: uso de um arquivo como payload de comunicação entre os dois processos distintos. Um exemplo dessa técnica em Java é a Serialização do Objeto por meio da API da própria plataforma.

> Pipe não é RPC por não realizar chamadas interprocessos, apenas há um protocolo definido: leitura de um arquivo específico dentro do padrão pré-determinado.

### Serialização

- Escrita:

```java
try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("itens.bin"))) { // closable resources do java
    final ItemEstoque item1 = new ItemEstoque("ARQ", 2);
    final ItemEstoque item2 = new ItemEstoque("SOA", 3);
    
    final List<ItemEstoque> itens = new ArrayList<>();
    itens.add(item1);
    itens.add(item2);
    
    oos.writeObject(itens); // marshalling
}
```

- Leitura:
```java
try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("itens.bin"))) {
    final List<ItemEstoque> itens = (List<ItemEstoque>) ois.readObject(); // unmarshalling
    
    for (ItemEstoque item : itens) {
        System.out.println(item.getCodigo());
        System.out.println(item.getQuantidade());
        System.out.println("--------------");
    }
}
```

> **!Interessante:** [Unmarshalling - passagem de representação de transmissão para representação de "execução"](https://en.wikipedia.org/wiki/Unmarshalling)

### Remote Method Invocation (RMI)

Técnica de RPC para plataformas orientadas a objetos.

Técnica é marcada por stubs e skeletons, onde o primeiro é a representação cliente do objeto do servidor, equanto os skeletons são responsáveis pela orquestração das chamadas no host servidor. Cada objeto no servidor terá seu skeleton para o controle de chamadas.

> Os códigos aqui presentes são referentes ao projeto fj36-estoque.

- **Protocolo**
Utilizado pelos dois peers:

```java
/**
* Deve extender de Remote.
* @see Remote interface de notação. Apenas determina que essa interface será uma interface remota.
*/
public interface EstoqueRmi extends Remote {

	public ItemEstoque getItemEstoque(final String codigoProduto) throws RemoteException;
	
}
```

- **Servidor**
Classe de serviço:

```java
/**
* @see UnicastRemoteObject é uma abstração da implementação do padrão RMI para servidores.
* @see Serializable
*/
public class EstoqueService extends UnicastRemoteObject implements EstoqueRmi {

    /**
    * Deve sempre lançar RemoteException
    */
	public EstoqueService() throws RemoteException {
		super();
        /*
        Lembrando: qualquer valor aqui alterado após registro de uma instância não irá refletir
        em sucessiva chamada do cliente.
        */
		repositorio.put("ARQ", new ItemEstoque("ARQ", 6));
		repositorio.put("SOA", new ItemEstoque("SOA", 2));
		repositorio.put("TDD", new ItemEstoque("TDD", 3));
		repositorio.put("RES", new ItemEstoque("RES", 4));
		repositorio.put("LOG", new ItemEstoque("LOG", 3));
		repositorio.put("WEB", new ItemEstoque("WEB", 4));
	}

    /**
    * Deve sempre lançar RemoteException
    */
	@Override
	public ItemEstoque getItemEstoque(String codigoProduto)
			throws RemoteException {
		System.out.println("Verificando estoque do produto " + codigoProduto);
		return repositorio.get(codigoProduto);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, ItemEstoque> repositorio = new HashMap<>();

}
```

Para levantarmos e registrarmos uma instância servidora:
```java
LocateRegistry.createRegistry(1099);// levanta o registry - catálogo de objetos
Naming.rebind("/estoque", new EstoqueService()); // cataloga um objeto
System.out.println("Estoque registrado e rodando");
```

- **Cliente**
```java
final EstoqueRmi estoque = (EstoqueRmi) Naming.lookup("rmi://localhost:1099/estoque");
final ItemEstoque item = estoque.getItemEstoque("ARQ");

System.out.println("Quantidade disponível: " + item.getQuantidade());
System.out.println("Classe: " + estoque.toString()); // resultado: Proxy[EstoqueRmi,RemoteObjectInvocationHandler[UnicastRef [liveRef: [endpoint:[127.0.1.1:45555](remote),objID:[-312b0b4a:15e959d74ff:-7fff, -3252849428727332287]]]]]
```