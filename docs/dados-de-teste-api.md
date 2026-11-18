# Dados de teste das APIs

Inicie primeiro o serviço de contratos na porta `8081` e depois o feed na porta `8080`. Os dados abaixo consideram o perfil de desenvolvimento padrão, que recria o banco H2 ao iniciar.

## Credenciais

| Usuário | Senha | Uso |
|---|---|---|
| `user` | `user123` | Criar, alterar e remover publicações. |
| `adm` | `adm123` | Criar contratos; também pode remover publicações. |

No Postman/Insomnia, selecione **Basic Auth** e informe o usuário e a senha. Em `curl`, use `-u usuario:senha`. No PowerShell, prefira `curl.exe`, pois `curl` pode ser um atalho para outro comando.

## Dados carregados ao iniciar

O serviço de contratos possui os contratos `1` a `5`, todos pertencentes ao usuário `user`. O contrato `3` começa sem publicações vinculadas e é útil para testar o limite de três publicações.

O feed inicia com quatro publicações, de ids `1` a `4`.

## Serviço de contratos — `http://localhost:8081`

### Consultar contratos

`GET http://localhost:8081/api/contracts`

Não precisa de autenticação. Deve responder `200 OK` e uma lista com os contratos iniciais.

Para consultar apenas um contrato, use `GET http://localhost:8081/api/contracts/2`. O id `9999` retorna `404 Not Found`.

### Criar contrato

`POST http://localhost:8081/api/contracts`

Use Basic Auth: `adm` / `adm123` e o cabeçalho `Content-Type: application/json`.

```json
{
  "titulo": "Pessoa Desenvolvedora Java",
  "empresa": "Empresa de Testes",
  "situacao": "EM_ANDAMENTO",
  "dataInicio": "2026-11-18",
  "dataFim": null,
  "responsavel": "user"
}
```

O resultado esperado é `201 Created`, com um `Location` no cabeçalho. Os valores válidos de `situacao` são `CONCLUIDO`, `EM_ANDAMENTO` e `FUTURO`. Para testar validação, envie `titulo` vazio ou omita `situacao`; a resposta será `400 Bad Request`.

## Serviço de feed — `http://localhost:8080`

### Consultar publicações e contratos disponíveis

| Método | URL | Resultado esperado |
|---|---|---|
| `GET` | `http://localhost:8080/api/posts` | Lista de publicações (`200 OK`). |
| `GET` | `http://localhost:8080/api/posts/1` | Publicação inicial (`200 OK`). |
| `GET` | `http://localhost:8080/api/posts/9999` | `404 Not Found`. |
| `GET` | `http://localhost:8080/api/posts/available-contracts` | Contratos vindos do serviço de contratos (`200 OK`). |

Essas consultas não exigem autenticação. Caso o serviço de contratos esteja fora do ar, a consulta de contratos disponíveis devolve uma lista vazia pelo fallback.

### Criar publicação sem contrato

`POST http://localhost:8080/api/posts`

Use Basic Auth: `user` / `user123`.

```json
{
  "content": "Publicação de teste sem contrato."
}
```

O resultado esperado é `201 Created`. Guarde o campo `id` retornado para testar atualização e remoção.

### Criar publicação vinculada a um contrato

`POST http://localhost:8080/api/posts`

Use Basic Auth: `user` / `user123`.

```json
{
  "content": "Iniciei um novo desafio profissional.",
  "contractId": 3
}
```

O feed consulta o contrato `3`, confirma que ele pertence a `user` e devolve `201 Created`. A resposta possui `conteudo`, `criadaEm`, `dataInicio`, `dataFim` e o resumo do contrato no campo `contrato`.

### Testar o limite por contrato

Repita a criação anterior com o contrato `3`, mas altere o texto em cada envio. As três primeiras publicações vinculadas ao contrato `3` retornam `201 Created`; a quarta retorna `422 Unprocessable Entity`. O limite é por usuário e contrato.

### Atualizar publicação

`PUT http://localhost:8080/api/posts/{id}`

Substitua `{id}` pelo id criado anteriormente. Use Basic Auth: `user` / `user123`.

```json
{
  "content": "Conteúdo atualizado da publicação.",
  "contractId": 2
}
```

O resultado esperado é `200 OK`. Reenvie exatamente o mesmo `content` para a mesma publicação: a regra de negócio responde `409 Conflict`.

### Remover publicação

`DELETE http://localhost:8080/api/posts/{id}`

Use Basic Auth: `user` / `user123` ou `adm` / `adm123`. A resposta de sucesso é `204 No Content`; para um id inexistente, a resposta é `404 Not Found`.

### Casos rápidos de autenticação e validação

- Faça um `POST /api/posts` sem Basic Auth: resposta `401 Unauthorized`.
- Faça um `POST /api/contracts` com `user` / `user123`: resposta `403 Forbidden`, pois o cadastro é de `admin`.
- Envie `{"content":""}` para `POST /api/posts` autenticado: resposta `400 Bad Request`.
- Informe um `contractId` inexistente, por exemplo `9999`, ao criar publicação: resposta `404 Not Found`.

## Health e métricas

| URL | Resultado esperado |
|---|---|
| `GET http://localhost:8080/q/health/live` | Estado `UP` do feed. |
| `GET http://localhost:8080/q/health/ready` | Estado `UP` quando contratos está disponível. |
| `GET http://localhost:8080/q/metrics` | Métricas, incluindo `posts_created` e `feed_publicacoes_cadastro`; exige Basic Auth de `adm`. |
| `GET http://localhost:8081/q/health` | Estado de saúde do serviço de contratos. |
| `GET http://localhost:8081/q/metrics` | Métricas, incluindo `contracts_created` e `contracts_cadastro`; exige Basic Auth de `adm`. |

Crie uma publicação e consulte novamente as métricas para observar o aumento do contador. Pare o serviço de contratos e acesse o readiness do feed para observar a indisponibilidade da dependência.
