package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

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
                System.out.println("----" + path + "----");

                do {
                    String line = in.readLine();
                    System.out.println(line);
                    if (line == null || line.isEmpty())
                        break;
                } while (true);

                sendFile(out, path);

                out.flush();
                s.close();

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void sendFile(PrintWriter out, String file) {
        try {
            File myObj = new File("test.html");
            Scanner myReader = new Scanner(myObj);

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Lenght " + myObj.length());
            out.println("Server: Java HTTP Server from Spagni: 1.0");
            out.println("Date: " + new Date());
            out.println("Content-Type: text/html; charset=utf-8");

            out.println();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                out.println(data);
            }
            myReader.close();

        } catch (Exception e) {
            out.println("HTTP/1.1 404 OK");
        }
    }
}
