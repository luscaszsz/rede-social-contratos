# Rede social de contratos

Monorepo Quarkus com dois serviços independentes.

- `contracts-service` executa na porta 8081 e mantém contratos de trabalho.
- `social-feed-service` executa na porta 8080 e mantém publicações vinculadas a contratos.

Execute os serviços em terminais separados.

```powershell
./mvnw.cmd -pl contracts-service quarkus:dev
./mvnw.cmd -pl social-feed-service quarkus:dev
```

As consultas são públicas. Para criar, alterar ou remover dados, use Basic Auth com `user` e senha `user123`.

```powershell
curl http://localhost:8080/api/posts/available-contracts
curl -u user:user123 -H "Content-Type: application/json" -d '{"content":"Novo contrato", "contractId":2}' http://localhost:8080/api/posts
```

Os dados em H2 e os `import.sql` são voltados aos perfis de desenvolvimento e teste. O perfil de produção valida o esquema e espera um datasource configurado externamente.
