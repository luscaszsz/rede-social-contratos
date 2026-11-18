insert into Contrato (id, titulo, empresa, nomeUsuario, situacao, dataInicio, dataFim) values (1, 'Desenvolvedor Java', 'CAIXA', 'user', 'CONCLUIDO', '2023-01-01', '2024-01-31');
insert into Contrato (id, titulo, empresa, nomeUsuario, situacao, dataInicio, dataFim) values (2, 'Analista de Sistemas', 'Empresa Pública', 'user', 'EM_ANDAMENTO', '2025-02-01', null);
insert into Contrato (id, titulo, empresa, nomeUsuario, situacao, dataInicio, dataFim) values (3, 'Arquiteto de Software', 'Laboratório Digital', 'user', 'FUTURO', '2027-01-10', null);
insert into Contrato (id, titulo, empresa, nomeUsuario, situacao, dataInicio, dataFim) values (4, 'Especialista em Dados', 'Inovação Brasil', 'user', 'EM_ANDAMENTO', '2026-03-01', null);
insert into Contrato (id, titulo, empresa, nomeUsuario, situacao, dataInicio, dataFim) values (5, 'Consultora de Produto', 'Soluções Cidadãs', 'user', 'CONCLUIDO', '2024-02-01', '2025-12-20');
alter sequence Contrato_SEQ restart with 6;
