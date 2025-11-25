import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Hash {
    // Tabela hash com encadeamento (lista de listas)
    private ArrayList<ArrayList<Registro>> tabelaHash;
    
    // Array de registros carregados
    private Registro[] listaReservas;
    private int quantidade;
    private int tamanhoTabela; // Calculado dinamicamente

    public static void main(String[] args) {
        // Array com os caminhos dos arquivos de entrada
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
        
        // Processa cada arquivo
        for (String arquivo : arquivos) {
            Hash util = new Hash();
            try {
                // Executa o procedimento 5 vezes e obtém tempo médio
                long mediaMs = util.executarCincoVezes(arquivo);
                System.out.println(new File(arquivo).getName() + " - tempo médio (ms): " + mediaMs);
            } catch (IOException e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }

    // 6.1 a 6.4: Executa carregar, pesquisar em hash e gravar 5 vezes; retorna média em ms
    public long executarCincoVezes(String nomeArquivo) throws IOException {
        long inicio = System.nanoTime();
        
        // Repete o procedimento 5 vezes
        for (int i = 0; i < 5; i++) {
            // 6.1: Carrega arquivo
            carregarArquivo(nomeArquivo);
            
            // Constrói tabela hash a partir dos dados carregados
            construirTabelaHash();
            
            // 6.2: Realiza pesquisa e gera arquivo de resultado
            String nomeSaida = gerarNomeSaida(nomeArquivo);
            pesquisarEGravar(nomeSaida);
        }
        
        // 6.3: Finaliza contagem de tempo
        long fim = System.nanoTime();
        
        // 6.4: Calcula tempo total e divide por 5 para média
        long totalMs = (fim - inicio) / 1_000_000;
        return totalMs / 5;
    }

    // Gera nome do arquivo de saída a partir do nome de entrada
    private String gerarNomeSaida(String caminhoEntrada) {
        String nome = new File(caminhoEntrada).getName();
        if (nome.startsWith("Reserva")) {
            return "src/arquivos_output/Hash" + nome.substring("Reserva".length());
        }
        return "src/arquivos_output/Hash_" + nome;
    }

    // Carrega arquivo; linhas são: reserva;nome;voo;data;assento;... (separador ';')
    public void carregarArquivo(String nomeArquivo) throws IOException {
        ArrayList<Registro> registros = new ArrayList<>();
        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                
                // Extrai os campos na ordem CORRETA
                String[] partes = linha.split(";", -1);
                String reserva = partes.length > 0 ? partes[0].trim() : "";   // Índice 0 - RESERVA (R000001)
                String nome = partes.length > 1 ? partes[1].trim() : "";      // Índice 1 - NOME
                String voo = partes.length > 2 ? partes[2].trim() : "";       // Índice 2 - VOO (V947)
                String data = partes.length > 3 ? partes[3].trim() : "";      // Índice 3 - DATA
                String assento = partes.length > 4 ? partes[4].trim() : "";   // Índice 4 - ASSENTO
                
                if (!nome.isEmpty()) {
                    registros.add(new Registro(nome, reserva, voo, data, assento));
                }
            }
        }
        listaReservas = registros.toArray(new Registro[0]);
        quantidade = listaReservas.length;
    }

    // Calcula um número primo próximo ao valor desejado
    private int calcularTamanhoPrimo(int valor) {
        // Começa do valor e procura o próximo número primo
        int candidato = valor;
        while (!ehPrimo(candidato)) {
            candidato++;
        }
        return candidato;
    }

    // Verifica se um número é primo
    private boolean ehPrimo(int numero) {
        if (numero < 2) return false;
        if (numero == 2) return true;
        if (numero % 2 == 0) return false;
        
        for (int i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0) return false;
        }
        return true;
    }

    // Constrói tabela hash a partir dos registros carregados
    private void construirTabelaHash() {
        // Calcula tamanho ideal baseado na quantidade de registros
        tamanhoTabela = calcularTamanhoPrimo(Math.max(quantidade, 1000));
        
        // Inicializa tabela hash vazia
        tabelaHash = new ArrayList<>();
        for (int i = 0; i < tamanhoTabela; i++) {
            tabelaHash.add(new ArrayList<>());
        }
        
        // Insere todos os registros na tabela hash
        for (Registro registro : listaReservas) {
            inserirNaTabelaHash(registro);
        }
    }

    // Insere registro na tabela hash usando o NOME como chave
    private void inserirNaTabelaHash(Registro registro) {
        int indice = funcaoHash(registro.nome);
        tabelaHash.get(indice).add(registro);
    }

    // Função hash: calcula índice baseado no NOME
    private int funcaoHash(String chave) {
        return Math.abs(chave.hashCode()) % tamanhoTabela;
    }

    // Busca TODOS os registros com o mesmo NOME na tabela hash
    private ArrayList<Registro> buscarPorNome(String nome) {
        ArrayList<Registro> resultados = new ArrayList<>();
        int indice = funcaoHash(nome);
        ArrayList<Registro> cadeia = tabelaHash.get(indice);
        
        // Percorre a cadeia procurando por todos os registros com esse nome
        for (Registro registro : cadeia) {
            if (registro.nome.equalsIgnoreCase(nome)) {
                resultados.add(registro);
            }
        }
        return resultados;
    }

    // Realiza pesquisa de 400 nomes e gera arquivo com resultados formatados
    private void pesquisarEGravar(String nomeSaida) throws IOException {
        // Cria diretório se necessário
        File arquivoSaida = new File(nomeSaida);
        File diretorio = arquivoSaida.getParentFile();
        if (diretorio != null) diretorio.mkdirs();
        
        // Tenta encontrar arquivo de pesquisa em diferentes locais
        String[] caminhosPesquisa = {
            "src/arquivos_input/nome.txt"
        };
        
        File arquivoPesquisa = null;
        for (String caminho : caminhosPesquisa) {
            File temp = new File(caminho);
            if (temp.exists()) {
                arquivoPesquisa = temp;
                System.out.println("Arquivo de pesquisa encontrado: " + caminho);
                break;
            }
        }
        
        if (arquivoPesquisa == null) {
            System.err.println("ERRO: Arquivo de pesquisa não encontrado!");
            System.err.println("Procurado em:");
            for (String caminho : caminhosPesquisa) {
                System.err.println("  - " + new File(caminho).getAbsolutePath());
            }
            return;
        }
        
        try (BufferedReader leitoPesquisa = new BufferedReader(new FileReader(arquivoPesquisa));
             BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {
            
            String nomePesquisa;
            int contador = 0;
            
            // Processa até 400 nomes de pesquisa
            while ((nomePesquisa = leitoPesquisa.readLine()) != null && contador < 400) {
                nomePesquisa = nomePesquisa.trim();
                
                if (nomePesquisa.isEmpty()) continue;
                
                // Busca TODOS os registros com esse nome
                ArrayList<Registro> resultados = buscarPorNome(nomePesquisa);
                
                // Formata e escreve resultado
                escritor.write("NOME " + nomePesquisa + ":\n");
                
                if (resultados.isEmpty()) {
                    // Nenhuma reserva encontrada
                    escritor.write("NÃO TEM RESERVA\n");
                } else {
                    // Exibe todas as reservas
                    for (Registro resultado : resultados) {
                        String linha = String.format("Reserva: %-8s Voo: %-8s Data: %-10s Assento: %-4s\n",
                            resultado.reserva, resultado.voo, resultado.data, resultado.assento);
                        escritor.write(linha);
                    }
                    // Exibe total de reservas
                    escritor.write("TOTAL: " + resultados.size() + " reservas\n");
                }
                
                escritor.write("\n"); // Linha em branco entre nomes
                contador++;
            }
        }
    }

    // Classe interna para armazenar dados de registro
    private static class Registro {
        String nome;          // Nome do cliente (chave de busca)
        String reserva;       // Número da reserva (V947)
        String voo;           // Número do voo (data na verdade)
        String data;          // Data da reserva (assento na verdade)
        String assento;       // Número do assento

        Registro(String nome, String reserva, String voo, String data, String assento) {
            this.nome = nome;
            this.reserva = reserva;
            this.voo = voo;
            this.data = data;
            this.assento = assento;
        }
    }
}