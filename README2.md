# Sistema de Gerenciamento de Alunos - Mapeamento dos Requisitos

Este documento mapeia especificamente como os requisitos do TDE são atendidos no código do projeto.

## Requisitos do TDE

### 1. "Elaborar um sistema em Java (estudantes definem o tema do sistema)"

**Tema definido:** Sistema de Gerenciamento de Alunos (CRUD completo)

**Localização no código:**
- **Tema:** Gerenciamento de alunos com operações CRUD
- **Arquivo principal:** `src/Main.java` - Ponto de entrada do sistema Java
- **Modelo de dados:** `src/model/Aluno.java` - Define a entidade Aluno

### 2. "com pelo menos 3 páginas com interface gráfica em web (HTML, CSS e JavaScript com API fetch para comunicação)"

**Páginas implementadas (4 páginas no total):**

#### Página 1: `src/view/index.html` - Página Inicial
- **Interface gráfica:** HTML5 + CSS3 responsivo
- **JavaScript:** `src/view/script.js` - SistemaAlunos() (objeto principal)
- **API fetch:** Não utiliza fetch nesta página (apenas navegação)

#### Página 2: `src/view/cadastro.html` - Cadastro de Alunos
- **Interface gráfica:** Formulário HTML com campos nome, email, curso
- **JavaScript:** `sistema.cadastrarNovoAluno()` - método que usa fetch
- **API fetch:**
```javascript
fetch(self.urlApi + '/alunos/cadastrar', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(aluno)
})
```

#### Página 3: `src/view/lista.html` - Lista de Alunos
- **Interface gráfica:** Tabela HTML dinâmica
- **JavaScript:** `sistema.carregarLista()` e `sistema.mostrarAlunos()` - usam fetch
- **API fetch:**
```javascript
fetch(self.urlApi + '/alunos')
.then(function(resposta) { return resposta.json(); })
.then(function(alunos) { self.mostrarAlunos(alunos); });
```

#### Página 4: `src/view/atualizar.html` - Atualização de Alunos
- **Interface gráfica:** Formulário de busca + formulário de atualização
- **JavaScript:** `sistema.buscarPorId()` e `sistema.atualizarDados()` - usam fetch
- **API fetch:**
```javascript
// Busca
fetch(self.urlApi + '/alunos/buscar/' + id)
// Atualização
fetch(self.urlApi + '/alunos/atualizar', {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(alunoAtualizado)
})
```

**Comunicação API:** Todas as páginas usam `API fetch` para comunicação assíncrona com o backend Java.

### 3. "O Java deverá ter sua implementação em um servidor"

**Servidor Java implementado:**
- **Arquivo:** `src/Main.java`
- **Biblioteca:** `com.sun.net.httpserver.HttpServer` (biblioteca própria do Java - não externa)
- **Implementação:**
```java
HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
// Registra contextos/rotas
servidor.createContext("/api/alunos", new ListarAlunosController(alunoController));
servidor.createContext("/api/alunos/cadastrar", new CadastrarAlunoController(alunoController));
// ... outros contextos
servidor.start();
```

**Controllers que implementam o servidor:**
- `src/controller/ListarAlunosController.java`
- `src/controller/CadastrarAlunoController.java`
- `src/controller/BuscarAlunoController.java`
- `src/controller/AtualizarAlunoController.java`
- `src/controller/ExcluirAlunoController.java`

Cada controller implementa `HttpHandler` e sobrescreve `handle(HttpExchange exchange)`.

### 4. "persistir os dados em banco de dados relacional e SGBD MySQL"

**Persistência MySQL implementada:**

#### Conexão com MySQL:
- **Arquivo:** `src/dao/Conexao.java`
- **Padrão:** Singleton para conexão única
- **Driver:** `com.mysql.cj.jdbc.Driver`
- **Configuração:**
```java
usuario_mysql = "root";
senha_mysql = "PUC@1234";
con_banco = "jdbc:mysql://127.0.0.1:3306/?useSSL=false";
```

#### Criação automática do banco:
```java
stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS escola");
stmt.executeUpdate("USE escola");
stmt.executeUpdate("CREATE TABLE IF NOT EXISTS alunos (" +
    "id INT AUTO_INCREMENT PRIMARY KEY, " +
    "nome VARCHAR(100) NOT NULL, " +
    "email VARCHAR(100) UNIQUE NOT NULL, " +
    "curso VARCHAR(50) NOT NULL" +
    ")");
```

#### Operações CRUD:
- **Arquivo:** `src/dao/AlunoDAO.java`
- **Métodos:**
  - `insereAluno()` - INSERT
  - `listarAlunos()` - SELECT
  - `buscarAlunoPorId()` - SELECT WHERE
  - `atualizarAluno()` - UPDATE
  - `excluirAluno()` - DELETE

#### Uso de PreparedStatement (segurança):
```java
this.query = "INSERT INTO alunos (nome, email, curso) VALUES (?, ?, ?)";
this.ps = this.conexao.getConexao().prepareStatement(this.query);
this.ps.setString(1, aluno.getNome());
this.ps.setString(2, aluno.getEmail());
this.ps.setString(3, aluno.getCurso());
```

### 5. "Não poderá usar Spring/Spring Boot"

**Confirmado:** O projeto NÃO usa Spring/Spring Boot.

**Tecnologias utilizadas:**
- **Servidor:** `com.sun.net.httpserver.HttpServer` (nativo do Java)
- **JSON:** Jackson (biblioteca externa, mas permitida)
- **Banco:** JDBC direto com MySQL Connector/J
- **Frontend:** HTML/CSS/JavaScript puro (vanilla)

## Arquitetura Geral

```
Cliente (Browser)
    ↓ HTTP requests (fetch API)
Servidor Java (HttpServer)
    ↓ Controllers (HttpHandler)
Lógica de Negócio (AlunoController)
    ↓ DAO (Data Access Object)
Banco MySQL (JDBC)

Bibliotecas (lib/):
- jackson-core-2.15.2.jar
- jackson-databind-2.15.2.jar
- jackson-annotations-2.15.2.jar
- mysql-connector-j-8.0.33.jar
```

## Como Executar

1. **Compilar:**
```bash
javac -cp "lib/*" src/Main.java src/model/*.java src/dao/*.java src/controller/*.java
```

2. **Executar:**
```bash
java -cp "lib/*:src" Main
```

3. **Acessar:** `http://localhost:8080`

## Endpoints da API REST

| Método | Endpoint | Controller | Descrição |
|--------|----------|------------|-----------|
| GET | `/api/alunos` | ListarAlunosController | Lista todos |
| POST | `/api/alunos/cadastrar` | CadastrarAlunoController | Cadastra novo |
| GET | `/api/alunos/buscar/{id}` | BuscarAlunoController | Busca por ID |
| PUT | `/api/alunos/atualizar` | AtualizarAlunoController | Atualiza aluno |
| DELETE | `/api/alunos/excluir/{id}` | ExcluirAlunoController | Exclui aluno |

## Equipe

- **Karen & Breno** (2 estudantes)