package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        try {
            System.out.println("Server in Avvio");
            ServerSocket server = new ServerSocket(8080);
            while (true) {

                Socket s = server.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream());

                String richiesta = in.readLine();
                String riga[] = richiesta.split(" ");
                String path = riga[1];
                path = path.substring(1);
                System.out.println("---- " + path + " ----");

                if (path.equals(""))
                    path = "index.html";

                if (path.endsWith("/"))
                    path += "index.html";

                do {
                    String line = in.readLine();
                    System.out.println(line);
                    if (line == null || line.isEmpty())
                        break;
                } while (true);

                sendBinaryFile(s, path, findExt(path));

                out.flush();
                s.close();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * private static String findFile (String path) {
     * try {
     * String p[] = path.split("/");
     * String s = p[1];
     * return s;
     * } catch (Exception e) {
     * System.out.println("ERROR findFile - 1: " + e.getMessage() + "\n");
     * }
     * return "ERROR findFile - 2 \n";
     * }
     */

    private static String findExt(String path) {
        try {
            String a[] = path.split("\\.");
            path = a[1];
            return path;
        } catch (Exception e) {
            System.out.println("ERROR findExt - 1: " + e.getMessage() + "\n");
        }
        return "ERROR findExt - 2 \n";
    }

    private static void sendBinaryFile(Socket socket, String name, String ext) throws IOException {

        String filename = "htdocs/" + name;
        File file = new File(filename);

        if (file.exists() && (filename.contains("\\."))) {

            try {

                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeBytes("HTTP/1.1 200 OK\n");
                out.writeBytes("Content-Lenght " + file.length() + "\n");
                out.writeBytes("Server: Java HTTP Server from Spagni: 1.0\n");
                out.writeBytes("Date: " + new Date() + "\n");
                checkExt(socket, ext, out);

                InputStream in = new FileInputStream(file);
                byte b[] = new byte[8192];
                int n;
                while ((n = in.read(b)) != -1) {
                    out.write(b, 0, n);
                }
                in.close();

            } catch (Exception e) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println(e.getMessage());
                write404(socket, file, out);
            }

        } else {

            redicted(socket, filename);

        }

    }

    private static void redicted(Socket socket, String filename) throws IOException {
        try {

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes("HTTP/1.1 301 Moved Permanently\n");
            out.writeBytes("Location: " + filename + "/\n");
            out.writeBytes("\n");
            System.out.println(filename + "-------------------------------------");

        } catch (Exception e) {
            System.out.println("ERROR: redicted");
        }
    }

    private static void write404(Socket socket, File file, DataOutputStream out) {

        try {
            out.writeBytes("HTTP/1.1 404 Not Found\n");
            out.writeBytes("Content-Lenght " + file.length() + "\n");
            out.writeBytes("Server: Java HTTP Server from Spagni: 1.0\n");
            out.writeBytes("Date: " + new Date() + "\n");
            out.writeBytes("\n");
        } catch (Exception e) {
            System.out.println("ERROR: write404");
        }

    }

    private static void checkExt(Socket socket, String ext, DataOutputStream out) {

        try {
            if (ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png"))
                out.writeBytes("Content-Type: image/" + ext + "; charset=utf-8\n");
            if (ext.equals("html"))
                out.writeBytes("Content-Type: text/html; charset=utf-8\n");
            if (ext.equals("css"))
                out.writeBytes("Content-Type: text/css; charset=utf-8\n");
            if (ext.equals("js"))
                out.writeBytes("Content-Type: application/js; charset=utf-8\n");
            out.writeBytes("\n");
        } catch (Exception e) {
            System.out.println("ERROR: checkExt");
        }

    }
}
