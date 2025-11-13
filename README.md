# Sistema de Gerenciamento de Alunos

Este projeto é uma aplicação web completa para gerenciamento de alunos, desenvolvida em Java puro (sem frameworks externos como Spring). Ele implementa uma arquitetura RESTful API com interface web responsiva, utilizando um servidor HTTP embutido no Java, banco de dados MySQL e manipulação automática de JSON.

## O que o código faz

O sistema permite realizar operações CRUD (Create, Read, Update, Delete) em registros de alunos. Cada aluno possui:
- ID (inteiro, chave primária, auto-incremento)
- Nome (string, obrigatório)
- Email (string, único, obrigatório)
- Curso (string, obrigatório)

A aplicação oferece uma interface web responsiva onde é possível:
- Cadastrar novos alunos
- Listar todos os alunos cadastrados em uma tabela
- Buscar um aluno específico por ID para atualização
- Atualizar dados de um aluno existente
- Excluir alunos do sistema

## Arquitetura e Estrutura do Projeto

O projeto segue o padrão arquitetural MVC (Model-View-Controller) com camadas adicionais:

```
src/
├── Main.java                 # Ponto de entrada, configura servidor HTTP
├── model/
│   └── Aluno.java           # Modelo de dados (entidade Aluno)
├── dao/
│   ├── Conexao.java         # Singleton para conexão com MySQL
│   └── AlunoDAO.java       # Data Access Object para operações no banco
├── controller/
│   ├── AlunoController.java # Lógica de negócio e conversão JSON
│   ├── ListarAlunosController.java
│   ├── CadastrarAlunoController.java
│   ├── BuscarAlunoController.java
│   ├── AtualizarAlunoController.java
│   └── ExcluirAlunoController.java
└── view/
    ├── index.html           # Página inicial
    ├── cadastro.html        # Formulário de cadastro
    ├── lista.html           # Tabela com lista de alunos
    ├── atualizar.html       # Formulário de atualização
    ├── style.css            # Estilos CSS responsivos
    └── script.js            # JavaScript para interações AJAX

lib/
├── jackson-annotations-2.15.2.jar
├── jackson-core-2.15.2.jar
├── jackson-databind-2.15.2.jar
└── mysql-connector-j-8.0.33.jar  
```

## Como funciona por trás das bibliotecas

### 1. Servidor HTTP (`com.sun.net.httpserver.HttpServer`)

O Java fornece uma implementação básica de servidor HTTP através do pacote `com.sun.net.httpserver`. Este não é um servidor web completo como Tomcat ou Jetty, mas sim uma API simples para criar servidores HTTP leves.

**Como funciona:**
- `HttpServer.create(new InetSocketAddress(8080), 0)` cria um servidor na porta 8080
- `servidor.createContext("/caminho", handler)` registra um `HttpHandler` para processar requisições em determinado caminho
- Cada controller implementa `HttpHandler` e sobrescreve o método `handle(HttpExchange exchange)`

**Processamento de uma requisição:**
1. Cliente faz requisição HTTP (GET, POST, PUT, DELETE)
2. Servidor recebe via `HttpExchange` (representa a troca HTTP)
3. Handler processa a requisição baseada no método HTTP
4. Handler envia resposta com `exchange.sendResponseHeaders(codigo, tamanho)`
5. Dados são escritos no `OutputStream` do response

**Arquitetura em Detalhes:**

**HttpServer (Servidor Principal):**
```java
HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
```
- Cria um servidor HTTP na porta 8080
- O segundo parâmetro (0) é o tamanho da fila de conexões pendentes
- Retorna uma instância de `HttpServer` pronta para configuração

**Contextos e Handlers:**
```java
servidor.createContext("/api/alunos", new ListarAlunosController(alunoController));
```
- Registra um "contexto" (rota) no servidor
- Quando uma requisição chega para `/api/alunos`, o `ListarAlunosController` é chamado
- Cada contexto tem seu próprio `HttpHandler`

**HttpExchange (Troca HTTP):**
```java
@Override
public void handle(HttpExchange exchange) throws IOException {
    // exchange contém toda a informação da requisição e resposta
}
```
- `HttpExchange` representa uma única troca HTTP (request/response)
- Contém métodos para acessar headers, corpo, método HTTP, URI, etc.
- Permite enviar resposta através de `sendResponseHeaders()` e `getResponseBody()`

