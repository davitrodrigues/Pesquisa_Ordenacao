// Main.java
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            Path entradaDir = Paths.get("entrada");
            if (!Files.exists(entradaDir) || !Files.isDirectory(entradaDir)) {
                System.err.println("Crie a pasta 'entrada' e coloque os arquivos dentro (Reserva*.txt e nome.txt)");
                return;
            }

            // Listar arquivos de entrada
            List<Path> files = new ArrayList<>();
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(entradaDir)) {
                for (Path p : ds) {
                    if (Files.isRegularFile(p)) files.add(p);
                }
            }

            if (files.isEmpty()) {
                System.err.println("Nenhum arquivo em entrada/");
                return;
            }

            // localizar nome.txt (arquivo de pesquisas) e separar os arquivos de reservas
            Path nomeFile = null;
            List<Path> reservaFiles = new ArrayList<>();
            for (Path p : files) {
                String nm = p.getFileName().toString().toLowerCase();
                if (nm.equals("nome.txt") || nm.equals("nomes.txt")) nomeFile = p;
                else reservaFiles.add(p);
            }

            if (nomeFile == null) {
                System.err.println("Arquivo nome.txt não encontrado em entrada/");
                return;
            }

            // ler nomes de pesquisa (trim, ignorar linhas vazias)
            List<String> pesquisas = new ArrayList<>();
            try (BufferedReader brn = Files.newBufferedReader(nomeFile)) {
                String line;
                while ((line = brn.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) pesquisas.add(line);
                }
            }

            // arquivo CSV de tempos (na raiz)
            Path temposCsv = Paths.get("tempos_avl.csv");
            try (BufferedWriter csv = Files.newBufferedWriter(temposCsv)) {
                csv.write("arquivo,media_ms");
                csv.newLine();

                // para cada arquivo de reserva
                for (Path reservaPath : reservaFiles) {
                    System.out.println("Processando: " + reservaPath.getFileName());
                    long t0 = System.nanoTime();

                    for (int run = 1; run <= 5; run++) {
                        // 1. Ler reservas e montar AVL
                        List<Reserva> lista = readReservas(reservaPath);
                        AVL avl = new AVL();
                        for (Reserva r : lista) {
                            avl.insert(r.nome, r);
                        }

                        // 2. Pesquisar os 400 nomes e escrever arquivo de resultado
                        String outName = "AVL" + reservaPath.getFileName().toString();
                        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(outName))) {
                            for (String nome : pesquisas) {
                                bw.write("NOME " + nome + ":");
                                bw.newLine();
                                List<Reserva> encontrados = avl.search(nome);
                                if (encontrados == null || encontrados.isEmpty()) {
                                    bw.write("NÃO TEM RESERVA");
                                    bw.newLine();
                                    bw.newLine();
                                } else {
                                    for (Reserva er : encontrados) {
                                        bw.write(er.toString());
                                        bw.newLine();
                                    }
                                    bw.write("TOTAL: " + encontrados.size() + " reservas");
                                    bw.newLine();
                                    bw.newLine();
                                }
                            }
                        }
                    }

                    long t1 = System.nanoTime();
                    double avgMs = (t1 - t0) / 5.0 / 1e6;
                    csv.write(reservaPath.getFileName().toString() + "," + String.format(Locale.ROOT,"%.3f", avgMs));
                    csv.newLine();
                    csv.flush();
                    System.out.println("Concluído: " + reservaPath.getFileName() + " - média (ms): " + String.format(Locale.ROOT,"%.3f", avgMs));
                }
            }

            System.out.println("Tudo pronto. Arquivos AVL*.txt gerados na raiz e tempos_avl.csv criado.");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // lê arquivo de reservas no formato: reserva;nome;voo;data;assento
    private static List<Reserva> readReservas(Path p) throws IOException {
        List<Reserva> res = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(p)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";", -1);
                if (parts.length < 5) continue; // ignora linhas mal formatadas
                // campos: codigo;nome;voo;data;assento
                Reserva r = new Reserva(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(), parts[4].trim());
                res.add(r);
            }
        }
        return res;
    }
}
