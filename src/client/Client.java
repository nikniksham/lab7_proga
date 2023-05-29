package client;

import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Client {
    private String login;
    private String password;
    Socket clientSocket;
    private Scanner scanner;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean run = true;
    private String message;
    public Client() {
        scanner = new Scanner(System.in);
        System.out.println("Укажите логин");
        this.login = scanner.nextLine();
        System.out.println("Укажите пароль");
        this.password = codeToSHA256(scanner.nextLine());
        System.out.println("Это новый аккаунт? (нажмите enter, если нет)");
        if (!scanner.nextLine().equals("")) {
            this.message = "register " + login + " " + password;
        }
    }

    private void connectToTheServer() {
        try {
            writer.close();
            reader.close();
            clientSocket.close();
        } catch (Exception e) {}

        try {
            clientSocket = new Socket("localhost", 4004);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (Exception e) {
            System.out.println("Что-то пошло не так");
            e.printStackTrace();
        }
    }
    public void start() {
        while (run) {
            if (message == null) {
                message = scanner.nextLine();
            }
            this.connectToTheServer();
            try {
                writer.write(message + " " + login + " " + password + "\n");
                writer.flush();

                CompletableFuture<String> waitMessageFromServer = CompletableFuture.supplyAsync(() -> wait_new_message(reader));

                Date c_date = new Date();
                while (!waitMessageFromServer.isDone()) {
                    if (new Date().getTime() - c_date.getTime() > 3000) {
                        System.out.println("Превышено время ожидания ответа от сервера");
                    }
                }

                String answer = waitMessageFromServer.get();
                if (answer != null) {
                    System.out.print(answer);
                }
                message = null;
            } catch (Exception e) {
                System.out.println("Сломалися");
//                e.printStackTrace();
            }
        }
    }
    private static String wait_new_message(BufferedReader reader) {
        try {
            StringBuilder answer = new StringBuilder();
            for (Iterator<String> it = reader.lines().iterator(); it.hasNext(); ) {
                String s = it.next();
                if (s.equals("end")) {break;}
                answer.append(s).append("\n");
            }
            return answer.toString();
        } catch (Exception e) {
            System.out.println("Потеря соединения с сервером");
            return "error";
        }
//        return null;
    }

    private String codeToSHA256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            System.out.println("это как?");
        }
        return null;
    }
}
