insert into Contrato (id, title, company, status, startDate, endDate) values (1, 'Desenvolvedor Java', 'CAIXA', 'CONCLUIDO', '2023-01-01', '2024-01-31');
insert into Contrato (id, title, company, status, startDate, endDate) values (2, 'Analista de Sistemas', 'Empresa Pública', 'EM_ANDAMENTO', '2025-02-01', null);
insert into Contrato (id, title, company, status, startDate, endDate) values (3, 'Arquiteto de Software', 'Laboratório Digital', 'FUTURO', '2027-01-10', null);
insert into Contrato (id, title, company, status, startDate, endDate) values (4, 'Especialista em Dados', 'Inovação Brasil', 'EM_ANDAMENTO', '2026-03-01', null);
insert into Contrato (id, title, company, status, startDate, endDate) values (5, 'Consultora de Produto', 'Soluções Cidadãs', 'CONCLUIDO', '2024-02-01', '2025-12-20');
alter sequence Contrato_SEQ restart with 6;
