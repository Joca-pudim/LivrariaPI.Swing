/* ============================================================
   Marketplace de Livros - dados_exemplo.sql
   Senha de TODOS os usuarios de exemplo: 123456
   (hash SHA-256 abaixo, igual ao gerado por util.Seguranca)
   Pode ser executado mais de uma vez (nao duplica).
   Execute depois do schema.sql.
   ============================================================ */
USE LivrariaPI;
GO
DECLARE @h VARCHAR(64) = '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92';

INSERT INTO categoria (nome)
SELECT v.nome FROM (VALUES ('Romance'),('Ficção Científica'),('Técnico'),('Infantil'),('Biografia'),('Fantasia')) v(nome)
WHERE NOT EXISTS (SELECT 1 FROM categoria c WHERE c.nome = v.nome);

INSERT INTO loja (nome, email, senha)
SELECT v.nome, v.email, @h FROM (VALUES
  ('Sebo do Centro','sebo@loja.com'),
  ('Livraria Horizonte','horizonte@loja.com'),
  ('Tech Books','techbooks@loja.com')) v(nome,email)
WHERE NOT EXISTS (SELECT 1 FROM loja l WHERE l.email = v.email);

INSERT INTO cliente (nome, cpf, data_aniv, email, telefone, cep, endereco, numero_end, complemento, bairro, cidade, senha)
SELECT v.nome, v.cpf, v.dn, v.email, '11999990000', '01001000', 'Praça da Sé', '100', NULL, 'Sé', 'São Paulo', @h
FROM (VALUES
  ('Ana Souza','11111111111',CAST('1998-05-20' AS DATE),'ana@email.com'),
  ('Bruno Lima','22222222222',CAST('1995-11-02' AS DATE),'bruno@email.com')) v(nome,cpf,dn,email)
WHERE NOT EXISTS (SELECT 1 FROM cliente c WHERE c.email = v.email);

INSERT INTO produto (titulo, autor, preco, estoque, condicao, cod_loja, cod_categoria)
SELECT v.titulo, v.autor, v.preco, v.estoque, v.condicao, l.codigo, c.codigo
FROM (VALUES
  ('Dom Casmurro','Machado de Assis',29.90,10,'Novo','Sebo do Centro','Romance'),
  ('Memórias Póstumas de Brás Cubas','Machado de Assis',24.50,3,'Usado','Sebo do Centro','Romance'),
  ('O Alquimista','Paulo Coelho',34.90,8,'Novo','Livraria Horizonte','Romance'),
  ('Duna','Frank Herbert',69.90,5,'Novo','Livraria Horizonte','Ficção Científica'),
  ('Fundação','Isaac Asimov',44.00,2,'Usado','Sebo do Centro','Ficção Científica'),
  ('O Hobbit','J. R. R. Tolkien',54.90,12,'Novo','Livraria Horizonte','Fantasia'),
  ('Clean Code','Robert C. Martin',99.90,6,'Novo','Tech Books','Técnico'),
  ('Banco de Dados - Projeto e Implementação','Felipe Machado',89.00,4,'Novo','Tech Books','Técnico'),
  ('O Pequeno Príncipe','Antoine de Saint-Exupéry',19.90,15,'Novo','Livraria Horizonte','Infantil'),
  ('Steve Jobs','Walter Isaacson',59.90,1,'Usado','Sebo do Centro','Biografia')
) v(titulo,autor,preco,estoque,condicao,loja,categoria)
JOIN loja l ON l.nome = v.loja
JOIN categoria c ON c.nome = v.categoria
WHERE NOT EXISTS (SELECT 1 FROM produto p WHERE p.titulo = v.titulo AND p.cod_loja = l.codigo);
GO

/* ---- Pedidos de exemplo (so se ainda nao houver nenhum) ---- */
IF NOT EXISTS (SELECT 1 FROM pedido)
BEGIN
    DECLARE @ana INT = (SELECT codigo FROM cliente WHERE email = 'ana@email.com');
    DECLARE @bru INT = (SELECT codigo FROM cliente WHERE email = 'bruno@email.com');
    DECLARE @ped INT;

    -- Pedido 1 (Ana): PIX, pago
    INSERT INTO pedido (cod_cliente, data_pedido, status, valor_total) VALUES (@ana, '2026-11-10T10:00:00', 'Pago', 0);
    SET @ped = SCOPE_IDENTITY();
    INSERT INTO item_pedido (cod_pedido, cod_produto, quantidade, preco_unitario)
        SELECT @ped, codigo, 1, preco FROM produto WHERE titulo IN ('Dom Casmurro','Duna');
    UPDATE pedido SET valor_total = (SELECT SUM(quantidade*preco_unitario) FROM item_pedido WHERE cod_pedido=@ped) WHERE codigo=@ped;
    INSERT INTO pagamento (cod_pedido, forma, status, valor, parcelas, data_pagamento)
        SELECT codigo, 'PIX', 'Aprovado', valor_total, 1, data_pedido FROM pedido WHERE codigo=@ped;

    -- Pedido 2 (Bruno): cartão em 3x, enviado
    INSERT INTO pedido (cod_cliente, data_pedido, status, valor_total) VALUES (@bru, '2026-11-15T14:30:00', 'Enviado', 0);
    SET @ped = SCOPE_IDENTITY();
    INSERT INTO item_pedido (cod_pedido, cod_produto, quantidade, preco_unitario)
        SELECT @ped, codigo, 1, preco FROM produto WHERE titulo IN ('Clean Code','O Hobbit');
    UPDATE pedido SET valor_total = (SELECT SUM(quantidade*preco_unitario) FROM item_pedido WHERE cod_pedido=@ped) WHERE codigo=@ped;
    INSERT INTO pagamento (cod_pedido, forma, status, valor, parcelas, data_pagamento)
        SELECT codigo, 'CARTAO', 'Aprovado', valor_total, 3, data_pedido FROM pedido WHERE codigo=@ped;

    -- Pedido 3 (Ana): boleto ainda pendente
    INSERT INTO pedido (cod_cliente, data_pedido, status, valor_total) VALUES (@ana, '2026-11-20T09:15:00', 'Aguardando pagamento', 0);
    SET @ped = SCOPE_IDENTITY();
    INSERT INTO item_pedido (cod_pedido, cod_produto, quantidade, preco_unitario)
        SELECT @ped, codigo, 2, preco FROM produto WHERE titulo = 'O Pequeno Príncipe';
    UPDATE pedido SET valor_total = (SELECT SUM(quantidade*preco_unitario) FROM item_pedido WHERE cod_pedido=@ped) WHERE codigo=@ped;
    INSERT INTO pagamento (cod_pedido, forma, status, valor, parcelas, data_pagamento)
        SELECT codigo, 'BOLETO', 'Pendente', valor_total, 1, NULL FROM pedido WHERE codigo=@ped;
END
GO
