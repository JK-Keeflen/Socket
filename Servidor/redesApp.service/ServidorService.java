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
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        
    }
}
