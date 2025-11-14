import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HeapUtil {
    private Registro[] listaReservas;
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
            // aqui voces adicionam os endereços dos arquivos, rapaziada
        };
        for (String arquivo : arquivos) { 
            HeapUtil util = new HeapUtil();
            try { 
                long mediaMs = util.executarCincoVezes(arquivo); // executa 5 vezes e obtém média
                System.out.println(new File(arquivo).getName() + " - tempo médio (ms): " + mediaMs); // exibe resultado
            } catch (IOException e) {
                System.err.println("Erro: " + e.getMessage()); // caso de erro
            }
        }
    }

    // Executa carregar, ordenar (Heapsort) e gravar 5 vezes; retorna média em ms
    public long executarCincoVezes(String nomeArquivo) throws IOException {
        long inicio = System.nanoTime();
        for (int i = 0; i < 5; i++) {
            carregarArquivo(nomeArquivo);
            heapSort();
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
            return "src/arquivos_output/heap" + nome.substring("Reserva".length());
        }
        return "src/arquivos_output/heap_" + nome;
    }

    // Carrega arquivo; linhas são: codigo;nome;... (separador ';')
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
        listaReservas = registros.toArray(new Registro[0]);
        quantidade = listaReservas.length;
    }

    public void gravarArquivo(String nomeSaida) throws IOException {
        File arquivoSaida = new File(nomeSaida);
        File diretorio = arquivoSaida.getParentFile();
        if (diretorio != null) diretorio.mkdirs();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {
            for (Registro registro : listaReservas) {
                escritor.write(registro.linhaOriginal);
                escritor.newLine();
            }
        }
    }

    // Heapsort sobre listaReservas: ordenar por nome, se igual por código
    public void heapSort() {
        if (listaReservas == null || quantidade <= 1) return;
        for (int i = quantidade / 2 - 1; i >= 0; i--) {
            heapify(quantidade, i);
        }
        for (int i = quantidade - 1; i >= 0; i--) {
            Registro temporario = listaReservas[0];
            listaReservas[0] = listaReservas[i];
            listaReservas[i] = temporario;
            heapify(i, 0);
        }
    }

    private void heapify(int n, int i) {
        int maior = i;
        int esquerda = 2 * i + 1;
        int direita = 2 * i + 2;
        if (esquerda < n && comparar(listaReservas[esquerda], listaReservas[maior]) > 0) {
            maior = esquerda;
        }
        if (direita < n && comparar(listaReservas[direita], listaReservas[maior]) > 0) {
            maior = direita;
        }
        if (maior != i) {
            Registro troca = listaReservas[i];
            listaReservas[i] = listaReservas[maior];
            listaReservas[maior] = troca;
            heapify(n, maior);
        }
    }

    // compara por nome; se igual, por código
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
