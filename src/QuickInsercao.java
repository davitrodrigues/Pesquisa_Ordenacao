import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class QuickInsercao {
    private Registro[] listaReservasQuickInsercao;
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
            QuickInsercao util = new QuickInsercao(); 
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
        listaReservasQuickInsercao = registros.toArray(new Registro[0]);
        quantidade = listaReservasQuickInsercao.length;
    }

    public void gravarArquivo(String nomeSaida) throws IOException {
        File arquivoSaida = new File(nomeSaida);
        File diretorio = arquivoSaida.getParentFile();
        if (diretorio != null) diretorio.mkdirs();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {
            for (Registro registro : listaReservasQuickInsercao) {
                escritor.write(registro.linhaOriginal);
                escritor.newLine();
            }
        }
    }

    public void InsertionSort(){
        int i,j,temp;
        int tamanho=i-j;
        int limite=Math.min(tamanho, 20);
        for (int i =esq)
    }

    public void quickInsercao() {
        ordena(0, this.quantidade - 1); 
    }
    
    private void ordena(int esq, int dir) {
        int i, j;
        Registro pivo, temp; 
        i = esq;
        j = dir;

        pivo = this.listaReservasQuickInsercao[(i + j) / 2];
        
        do { 
            while (comparar(this.listaReservasQuickInsercao[i], pivo) < 0) { 
                i++;
            }
            while (comparar(this.listaReservasQuickInsercao[j], pivo) > 0) { 
                j--;
            }
            if (i <= j) {
                temp = this.listaReservasQuickInsercao[i];
                this.listaReservasQuickInsercao[i] = this.listaReservasQuickInsercao[j];
                this.listaReservasQuickInsercao[j] = temp;
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