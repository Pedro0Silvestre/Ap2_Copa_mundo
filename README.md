# ⚽ Sistema de Gestão da FIFA World Cup - API REST

Este projeto consiste em uma API REST desenvolvida em **Java** utilizando o framework **Spring Boot** para a avaliação AP2. O sistema foi modernizado para utilizar:

* **Spring Data JPA / Hibernate** para persistência de dados;
* **Lombok** para redução de código boilerplate;
* **MySQL** como banco de dados relacional;
* **Swagger (OpenAPI)** para documentação automática e interativa da API.

---

# 📝 1. Mini-Mundo

O sistema gerencia os dados de um grande evento esportivo, especificamente a **Copa do Mundo FIFA**, coordenando o relacionamento entre três entidades principais:

## Seleções

Representam as federações nacionais participantes do torneio.

### Atributos

* ID único
* Nome do país
* Nome do técnico
* Posição no Ranking Oficial da FIFA

---

## Jogadores

Representam os atletas convocados por cada seleção.

### Atributos

* ID único
* Nome
* Número da camisa
* Posição tática
* Idade

### Relacionamento

**Uma Seleção → Muitos Jogadores (1:N)**

* Uma seleção possui vários jogadores.
* Um jogador pertence a apenas uma seleção.

---

## Partidas

Representam os confrontos realizados durante a competição.

### Atributos

* Data e hora da partida
* Estádio
* Fase do torneio
* Placar

### Relacionamento

**Seleções ↔ Partidas (N:N)**

* Uma partida é disputada por duas seleções.
* Uma seleção pode disputar várias partidas.

O relacionamento é gerenciado por meio da tabela associativa:

```sql
selecao_partida
```

---

# 📁 2. Estrutura do Projeto e Arquitetura

O projeto segue rigorosamente o padrão **Layered Architecture (Arquitetura em Camadas)**.

## Estrutura de Diretórios

```text
src/main/java/com/example/ap2/
├── controller/     # Exposição dos endpoints REST
├── service/        # Regras de negócio e validações
├── repository/     # Persistência de dados
└── model/         # Entidades JPA e relacionamentos
```

---

## model/

### O que contém

Classes de domínio anotadas com `@Entity`:

* Selecao
* Jogador
* Partida

### Objetivo

Definir a estrutura do banco de dados através do ORM (Object Relational Mapping), centralizando os modelos de dados da aplicação.

---

## repository/

### O que contém

Interfaces que estendem:

```java
JpaRepository
```

### Objetivo

Isolar a camada de acesso a dados.

O Spring Data JPA elimina a necessidade de escrever consultas SQL manualmente, mantendo a camada de negócio desacoplada da tecnologia de persistência.

---

## service/

### O que contém

* Regras de negócio
* Validações
* Orquestração das operações

### Objetivo

Centralizar toda a lógica operacional do sistema.

### Exemplos

* Impedir números de camisa fora do intervalo permitido.
* Garantir que uma partida possua exatamente duas seleções participantes.
* Validar dados antes da persistência.

---

## controller/

### O que contém

Controladores REST responsáveis pelos endpoints HTTP.

### Objetivo

Receber requisições, converter payloads JSON para objetos Java, delegar o processamento à camada Service e retornar respostas HTTP apropriadas.

### Exemplos de Status HTTP

| Código | Significado |
| ------ | ----------- |
| 200    | OK          |
| 201    | Created     |
| 400    | Bad Request |
| 404    | Not Found   |

---

# 🏛️ 3. Princípios de Engenharia de Software Implementados

Para garantir qualidade, manutenibilidade e escalabilidade, o projeto utiliza:

* Design Patterns
* Princípios SOLID
* Boas práticas de Clean Code

---

# 🔄 Design Patterns

## A. Dependency Injection (Injeção de Dependência)

As dependências são fornecidas automaticamente pelo Spring Boot, evitando alto acoplamento entre classes.

### Exemplo

```java
@RestController
@RequestMapping("/api/jogadores")
public class JogadorController {

    private final JogadorService jogadorService;

    public JogadorController(JogadorService jogadorService) {
        this.jogadorService = jogadorService;
    }
}
```

### Benefícios

* Baixo acoplamento
* Maior testabilidade
* Melhor manutenção

---

## B. Repository Pattern + Proxy Dinâmico

O acesso aos dados é encapsulado em interfaces.

O Spring Data JPA gera automaticamente a implementação em tempo de execução.

### Exemplo

```java
@Repository
public interface JogadorRepository extends JpaRepository<Jogador, Long> {

    List<Jogador> findBySelecaoId(Long selecaoId);

}
```

### Benefícios

* Menos código repetitivo
* Menor dependência de SQL manual
* Maior produtividade

---

## C. Singleton Pattern (Padrão Singleton)

Garante que uma classe tenha apenas uma única instância na memória durante todo o ciclo de vida da aplicação, centralizando o acesso e otimizando o uso de recursos. No projeto, o Spring Boot gerencia automaticamente os Controllers e Services sob este escopo por padrão.

### Exemplo

