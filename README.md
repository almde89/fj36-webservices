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

# Aula 2

## XML

Falando um pouco sobre XML marshalling e unmarshalling. Sempre confundo esses dois, mas é fácil: é lembrar que a ideia do processo é a serialização, ou seja, o marshalling, que é a transformação da representação existente no sistema para um formato intermediário e padrão: XML, neste caso. Enquanto o unmarshalling é a "destruição" do XML e recuperação da representação em sistema.

> **!Uma coisa legal** Olhando a característica central de um XML, que deve ser bem formatado e ter um elemento raiz, vemos a representação de um WSDL respeitando todas essas características.
> `:envelop` como o elemento root, sua hierarquia representada de maneira bem formatada e respeitando toda a especificação XML. Apenas salientando o quanto essa linguagem de marcação é versátil e cresceu muito em uso nas últimas décadas.
> Vemos o JSON como a representação do século, e realmente é =) - em partes, mas o XML tem uso bastante relevante e facilitado, se pensarmos na infinidade de ferramentas existentes no mercado hoje em dia.

### JAX-B

Falando mais sobre as operações de marshalling e unmarshalling do JAX-B. Acho que isso tá bem tranquilo. Não tem tanto o que anotar.

## XML Schema

Coisas interessantes foram mencionadas. Ideias como: namespace, XSD e definição de Schemas para definição dos serviços. Na realidade, utilização de Schema para geração das tags válidas na definição de um WSDL.

Isso quer dizer que poderíamos criar um XSD com as informações das entidades e estruturas que queremos definir para os serviços. É um formalismo desnecessário, mas há aplicabilidade, uma vez que restringe ainda mais o que pode ser definido em um XML.

### Criação Schema

```xml
<xs:schema
    version="1.0"
    targetNamespace="http://www.caelum.com.br/fj36"> <!- Análogo ao pacote Java ->
```
> Imagino que o `targetNamespace` seja uma URL válida para a descoberta desses arquivo na Web. Uma vez que somos obrigados a definir uma URL de um Domínio da organização.

### Uso Schema

```xml
<caelum:livro xmlns:caelum="http://www.caelum.com.br/fj36">
```

> **!Importante** `caelum` após o `xmlns:`é o apelido dado ao namespace para faclidade de uso durante a especificação do XML.

### Gerando Schema from Java Class

```java
final JAXBContext context = JAXBContext.newInstance(Livro.class);
context.generateSchema(new SchemaOutputResolver() {
    @Override
    public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
        final StreamResult result = new StreamResult(new File("schema.xsd"));
        return result;
    }
});
```

XML Gerado:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xs:element name="livro" type="livro"/>

  <xs:complexType name="livro">
    <xs:sequence>
      <xs:element name="categoria" type="categoria" minOccurs="0"/>
      <xs:element name="codigo" type="xs:string" minOccurs="0"/>
      <xs:element name="nomeAutor" type="xs:string" minOccurs="0"/>
      <xs:element name="titulo" type="xs:string" minOccurs="0"/>
      <xs:element name="valor" type="xs:decimal" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="categoria">
    <xs:sequence>
      <xs:element name="nome" type="xs:string" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

### Mudando o tipo @XmlType

```java
@XmlType(name = "CAT") // mudando o tipo
public class Categoria {
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
```

XML Gerado:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xs:element name="livro" type="livro"/>

  <xs:complexType name="livro">
    <xs:sequence>
      <xs:element name="categoria" type="CAT" minOccurs="0"/>
      <xs:element name="codigo" type="xs:string" minOccurs="0"/>
      <xs:element name="nomeAutor" type="xs:string" minOccurs="0"/>
      <xs:element name="titulo" type="xs:string" minOccurs="0"/>
      <xs:element name="valor" type="xs:decimal" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="CAT">
    <xs:sequence>
      <xs:element name="nome" type="xs:string" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

### Gerando com targetNamespace

Criar na raiz do pacote o arquivo `package-info.java` com o conteúdo:

```java
@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.caelum.com.br/fj36")
package br.com.caelum.jaxb;
```

XML Gerado:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema version="1.0" targetNamespace="http://www.caelum.com.br/fj36" xmlns:tns="http://www.caelum.com.br/fj36" xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xs:element name="livro" type="tns:livro"/>

  <xs:complexType name="livro">
    <xs:sequence>
      <xs:element name="categoria" type="tns:CAT" minOccurs="0"/>
      <xs:element name="codigo" type="xs:string" minOccurs="0"/>
      <xs:element name="nomeAutor" type="xs:string" minOccurs="0"/>
      <xs:element name="titulo" type="xs:string" minOccurs="0"/>
      <xs:element name="valor" type="xs:decimal" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="CAT">
    <xs:sequence>
      <xs:element name="nome" type="xs:string" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>
