package br.ufal.ic.p2.jackut;

import br.ufal.ic.p2.jackut.exeptions.CriacaoUsuarioExeption;
import java.io.*;
import java.util.*;

public class Facade implements Serializable {

    private static final long serialVersionUID = 2L;
    private static final String ARQUIVO_DADOS = "arquivo.dat";
    private List<Users> usersList = new ArrayList<>();
    private List<Sessao> sessoesList = new ArrayList<>();

    // Classe interna para substituir o Map de sess�es
    private static class Sessao implements Serializable {
        private final String idSessao;
        private final String login;

        public Sessao(String idSessao, String login) {
            this.idSessao = idSessao;
            this.login = login;
        }
    }


    public Facade() {
        this.usersList = new ArrayList<>();
        this.sessoesList = new ArrayList<>();
    }

    public void zerarSistema() {
        this.usersList = new ArrayList<>();
        this.sessoesList = new ArrayList<>();
    }

    public String getAtributoUsuario(String login, String atributo) throws CriacaoUsuarioExeption {
        Users usuario = buscarUsuario(login);

        if ("nome".equalsIgnoreCase(atributo)) {
            return usuario.getNome();
        }

        String valor = usuario.getAtributo(atributo);

        if (valor == null || valor.isEmpty()) {
            throw new CriacaoUsuarioExeption("Atributo n�o preenchido.");
        }

        return valor;
    }

    public void adicionarAmigo(String idSessao, String amigo) throws CriacaoUsuarioExeption {
        // Passo 1: Tenta obter o usu�rio mesmo com sess�o inv�lida
        Users usuarioAtual;
        try {
            usuarioAtual = getUsuarioPorSessao(idSessao);
        } catch (CriacaoUsuarioExeption e) {
            // For�a a verifica��o de usu�rio n�o cadastrado
            usuarioAtual = buscarUsuario("usu�rio_inexistente_123");
        }

        Users usuarioAmigo = buscarUsuario(amigo);
        usuarioAtual.adicionarAmigo(amigo, usuarioAmigo);
    }


    public void criarUsuario(String login, String senha, String nome) throws CriacaoUsuarioExeption {
        validarCredenciais(login, senha);
        verificarLoginExistente(login);
        usersList.add(new Users(login, senha, nome));
    }

    private void validarCredenciais(String login, String senha) throws CriacaoUsuarioExeption {
        if (login == null || login.trim().isEmpty()) {
            throw new CriacaoUsuarioExeption("Login inv�lido.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new CriacaoUsuarioExeption("Senha inv�lida.");
        }
    }

    private void verificarLoginExistente(String login) throws CriacaoUsuarioExeption {
        for (Users user : usersList) {
            if (user.getLogin().equals(login)) {
                throw new CriacaoUsuarioExeption("Conta com esse nome j� existe.");
            }
        }
    }

    public String abrirSessao(String login, String senha) throws CriacaoUsuarioExeption {
        Users usuario = validarLoginSenha(login, senha);
        String idSessao = UUID.randomUUID().toString();
        sessoesList.add(new Sessao(idSessao, login));
        return idSessao;
    }

    private Users validarLoginSenha(String login, String senha) throws CriacaoUsuarioExeption {
        for (Users user : usersList) {
            if (user.getLogin().equals(login) && user.getSenha().equals(senha)) {
                return user;
            }
        }
        throw new CriacaoUsuarioExeption("Login ou senha inv�lidos.");
    }

    public void encerrarSistema() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            oos.writeObject(this);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public static Facade iniciarSistema() {
        File arquivo = new File(ARQUIVO_DADOS);
        if (arquivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DADOS))) {
                return (Facade) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Erro ao carregar dados: " + e.getMessage());
            }
        }
        return new Facade();
    }


    public void editarPerfil(String idSessao, String atributo, String valor) throws CriacaoUsuarioExeption {
        // Primeiro busca o usu�rio (pode lan�ar "Usu�rio n�o cadastrado")
        String login = null;
        for (Sessao s : sessoesList) {
            if (s.idSessao.equals(idSessao)) {
                login = s.login;
                break;
            }
        }

        Users usuario = buscarUsuario(login); // Lan�a exce��o se login for null/n�o existir

        // Depois valida a sess�o
        validarSessao(idSessao);

        // Valida o atributo
        if (atributo == null || atributo.trim().isEmpty()) {
            throw new CriacaoUsuarioExeption("Atributo inv�lido");
        }

        usuario.setAtributo(atributo, valor);
    }

    private Users getUsuarioPorSessao(String idSessao) throws CriacaoUsuarioExeption {
        for (Sessao s : sessoesList) {
            if (s.idSessao.equals(idSessao)) {
                return buscarUsuario(s.login);
            }
        }
        throw new CriacaoUsuarioExeption("Usu�rio n�o cadastrado."); // Altera a mensagem aqui
    }
    private void validarSessao(String idSessao) throws CriacaoUsuarioExeption {
        for (Sessao s : sessoesList) {
            if (s.idSessao.equals(idSessao)) {
                return;
            }
        }
        throw new CriacaoUsuarioExeption("Sess�o inv�lida");
    }


    private Users buscarUsuario(String login) throws CriacaoUsuarioExeption {
        if (login == null) {
            throw new CriacaoUsuarioExeption("Usu�rio n�o cadastrado.");
        }

        for (Users u : usersList) {
            if (u.getLogin().equals(login)) {
                return u;
            }
        }
        throw new CriacaoUsuarioExeption("Usu�rio n�o cadastrado.");
    }


    public boolean ehAmigo(String login, String amigo) throws CriacaoUsuarioExeption {
        Users usuario1 = buscarUsuario(login);
        Users usuario2 = buscarUsuario(amigo);
        return usuario1.ehAmigo(amigo) && usuario2.ehAmigo(login);
    }

    public String getAmigos(String login) throws CriacaoUsuarioExeption {
        List<String> amigos = buscarUsuario(login).getAmigos();
        return formatarComoConjunto(amigos);
    }

    private String formatarComoConjunto(List<String> lista) {
        // Mant�m a ordem original de inser��o
        return "{" + String.join(",", lista) + "}";
    }


}