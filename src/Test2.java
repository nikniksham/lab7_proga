import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class SimpleClass extends RecursiveTask<String> {
    int name, delay;
    public SimpleClass(int name, int delay) {
        this.name = name;
        this.delay = delay;
    }
    @Override
    protected String compute() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("I am work in thread: " + Thread.currentThread().getName() + " my name is " + name);
        return "I am just innocent simple class";
    }
}


public class Test2 {
    public static void main(String[] args) throws InterruptedException, NoSuchAlgorithmException {
//        SimpleClass simpleClass1 = new SimpleClass(1, 2000);
//        SimpleClass simpleClass2 = new SimpleClass(2, 1000);
////        ForkJoinPool forkJoinPool = new ForkJoinPool();
//        simpleClass1.fork();
//        simpleClass2.fork();
//        Thread.sleep(2000);
//        System.out.println(simpleClass1.join());
//        System.out.println(simpleClass2.join());
        String text = "qwerty";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        System.out.println(encoded);
    }
}