</xs:schema>
```

### Gerando a classe com base em um XSD

```cmd

$ xjc schema.xsd -d src -p br.com.caelum.generated

parsing a schema...
compiling a schema...
br/com/caelum/generated/CAT.java
br/com/caelum/generated/Livro.java
br/com/caelum/generated/ObjectFactory.java
br/com/caelum/generated/package-info.java
```

Resultado:

br/com/caelum/generated/CAT.java
```java
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.19 at 02:17:21 PM BRT 
//


package br.com.caelum.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CAT complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CAT">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CAT", propOrder = {
    "nome"
})
public class CAT {

    protected String nome;

    /**
     * Gets the value of the nome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the value of the nome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNome(String value) {
        this.nome = value;
    }

}
```

br/com/caelum/generated/Livro.java
```java
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.19 at 02:17:21 PM BRT 
//


package br.com.caelum.generated;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for livro complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="livro">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="categoria" type="{http://www.caelum.com.br/fj36}CAT" minOccurs="0"/>
 *         &lt;element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nomeAutor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="titulo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "livro", propOrder = {
    "categoria",
    "codigo",
    "nomeAutor",
    "titulo",
    "valor"
})
public class Livro {

    protected CAT categoria;
    protected String codigo;
    protected String nomeAutor;
    protected String titulo;
    protected BigDecimal valor;

    /**
     * Gets the value of the categoria property.
     * 
     * @return
     *     possible object is
     *     {@link CAT }
     *     
     */
    public CAT getCategoria() {
        return categoria;
    }

    /**
     * Sets the value of the categoria property.
     * 
     * @param value
     *     allowed object is
     *     {@link CAT }
     *     
     */
    public void setCategoria(CAT value) {
        this.categoria = value;
    }

    /**
     * Gets the value of the codigo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Sets the value of the codigo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigo(String value) {
        this.codigo = value;
    }

    /**
     * Gets the value of the nomeAutor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeAutor() {
        return nomeAutor;
    }

    /**
     * Sets the value of the nomeAutor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeAutor(String value) {
        this.nomeAutor = value;
    }

    /**
     * Gets the value of the titulo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Sets the value of the titulo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitulo(String value) {
        this.titulo = value;
    }

    /**
     * Gets the value of the valor property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Sets the value of the valor property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValor(BigDecimal value) {
        this.valor = value;
    }

}
```

br/com/caelum/generated/ObjectFactory.java
```java
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.19 at 02:17:21 PM BRT 
//


package br.com.caelum.generated;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the br.com.caelum.generated package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Livro_QNAME = new QName("http://www.caelum.com.br/fj36", "livro");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: br.com.caelum.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Livro }
     * 
     */
    public Livro createLivro() {
        return new Livro();
    }

    /**
     * Create an instance of {@link CAT }
     * 
     */
    public CAT createCAT() {
        return new CAT();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Livro }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.caelum.com.br/fj36", name = "livro")
    public JAXBElement<Livro> createLivro(Livro value) {
        return new JAXBElement<Livro>(_Livro_QNAME, Livro.class, null, value);
    }

}
```

br/com/caelum/generated/package-info.java
```java
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.19 at 02:17:21 PM BRT 
//

@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.caelum.com.br/fj36")
package br.com.caelum.generated;
```

### Trabalhando com Validação do XML e o tratamento de erros

Quando utilizamos um XSD para criar a taxonomia de um XML, criamos a possibilidade de validação da formatação de um payload em XML. Segue código para execução da validação.

```java
final Livro livro = new Livro();
livro.setCodigo("arq");

final JAXBContext context = JAXBContext.newInstance(Livro.class);
final JAXBSource source = new JAXBSource(context, livro);

final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
final Schema schema = sf.newSchema(new File("schema.xsd"));

