package redesApp.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author jessi
 */
public class ChatMessage implements Serializable {
    private String nome;
    private String texto;
    private String nomeReservado;   //Armazenar o nome do cliente
    private Set<String> setOnlines = new HashSet<String>();   // A lista armazena todos os clientes conectados no servidor, durante o tempo que o servidor estiver ativo
    private Action action;  //Para cada mensagem vai dizer a ação --> criar uma conexão, sair do chat, enviar mensagem reservada, mensagem para todos os usuarios, fazendo atualização da lista

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getNomeReservado() {
        return nomeReservado;
    }

    public void setNomeReservado(String nomeReservado) {
        this.nomeReservado = nomeReservado;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
    
    
    public enum Action {
        CONNECT, DISCONECT, SEND_ONE, SEND_ALL, USERS_ONLINE
    }
    
    
}
