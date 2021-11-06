/*
 * Made by TP 4208 - Tz. Antonios | ultrab
 */
package UDPClientServer;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

    String username = null, s, data;
    String[] sp;
    JFrame frame = new JFrame("UDP Client / Server Messenger - TP4208");
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);

    InetAddress serverAddress = null;
    int serverPort = 4242;
    DatagramSocket socket = null;

    public Client() {
        // User Interface
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, "North");
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame.pack();

        // Add Listeners
        textField.addActionListener((ActionEvent e) -> {
            try {
                NetworkTools.sendData(textField.getText(), serverAddress, serverPort, socket);
                if (textField.getText().equals("/bye")) {

                    System.exit(0);
                }
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            textField.setText("");
        }
        );
    }

    /**
     * Prompt for & return the address of the server.
     */
    private String getServerAddress() throws UnknownHostException {
        String serverIP = null;
        do {
            serverIP = (String) JOptionPane.showInputDialog(
                    frame,
                    "Enter IP Address of the Server:",
                    "Welcome to Chatter",
                    JOptionPane.QUESTION_MESSAGE, null, null, InetAddress.getLocalHost().getHostAddress());
        } while ((serverIP == null || (serverIP != null && ("".equals(serverIP))))); // Do not procced if null or user press the Cancel button
        return serverIP;
    }

    /**
     * Prompt for & return the desired screen name.
     */
    private String getName() {
        String name = null;
        do {
            name = JOptionPane.showInputDialog(
                    frame,
                    "username:",
                    "Please enter a username",
                    JOptionPane.PLAIN_MESSAGE);
        } while ((name == null || (name != null && ("".equals(name))))); // Do not procced if null or user press the Cancel button
        return name;
    }

    private void run() throws IOException {
        //Metatroph IP paralipth apo String se InetAddress
        String serverIP = getServerAddress();
        try {
            serverAddress = InetAddress.getByName(serverIP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // Creates a socker for send & recieve
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //Diaxeirish Sign In. Stelnw /signin--username, to kanw split ws (--) ston server gia na mpei stin if tou /signin kai na parw to username tou xristi
        String newUser = "/signin";
        username = getName();
        newUser = newUser.concat("--" + username);
        frame.setTitle("UDP Client / Server Messenger - TP4208 | " + username);
        /* System.out.println(newUser); */
        NetworkTools.sendData(newUser, serverAddress, serverPort, socket);
        s = NetworkTools.receiveData(socket);
        sp = s.split("#"); //sp[0] = keimeno mhnymatos, sp[1] = IP apostolea san String, sp[2]= port apostolea san String
        System.out.println("Server: " + sp[0]);
        messageArea.append("Avaliable commands: /who, /bye" + "\n");
        messageArea.append("Sever: " + sp[0] + "\n");
        textField.setEditable(true);

        while (true) {
            data = null;
            try {
                String s = NetworkTools.receiveData(socket);
                //sp[0] = message, sp[1] = Sender IP, sp[2]= Sender Port
                // the IP and the port are the server's as the server recieve and forwarding the message
                String[] sp = s.split("#");
                System.out.println("Message: " + sp[0] + " from " + sp[1] + ":" + Integer.parseInt(sp[2]));
                messageArea.append(sp[0] + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}
