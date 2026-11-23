# Plano de Testes — Marketplace de Livros

Pré-condição: `schema.sql` e `dados_exemplo.sql` executados; driver em `lib/`.
Preencha a coluna **Resultado** (OK / Falhou) ao executar.

| ID | Funcionalidade | Passos | Resultado esperado | Resultado |
|---|---|---|---|---|
| T01 | Cadastro de cliente | Login > Cadastrar > dados válidos > Salvar | Mensagem de sucesso; cliente na tabela `cliente` com senha em hash | |
| T02 | Cadastro duplicado | Repetir T01 com o mesmo e-mail/CPF | Aviso "Já existe um cadastro" | |
| T03 | Cadastro inválido | CPF com 10 dígitos / e-mail sem @ / senhas diferentes | Mensagem de validação correspondente | |
| T04 | Login cliente | ana@email.com / 123456, tipo Cliente | "Bem-vindo(a), Ana Souza!" | |
| T05 | Login inválido | Senha errada | "E-mail ou senha incorretos." | |
| T06 | Login loja | sebo@loja.com / 123456, tipo Loja | "Bem-vindo(a), Sebo do Centro!" | |
| T07 | Pesquisa no catálogo | Buscar "dom"; filtrar categoria e preço | Só livros que atendem aos filtros e com estoque > 0 | |
| T08 | Adicionar ao carrinho | Cliente logado, selecionar livro > Compras > Adicionar > qtd 1 | "Livro adicionado ao carrinho" | |
| T09 | Estoque no carrinho | Adicionar quantidade maior que o estoque | Aviso "Estoque insuficiente"; carrinho não muda | |
| T10 | Ver carrinho | Compras > Meu carrinho | Itens, subtotais e total corretos | |
| T11 | Remover / esvaziar | Remover item; depois Esvaziar | Tabela e total atualizam | |
| T12 | Compra com PIX | Finalizar > PIX | Pedido "Pago"; pagamento "Aprovado"; estoque reduzido; carrinho vazio | |
| T13 | Compra com cartão | Finalizar > Cartão, 3 parcelas | Pagamento "Aprovado" com 3 parcelas | |
| T14 | Compra com boleto | Finalizar > Boleto | Pedido "Aguardando pagamento"; pagamento "Pendente" | |
| T15 | Confirmar boleto | Meus pedidos > selecionar pedido pendente > Confirmar | Pagamento "Aprovado"; pedido "Pago" | |
| T16 | Atomicidade da compra | Dois clientes compram o último exemplar de um livro | O segundo recebe "Estoque insuficiente"; nenhum pedido parcial é gravado | |
| T17 | Histórico | Meus pedidos e pagamentos | Abas "Pedidos e pagamentos" e "Livros comprados" corretas | |
| T18 | Cadastro de livro | Loja > Meus livros > preencher > Cadastrar | Livro aparece na lista e no catálogo | |
| T19 | Alterar preço/estoque | Selecionar livro > Alterar | Valores atualizados | |
| T20 | Perfil errado | Cliente abre "Minha loja" (e vice-versa) | Aviso de acesso | |
| T21 | Relatório: vendas da loja | Relatórios > Vendas da minha loja | Só vendas de livros da loja logada | |
| T22 | Relatório: total por loja | Relatórios > Total vendido por loja | Ranking com soma correta (conferir com `queries.sql` A1) | |
| T23 | Relatório: pedidos por status | Relatórios > Pedidos por status | Contagens iguais a `SELECT status, COUNT(*) FROM pedido GROUP BY status` | |
| T24 | Relatório: ticket médio | Relatórios > Ticket médio | AVG/MIN/MAX corretos | |
| T25 | Relatório: estoque baixo | Informar limite 5 | Só livros da loja com estoque <= 5 | |
| T26 | Relatório: extremos por categoria | Relatórios > Livro mais caro/mais barato | MIN/MAX por categoria corretos | |
| T27 | Execução do JAR | `java -jar dist/MarketplaceLivros.jar` | Abre a tela de login | |

## Validações já realizadas no desenvolvimento
- Compilação completa do projeto sem erros.
- As consultas de `sql/queries.sql` (grupos A e B) foram executadas sobre o schema e os dados de exemplo e retornaram os resultados esperados.
