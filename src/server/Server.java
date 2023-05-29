package server;

import my_programm.CustomFileReader;
import my_programm.Manager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import server.api.UserApi;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {
    private ServerSocket server;
    private boolean run = true;
    private ThreadPoolExecutor clients;
    private Manager manager;

    public Server() throws IOException {
        server = new ServerSocket(4004);
        this.clients = new ThreadPoolExecutor(0, 20, 120L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
        manager = new Manager();
        manager.load_table();

//        server.setSoTimeout(1000);
    }

    public void start() throws NullPointerException {
        ConnectionCatcher connectionCatcher = new ConnectionCatcher(clients, server, manager);
        clients.submit(connectionCatcher);
        System.out.println("Сервер запущен");
        while (run) {

        }

    }
}

class ConnectionCatcher implements Runnable {
    ThreadPoolExecutor clients;
    ServerSocket server;
    boolean run = true;
    Manager manager;
    public ConnectionCatcher(ThreadPoolExecutor clients, ServerSocket server, Manager manager) {
        this.clients = clients;
        this.server = server;
        this.manager = manager;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Socket newClient = server.accept();
                if (newClient != null) {
                    clients.submit(new Client(newClient, manager));
//                    System.out.println("Новый клиент добавлен");
                }
            } catch (Exception e) {
                System.out.println("Какая-то ошибка в получении клиента");
                e.printStackTrace();
            }
        }
    }
}

class Client implements Runnable {
    Socket socket;
    BufferedWriter writer;
    BufferedReader reader;
    Manager manager;

    public Client(Socket socket, Manager manager) throws IOException {
        this.manager = manager;
        this.socket = socket;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
        try {
            boolean jsonsend = false;
            String message = reader.readLine();
            System.out.println(message);
            String[] messageList = message.split("\s{1,}");
            if (messageList.length >= 3) {
                StringBuilder answer = new StringBuilder("Неправильный логин или пароль\n");
                int userStatus = UserApi.login(messageList[messageList.length - 2], messageList[messageList.length - 1]);
                if (userStatus >= 0) {
                    int userId = UserApi.getUserId(messageList[messageList.length - 2]);

                    StringBuilder command = new StringBuilder();
                    for (int i = 0; i < messageList.length - 2; i++) {
                        command.append(messageList[i]).append(" ");
                    }
                    command.delete(command.length() - 1, command.length());

                    answer = new StringBuilder();
                    for (String s : manager.commandHandler(command.toString(), userStatus, userId)) {
                        if (s.strip().equals("отправить json")) {
                            jsonsend = true;
                        } else {
                            answer.append(s.strip()).append("\n");
                        }
                    }
                } else {
                    if (messageList[0].equals("register")) {
                        answer.append(UserApi.register(messageList[1], messageList[2]));
                    }
                }
                String ans = "";
                if (jsonsend) {
                    String jsonString = "";
                    try {
                        List<String> arr = CustomFileReader.readFile("sendData.json");
                        for (String s : arr) {
                            jsonString += s + "\n";
                        }
                        Object obj = new JSONParser().parse(jsonString);
                        JSONObject jo = (JSONObject) obj;
                        ans = jo.toString() + "\n";
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Ошибка?!");
                    }
                } else {
                    ans = answer.toString();
                }
                SendAnswer sendAnswer = new SendAnswer(socket, writer, ans);
                sendAnswer.fork();
                sendAnswer.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Клиент сломался, пока пока");
        } finally {
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (Exception e) {}
        }
    }
}

class SendAnswer extends RecursiveTask<String> {
    Socket socket;
    BufferedWriter writer;
    String message;
    public SendAnswer(Socket socket, BufferedWriter writer, String message) {
        this.socket = socket;
        this.writer = writer;
        this.message = message;
    }
    @Override
    protected String compute() {
        String result = "success";
        try {
            writer.write(message + "end\n");
            writer.flush();
        } catch (Exception e) {
            System.out.println("Проблема при отправке сообщения");
            result = null;
        }
        return result;
    }
}