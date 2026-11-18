# Casos de teste da API

Este roteiro exercita os dois serviços em modo de desenvolvimento. Abra dois terminais na raiz do monorepo e inicie primeiro o serviço de contratos. Em seguida, inicie o serviço de publicações. Os dados H2 são recriados a cada inicialização e carregam cinco contratos e quatro publicações.

```powershell
# Terminal 1
.\mvnw.cmd -pl contracts-service quarkus:dev

# Terminal 2
.\mvnw.cmd -pl social-feed-service quarkus:dev
```

As rotas de escrita exigem Basic Auth com usuário `user` e senha `user123`. As consultas, health checks e métricas não exigem credencial.

## Serviço de contratos — porta 8081

### Listar contratos

`GET /api/contracts` retorna `200 OK` e os cinco contratos de carga. Cada contrato contém `id`, `title`, `company`, `status`, `startDate`, `endDate` e `user`. O campo `user` é preenchido com o login Basic Auth na criação do contrato.

```powershell
curl.exe http://localhost:8081/api/contracts
```

### Buscar contrato por ID

O contrato `2` existe e retorna `200 OK`. Um identificador inexistente retorna `404 Not Found` com o JSON de erro padronizado.

```powershell
curl.exe http://localhost:8081/api/contracts/2
curl.exe http://localhost:8081/api/contracts/999999
```

### Criar contrato

`POST /api/contracts` retorna `201 Created` e o cabeçalho `Location`. O usuário vinculado ao contrato vem da credencial, portanto o corpo não recebe o campo `user`.

```json
{
  "title": "Pessoa Desenvolvedora de Plataforma",
  "company": "Tecnologia Pública",
  "status": "FUTURO",
  "startDate": "2027-06-01",
  "endDate": null
}
```

```powershell
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"title":"Pessoa Desenvolvedora de Plataforma","company":"Tecnologia Pública","status":"FUTURO","startDate":"2027-06-01","endDate":null}' http://localhost:8081/api/contracts
```

Um corpo com dados obrigatórios vazios retorna `400 Bad Request`. A mesma chamada sem `-u user:user123` retorna `401 Unauthorized`.

```powershell
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"title":"","company":"","status":null,"startDate":null}' http://localhost:8081/api/contracts
curl.exe -H "Content-Type: application/json" -d '{"title":"Sem credencial","company":"Teste","status":"FUTURO","startDate":"2027-06-01"}' http://localhost:8081/api/contracts
```

## Serviço de publicações — porta 8080

### Listar publicações e buscar por ID

`GET /api/posts` retorna `200 OK` com as quatro publicações de carga. `GET /api/posts/1` retorna uma publicação existente. Um ID inexistente retorna `404 Not Found`.

```powershell
curl.exe http://localhost:8080/api/posts
curl.exe http://localhost:8080/api/posts/1
curl.exe http://localhost:8080/api/posts/999999
```

### Listar contratos disponíveis

`GET /api/posts/available-contracts` consulta o `contracts-service` pelo REST Client e retorna `200 OK` com os contratos disponíveis. Com o serviço de contratos desligado, o fallback retorna uma lista vazia, também com `200 OK`.

```powershell
curl.exe http://localhost:8080/api/posts/available-contracts
```

### Criar publicação sem contrato

Uma publicação pode ser criada sem vínculo de contrato. Esse cenário serve para testar a criação local e retorna `201 Created`.

```json
{
  "content": "Publicação criada sem contrato vinculado."
}
```

```powershell
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":"Publicação criada sem contrato vinculado."}' http://localhost:8080/api/posts
```

Um conteúdo vazio retorna `400 Bad Request`. A ausência de credencial retorna `401 Unauthorized`.

```powershell
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":""}' http://localhost:8080/api/posts
curl.exe -H "Content-Type: application/json" -d '{"content":"Publicação sem autenticação."}' http://localhost:8080/api/posts
```

### Criar publicação vinculada e testar o limite

O contrato `3` pertence ao usuário `user` e não possui publicação inicial. A configuração `feed.publicacoes.limite-por-contrato=3` permite três publicações deste usuário com o mesmo contrato. As três primeiras chamadas retornam `201 Created`. A quarta retorna `422 Unprocessable Entity`.

