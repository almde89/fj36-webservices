# Aula 1

## Reconstruindo as técnicas de RPC

Fala desde pipes entre processos: uso de um arquivo como payload de comunicação entre os dois processos distintos. Um exemplo dessa técnica em Java é a Serialização do Objeto por meio da API da própria plataforma.

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