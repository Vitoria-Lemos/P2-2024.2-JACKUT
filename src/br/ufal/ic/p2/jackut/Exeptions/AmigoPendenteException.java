package br.ufal.ic.p2.jackut.Exeptions;

public class AmigoPendenteException extends Exception {
    public AmigoPendenteException() {
        super("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
    }
}