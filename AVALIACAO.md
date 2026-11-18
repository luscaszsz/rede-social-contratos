# Autoavaliação — Rede social de contratos

Grupo: preencher com integrantes e papéis antes da entrega.  
Tema/domínio: rede social profissional em que publicações podem ser vinculadas a contratos de trabalho do próprio usuário.  
Versões: Quarkus 3.40.1.2 · Java 21.

## Evidências por critério

1. **Serviços e API REST — nível 2**
   - Evidências: `pom.xml` agrega `contracts-service` e `social-feed-service`; recursos em `*/resource`; endpoints, portas e status em `docs/arquitetura.md`.
   - Testes: `ContratoResourceTest` e `PublicacaoResourceTest` cobrem sucesso, `201` com `Location`, `204`, `404`, `409` e `422`.

2. **Integração e tolerância a falhas — nível 2**
   - Evidências: `social-feed-service/.../client/ClienteContratos.java` e `service/GatewayContratos.java`; URL e parâmetros de timeout, retry e circuit breaker em `application.properties`.
   - Testes: `PublicacaoResourceTest.listaContratosDisponiveisUsaFallbackQuandoServicoFalha` e `servicoDeContratosIndisponivelRetorna503ENaoGravaPublicacao` usam `@InjectMock @RestClient`.

3. **Persistência com Panache — nível 2**
   - Evidências: entidades Active Record `Contrato` e `Publicacao`, transações nos serviços e consulta de contagem por contrato em `PublicacaoService`.
   - Testes: criação, consulta, atualização, remoção e regra de limite em `ContratoResourceTest` e `PublicacaoResourceTest`.

4. **Validação e tratamento de erros — nível 2**
   - Evidências: DTOs `ContratoRequest` e `PublicacaoRequest`, modelo `ApiErro`, `MapeadorDeErros` e exceções no pacote `exception`.
   - Testes: entradas inválidas retornam `400`; conteúdo inalterado retorna `409`; limite retorna `422`; recurso inexistente retorna `404`.

5. **Segurança — nível 2**
   - Evidências: `usuarios/Usuario.java` usa `quarkus-security-jpa` e BCrypt; `CargaDeUsuarios` cria os papéis `user` e `admin`; recursos usam `@RolesAllowed` e as métricas exigem `admin`.
   - Testes: `PublicacaoResourceTest.criaPublicacaoSemCredencialRetorna401` e testes `metricasExigemPapelAdministrador` em ambos os módulos.

6. **Configuração, health e métricas — nível 2**
   - Evidências: perfis e propriedades nos dois `application.properties`; `FeedConfig`; checks `PublicacoesLivenessCheck` e `VerificacaoProntidaoContratos`; métricas `posts.created` e `contracts.created`.
   - Testes: `ObservabilidadePublicacaoTest` e `ObservabilidadeContratoTest` validam health, readiness, métricas e acesso de administrador.

7. **Testes — nível 3**
   - Evidências: testes `@QuarkusTest` em ambos os módulos, com `quarkus.http.test-port=0`; o REST Client é simulado nos testes do feed.
   - Resultado atual: `mvn verify` executa **28 testes**, sem falhas.

8. **Uso crítico de IA — nível 2**
   - A IA foi usada para organizar documentação, sugerir testes e auxiliar correções. O grupo conferiu as alterações pela leitura do código e pela execução dos testes. Foram corrigidos manualmente problemas identificados na integração JSON entre os serviços e no `import.sql` após alterações das datas da publicação.

9. **Execução — nível 3**
   - Evidências: `README.md` documenta comandos, portas e credenciais; `docs/dados-de-teste-api.md` contém URLs, JSONs e caminhos de sucesso e falha; `docs/arquitetura.md` descreve a integração.
   - Execução: `contracts-service` na porta 8081 e `social-feed-service` na porta 8080, sem Docker.

## Opcionais entregues

- Métricas de negócio próprias para criação de contratos e publicações.
- Health checks próprios de liveness do feed e readiness da dependência de contratos.
