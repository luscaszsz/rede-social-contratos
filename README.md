# Rede social de contratos

Projeto Quarkus multi-módulo de uma rede social profissional simples. O feed consulta contratos de trabalho antes de vincular um contrato a uma publicação.

| Serviço | Porta | Responsabilidade |
|---|---:|---|
| `social-feed-service` | 8080 | Publicações e integração com contratos. |
| `contracts-service` | 8081 | Cadastro e consulta de contratos. |

**Tecnologias:** Java 21, Quarkus, Maven, Hibernate ORM com Panache, H2, REST Client, Fault Tolerance e Basic Auth com usuários no banco.

## Executar

Em dois terminais, na raiz do repositório:

```powershell
.\mvnw.cmd -pl contracts-service quarkus:dev
.\mvnw.cmd -pl social-feed-service quarkus:dev
```

Para executar a suíte completa:

```powershell
.\mvnw.cmd verify --> 27 testes passam.
```

## Credenciais de demonstração

| Usuário | Senha | Papel |
|---|---|---|
| `user` | `user123` | Cria, atualiza e remove publicações. |
| `adm` | `adm123` | Cria contratos e também pode remover publicações. |

As consultas são públicas. As operações protegidas usam Basic Auth. A documentação completa com URLs, JSONs e casos de teste está em [dados-de-teste-api.md](docs/dados-de-teste-api.md). A visão da solução está em [arquitetura.md](docs/arquitetura.md).
