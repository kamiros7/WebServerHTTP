package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {
    private static final int port = 9009;
    private static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(port);
        while(!Thread.currentThread().isInterrupted()) {
            //The accept function is a blocking function
            Socket client = serverSocket.accept();

            // Construct an object to process the HTTP request message.
            HttpRequest request = new HttpRequest(client);
            // Create a new thread to process the request.
            Thread thread = new Thread(request);
            // Start the thread.
            thread.start();
        }
    }
}
