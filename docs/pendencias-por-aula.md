# Pendências por aula

Esta análise considera os módulos que o Maven executa atualmente, `contracts-service` e `social-feed-service`. Os testes em `src/test` na raiz pertencem à API legada de chaves PIX. Eles usam endpoints como `/chaves` e não validam o monorepo de contratos e publicações.

Os módulos novos possuem três testes. `ContratoResourceTest` verifica a quantidade de contratos carregados. `PublicacaoResourceTest` verifica a quantidade de publicações e que a criação sem credencial responde `401`. Portanto, os testes ainda não cobrem o fluxo funcional completo apresentado nas aulas.

## Aula 1 — Projeto Quarkus e primeiro teste

- O monorepo, os dois módulos Maven, Java 21 e os recursos REST estão presentes.
- O modo `quarkus:dev` foi configurado e os serviços usam portas distintas.
- Falta executar e registrar `mvn verify` com os testes dos módulos novos concluindo com sucesso.
- Falta um teste de integração empacotado, com `@QuarkusIntegrationTest`, para cada serviço.
- FEITO

## Aula 2 — REST, CDI e OpenAPI

- Recursos CDI e contratos REST existem para contratos e publicações.
- Os endpoints principais usam os verbos e os códigos de sucesso esperados, incluindo `201`, `Location` e `204`.
- Falta adicionar OpenAPI e Swagger UI aos módulos novos, pois eles não declaram `quarkus-smallrye-openapi`.
- Faltam testes para todos os códigos REST dos endpoints, especialmente busca por ID, criação, atualização e remoção.

## Aula 3 — Panache, H2 e transações

- As entidades Panache, H2 em memória, `import.sql` e métodos transacionais estão implementados.
- Os dados iniciais dos dois serviços estão cobertos apenas por testes de quantidade.
- Faltam testes que comprovem persistência de criação, atualização e exclusão nos dois bancos H2.
- Falta testar o isolamento de dados entre execuções e documentar a estratégia de migração persistente, caso ela seja adotada depois.

## Aula 4 — Configuração, health e métricas

- Há perfis `dev`, `test` e `prod`, endpoint de health e métricas Prometheus nos módulos.
- O feed possui readiness customizado para consultar o serviço de contratos.
- Faltam testes para `/q/health/live`, `/q/health/ready` e `/q/metrics`.
- Faltam testes para confirmar os contadores `contracts.created` e `posts.created` após operações de criação.
- Falta um teste de perfil, por exemplo usando `@TestProfile`, que valide uma propriedade específica do ambiente de teste.

## Aula 5 — REST Client e tolerância a falhas

- O feed possui `ClienteContratos`, timeout, retry, circuit breaker, fallback e exceções de integração.
- O readiness consulta o serviço de contratos.
- Faltam testes com o cliente remoto simulado para contrato inexistente, indisponibilidade, timeout, retentativas e abertura do circuit breaker.
- Falta testar o fallback da lista vazia em `GET /api/posts/available-contracts`.
- Falta testar que a criação ou atualização com contrato, quando o serviço remoto está indisponível, retorna `503` com o corpo padronizado.

## Aula 6 — Validação e tratamento de erros

- Os requests usam Bean Validation e os dois serviços possuem mapeadores globais de exceção.
- Os corpos de erro possuem instante, status, descrição, mensagem e caminho.
- Faltam testes de `400` para conteúdo vazio, contrato sem campos obrigatórios e datas inválidas ou ausentes.
- Faltam testes que comprovem o formato completo do JSON de erro em `400`, `404` e `503`.
- Falta validar explicitamente a mensagem de negócio para um `contractId` inexistente na criação e atualização de publicação.

## Aula 7 — Autenticação

- Basic Auth com usuário `user` e papel `USER` está configurado nos dois serviços.
- A criação de publicação sem credencial possui um teste de `401`.
- Faltam testes de autenticação para criação de contrato, atualização de publicação e remoção de publicação.
- Faltam testes para senha inválida e para uma credencial válida em cada operação protegida.
- As credenciais em texto puro são adequadas para o cenário didático. Antes de produção, falta substituí-las por uma fonte segura de identidades.

## Aula 8 — Autorização, CORS e políticas por caminho

- Os endpoints de escrita exigem o papel `USER`.
- Health e métricas do feed possuem regras por caminho que permitem acesso público.
- Faltam testes de `403` usando um usuário autenticado sem o papel `USER`.
- Falta criar usuários com papéis distintos para exercitar autorização por papel nos módulos novos.
- Falta configurar e testar CORS nos dois módulos, caso exista um front-end em outra origem.
- A regra de posse apresentada na aula de PIX não foi transportada, pois o domínio atual não possui dono da publicação ou do contrato. Essa regra precisa ser definida antes de ser implementada e testada.

## Aula 9 — Preparação para a banca

- O guia de chamadas manuais está em `docs/guia-testes-api.md` e o roteiro de arquitetura está em `docs/roteiro-implementacao.md`.
- Falta criar um arquivo de autoavaliação com evidências por critério da rubrica.
- Falta executar `mvn verify` limpo e registrar a contagem de testes dos dois módulos.
- Falta demonstrar, em testes automatizados, os caminhos de sucesso e de erro de cada requisito de negócio e de integração.
