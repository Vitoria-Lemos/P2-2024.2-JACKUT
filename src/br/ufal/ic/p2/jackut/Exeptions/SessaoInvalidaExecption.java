package br.ufal.ic.p2.jackut.Exeptions;

public class SessaoInvalidaExecption extends Exception{
    public SessaoInvalidaExecption() {
        super("Sess�o inv�lida.");
    }

    public static class UsuarioAdicionadoExeption extends Exception{
        public UsuarioAdicionadoExeption() {
            super("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
        }
    }
}
