package br.ufal.ic.p2.jackut.Exeptions;

public class AmigoJaExistenteException extends Exception{
    public AmigoJaExistenteException() {
        super("Usu�rio j� est� adicionado como amigo.");
    }
}
