package redesApp.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import redesApp.bean.ChatMessage;
import redesApp.bean.ChatMessage.Action;

/**
 *
 * @author jessi
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String,ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    //Método construtor para ServidorService
    public ServidorService() {

        try {
            serverSocket = new ServerSocket(5555);  //Inicializar o objeto serviceSocket, adicionando a porta com o cliente servidor

            System.out.println("Servidor online!");

            // Para manter esperando por uma nova conexão
            while(true) {
                socket = serverSocket.accept();     //Servidor se conecta e é criado um objeto socket

                new Thread(new ListenerSocket(socket)).start();     //Cria uma nova thread
            }

        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private class ListenerSocket implements Runnable {  // Ouvinte do servidor

        private ObjectOutputStream output;      //Saida, envio de mensagem do servidor
        private ObjectInputStream input;        //Recebe as mensagens enviadas pelo cliente

        //Método construtor
        public ListenerSocket(Socket socket) {
            try {
                // Pega entrada e saída do cliente"
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {

            ChatMessage message = null;

            try {
                // Ouvinte do servidor

                while ((message = (ChatMessage) input.readObject()) != null) {    // readObject recebe as mensagens enviadas pelo cliente
                    Action action = message.getAction();    //Recebe a action que está sendo enviada

                    if (action.equals(Action.CONNECT)) {    //Pedido de conexão
                        boolean isConnect = connect(message, output);   //message --> tem o que o cliente enviou para o servidor, output --> saida, envio de mensagem

                        if (isConnect) {    //Se a conexão ocorreu
                            mapOnlines.put(message.getNome(), output);  //Vai adicionar o nome do cliente na lista
                            sendOnLines();
                        }

                    } else if (action.equals(Action.DISCONECT)) {   //Cliente que sair do chat
                        desconectado(message, output );
                        sendOnLines();
                        return;
                    } else if (action.equals(Action.SEND_ONE)){ // mandar uma mensagem individual pra um cliente
                        sendOne(message);
                    } else  if (action.equals(Action.SEND_ALL)){ // o cliente manda mensagem pra todos os usuarios
                        sendAll(message);

                    }

                }
            } catch (IOException ex) {
                if (message != null) {
                    ChatMessage disconnectMessage = new ChatMessage();
                    disconnectMessage.setNome(message.getNome());
                    disconnectMessage.setTexto("deixou o chat");
                    disconnectMessage.setAction(Action.SEND_ONE);
                    desconectado(disconnectMessage, output);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean connect(ChatMessage message, ObjectOutputStream output){

        if (mapOnlines.size() == 0){      //Para não ter nome de cliente repetido
            message.setTexto("Conectado!");     //Mensagem para informar que se conectou no lado do cliente
            send(message, output);

            return true;    //Cliente Ok, retorna true
        }

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNome())) {    //Se objeto atual tiver a chave igual ao getNome --> return false
                message.setTexto("Não foi possivel conexão!");
                send(message, output);

                return false;
            } else {
                message.setTexto("Conexão estabelecida.");
                send(message, output);

                return true;
            }
        }

        return false;
    }
    // desconectar cliente do chat
    private void desconectado(ChatMessage message, ObjectOutputStream output){
        String nome = message.getNome();
        mapOnlines.remove(nome);

        ChatMessage disconnectMessage = new ChatMessage();
        disconnectMessage.setNome(nome);
        disconnectMessage.setTexto("deixou o chat");
        disconnectMessage.setAction(Action.SEND_ONE);

        System.out.println("Enviando desconexão: " + disconnectMessage.getNome() + " - " + disconnectMessage.getTexto());
        sendAll(disconnectMessage);

        System.out.println("O usuario " + nome + " saiu da sala." + "\n");

    }
    //função pra mandar uma mensagem/informação pra um usuario individual
    private void send(ChatMessage message, ObjectOutputStream output){
        try {
            output.writeObject(message); //Manda mensagem só pra um cliente
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void sendOne(ChatMessage message){
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {

            if (kv.getKey().equals(message.getNomeReservado())) {

                try {
                    kv.getValue().writeObject(message); //Manda mensagem só pra um cliente
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }
    //função pra mandar a mensagem pra todos menos pro quem envio
    private void sendAll(ChatMessage message) {

        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            //Se o nome cliente não for o que mandou mensagem
            if (!kv.getKey().equals(message.getNome())) {
                try {
                    kv.getValue().reset(); //Limpa o envio pra não repetir mensagem velhas no chat
                    kv.getValue().writeObject(message); // onde fica o conteudo da mensagem
                } catch (IOException e) {
                    System.out.println("Erro ao enviar para " + kv.getKey() + ": " + e.getMessage());
                }
            }
        }

    }

    private void sendOnLines() {

        Set<String> setNomes = new HashSet<String>(); // ncria uma lista pra guardar o nome dos clientes
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNomes.add(kv.getKey()); //pega os nomes de todos os clientes no servidorz

        }
        ChatMessage message = new ChatMessage();
        message.setAction(Action.USERS_ONLINE); //message podera fazer as ações de action.users_online
        message.setSetOnlines(setNomes); //message obtem os nomes dos clientes online
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            message.setNome(kv.getKey());
            try {

                kv.getValue().writeObject(message); //Envia a lista de quem tá online pros clientes

            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}

