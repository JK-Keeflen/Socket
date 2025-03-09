package redesApp.frame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.border.Border;

import redesApp.bean.ChatMessage;
import redesApp.bean.ChatMessage.Action;
import redesApp.service.ClienteService;

/**
 *
 * @author jessi
 */
public class ClienteFrame extends javax.swing.JFrame {

    private Socket socket;
    private ChatMessage message;
    private ClienteService service;

    /**
     * Creates new form ClienteFrame
     */
    public ClienteFrame() {
        initComponents();
    }

    private class ListenerSocket implements Runnable {

        private ObjectInputStream input;

        //Método construtor
        public ListenerSocket(Socket socket) {
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {

            ChatMessage message = null;

            try {

                while ( (message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    if (action.equals(Action.CONNECT)){ //Faz a conexão do cliente - servidor".
                        conectado(message);

                    } else if (action.equals(Action.DISCONECT)){ //desconectar o cliente
                        desconectado();
                        socket.close();

                    } else if (action.equals(Action.SEND_ONE)) { //mandar a mensagem indidualmente pro cliente
                        //sendOne(message);
                        receive(message);

                    } else if (action.equals(Action.SEND_ALL)) { // mandar a mensagem pra todos os clientes
                        receive(message);
                    }else if (action.equals(Action.USERS_ONLINE)) {
                        refreshOnlines(message);      //Atualizar

                    }

                }
            } catch (IOException ex) {
                desconectado();
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    // verificar se o cliente se conectou ao servidor
    private void conectado(ChatMessage message) {
        //aqui se a conexão falhou
        if(message.getTexto().equals("NÃO")){
            this.txtName.setText("");
            JOptionPane.showMessageDialog(this, "A conexão falhou! tente novamente com um novo nome.");
            return;
        }
        // aqui caso se a conexão ocorreu tudo bem
        this.message = message;
        this.btnConectar.setEnabled(false);
        this.txtName.setEditable(false);

        this.btnEnviar.setEnabled(true);
        this.btnSair.setEnabled(true);
        this.txtAreaSend.setEnabled(true);
        this.txtAreaReceive.setEnabled(true);
        this.btnLimpar.setEnabled(true);
        this.btnAtualizar.setEnabled(true);

        JOptionPane.showMessageDialog(this, "Você está conectado no chat.");
    }

    //caso o cliente se desconectar do chat
    private void desconectado() {
        this.btnConectar.setEnabled(true);
        this.txtName.setEditable(true);

        this.btnEnviar.setEnabled(false);
        this.btnSair.setEnabled(false);
        this.txtAreaSend.setEnabled(false);
        this.txtAreaReceive.setEnabled(false);
        this.btnLimpar.setEnabled(false);
        this.btnAtualizar.setEnabled(false);

        JOptionPane.showMessageDialog(this, "Você saiu do chat.");

    }
    // Mostra a menssagem recebida no chat
    private void receive(ChatMessage message) {
        this.txtAreaReceive.append(message.getNome() + ": " + message.getTexto() + "\n");

    }

    private void refreshOnlines(ChatMessage message) {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtName = new javax.swing.JTextField();
        btnConectar = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnAtualizar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        listOnlines = new javax.swing.JList<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaReceive = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaSend = new javax.swing.JTextArea();
        btnEnviar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder((Border) null));

        btnConectar.setText("Conectar");
        btnConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConectarActionPerformed(evt);
            }
        });

        btnSair.setText("Sair");
        btnSair.setEnabled(false);
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnConectar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnConectar)
                                .addComponent(btnSair))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder((Border) null));

        btnAtualizar.setText("Atualizar");
        btnAtualizar.setEnabled(false);
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        listOnlines.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(listOnlines);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3)
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(43, Short.MAX_VALUE)
                                .addComponent(btnAtualizar)
                                .addGap(41, 41, 41))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAtualizar)
                                .addGap(12, 12, 12))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        txtAreaReceive.setEditable(false);
        txtAreaReceive.setColumns(20);
        txtAreaReceive.setRows(5);
        txtAreaReceive.setEnabled(false);
        jScrollPane1.setViewportView(txtAreaReceive);

        txtAreaSend.setColumns(20);
        txtAreaSend.setRows(5);
        txtAreaSend.setEnabled(false);
        jScrollPane2.setViewportView(txtAreaSend);

        btnEnviar.setText("Enviar");
        btnEnviar.setEnabled(false);
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        btnLimpar.setText("Limpar");
        btnLimpar.setEnabled(false);
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                                        .addComponent(jScrollPane2))
                                .addContainerGap())
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnLimpar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEnviar)
                                .addGap(19, 19, 19))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnEnviar)
                                        .addComponent(btnLimpar))
                                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void btnConectarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        String name = this.txtName.getText();        //Vai receber o nome digitado na interface

        if (!name.isEmpty()) {
            this.message = new ChatMessage();
            this.message.setAction(Action.CONNECT);
            this.message.setNome(name);

            //Significa que ainda não foi criado
            this.service = new ClienteService();
            this.socket = this.service.connect();

            new Thread(new ListenerSocket(this.socket)).start(); //Thread que vai iniciar o ouvinte


            this.service.send(message);
        }

    }

    //função sair do chat clicando no botão 'sair'
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {

        ChatMessage disconnectMessage = new ChatMessage(); //um novo objeto da classe
        disconnectMessage.setNome(this.message.getNome()); //pega o nome do cliente
        disconnectMessage.setAction(Action.DISCONECT);     // o objeto recebe os atributos de action disconect
        this.service.send(disconnectMessage);      // desconecta o cliente atravez da ação disconect
        try {
            socket.close(); // Força desconexão
        } catch (IOException ex) {
            Logger.getLogger(ClienteFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {
        this.txtAreaSend.setText("");
    }

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {

        String texto = this.txtAreaSend.getText(); //pegar o texto digitado pelo cliente
        String nome = this.message.getNome(); //pegar o nome do cliente e guardada

        if(!texto.isEmpty()) {

            this.message = new ChatMessage(); //limpar o objeto message
            this.message.setNome(nome); //nome do cliente
            this.message.setTexto(texto); // texto/mensagem que ele enviou
            this.message.setAction(Action.SEND_ALL); //manda para todos os usuarios
            this.txtAreaReceive.append("você: " + texto + "\n");
            this.service.send(this.message); //enviar pro chat
        }
        this.txtAreaSend.setText(""); //limpar o local de texto que o usuario digita


    }

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    /**
     * @param args the command line arguments
     */




    // Variables declaration - do not modify
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnConectar;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnSair;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList<String> listOnlines;
    private javax.swing.JTextArea txtAreaReceive;
    private javax.swing.JTextArea txtAreaSend;
    private javax.swing.JTextField txtName;
    // End of variables declaration
}
