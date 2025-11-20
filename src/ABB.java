import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ABB {
    // Raiz da árvore
    public NoABB raiz;
    public int quant;
        // Classe interna para armazenar dados de registro
    private static class Registro {
        String nome;       
        String reserva;    
        String voo;        
        String data;       
        String assento;    

        Registro(String nome, String reserva, String voo, String data, String assento) {
            this.nome = nome;
            this.reserva = reserva;
            this.voo = voo;
            this.data = data;
            this.assento = assento;
        }
    }

    public static void main(String[] args) {
        String[] arquivos = {
            "src/arquivos_input/Reserva1000alea.txt",
            "src/arquivos_input/Reserva1000inv.txt",
            "src/arquivos_input/Reserva1000ord.txt",
            "src/arquivos_input/Reserva5000alea.txt",
            "src/arquivos_input/Reserva5000inv.txt",
            "src/arquivos_input/Reserva5000ord.txt",
            "src/arquivos_input/Reserva10000alea.txt",
            "src/arquivos_input/Reserva10000inv.txt",
            "src/arquivos_input/Reserva10000ord.txt",
            "src/arquivos_input/Reserva50000alea.txt",
            "src/arquivos_input/Reserva50000inv.txt",
            "src/arquivos_input/Reserva50000ord.txt"
        };

        for (String arquivo : arquivos) {
            ABB util = new ABB();
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

            String nomeSaida = gerarNomeSaida(nomeArquivo);
            pesquisarEGravar(nomeSaida);
        }

        long fim = System.nanoTime();
        
        long totalMs = (fim - inicio) / 1_000_000;
        return totalMs / 5;
    }

    private String gerarNomeSaida(String caminhoEntrada) {
        String nome = new File(caminhoEntrada).getName();
        if (nome.startsWith("Reserva")) {
            return "src/arquivos_output/ABB" + nome.substring("Reserva".length());
        }
        return "src/arquivos_output/ABB_" + nome;
    }

    public void carregarArquivo(String nomeArquivo) throws IOException {
        // Reseta a árvore
        raiz = null;
        quant = 0;
        
        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;
                
                // Extrai os campos
                String[] partes = linha.split(";", -1);
                String reserva = partes.length > 0 ? partes[0].trim() : "";
                String nome = partes.length > 1 ? partes[1].trim() : "";
                String voo = partes.length > 2 ? partes[2].trim() : "";
                String data = partes.length > 3 ? partes[3].trim() : "";
                String assento = partes.length > 4 ? partes[4].trim() : "";
                
                if (!nome.isEmpty()) {
                    // Insere diretamente na ABB
                    inserir(new Registro(nome, reserva, voo, data, assento));
                }
            }
        }
        
        // balanceia usando o in order
        balancear();
    }

    // insere na a
    private void inserir(Registro registro) {
        raiz = inserir(registro, raiz);
        quant++;
    }

    private NoABB inserir(Registro registro, NoABB no) {
        if (no == null) {
            return new NoABB(registro);
        }
        
        int comparacao = compararRegistros(registro, no.registro);
        
        if (comparacao < 0) {
            no.esq = inserir(registro, no.esq);
        } else {
            no.dir = inserir(registro, no.dir);
        }
        
        return no;
    }

    // metodo que balanceia a arvore
    private void balancear() {
        ArrayList<Registro> registros = new ArrayList<>();
        caminharInOrder(raiz, registros);
        raiz = construirBalanceada(registros, 0, registros.size() - 1);
    }

    // faz o caminhamento in order e armazena em uma lista
    private void caminharInOrder(NoABB no, ArrayList<Registro> registros) {
        if (no != null) {
            caminharInOrder(no.esq, registros);
            registros.add(no.registro);
            caminharInOrder(no.dir, registros);
        }
    }

    // compara dois registros, se o nome tiver igual, vai por reserva
    private int compararRegistros(Registro a, Registro b) {
        int comparacaoNome = a.nome.compareToIgnoreCase(b.nome);
        if (comparacaoNome != 0) return comparacaoNome;
        return a.reserva.compareTo(b.reserva);
    }

    // pega o array ordenado, e constrói a arvore balanceada
    private NoABB construirBalanceada(ArrayList<Registro> registros, int esq, int dir) {
        if (esq > dir) return null;
        
        // pega o do meio como raiz
        int meio = (esq + dir) / 2;
        Registro registro = registros.get(meio);
        
        // cria nó com o registro do meio
        NoABB no = new NoABB(registro);
        
        // faz as subarvores esquerda e direita recursivamente
        no.esq = construirBalanceada(registros, esq, meio - 1);
        no.dir = construirBalanceada(registros, meio + 1, dir);
        
        return no;
    }

    // busca todos os registros com o nome dado
    public ArrayList<Registro> pesquisar(String nome) {
        return pesquisar(nome, this.raiz);
    }

    // busca recursiva na ABB
    private ArrayList<Registro> pesquisar(String nome, NoABB no) {
        ArrayList<Registro> resultados = new ArrayList<>();
        
        if (no == null) {
            return resultados; // Retorna lista vazia
        }
        
        int comparacao = nome.compareToIgnoreCase(no.registro.nome);
        
        if (comparacao == 0) {
            // se achar, adiciona o registro atual
            resultados.add(no.registro);
            
            // vai para esquerda 
            resultados.addAll(pesquisar(nome, no.esq));
            
            // vai para direita
            resultados.addAll(pesquisar(nome, no.dir));
            
        } else if (comparacao < 0) {
            // nome menor, esquerda
            return pesquisar(nome, no.esq);
        } else {
            // nome maior, direita
            return pesquisar(nome, no.dir);
        }
        
        return resultados;
    }

    // pesquisa os 400 nome
    private void pesquisarEGravar(String nomeSaida) throws IOException {
        
        File arquivoSaida = new File(nomeSaida);
        File diretorio = arquivoSaida.getParentFile();
        if (diretorio != null) diretorio.mkdirs();
        
        // varios locais para procurar o arquivo nome.txt
        String[] caminhosPesquisa = {
            "src/arquivos_input/nome.txt",
            "src/arquivos_input/pesquisa.txt",
            "arquivos_input/nome.txt",
            "nome.txt"
        };
        
        File arquivoPesquisa = null;
        for (String caminho : caminhosPesquisa) {
            File temp = new File(caminho);
            if (temp.exists()) {
                arquivoPesquisa = temp;
                break;
            }
        }
        
        if (arquivoPesquisa == null) {
            System.err.println("ERRO: Arquivo nome.txt não encontrado!");
            return;
        }
        
        try (BufferedReader leitoPesquisa = new BufferedReader(new FileReader(arquivoPesquisa));
             BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {
            
            String nomePesquisa;
            int contador = 0;
            
            // processa os nomes
            while ((nomePesquisa = leitoPesquisa.readLine()) != null && contador < 400) {
                nomePesquisa = nomePesquisa.trim();
                
                if (nomePesquisa.isEmpty()) continue;
                
                // bysca na ABB
                ArrayList<Registro> resultados = pesquisar(nomePesquisa);
                
                // escreve o resultado
                escritor.write("NOME " + nomePesquisa + ":\n");
                
                if (resultados.isEmpty()) {
                    // nao tem reserva
                    escritor.write("NÃO TEM RESERVA\n");
                } else {
                    // mostra todas as reservas encontradas
                    for (Registro resultado : resultados) {
                        String linha = String.format("Reserva: %-8s Voo: %-8s Data: %-10s Assento: %-4s\n",
                            resultado.reserva, resultado.voo, resultado.data, resultado.assento);
                        escritor.write(linha);
                    }
                    // exibe total de reservas
                    escritor.write("TOTAL: " + resultados.size() + " reservas\n");
                }
                
                escritor.write("\n"); // isso para ficar mais bonitinho
                contador++;
            }
        }
    }
    private static class NoABB {
        Registro registro; 
        NoABB esq;         
        NoABB dir;         
        
        NoABB(Registro registro) {
            this.registro = registro;
            this.esq = null;
            this.dir = null;
        }

}
}