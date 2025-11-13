# Sistema de Gerenciamento de Alunos - POO

Este projeto é uma aplicação web Java que implementa um sistema de gerenciamento de alunos usando arquitetura MVC (Model-View-Controller). O sistema utiliza o servidor HTTP integrado do JDK para fornecer uma API REST e uma interface web para operações CRUD (Create, Read, Update, Delete) de alunos.

## 📋 Visão Geral

O sistema permite:
- Cadastrar novos alunos
- Listar todos os alunos
- Buscar alunos por ID
- Atualizar informações de alunos
- Excluir alunos

## 🏗️ Arquitetura e Tecnologias

### Arquitetura MVC
O projeto segue o padrão arquitetural **Model-View-Controller**:

- **Model**: Representa os dados e regras de negócio
- **View**: Interface do usuário (HTML, CSS, JavaScript)
- **Controller**: Gerencia as requisições HTTP e coordena Model e View

### Tecnologias Principais

#### Java
- **Versão**: Java 8 ou superior (recomendado Java 14+ para features modernas)
- **Servidor HTTP**: `com.sun.net.httpserver.HttpServer` (integrado no JDK)
- **Banco de Dados**: MySQL via JDBC
- **Paradigma**: Programação Orientada a Objetos (POO)

#### HTTP Protocol
O sistema implementa um servidor HTTP básico que:
- Escuta na porta 8080
- Processa requisições GET, POST, PUT, DELETE
- Serve arquivos estáticos (HTML, CSS, JS)
- Fornece API REST JSON
- Suporta CORS para requisições do navegador

#### MySQL
- **Driver JDBC**: MySQL Connector/J (versões 8.0.32 e 8.0.33)
- **Banco**: `escola`
- **Tabela**: `alunos` com campos: `id`, `nome`, `email`, `curso`

## 📁 Estrutura do Projeto

```
POO/
├── .gitignore
├── POO.iml
├── data.txt
├── lib/
│   ├── mysql-connector-j-8.0.32.jar
│   └── mysql-connector-j-8.0.33.jar
└── src/
    ├── Main.java                           # Ponto de entrada da aplicação
    ├── README.md                           # Este arquivo
    ├── controller/
    │   ├── AlunoController.java            # Controller principal
    │   ├── ListarAlunosController.java     # Handler para GET /api/alunos
    │   ├── CadastrarAlunoController.java   # Handler para POST /api/alunos/cadastrar
    │   ├── AtualizarAlunoController.java   # Handler para PUT /api/alunos/atualizar
    │   ├── BuscarAlunoController.java      # Handler para GET /api/alunos/buscar/{id}
    │   └── ExcluirAlunoController.java     # Handler para DELETE /api/alunos/excluir/{id}
    ├── dao/
    │   ├── Conexao.java                    # Singleton para conexão MySQL
    │   └── AlunoDAO.java                   # Data Access Object para Aluno
    ├── model/
    │   └── Aluno.java                      # Modelo de dados Aluno
    └── view/
        ├── index.html                      # Página inicial
        ├── cadastro.html                   # Formulário de cadastro
        ├── lista.html                      # Lista de alunos
        ├── atualizar.html                  # Formulário de atualização
        ├── style.css                       # Estilos CSS
        └── script.js                       # JavaScript do frontend
```

## 🔧 Como Funciona o HTTP no Sistema

### Servidor HTTP
O `HttpServer` do JDK cria um servidor HTTP simples que:

1. **Inicialização**:
   ```java
   HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
   ```

2. **Contextos de Rota**:
   - `/api/alunos` → Listar alunos (GET)
   - `/api/alunos/cadastrar` → Cadastrar aluno (POST)
   - `/api/alunos/atualizar` → Atualizar aluno (PUT)
   - `/api/alunos/buscar/{id}` → Buscar aluno por ID (GET)
   - `/api/alunos/excluir/{id}` → Excluir aluno (DELETE)
   - `/` → Servir arquivos estáticos (GET)

3. **Tratamento de Requisições**:
   Cada controller implementa `HttpHandler` e sobrescreve o método `handle(HttpExchange exchange)`.

### Headers HTTP
- **Content-Type**: Define o tipo de conteúdo (application/json, text/html, etc.)
- **CORS Headers**: Permite requisições do navegador
- **Charset**: UTF-8 para suporte a caracteres especiais

### Métodos HTTP
- **GET**: Recupera dados (listar, buscar)
- **POST**: Cria novos recursos (cadastrar)
- **PUT**: Atualiza recursos existentes (atualizar)
- **DELETE**: Remove recursos (excluir)
- **OPTIONS**: Preflight CORS

## 🗄️ Como Funciona o Java no Sistema

### Programação Orientada a Objetos (POO)