final Validator validator = schema.newValidator();
validator.setErrorHandler(new ValidationErrorHandler());
validator.validate(source);
```

Onde ValidationErrorHandler() é um tipo definido pelo usuário:

```java
public class ValidationErrorHandler implements ErrorHandler {
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.out.println(exception.getMessage());
    }
}
```

Dessa maneira, qualquer `warning`, `error` ou `fatalError` será delegado à instância de ErrorHandler para tratamento.

## JSON

É o que já estamos acostumados a utilizar. Não houve muita novidade.

> **!Importante** vide: JSONSchema funciona como um XSD para o JSON.

## Granularidade

Um dos assuntos mais importantes para a construção de serviços. Trata do tamanho da responsabilidade que cada serviço implementa. Além de um conceito "moral" - coesão, acoplamento e reusabilidade - há incluso um conceito técnico, que abarca a questão de latência, tempo necessário para completar todo o processo de troca de mensagem entre cliente e servidor. É a latência que muda a percepção de tempo de resposta de um aplicativo para um usuário.

É comum que os endpoints de fronteira, aqueles que se encontram no limite do escopo da resposabilidade do sistema servidor acomodem mais responsabilidade, ou seja, são menos agnósticos. Isso ocorre, pois o link existente entre cliente e servidor usualmente é mais lento do que a rede interna utilizada pelos sistemas servidores. Para minimizar o overhead de troca de mensagem via TCP, os serviços menos agnósticos instrumentam os mais agnósticos para, por fim, entregarem o serviço exigido por um cliente. Ou seja, todo o trade-off de overhead de troca de mensagem é delegado ao servidor, restando à comunicação de fronteira apenas resolver os problemas funcionais ligados às atividades que um cliente aciona.

Isso não quer dizer que um serviço é implementado sobre o prisma do cliente, mas, sim, que um processo corporativo pode ser identificado e melhor detalhado durante a implementação, por exemplo: o serviço de PROCESSO de um TRIBUNAL é mais agnóstico ao NEGÓCIO de tribunal. Entretanto, para realizar o PROTOCOLO de um PROCESSO é necessário várias etapas antes. Uma delas é a PREVENÇÃO. Podemos ter um serviço de atividade (identificado em BPMN) que remonta todas essas mais granulares operações. Isso é dizer que um cliente apenas chama um serviço de atividade PROTOCOLAR que aciona outros contratos de outros serviços para completar todos os requisitos exigidos ao PROTOCOLAR um novo PROCESSO.

## Pasta Theory of Software

Analogia às comidas italianas que utilizando massa. Iniciamos com o Spaghetti, vamos à Lasanha e terminamos com um Raviole.

### Spaghetti

O software é um emaranhado de resonsabilidades. Os 'concerns' se misturam.

### Lasanha

O software é dividido em camadas, layers e tiers, para a abstração de preocupações (concerns).

### Raviole

O software é autocontido em pequenas preocupações. Cada qual coesa o suficiente para o fornecimento de um serviço, de modo geral.

## JAX-RS

Algumas observações sobre a implementação de endpoints com JAX-RS:

1 - é ideal colocar no `@Consume` o maior número de `MediaType`'s o possível. Assim é possível aos clientes escolherem o formato de payload enviado ao servidor.
2 - é ideal que façamos a mesma coisa com o `@Produces`.
3 - ao dar um return em um `Response`, não colocar `.type(MediaType.XPTO)`, pois a negociação de conteúdo poderá acontecer também a nível de retorno da chamada dos endpoints.  

## HATE O-A-S

Pronuncia-se hateious, como em hideous. Utiliza links como motor da passagem do estado da representação. Isso quer dizer que toda reposta de um endpoint (recurso - substantivo do REST) será também identificada por seus diversos hyperlinks, ou apenas links.

Dessa maneira representamos nosso modelo juntamente suas relações com outros recursos e "operações".

Exemplo:

```javascript
// GET http://localhost:8080/fj36_webservice/pagamentos/1
{
    "id": 1,
    "status": "CRIADO",
    "valor": 10,
    "links": [
        {
            "rel": "confirmar",
            "uri": "/pagamentos/1",
            "method": "PUT"
        },
        {
            "rel": "cancelar",
            "uri": "/pagamentos/1",
            "method": "DELETE"
        }
    ]
}
```

Para a solicitação temos todos os links associados ao recurso `pagamentos/1`, que representa um pagamento de código 1. Sua representação é o fragmento:

```javascript
{
    "id": 1,
    "status": "CRIADO",
    "valor": 10
}
```

Enquanto o restante, mesmo que dentro da representação JSON, simboliza seus vínculos a outros Recursos ou Verbos (web methods). Nesse caso temos a relação entre os Verbos PUT e DELETE do mesmo recurso.

Uma maneira mais purista de realizar tão feito é identificar os links por intermédio de Web Links no cabeçalho da resposta. Seria algo no formato:

`Link: <http://example.com/TheBook/chapter2>; rel="previous"; title="previous chapter"`

Sendo Link o nome do cabeçalho, a url é o href, o rel é a função do relacionamento e title um sumary sobre a relation.

> **!Importante** [RFC web linking](https://tools.ietf.org/html/rfc5988)