**Fluxo Completo de uma Requisição:**

1. **Cliente envia requisição:**
   ```
   GET /api/alunos HTTP/1.1
   Host: localhost:8080
   User-Agent: Mozilla/5.0
   ```

2. **Servidor recebe e roteia:**
   - `HttpServer` aceita a conexão TCP
   - Identifica a rota (`/api/alunos`)
   - Cria `HttpExchange` com dados da requisição
   - Chama o handler apropriado (`ListarAlunosController.handle()`)

3. **Handler processa:**
   ```java
   if ("GET".equals(exchange.getRequestMethod())) {
       // Processa GET
       var alunos = alunoController.getAlunoDAO().listarAlunos();
       String resposta = alunoController.converterAlunosParaJson(alunos);
   }
   ```

4. **Handler responde:**
   ```java
   exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
   exchange.sendResponseHeaders(200, resposta.getBytes("UTF-8").length);

   OutputStream saida = exchange.getResponseBody();
   saida.write(resposta.getBytes("UTF-8"));
   saida.close();
   ```

**Vantagens desta arquitetura:**
- **Leve:** Não precisa de container web (como Tomcat)
- **Controle total:** Acesso direto a todos os aspectos HTTP
- **Thread-safe:** Cada requisição roda em sua própria thread
- **Simples:** API direta sem camadas de abstração

**Limitações:**
- **Manual:** Tudo é feito manualmente (headers, encoding, etc.)
- **Básico:** Não tem recursos avançados como sessions, cookies automáticos
- **Single-threaded por requisição:** Não há pool de threads configurável

### 2. Manipulação de JSON (Jackson)

O projeto usa a **biblioteca Jackson** para conversão automática entre JSON e objetos Java, tornando o código muito mais simples e robusto comparado à implementação manual anterior.

#### Conversão com Jackson (Nova Implementação)

```java
public String converterAlunosParaJson(ArrayList<Aluno> alunos) {
    try {
        return objectMapper.writeValueAsString(alunos);
    } catch (IOException e) {
        e.printStackTrace();
        return "[]";
    }
}

public Aluno converterJsonParaAluno(String json) {
    try {
        return objectMapper.readValue(json, Aluno.class);
    } catch (IOException e) {
        e.printStackTrace();
        return new Aluno();
    }
}
```

**Vantagens da abordagem Jackson:**
- Conversão automática e confiável
- Trata tipos complexos (datas, objetos aninhados)
- Escape automático de caracteres especiais
- Código muito mais limpo e legível
- Menos propenso a bugs

### 3. Manipulação de Bytes e Encoding

Em aplicações web, todos os dados trafegam como bytes através da rede. O Java força a consciência disso através de `InputStream` e `OutputStream`, exigindo conversões explícitas entre strings e bytes.

#### Leitura do corpo da requisição (POST/PUT):

```java
String corpoRequisicao = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
```

**O que acontece:**
1. `exchange.getRequestBody()` retorna um `InputStream` contendo os dados brutos da requisição
2. `readAllBytes()` lê todos os bytes do stream em um array de bytes
3. `new String(bytes, "UTF-8")` decodifica os bytes para string usando UTF-8

**Por que isso é necessário:**
- HTTP transmite tudo como bytes (protocolo de rede)
- Sem conversão explícita, você teria dados binários incompreensíveis
- UTF-8 garante que caracteres especiais sejam interpretados corretamente

#### Escrita da resposta:

```java
exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
exchange.sendResponseHeaders(200, resposta.getBytes("UTF-8").length);

OutputStream saida = exchange.getResponseBody();
saida.write(resposta.getBytes("UTF-8"));
saida.close();
```

**O que acontece:**
1. String é codificada para bytes usando `getBytes("UTF-8")`
2. Headers HTTP são enviados primeiro (código 200, Content-Type, Content-Length)
3. Bytes são escritos no `OutputStream` da resposta
4. Stream é fechado para liberar recursos e sinalizar fim da resposta

