# Integration Test Project

## Pré-requisitos

Para executar este projeto, você precisará ter instalado em sua máquina:

- Java JDK 21 ou superior
- Docker

## Configuração do Banco de Dados com Docker

Este projeto utiliza Docker para rodar uma instância do PostgreSQL. Siga os passos abaixo para configurar o banco de dados:

1. Certifique-se de que o Docker esteja instalado em sua máquina.
2. Navegue até a pasta `docker` na raiz do projeto.
3. Execute o comando `docker compose up -d` para iniciar uma instância do PostgreSQL.
4. O Docker Compose irá configurar o banco de dados conforme especificado no arquivo `docker-compose.yml` localizado na pasta `docker`.

## Executando o Projeto

Para executar o projeto, siga os passos abaixo:

1. Abra um terminal na raiz do projeto.
2. Execute o comando `./mvnw spring-boot:run` para iniciar a aplicação utilizando o Maven Wrapper incluído no projeto.

A aplicação estará disponível em `http://localhost:8080`.

## Executando os Testes

Para executar os testes, siga os passos abaixo:

1. Abra um terminal na raiz do projeto.
2. Execute o comando `./mvnw test` para rodar os testes utilizando o Maven Wrapper.

Os testes utilizarão um banco de dados em memória (H2), portanto, não é necessário configurar um ambiente de banco de dados para testes.

---

Este README foi criado com o auxílio do GitHub Copilot.