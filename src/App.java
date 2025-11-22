import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("WD = " + System.getProperty("user.dir"));
        File f = new File("arquivos_input/Reserva1000alea.txt");
        System.out.println("exists = " + f.exists());
        System.out.println("abs = " + f.getAbsolutePath());

    }
}
