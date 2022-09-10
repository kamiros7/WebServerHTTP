package main;

import model.StringTokenizer;

import java.io.*;
import java.net.Socket;

public class HttpRequest implements Runnable {

    private final static String CRLF = "\r\n";
    private Socket socket;

    public HttpRequest() {

    }

    public HttpRequest(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        DataOutputStream output = new DataOutputStream(this.socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Get the request line of the HTTP request message
        String requestLine = bufferedReader.readLine();
        System.out.println("request line: " + requestLine);

        //Get the header lines of the HTTP request message
        String headerLine;
        //Receives all the header of HTTP request
        while ((headerLine = bufferedReader.readLine()).length() != 0) {
            System.out.println("header line: " + headerLine);
        }

        //Always is considered a get request, so is skip the type HTTP request
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();

        //String fileName = "." + tokens.nextToken();
        String fileName = tokens.nextToken();

        // Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("error: " + e.getMessage());
            fileExists = false;
        }

        //Construct the response message
        String statusLine = null;
        String contentTypeLine = null; //Header of HTTP response message
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " +
                    contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " +
                    contentType(fileName) + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>Not Found</TITLE></HEAD>" +
                    "<BODY>Not Found</BODY></HTML>";
        }

        // Send the status line.
        output.writeBytes(statusLine);
        // Send the content type line.
        output.writeBytes(contentTypeLine);
        // Send a blank line to indicate the end of the header lines.
        output.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
            try{
                sendBytes(fis, output);
                fis.close();
            } catch (IOException e) {
                System.out.println("error: " + e.getMessage());
            }

        } else {
            output.writeBytes(entityBody);
        }

        // Close streams and socket.
        output.close();
        is.close();
        socket.close();
    }

    private String contentType(String fileName) {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if(fileName.endsWith(".png")) {
            return "image/png";
        }
        if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if(fileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }

    private static void sendBytes(FileInputStream fis, DataOutputStream output) throws IOException {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        // Copy requested file into the socket's output stream.
        while ((bytes = fis.read(buffer)) != -1) {
            output.write(buffer, 0, bytes);
        }
    }
}
