// Reserva.java
public class Reserva {
    public String codigo;
    public String nome;
    public String voo;
    public String data;
    public String assento;

    public Reserva(String codigo, String nome, String voo, String data, String assento) {
        this.codigo = codigo;
        this.nome = nome;
        this.voo = voo;
        this.data = data;
        this.assento = assento;
    }

    @Override
    public String toString() {
        // formato usado na saída: Reserva: XXXX Voo: XXXXX Data: XX/XX/XXXX Assento: XXX
        return String.format("Reserva: %s Voo: %s Data: %s Assento: %s", codigo, voo, data, assento);
    }
}
