/* ============================================================
   Marketplace de Livros - (SQL Server)
   Banco: LivrariaPI
   ============================================================ */
IF DB_ID('LivrariaPI') IS NULL CREATE DATABASE LivrariaPI;
GO
USE LivrariaPI;
GO

IF OBJECT_ID('dbo.cliente','U') IS NULL
CREATE TABLE dbo.cliente (
    codigo      INT IDENTITY(1,1) PRIMARY KEY,
    nome        VARCHAR(100) NOT NULL,
    cpf         VARCHAR(14)  NOT NULL UNIQUE,
    data_aniv   DATE         NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    telefone    VARCHAR(20)  NOT NULL,
    cep         VARCHAR(9)   NOT NULL,
    endereco    VARCHAR(150) NOT NULL,
    numero_end  VARCHAR(10)  NOT NULL,
    complemento VARCHAR(60)  NULL,
    bairro      VARCHAR(60)  NOT NULL,
    cidade      VARCHAR(60)  NOT NULL,
    senha       VARCHAR(64)  NOT NULL            -- hash SHA-256 (hex)
);
GO
IF OBJECT_ID('dbo.loja','U') IS NULL
CREATE TABLE dbo.loja (
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    nome   VARCHAR(100) NOT NULL,
    email  VARCHAR(100) NOT NULL UNIQUE,
    senha  VARCHAR(64)  NOT NULL                 -- hash SHA-256 (hex)
);
GO
IF OBJECT_ID('dbo.categoria','U') IS NULL
CREATE TABLE dbo.categoria (
    codigo INT IDENTITY(1,1) PRIMARY KEY,
    nome   VARCHAR(60) NOT NULL UNIQUE
);
GO
IF OBJECT_ID('dbo.produto','U') IS NULL
CREATE TABLE dbo.produto (
    codigo        INT IDENTITY(1,1) PRIMARY KEY,
    titulo        VARCHAR(150) NOT NULL,
    autor         VARCHAR(100) NOT NULL,
    preco         DECIMAL(10,2) NOT NULL CHECK (preco >= 0),
    estoque       INT NOT NULL DEFAULT 0 CHECK (estoque >= 0),
    condicao      VARCHAR(20) NOT NULL,          
    cod_loja      INT NOT NULL,
    cod_categoria INT NOT NULL,
    CONSTRAINT fk_produto_loja      FOREIGN KEY (cod_loja)      REFERENCES dbo.loja(codigo),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (cod_categoria) REFERENCES dbo.categoria(codigo)
);
GO

-- Carrinho: um por cliente
IF OBJECT_ID('dbo.carrinho','U') IS NULL
CREATE TABLE dbo.carrinho (
    codigo       INT IDENTITY(1,1) PRIMARY KEY,
    cod_cliente  INT NOT NULL UNIQUE,
    data_criacao DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_carrinho_cliente FOREIGN KEY (cod_cliente) REFERENCES dbo.cliente(codigo)
);
GO
IF OBJECT_ID('dbo.item_carrinho','U') IS NULL
CREATE TABLE dbo.item_carrinho (
    cod_carrinho INT NOT NULL,
    cod_produto  INT NOT NULL,
    quantidade   INT NOT NULL CHECK (quantidade > 0),
    CONSTRAINT pk_item_carrinho PRIMARY KEY (cod_carrinho, cod_produto),
    CONSTRAINT fk_itemcar_carrinho FOREIGN KEY (cod_carrinho) REFERENCES dbo.carrinho(codigo) ON DELETE CASCADE,
    CONSTRAINT fk_itemcar_produto  FOREIGN KEY (cod_produto)  REFERENCES dbo.produto(codigo)
);
GO
-- Pedido (venda)
IF OBJECT_ID('dbo.pedido','U') IS NULL
CREATE TABLE dbo.pedido (
    codigo      INT IDENTITY(1,1) PRIMARY KEY,
    cod_cliente INT NOT NULL,
    data_pedido DATETIME NOT NULL DEFAULT GETDATE(),
    status      VARCHAR(30) NOT NULL DEFAULT 'Aguardando pagamento'
                CHECK (status IN ('Aguardando pagamento','Pago','Enviado','Entregue','Cancelado')),
    valor_total DECIMAL(10,2) NOT NULL CHECK (valor_total >= 0),
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cod_cliente) REFERENCES dbo.cliente(codigo)
);
GO
-- Itens do pedido (guarda o preco da epoca da compra)
IF OBJECT_ID('dbo.item_pedido','U') IS NULL
CREATE TABLE dbo.item_pedido (
    codigo         INT IDENTITY(1,1) PRIMARY KEY,
    cod_pedido     INT NOT NULL,
    cod_produto    INT NOT NULL,
    quantidade     INT NOT NULL CHECK (quantidade > 0),
    preco_unitario DECIMAL(10,2) NOT NULL CHECK (preco_unitario >= 0),
    CONSTRAINT uq_item_pedido UNIQUE (cod_pedido, cod_produto),
    CONSTRAINT fk_itemped_pedido  FOREIGN KEY (cod_pedido)  REFERENCES dbo.pedido(codigo),
    CONSTRAINT fk_itemped_produto FOREIGN KEY (cod_produto) REFERENCES dbo.produto(codigo)
);
GO
-- Pagamento (1 por pedido)
IF OBJECT_ID('dbo.pagamento','U') IS NULL
CREATE TABLE dbo.pagamento (
    codigo         INT IDENTITY(1,1) PRIMARY KEY,
    cod_pedido     INT NOT NULL UNIQUE,
    forma          VARCHAR(10) NOT NULL CHECK (forma IN ('PIX','CARTAO','BOLETO')),
    status         VARCHAR(10) NOT NULL CHECK (status IN ('Pendente','Aprovado','Recusado')),
    valor          DECIMAL(10,2) NOT NULL CHECK (valor >= 0),
    parcelas       INT NOT NULL DEFAULT 1 CHECK (parcelas BETWEEN 1 AND 12),
    data_pagamento DATETIME NULL,
    CONSTRAINT fk_pagamento_pedido FOREIGN KEY (cod_pedido) REFERENCES dbo.pedido(codigo)
);
GO
