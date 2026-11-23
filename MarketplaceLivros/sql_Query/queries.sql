/* ============================================================
   Marketplace de Livros - queries.sql
   Todas as consultas chamadas pelo sistema Java.
   (? = parametro do PreparedStatement; aqui trocados por exemplos)
   ============================================================ */
USE LivrariaPI;
GO

/* ------------------------------------------------------------
   A) OPERACOES COM CONDICIONAIS / FUNCOES DE AGREGACAO  (7)
   ------------------------------------------------------------ */

-- A1. Total vendido por loja (SUM, COUNT, GROUP BY)         [RelatorioDAO.totalVendidoPorLoja]
SELECT l.nome AS loja, COUNT(DISTINCT pe.codigo) AS pedidos,
       SUM(ip.quantidade) AS livros_vendidos,
       SUM(ip.quantidade * ip.preco_unitario) AS total_vendido
FROM loja l
JOIN produto p      ON p.cod_loja = l.codigo
JOIN item_pedido ip ON ip.cod_produto = p.codigo
JOIN pedido pe      ON pe.codigo = ip.cod_pedido
WHERE pe.status <> 'Cancelado'
GROUP BY l.codigo, l.nome
ORDER BY total_vendido DESC;

-- A2. Quantidade de pedidos por status (COUNT, SUM, GROUP BY) [RelatorioDAO.pedidosPorStatus]
SELECT status, COUNT(*) AS quantidade, SUM(valor_total) AS valor_total
FROM pedido GROUP BY status ORDER BY quantidade DESC;

-- A3. Ticket medio dos pedidos (AVG, MIN, MAX)               [RelatorioDAO.ticketMedio]
SELECT COUNT(*) AS pedidos, AVG(valor_total) AS ticket_medio,
       MIN(valor_total) AS menor_pedido, MAX(valor_total) AS maior_pedido
FROM pedido WHERE status <> 'Cancelado';

-- A4. Livros com estoque baixo (condicional WHERE)           [RelatorioDAO.estoqueBaixo]
SELECT p.codigo, p.titulo, c.nome AS categoria, p.estoque
FROM produto p JOIN categoria c ON c.codigo = p.cod_categoria
WHERE p.cod_loja = 1 AND p.estoque <= 5
ORDER BY p.estoque, p.titulo;

-- A5. Mais caro / mais barato por categoria (MIN, MAX, AVG)  [RelatorioDAO.extremosPorCategoria]
SELECT c.nome AS categoria, COUNT(*) AS livros, MIN(p.preco) AS mais_barato,
       MAX(p.preco) AS mais_caro, AVG(p.preco) AS preco_medio
FROM categoria c JOIN produto p ON p.cod_categoria = c.codigo
GROUP BY c.codigo, c.nome ORDER BY c.nome;

-- A6. Total do carrinho do cliente (SUM)                     [CarrinhoDAO.total]
SELECT COALESCE(SUM(p.preco * ic.quantidade), 0)
FROM carrinho c
JOIN item_carrinho ic ON ic.cod_carrinho = c.codigo
JOIN produto p        ON p.codigo = ic.cod_produto
WHERE c.cod_cliente = 1;

-- A7. Cliente ja cadastrado? (COUNT)                         [ClienteDAO.existe - codigo anterior]
SELECT COUNT(*) FROM cliente WHERE email = 'ana@email.com' OR cpf = '11111111111';

/* ------------------------------------------------------------
   B) OPERACOES COM JUNCOES DE TABELAS  (7)
   ------------------------------------------------------------ */

-- B1. Catalogo: produto + loja + categoria (filtros opcionais) [ProdutoDAO.buscar - codigo anterior]
SELECT p.codigo, p.titulo, p.autor, p.preco, p.estoque, p.condicao,
       l.nome AS loja, c.nome AS categoria
FROM produto p
JOIN loja l      ON l.codigo = p.cod_loja
JOIN categoria c ON c.codigo = p.cod_categoria
WHERE p.estoque > 0 AND (p.titulo LIKE '%dom%' OR p.autor LIKE '%dom%')
ORDER BY p.titulo;

-- B2. Itens do carrinho com livro e loja                     [CarrinhoDAO.listar]
SELECT p.codigo, p.titulo, p.autor, l.nome AS loja, p.preco, ic.quantidade, p.estoque
FROM carrinho c
JOIN item_carrinho ic ON ic.cod_carrinho = c.codigo
JOIN produto p        ON p.codigo = ic.cod_produto
JOIN loja l           ON l.codigo = p.cod_loja
WHERE c.cod_cliente = 1 ORDER BY p.titulo;

-- B3. Historico do cliente: pedido + itens + livro + loja + pagamento  [PedidoDAO.historicoItens]
SELECT pe.codigo AS pedido, pe.data_pedido, p.titulo AS livro, l.nome AS loja,
       ip.quantidade, ip.preco_unitario, (ip.quantidade * ip.preco_unitario) AS subtotal,
       pe.status AS status_pedido, pg.status AS status_pagamento
FROM pedido pe
JOIN item_pedido ip ON ip.cod_pedido = pe.codigo
JOIN produto p      ON p.codigo = ip.cod_produto
JOIN loja l         ON l.codigo = p.cod_loja
LEFT JOIN pagamento pg ON pg.cod_pedido = pe.codigo
WHERE pe.cod_cliente = 1
ORDER BY pe.data_pedido DESC, pe.codigo DESC, p.titulo;

-- B4. Pedidos com status do pagamento                        [PedidoDAO.pedidosComPagamento]
SELECT pe.codigo AS pedido, pe.data_pedido, pe.valor_total, pg.forma, pg.parcelas,
       pg.status AS status_pagamento, pe.status AS status_pedido
FROM pedido pe
JOIN pagamento pg ON pg.cod_pedido = pe.codigo
JOIN cliente c    ON c.codigo = pe.cod_cliente
WHERE c.codigo = 1 ORDER BY pe.data_pedido DESC, pe.codigo DESC;

-- B5. Vendas da loja por livro e comprador                   [RelatorioDAO.vendasDaLoja]
SELECT pe.codigo AS pedido, pe.data_pedido, p.titulo AS livro, cl.nome AS comprador,
       ip.quantidade, ip.preco_unitario, (ip.quantidade * ip.preco_unitario) AS subtotal,
       pe.status AS status_pedido
FROM item_pedido ip
JOIN pedido pe  ON pe.codigo = ip.cod_pedido
JOIN produto p  ON p.codigo = ip.cod_produto
JOIN cliente cl ON cl.codigo = pe.cod_cliente
WHERE p.cod_loja = 1 ORDER BY pe.data_pedido DESC, pe.codigo DESC;

-- B6. Livros da loja com categoria                           [LivroDAO.listarDaLoja]
SELECT p.codigo, p.titulo, p.autor, c.nome AS categoria, p.condicao, p.preco, p.estoque
FROM produto p JOIN categoria c ON c.codigo = p.cod_categoria
WHERE p.cod_loja = 1 ORDER BY p.titulo;

-- B7. Itens do carrinho no fechamento da compra (mesma juncao de B2, dentro da transacao)
--     [PedidoDAO.finalizarCompra]
