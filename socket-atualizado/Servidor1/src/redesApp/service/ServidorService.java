package redesApp.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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
                // Os dois objetos passam a ser do cliente que se conectou
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
                        }
                        
                    } else if (action.equals(Action.DISCONECT)) {   //Cliente que sair do chat
                        desconectado(message, output );
                        return;
                    } else if (action.equals(Action.SEND_ONE)){ // mandar uma mensagem individual pra um cliente
                        sendOne(message, output);
                    } else  if (action.equals(Action.SEND_ALL)){ // o cliente manda mensagem pra todos os usuarios
                        sendAll(message);
                        
                    }else if (action.equals(Action.USERS_ONLINE)) { // Lista de usuários online
                        
                    }
                    
                }
            } catch (IOException ex) {
                desconectado(message, output);
                System.out.println(message.getNome() + "deixou o chat.");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private boolean connect(ChatMessage message, ObjectOutputStream output){
        
        if (mapOnlines.size() == 0){      //Para não ter nome de cliente repetido
            message.setTexto("Conectado!");     //Mensagem para informar que se conectou no lado do cliente
            sendOne(message, output);
            
            return true;    //Cliente Ok, retorna true
        }
        
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getNome())) {    //Se objeto atual tiver a chave igual ao getNome --> return false
                message.setTexto("Não foi possivel conexão!");
                sendOne(message, output);
                
                return false;
            } else {
                message.setTexto("Conexão estabelecida.");
                sendOne(message, output);
                
                return true;
            }
        }
        
        return false;
    }
     // desconectar cliente do chat
    private void desconectado(ChatMessage message, ObjectOutputStream output){
        mapOnlines.remove(message.getNome());

        message.setTexto("deixou o chat");
        message.setAction(Action.SEND_ONE);
        sendAll(message);

        System.out.println("O usuario" + message.getNome() + "saiu da sala");
        
    }
    //função pra mandar uma mensagem/informação pra um usuario individual
    private void sendOne(ChatMessage message, ObjectOutputStream output){
        try {
            output.writeObject(message); //Envia a mensagem a partir do objeto write 
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //função pro usuario enviar uma mensagem pra todos com exceção dele
    private void sendAll(ChatMessage message) {
        for (Map.Entry<String, ObjectOutputStream> kv: mapOnlines.entrySet()){
            if (!kv.getKey().equals(message.getNome())){
                try {
                    kv.getValue().writeObject(message); // enviar a mensagem do usuario pra todos os clientes online
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
