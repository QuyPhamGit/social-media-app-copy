import java.net.Socket;
import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
/**
 * Team Project -- Server
 *
 * Defines a Server which accepts clients and
 * starts UserHandler threads to manage network
 * interaction with multiple concurrent clients.
 *
 * @author Ryan Jo, lab sec 002
 * @author Omkar Govil-Nair, lab sec 002
 *
 * @version November 17, 2024
 *
 */
public class Server implements ServerInterface {
    private static final int PORT = 5000;  // constant defining port number

    public Database db;   // stores a database object to be used by all UserHandlers
    private ServerSocket serverSocket;  // stores a serverSocket to accept clients at
    public ArrayList<UserHandler> activeUsers;  // ArrayList of active UserHandlers

    public Server() throws IOException {
        db = new Database(Constants.USERFILE, Constants.POSTFILE);
        activeUsers = new ArrayList<UserHandler>();
        serverSocket = new ServerSocket(PORT);
    }

    public void acceptClients() {
        try {
            System.out.println("Accepting clients ...");
            while (serverSocket != null && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                UserHandler userHandler = new UserHandler(socket, bw, br, this);

                Thread thread = new Thread(userHandler);
                thread.start();

                activeUsers.add(userHandler);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void removeActiveUser(UserHandler u) {
        activeUsers.remove(u);
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server ...");
        Server s = new Server();

        s.acceptClients();
    }

}