**Sequência crítica:**
- Headers **devem** ser enviados antes do corpo
- Content-Length deve ser exato (número de bytes, não caracteres)
- Stream deve ser fechado ou a resposta ficará "pendurada"

**Problemas comuns sem UTF-8:**
- Caracteres acentuados (ç, ã, é) viram símbolos estranhos
- Emojis e caracteres especiais são corrompidos
- Dados JSON ficam inválidos no cliente

**Jackson vs Manipulação Manual:**
- **Jackson:** Trata encoding automaticamente
- **Manual:** Você controla tudo, mas deve especificar UTF-8 sempre

### 4. Banco de Dados (JDBC + MySQL)

#### Conexão Singleton (`Conexao.java`)

```java
private Conexao(){
    usuario_mysql = "root";
    senha_mysql = "PUC@1234";
    con_banco = "jdbc:mysql://127.0.0.1:3306/?useSSL=false";

    try{
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = (Connection) DriverManager.getConnection(con_banco, usuario_mysql, senha_mysql);
        java.sql.Statement stmt = conn.createStatement();
        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS escola");
        stmt.executeUpdate("USE escola");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS alunos (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "nome VARCHAR(100) NOT NULL, " +
            "email VARCHAR(100) UNIQUE NOT NULL, " +
            "curso VARCHAR(50) NOT NULL" +
            ")");
        stmt.close();
    }
    catch(Exception e){
        e.printStackTrace();
    }
}
```

**Padrão Singleton:** Garante uma única conexão compartilhada, evitando overhead de múltiplas conexões.

**Criação automática do banco e tabela:**
- `CREATE DATABASE IF NOT EXISTS escola` - Cria o banco se não existir
- `CREATE TABLE IF NOT EXISTS alunos` - Cria a tabela com estrutura completa
- Constraints: NOT NULL, UNIQUE, AUTO_INCREMENT

#### Operações CRUD (`AlunoDAO.java`)

Usa `PreparedStatement` para prevenir SQL Injection:

```java
public boolean insereAluno(Aluno aluno) {
    try {
        this.query = "INSERT INTO alunos (nome, email, curso) VALUES (?, ?, ?)";
        this.ps = this.conexao.getConexao().prepareStatement(this.query);
        this.ps.setString(1, aluno.getNome());
        this.ps.setString(2, aluno.getEmail());
        this.ps.setString(3, aluno.getCurso());
        int linhasAfetadas = this.ps.executeUpdate();
        this.ps.close();
        return linhasAfetadas > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

**Vantagens do PreparedStatement:**
- Parâmetros são escapados automaticamente
- Melhor performance para queries executadas múltiplas vezes
- Proteção contra SQL Injection

### 5. Interface Web (HTML/CSS/JavaScript)

#### AJAX com Fetch API (`script.js`)

```javascript
fetch(self.urlApi + '/alunos/cadastrar', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json'
    },
    body: JSON.stringify(aluno)
})
.then(function(resposta) {
    return resposta.json();
})
.then(function(resultado) {
    // Processa resultado
});
```

**Como funciona:**
1. `fetch()` faz requisição HTTP assíncrona
2. Headers especificam tipo de conteúdo (JSON)
3. Corpo da requisição é convertido para JSON string
4. Resposta é processada como JSON
5. Interface é atualizada sem recarregar a página

## Pré-requisitos

- Java 11 ou superior
- MySQL Server
- JDBC Driver MySQL (incluído em `lib/`)

## Configuração do Banco de Dados

O banco de dados é criado automaticamente pelo código. Basta ter o MySQL Server rodando.

**Configuração da conexão (Conexao.java):**
- Host: 127.0.0.1 (localhost)
- Porta: 3306 (padrão MySQL)
- Usuário: root
- Senha: PUC@1234
- Banco: escola (criado automaticamente)

## Como executar

1. Compilar o projeto:
```bash
javac -cp "lib/*" src/Main.java src/model/*.java src/dao/*.java src/controller/*.java
```

2. Executar:
```bash
java -cp "lib/*:src" Main
```

3. Acessar no navegador: `http://localhost:8080`

## Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/alunos` | Lista todos os alunos |
| POST | `/api/alunos/cadastrar` | Cadastra novo aluno |
| GET | `/api/alunos/buscar/{id}` | Busca aluno por ID |
| PUT | `/api/alunos/atualizar` | Atualiza aluno |
| DELETE | `/api/alunos/excluir/{id}` | Exclui aluno |

## Exemplos de Uso da API

### Cadastrar um novo aluno
```bash
curl -X POST http://localhost:8080/api/alunos/cadastrar \
  -H "Content-Type: application/json" \
  -d '{"nome": "João Silva", "email": "joao@email.com", "curso": "Engenharia"}'
```

### Listar todos os alunos
```bash
curl http://localhost:8080/api/alunos
```

### Buscar aluno por ID
```bash
curl http://localhost:8080/api/alunos/buscar/1
```

### Atualizar aluno
```bash
curl -X PUT http://localhost:8080/api/alunos/atualizar \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "nome": "João Silva Atualizado", "email": "joao.atualizado@email.com", "curso": "Engenharia Civil"}'
```

### Excluir aluno
```bash
curl -X DELETE http://localhost:8080/api/alunos/excluir/1
```

## Interface Web

A aplicação oferece uma interface web responsiva acessível em `http://localhost:8080`:

- **Página Inicial (`/`)**: Apresentação do sistema e navegação para funcionalidades
- **Cadastro (`/cadastro.html`)**: Formulário para cadastrar novos alunos
- **Lista (`/lista.html`)**: Tabela com todos os alunos cadastrados, com opções de editar e excluir
- **Atualizar (`/atualizar.html`)**: Formulário para atualizar dados de um aluno existente

A interface utiliza JavaScript vanilla para fazer requisições AJAX à API, proporcionando uma experiência fluida sem recarregamento de página.

## Tecnologias Utilizadas

- **Java 11+**: Linguagem principal
- **HttpServer**: Servidor HTTP embutido
- **Jackson**: Biblioteca para manipulação de JSON (conversão automática)
- **MySQL**: Banco de dados relacional
- **JDBC**: API para acesso a bancos relacionais
- **HTML5/CSS3**: Interface web
- **JavaScript (ES5)**: Interações AJAX
- **UTF-8**: Encoding para suporte a caracteres especiais

## Detalhes Técnicos das Bibliotecas

### Jackson (JSON Processing)

**Bibliotecas utilizadas:**
- `jackson-core-2.15.2.jar`: Núcleo do Jackson
- `jackson-databind-2.15.2.jar`: Vinculação de dados (conversão objeto-JSON)
- `jackson-annotations-2.15.2.jar`: Anotações para configuração

**Como funciona:**
```java
ObjectMapper objectMapper = new ObjectMapper();
String json = objectMapper.writeValueAsString(objeto);  // Java -> JSON
Objeto obj = objectMapper.readValue(json, Classe.class); // JSON -> Java
```

**Por que Jackson:**
- **Automático:** Não precisa escrever parsers manuais
- **Type-safe:** Conversões seguras com tipos Java
- **Flexível:** Suporte a anotações para customização
- **Performático:** Otimizado para alta performance
- **Robusto:** Trata casos edge automaticamente

### MySQL Connector/J

**Biblioteca:** `mysql-connector-j-8.0.32.jar` (ou 8.0.33.jar)

**Como funciona:**
```java
Class.forName("com.mysql.cj.jdbc.Driver");
Connection conn = DriverManager.getConnection(url, usuario, senha);
```

**Recursos utilizados:**
- **PreparedStatement:** Queries parametrizadas seguras
- **ResultSet:** Leitura de resultados de queries
- **AutoCommit:** Transações automáticas
- **Connection Pooling:** Não utilizado (singleton simples)

**Por que MySQL:**
- **Relacional:** Estrutura de dados organizada
- **SQL padrão:** Linguagem de consulta universal
- **Transacional:** ACID compliance
- **Escalável:** Suporte a grandes volumes de dados
- **Maduro:** Tecnologia consolidada no mercado

Este projeto serve como exemplo educacional completo de como construir uma aplicação web full-stack usando apenas recursos nativos do Java, demonstrando conceitos fundamentais de desenvolvimento web sem depender de frameworks externos.