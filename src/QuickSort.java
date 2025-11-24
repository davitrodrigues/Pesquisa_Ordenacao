import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class QuickSort {
    private Registro[] listaReservasQuick;
    private int quantidade;

    public static void main(String[] args) {
        String[] arquivos = {
            "src/arquivos_input/Reserva1000alea.txt"
            , "src/arquivos_input/Reserva1000inv.txt"
            , "src/arquivos_input/Reserva1000ord.txt"
            , "src/arquivos_input/Reserva5000alea.txt"
            , "src/arquivos_input/Reserva5000inv.txt"
            , "src/arquivos_input/Reserva5000ord.txt"
            , "src/arquivos_input/Reserva10000alea.txt"
            , "src/arquivos_input/Reserva10000inv.txt"
            , "src/arquivos_input/Reserva10000ord.txt"
            , "src/arquivos_input/Reserva50000alea.txt"
            , "src/arquivos_input/Reserva50000inv.txt"
            , "src/arquivos_input/Reserva50000ord.txt"
        };
        
        for (String arquivo : arquivos) { 
            QuickSort util = new QuickSort(); 
            try { 
                long mediaMs = util.executarCincoVezes(arquivo);
                System.out.println(new File(arquivo).getName() + " - tempo médio (ms): " + mediaMs);
            } catch (IOException e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }

    public long executarCincoVezes(String nomeArquivo) throws IOException {
        long inicio = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            carregarArquivo(nomeArquivo);
            quickSort();
            String nomeSaida = gerarNomeSaida(nomeArquivo);
            gravarArquivo(nomeSaida);
        }
        long fim = System.nanoTime();
        long totalMs = (fim - inicio) / 1_000_000;
        return totalMs / 5;
    }

    private String gerarNomeSaida(String caminhoEntrada) {
        String nome = new File(caminhoEntrada).getName();
        if (nome.startsWith("Reserva")) {
            return "src/arquivos_output/quick" + nome.substring("Reserva".length()); 
        }
        return "src/arquivos_output/quick_" + nome;
    }

    public void carregarArquivo(String nomeArquivo) throws IOException {
        ArrayList<Registro> registros = new ArrayList<>(); 
        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                String[] partes = linha.split(";", -1);
                String codigo = partes.length > 0 ? partes[0].trim() : "";
                String nome = partes.length > 1 ? partes[1].trim() : "";
                registros.add(new Registro(codigo, nome, linha));
            }
        }
        listaReservasQuick = registros.toArray(new Registro[0]);
        quantidade = listaReservasQuick.length;
    }

    public void gravarArquivo(String nomeSaida) throws IOException {
        File arquivoSaida = new File(nomeSaida);
        File diretorio = arquivoSaida.getParentFile();
        if (diretorio != null) diretorio.mkdirs();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {
            for (Registro registro : listaReservasQuick) {
                escritor.write(registro.linhaOriginal);
                escritor.newLine();
            }
        }
    }
    // metodo que chama o quicksort 
    public void quickSort() {
        ordena(0, this.quantidade - 1); 
    }
    // metodo  recursivo
    private void ordena(int esq, int dir) {
        int i, j;
        // declara o pivo como centro
        Registro pivo, temp; 
        i = esq;
        j = dir;
        // elemento central
        pivo = this.listaReservasQuick[(i + j) / 2];
        
        do {
            // anda da esquerda ate encontrar o elemento menor ou igual o pivo
            while (comparar(this.listaReservasQuick[i], pivo) < 0) { 
                i++;
            }
            // anda da esquerda ate encontrar o elemento maior ou igual o pivo
            while (comparar(this.listaReservasQuick[j], pivo) > 0) { 
                j--;
            }
            // troca os elementos quando se encontram
            if (i <= j) {
                temp = this.listaReservasQuick[i];
                this.listaReservasQuick[i] = this.listaReservasQuick[j];
                this.listaReservasQuick[j] = temp;
                i++;
                j--;
            }
        } while (i <= j);
        
        if (esq < j) {
            ordena(esq, j);
        }
        if (dir > i) {
            ordena(i, dir);
        }
    }

    // compara o nome, e se for igual, pela reserva
    private int comparar(Registro a, Registro b) {
        int comparacaoNome = a.nome.compareTo(b.nome);
        if (comparacaoNome != 0) return comparacaoNome;
        return a.codigo.compareTo(b.codigo);
    }

    private static class Registro {
        String codigo;
        String nome;
        String linhaOriginal;
        
        Registro(String codigo, String nome, String linhaOriginal) {
            this.codigo = codigo;
            this.nome = nome;
            this.linhaOriginal = linhaOriginal;
        }
    }
}