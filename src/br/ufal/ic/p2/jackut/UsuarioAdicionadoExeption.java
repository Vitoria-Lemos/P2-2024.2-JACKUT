package br.ufal.ic.p2.jackut;

public class UsuarioAdicionadoExeption extends Exception{
    public UsuarioAdicionadoExeption() {
        super("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
    }
}
