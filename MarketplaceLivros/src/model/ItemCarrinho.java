package model;

import java.math.BigDecimal;

public class ItemCarrinho {
    private final int codProduto;
    private final String titulo, autor, loja;
    private final BigDecimal preco;
    private final int quantidade, estoque;

    public ItemCarrinho(int codProduto, String titulo, String autor, String loja,
                        BigDecimal preco, int quantidade, int estoque) {
        this.codProduto = codProduto;
        this.titulo = titulo;
        this.autor = autor;
        this.loja = loja;
        this.preco = preco;
        this.quantidade = quantidade;
        this.estoque = estoque;
    }
    public int getCodProduto() { return codProduto; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getLoja() { return loja; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public int getEstoque() { return estoque; }
    public BigDecimal getSubtotal() { return preco.multiply(BigDecimal.valueOf(quantidade)); }
}
