package org.caixa.usuarios;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

/**
 * Aula 7: cria os usuários de aula quando a aplicação sobe (o import.sql não serve:
 * a senha precisa passar pelo BCrypt antes de ir para o banco).
 * Senhas de aula, conhecidas de todos: em produção, usuários vêm de cadastro ou de um provedor de identidade.
 */
@ApplicationScoped
public class CargaDeUsuarios {

    @Transactional
    void carregar(@Observes StartupEvent evento) {
        Usuario.deleteAll();
        Usuario.adicionar("adm", "adm123", "admin");
        Usuario.adicionar("user","user123","user");
    }
}
