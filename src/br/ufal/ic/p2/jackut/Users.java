package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.exeptions.CriacaoUsuarioExeption;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {
    private static final long serialVersionUID = 2L;
    private final String login;
    private final String senha;
    private final String nome;
    private final List<Atributo> atributos = new ArrayList<>(); // Substitui o Map por lista
    private final List<String> amigos = new ArrayList<>();
    private final List<String> solicitacoesEnviadas = new ArrayList<>();

    // Classe interna para substituir o Map de atributos
    private static class Atributo implements Serializable {
        private final String chave;
        private final String valor;

        public Atributo(String chave, String valor) {
            this.chave = chave.toLowerCase();
            this.valor = valor;
        }
    }

    public Users(String login, String senha, String nome) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
    }

    // M�todo para adicionar amigos (s� com listas)
    public void adicionarAmigo(String amigo, Users usuarioAmigo) throws CriacaoUsuarioExeption {
        if (amigo.equals(this.login)) {
            throw new CriacaoUsuarioExeption("Usu�rio n�o pode adicionar a si mesmo como amigo.");
        }

        // Verifica primeiro se j� � amigo
        if (ehAmigo(amigo)) {
            throw new CriacaoUsuarioExeption("Usu�rio j� est� adicionado como amigo.");
        }

        // Verifica solicita��o pendente
        if (contemSolicitacao(amigo)) {
            throw new CriacaoUsuarioExeption("Usu�rio j� est� adicionado como amigo, esperando aceita��o do convite.");
        }

        if (usuarioAmigo.contemSolicitacao(this.login)) {
            // Confirma amizade m�tua
            this.amigos.add(amigo);
            usuarioAmigo.amigos.add(this.login);

            // Remove solicita��es
            this.removerSolicitacao(amigo);
            usuarioAmigo.removerSolicitacao(this.login);
        } else {
            this.solicitacoesEnviadas.add(amigo);
        }
    }

    // M�todos auxiliares
    public boolean ehAmigo(String outroUsuario) {
        return buscarNaLista(this.amigos, outroUsuario);
    }

    private boolean contemSolicitacao(String login) {
        return buscarNaLista(this.solicitacoesEnviadas, login);
    }

    private boolean buscarNaLista(List<String> lista, String item) {
        for (String s : lista) {
            if (s.equals(item)) return true;
        }
        return false;
    }

    private void removerSolicitacao(String login) {
        for (int i = 0; i < solicitacoesEnviadas.size(); i++) {
            if (solicitacoesEnviadas.get(i).equals(login)) {
                solicitacoesEnviadas.remove(i);
                break;
            }
        }
    }

    // M�todos de atributos (sem Map)
    public void setAtributo(String chave, String valor) {
        // Remove atributo existente
        for (int i = 0; i < atributos.size(); i++) {
            if (atributos.get(i).chave.equalsIgnoreCase(chave)) {
                atributos.remove(i);
                break;
            }
        }
        atributos.add(new Atributo(chave, valor));
    }

    public String getAtributo(String chave) {
        for (Atributo a : atributos) {
            if (a.chave.equalsIgnoreCase(chave)) {
                return a.valor;
            }
        }
        return null;
    }

    // Getters
    public List<String> getAmigos() { return new ArrayList<>(amigos); }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public String getNome() { return nome; }
}