```json
{
  "content": "Publicação vinculada ao contrato.",
  "contractId": 3
}
```

```powershell
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":"Primeira publicação do contrato 3.","contractId":3}' http://localhost:8080/api/posts
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":"Segunda publicação do contrato 3.","contractId":3}' http://localhost:8080/api/posts
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":"Terceira publicação do contrato 3.","contractId":3}' http://localhost:8080/api/posts
curl.exe -u user:user123 -H "Content-Type: application/json" -d '{"content":"Quarta publicação do contrato 3.","contractId":3}' http://localhost:8080/api/posts
```

O último corpo de resposta deve conter `status` igual a `422` e informar que o limite foi atingido. Para alterar o limite sem recompilar, defina a variável de ambiente antes de iniciar o feed.

```powershell
$env:FEED_PUBLICACOES_LIMITE_POR_CONTRATO = "1"
.\mvnw.cmd -pl social-feed-service quarkus:dev
```

### Atualizar publicação

`PUT /api/posts/{id}` atualiza a publicação e retorna `200 OK`. O exemplo atualiza o registro de carga `1`. Repetir o mesmo conteúdo gera a regra de conflito e retorna `409 Conflict`.

```powershell
curl.exe -u user:user123 -X PUT -H "Content-Type: application/json" -d '{"content":"Conteúdo atualizado para validar o endpoint."}' http://localhost:8080/api/posts/1
curl.exe -u user:user123 -X PUT -H "Content-Type: application/json" -d '{"content":"Conteúdo atualizado para validar o endpoint."}' http://localhost:8080/api/posts/1
curl.exe -u user:user123 -X PUT -H "Content-Type: application/json" -d '{"content":"Outro conteúdo."}' http://localhost:8080/api/posts/999999
```

As respostas esperadas são `200`, `409` e `404`, nessa ordem.

### Remover publicação

`DELETE /api/posts/{id}` retorna `204 No Content`. Para preservar os dados de carga, crie uma publicação sem contrato, use o ID retornado no corpo da resposta e substitua `<id-criado>` nos comandos abaixo. Uma segunda remoção retorna `404 Not Found`.

```powershell
curl.exe -u user:user123 -X DELETE http://localhost:8080/api/posts/<id-criado>
curl.exe -u user:user123 -X DELETE http://localhost:8080/api/posts/<id-criado>
```

## Health checks — Aula 4

O readiness confirma que o serviço e suas dependências estão prontos. O serviço de contratos informa a saúde do datasource. O feed inclui datasource e a verificação de prontidão do `contracts-service`.

```powershell
curl.exe http://localhost:8081/q/health/ready
curl.exe http://localhost:8080/q/health/ready
```

Ambas as respostas devem conter `"status":"UP"`. No feed, procure por `Database connections health check` e `contracts-service` no array `checks`.

O liveness confirma que o processo está vivo sem consultar dependências externas. O feed expõe também a configuração que está em vigor.

```powershell
curl.exe http://localhost:8080/q/health/live
```

A resposta deve conter o check `feed-publicacoes` com `limite-por-contrato` igual a `3`, ou ao valor definido pela variável `FEED_PUBLICACOES_LIMITE_POR_CONTRATO`.

## Métricas — Aula 4

Os contadores e timers aparecem após a primeira criação bem-sucedida. Execute os `POST` de contrato e publicação deste roteiro e consulte as métricas.

```powershell
curl.exe http://localhost:8081/q/metrics | Select-String "contracts_created_total|contracts_cadastro_seconds_count"
curl.exe http://localhost:8080/q/metrics | Select-String "posts_created_total|feed_publicacoes_cadastro_seconds_count"
```

No serviço de contratos, `contracts_created_total` conta contratos criados e `contracts_cadastro_seconds_count` mede chamadas de cadastro. No feed, `posts_created_total` conta publicações criadas e `feed_publicacoes_cadastro_seconds_count` mede o tempo do `POST /api/posts`. Os nomes seguem o formato Prometheus, que converte pontos para sublinhados e acrescenta `_total` aos contadores.
