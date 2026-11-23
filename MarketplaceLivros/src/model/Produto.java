package model;

import java.math.BigDecimal;

public class Produto {
    private final int codigo;
    private final String titulo, autor, condicao, loja, categoria;
    private final BigDecimal preco;
    private final int estoque;

    public Produto(int codigo, String titulo, String autor, BigDecimal preco,
                   int estoque, String condicao, String loja, String categoria) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.autor = autor;
        this.preco = preco;
        this.estoque = estoque;
        this.condicao = condicao;
        this.loja = loja;
        this.categoria = categoria;
    }
    public int getCodigo() { return codigo; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public BigDecimal getPreco() { return preco; }
    public int getEstoque() { return estoque; }
    public String getCondicao() { return condicao; }
    public String getLoja() { return loja; }
    public String getCategoria() { return categoria; }
}