#### Classes e Objetos
- **Aluno**: Classe modelo com encapsulamento (getters/setters)
- **AlunoDAO**: Classe para acesso a dados
- **Controllers**: Classes que herdam de `HttpHandler`

#### Padrões de Projeto
- **Singleton**: `Conexao.java` garante uma única conexão com o banco
- **DAO (Data Access Object)**: Separa lógica de acesso a dados
- **MVC**: Separação de responsabilidades

#### Tratamento de Exceções
- Try-catch em operações críticas
- Propagação de erros com `RuntimeException`
- Logs de erro no console

### JDBC (Java Database Connectivity)
1. **Carregamento do Driver**:
   ```java
   Class.forName("com.mysql.cj.jdbc.Driver");
   ```

2. **Conexão**:
   ```java
   Connection conn = DriverManager.getConnection(url, user, password);
   ```

3. **PreparedStatement**: Previne SQL injection
4. **ResultSet**: Processa resultados das consultas

## 🚀 Como Executar

### Pré-requisitos
- Java 8 ou superior instalado
- MySQL Server rodando
- Banco de dados `escola` criado
- Tabela `alunos` com estrutura:
  ```sql
  CREATE TABLE alunos (
      id INT AUTO_INCREMENT PRIMARY KEY,
      nome VARCHAR(255) NOT NULL,
      email VARCHAR(255),
      curso VARCHAR(255)
  );
  ```

### Compilação e Execução

1. **Compilar**:
   ```bash
   cd src
   javac -cp "../lib/*" Main.java controller/*.java dao/*.java model/*.java
   ```

2. **Executar**:
   ```bash
   java -cp ".:../lib/*" Main
   ```

3. **Acessar**:
   - Interface web: http://localhost:8080
   - API: http://localhost:8080/api/alunos

### Configuração do Banco
- **Host**: 127.0.0.1:3306
- **Usuário**: root
- **Senha**: (vazia)
- **Banco**: escola

## 📚 Bibliotecas e Dependências

### MySQL Connector/J
- **Localização**: `lib/mysql-connector-j-8.0.32.jar` e `lib/mysql-connector-j-8.0.33.jar`
- **Função**: Driver JDBC para conectar Java ao MySQL
- **Versão**: 8.0.32/8.0.33 (compatível com MySQL 8.x)

### JDK Integrado
- **HttpServer**: Servidor HTTP simples
- **JDBC**: API para acesso a bancos relacionais
- **NIO**: Para manipulação de arquivos (Files.readAllBytes)

## 🌐 API REST

### Endpoints

#### GET /api/alunos
Lista todos os alunos.
**Resposta**: `[{"id": 1, "nome": "João", "email": "joao@email.com", "curso": "Engenharia"}]`

#### POST /api/alunos/cadastrar
Cadastra um novo aluno.
**Corpo**: `{"nome": "João", "email": "joao@email.com", "curso": "Engenharia"}`
**Resposta**: `{"sucesso": true}`

#### PUT /api/alunos/atualizar
Atualiza um aluno existente.
**Corpo**: `{"id": 1, "nome": "João Silva", "email": "joao@email.com", "curso": "Engenharia"}`
**Resposta**: `{"sucesso": true}`

#### GET /api/alunos/buscar/{id}
Busca um aluno por ID.
**Resposta**: `{"id": 1, "nome": "João", "email": "joao@email.com", "curso": "Engenharia"}`

#### DELETE /api/alunos/excluir/{id}
Exclui um aluno por ID.
**Resposta**: `{"sucesso": true}`

## 🎨 Interface Web

### Tecnologias Frontend
- **HTML5**: Estrutura das páginas
- **CSS3**: Estilização responsiva
- **JavaScript (ES5)**: Interação com a API

### Funcionalidades
- Navegação entre páginas
- Formulários para CRUD
- Tabela para listagem
- Mensagens de feedback
- Validação básica

## 🔒 Segurança e Boas Práticas

### Implementadas
- PreparedStatement (prevenção SQL injection)
- CORS configurado
- Validação básica de entrada
- Tratamento de erros
- Encapsulamento de dados

### Melhorias Possíveis
- Autenticação/autorização
- Validação mais robusta
- Logs estruturados
- Configurações externalizadas
- Testes unitários

## 📝 Notas de Desenvolvimento

### Melhorias Recentes (por Breno)
- Remoção de campos não utilizados (data_nascimento)
- Tratamento de exceções aprimorado
- Suporte a CORS
- Encoding UTF-8 consistente
- Validação de entrada
- Escape de caracteres especiais em JSON
- Uso de switch expressions modernos

### Estrutura de Pacotes
- `model`: Classes de domínio
- `dao`: Acesso a dados
- `controller`: Lógica de controle HTTP
- `view`: Interface do usuário

Este sistema demonstra conceitos fundamentais de desenvolvimento web Java, POO, HTTP e bancos de dados relacionais em uma aplicação prática e funcional.
