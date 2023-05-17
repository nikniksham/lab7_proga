import server.api.UserApi;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        Server server = new Server();
//        server.start();
//        System.out.println(UserApi.register("aaaa3", "asfsaf"));
        System.out.println(UserApi.login("aaaa3", "asfsaf"));
    }
}