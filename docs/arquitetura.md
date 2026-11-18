# Arquitetura

## Visão do projeto

O projeto representa uma rede social profissional simples. Uma pessoa pode ter contratos de trabalho cadastrados e criar publicações no feed. Ao criar uma publicação, ela pode vinculá-la a um contrato que pertence ao próprio usuário.

O repositório é um projeto Maven multi-módulo com dois serviços Quarkus, Java 21 e banco H2 em memória.

```text
Cliente HTTP
     |
     +--> social-feed-service :8080 ---- REST Client ----> contracts-service :8081
              |                                           |
          Publicações                                  Contratos
```

## Serviços e responsabilidades

### `contracts-service` — porta 8081

É o dono dos contratos. Mantém os dados de título, empresa, situação, período e nome do usuário ao qual o contrato pertence. No cadastro, a autenticação verifica se é um administrador realizando a publicação. Ele deve indicar a qual usuário o contrato pertence. 

| Método | Endpoint | Função |
|---|---|---|
| `GET` | `/api/contracts` | Lista os contratos cadastrados. |
| `GET` | `/api/contracts/{id}` | Busca um contrato pelo identificador. |
| `POST` | `/api/contracts` | Cria um contrato e devolve `201 Created` com cabeçalho `Location`. |

### `social-feed-service` — porta 8080

É o serviço principal do feed. Mantém as publicações e consulta o serviço de contratos quando precisa listar contratos disponíveis ou confirmar o contrato informado em uma nova publicação. A publicação guarda uma cópia resumida do contrato (id, título, empresa e situação), evitando depender da consulta remota para ler o feed já criado.

| Método | Endpoint | Função |
|---|---|---|
| `GET` | `/api/posts` | Lista as publicações. |
| `GET` | `/api/posts/{id}` | Busca uma publicação pelo identificador. |
| `GET` | `/api/posts/available-contracts` | Consulta os contratos pelo REST Client. |
| `POST` | `/api/posts` | Cria uma publicação e devolve `201 Created` com `Location`. |
| `PUT` | `/api/posts/{id}` | Atualiza o conteúdo e/ou contrato de uma publicação. |
| `DELETE` | `/api/posts/{id}` | Remove uma publicação e devolve `204 No Content`. |

## Fluxo de criação de publicação

1. O cliente autentica-se e envia uma publicação ao `social-feed-service`.
2. O feed associa a publicação ao usuário autenticado.
3. Se houver `idContrato`, o feed consulta `contracts-service` por REST Client.
4. O feed confirma que o contrato existe e pertence ao usuário autenticado.
5. O feed grava a publicação e o resumo do contrato no seu próprio banco.

Uma publicação sem contrato é permitida. Um contrato de outro usuário é recusado com `403 Forbidden`; contrato inexistente resulta em `404 Not Found`; e indisponibilidade do serviço de contratos na confirmação resulta em `503 Service Unavailable`.

## Regras de negócio

- O mesmo usuário pode criar, no máximo, **três publicações** vinculadas ao mesmo contrato. O limite vem da propriedade `feed.publicacoes.limite-por-contrato` e pode ser alterado pela variável de ambiente `FEED_PUBLICACOES_LIMITE_POR_CONTRATO`.
- A contagem é feita por par `usuário + id do contrato`; portanto, outro usuário não consome o limite alheio.
- Não é permitido atualizar uma publicação mantendo exatamente o mesmo conteúdo. A regra devolve `409 Conflict`.
- Entradas inválidas e exceções de domínio usam um corpo de erro padronizado (`ApiErro`).

## Persistência

Cada serviço possui seu próprio banco H2 em memória e suas próprias entidades Panache:

- `contracts-service`: entidade `Contrato`;
- `social-feed-service`: entidade `Publicacao`.

Os arquivos `import.sql` fornecem dados iniciais para desenvolvimento e testes. O perfil de produção usa validação do esquema, sem recriá-lo automaticamente.

## Segurança

Os dois serviços usam Basic Auth com usuários armazenados no banco por `quarkus-security-jpa`, com senha protegida por BCrypt. Os usuários fictícios carregados ao iniciar são:

| Usuário | Senha de demonstração | Papel |
|---|---|---|
| `user` | `user123` | `user` |
| `adm` | `adm123` | `admin` |

No feed, criação e atualização exigem o papel `user`; remoção aceita `user` ou `admin`. A identidade também é usada para garantir que o contrato associado à publicação pertença ao autor.

## Integração e tolerância a falhas

O `social-feed-service` consome o `contracts-service` por meio de `ClienteContratos` e `GatewayContratos`. A URL é configurável pela propriedade `quarkus.rest-client.contracts.url`.

As chamadas possuem timeout de 2 segundos, até duas tentativas adicionais e circuit breaker. Para a listagem de contratos, uma falha retorna uma lista vazia; para a confirmação de um contrato durante o cadastro, a falha é tratada como indisponibilidade e retorna `503`. Erro `404` não é repetido, pois representa uma regra de negócio e não uma falha temporária.

## Configuração, observabilidade e testes

Os dois serviços usam perfis `dev`, `test` e `prod`. As portas de teste são aleatórias (`quarkus.http.test-port=0`), permitindo executar as suítes sem conflito de portas.

O projeto expõe os endpoints do Quarkus para acompanhamento:

- `/q/health/live`: indica que o feed está em execução;
- `/q/health/ready`: verifica a prontidão, inclusive a comunicação com contratos no feed;
- `/q/metrics`: expõe métricas de criação de contratos e publicações, além do tempo de cadastro.

Os testes `@QuarkusTest` cobrem operações bem-sucedidas e de falha dos recursos, validação, regras de limite, autenticação/autorização, health, métricas e a indisponibilidade simulada do REST Client.

## Como executar

Em dois terminais, a partir da raiz do repositório:

```powershell
.\mvnw.cmd -pl contracts-service quarkus:dev
.\mvnw.cmd -pl social-feed-service quarkus:dev
```

O serviço de contratos deve estar ativo antes de testar o fluxo de publicação com contrato. Para executar a validação completa do projeto, use:

```powershell
.\mvnw.cmd verify
```
