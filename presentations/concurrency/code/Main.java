public class Main {
    private static boolean done;

    public static void main(final String[] args) throws Exception {
        new Thread() {
            public void run() {
                int i = 0;
                while(!done) { i++; }
                System.out.println("Thread done!");
            }
        }.start();

        System.out.println("Sleeping");
        Thread.sleep(2000);
        done = true;
        System.out.println("'done' set to true");
    }
}
