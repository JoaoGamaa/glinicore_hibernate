# Glinicore

Sistema odontologico academico em Java Swing, Maven e Hibernate/JPA.

## Banco PostgreSQL

Crie o banco local antes de executar:

```sql
CREATE DATABASE glinicore;
```

A configuracao padrao fica centralizada na classe `src/main/java/dao/ConexaoHibernate.java`:

- URL: `jdbc:postgresql://localhost:5432/glinicore`
- Usuario: `postgres`
- Senha: `123456`

Se sua senha for diferente, altere apenas a constante `DB_PASSWORD` em `ConexaoHibernate`.

## Executar pelo Maven

```bash
mvn clean compile exec:java
```

A classe principal configurada no Maven e:

```text
controller.GerInterGrafica
```

## Executar pelo NetBeans

Abra a pasta do projeto como projeto Maven e execute a classe `controller.GerInterGrafica`.

## API de CEP

O cadastro de paciente possui consulta de CEP pela API publica ViaCEP:

```text
https://viacep.com.br/ws/{cep}/json/
```

Na tela de cadastro, informe o CEP e clique em `Buscar` para preencher rua, bairro, cidade, estado e complemento.
