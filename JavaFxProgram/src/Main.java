public class Main {
    public static Thread mainThread;
    public static void main(String[] args) {
        mainThread = new Thread(new Program());
        mainThread.start();
//        new Thread(new Program()).start();
    }
}