```java
@Service
public class JogadorService {

    // O Spring cria apenas UMA instância desta classe na memória.
    // Sempre que o JogadorController ou o PartidaService precisarem dela,
    // o Spring fornecerá exatamente a mesma referência já existente.
    private final JogadorRepository jogadorRepository;

    public JogadorService(JogadorRepository jogadorRepository) {
        this.jogadorRepository = jogadorRepository;
    }
}

# 🧮 Princípios SOLID

## Single Responsibility Principle (SRP)

Cada classe possui apenas uma responsabilidade.

### Exemplos

| Camada     | Responsabilidade    |
| ---------- | ------------------- |
| Controller | Comunicação HTTP    |
| Service    | Regras de negócio   |
| Repository | Persistência        |
| Entity     | Modelagem dos dados |

### Caso de Mudança

Se a idade máxima permitida para jogadores mudar, apenas a classe:

```java
JogadorService
```

precisará ser alterada.

---

## Dependency Inversion Principle (DIP)

Classes de alto nível dependem de abstrações, não de implementações concretas.

### Fluxo

```text
Controller
    ↓
Service
    ↓
Repository (JpaRepository)
```

---

# 🧼 Práticas de Clean Code

## A. Fail Fast e Proteção Contra NullPointerException

As validações são realizadas logo no início do método.

### Exemplo

```java
public Jogador cadastrar(Jogador jogador) {

    if (jogador.getNome() == null ||
        jogador.getNome().trim().isEmpty()) {

        throw new IllegalArgumentException(
            "O nome do jogador é obrigatório."
        );
    }

    if (jogador.getNumeroCamisa() == null ||
        jogador.getNumeroCamisa() <= 0 ||
        jogador.getNumeroCamisa() > 99) {

        throw new IllegalArgumentException(
            "O número da camisa deve estar entre 1 e 99."
        );
    }

    return jogadorRepository.save(jogador);
}
```

### Benefícios

* Falha rápida
* Menos processamento desnecessário
* Proteção contra dados inválidos

---

## B. Atualização Parcial com Merge de Estado

Durante atualizações, campos ausentes no JSON não sobrescrevem dados existentes com `null`.

### Exemplo

```java
public void atualizar(Long id, Selecao selecaoAtualizada) {

    Selecao selecaoBanco = buscarPorId(id);

    if (selecaoAtualizada.getNomePais() != null &&
        !selecaoAtualizada.getNomePais().trim().isEmpty()) {

        selecaoBanco.setNomePais(
            selecaoAtualizada.getNomePais()
        );
    }

    if (selecaoAtualizada.getRankingFifa() != null) {

        if (selecaoAtualizada.getRankingFifa() <= 0) {
            throw new IllegalArgumentException(
                "Ranking FIFA inválido."
            );
        }

        selecaoBanco.setRankingFifa(
            selecaoAtualizada.getRankingFifa()
        );
    }

    selecaoRepository.save(selecaoBanco);
}
```

### Benefícios

* Preserva dados existentes
* Evita perda acidental de informações
* Atualizações mais seguras

---

## C. Validação de Relacionamentos Complexos (N:N)

Antes de salvar uma partida, o sistema valida:

* Existência das seleções;
* Quantidade exata de participantes;
* Integridade dos relacionamentos.

### Exemplo

```java
if (partida.getSelecoes() == null ||
    partida.getSelecoes().size() != 2) {

    throw new IllegalArgumentException(
        "Uma partida deve possuir exatamente 2 seleções."
    );
}

List<Selecao> selecoesValidadas = new ArrayList<>();

for (Selecao s : partida.getSelecoes()) {

    Selecao selecaoBanco =
        selecaoService.buscarPorId(s.getId());

    selecoesValidadas.add(selecaoBanco);
}

partida.setSelecoes(selecoesValidadas);

partidaRepository.save(partida);
```

### Benefícios

* Integridade relacional
* Segurança dos dados
* Sincronização automática da tabela associativa

---

# 🛠️ 4. Como Executar o Projeto

## Pré-requisitos

* Java 17 ou superior
* MySQL Server
* MySQL Workbench (opcional)
* IntelliJ IDEA ou VS Code

---

## 1. Criar o Banco de Dados

Execute o script DDL fornecido para criar a estrutura inicial do banco.

---

## 2. Configurar o application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_copa_mundo?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=seu_usuario_root
spring.datasource.password=sua_senha_do_mysql

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.hibernate.ddl-auto=update
```

---

## 3. Executar a Aplicação

Inicie o projeto pela IDE.

Verifique no console:

* Inicialização do Tomcat na porta 8080;
* Conexão com o banco de dados;
* Execução das migrações do Hibernate.

---

## 4. Acessar o Swagger

Após iniciar a aplicação, abra:

```text
http://localhost:8080/swagger-ui/index.html
```

O Swagger permitirá visualizar, testar e documentar todos os endpoints da API de forma interativa.

---

# ✅ Tecnologias Utilizadas

* Java 17+
* Spring Boot
* Spring Data JPA
* Hibernate
* Lombok
* MySQL
* Swagger/OpenAPI
* Maven
