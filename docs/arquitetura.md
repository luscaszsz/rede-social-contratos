# Comsiderações sobre arquitetura

Aplicação de rede social. Preciso que tenha o get de informações (feed) e um post para o feed tambem.

## publicações

A publicação deve consultar api de consulta a contratos cadastrados para a pessoa. Encontrando, a api principal
terá a possiblidade de vincular as infomrações de contrato a uma publicação específica.
Publicado, esse id não deve mais estar disponivel para consulta de publlicações

## contratos

A api de consulta deve ter informações sobre o usuário. Essa informação deve vir de token jwt.

