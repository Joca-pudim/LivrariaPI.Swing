package model;

public class Usuario {
    private final int codigo;
    private final String nome;
    private final String tipo; // "Cliente" ou "Loja"

    public Usuario(int codigo, String nome, String tipo) {
        this.codigo = codigo;
        this.nome = nome;
        this.tipo = tipo;
    }
    public int getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
}