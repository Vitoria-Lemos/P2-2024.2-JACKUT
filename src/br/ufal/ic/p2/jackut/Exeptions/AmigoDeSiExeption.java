package br.ufal.ic.p2.jackut.Exeptions;

public class AmigoDeSiExeption extends Exception {
    public AmigoDeSiExeption() {
        super("Usu�rio n�o pode adicionar a si mesmo como amigo.");
    }
}
