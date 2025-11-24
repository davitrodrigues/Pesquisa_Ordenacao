import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AVL {
    private NoAVL raiz;
    public int quant;

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

    private static class NoAVL {
        String nome;
        ArrayList<Registro> registros;
        NoAVL esq;
        NoAVL dir;
        int altura;

        NoAVL(Registro registro) {
            this.nome = registro.nome;
            this.registros = new ArrayList<>();
            this.registros.add(registro);
            this.altura = 1;
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
            AVL avl = new AVL();
            try {
                long mediaMs = avl.executarCincoVezes(arquivo);
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
            return "src/arquivos_output/AVL" + nome.substring("Reserva".length());
        }
        return "src/arquivos_output/AVL_" + nome;
    }

    public void carregarArquivo(String nomeArquivo) throws IOException {
        raiz = null;
        quant = 0;

        try (BufferedReader leitor = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] partes = linha.split(";", -1);
                String reserva = partes.length > 0 ? partes[0].trim() : "";
                String nome = partes.length > 1 ? partes[1].trim() : "";
                String voo = partes.length > 2 ? partes[2].trim() : "";
                String data = partes.length > 3 ? partes[3].trim() : "";
                String assento = partes.length > 4 ? partes[4].trim() : "";

                if (!nome.isEmpty()) {
                    inserir(new Registro(nome, reserva, voo, data, assento));
                }
            }
        }
    }

    private void inserir(Registro registro) {
        // insere e atualiza a raiz
        raiz = inserir(registro, raiz);
    }

    private NoAVL inserir(Registro registro, NoAVL no) {
        // isso faz quando a árvore esta vazia
        if (no == null) {
            quant++;
            return new NoAVL(registro);
        }
        // compara o nome do registro com o atual, ignorando maiusculas e minusculas
        int comparacao = registro.nome.compareToIgnoreCase(no.nome);
        // decide se vai para esquerda ou direita
        if (comparacao < 0) {
            no.esq = inserir(registro, no.esq);
        } else if (comparacao > 0) {
            no.dir = inserir(registro, no.dir);
        } else {
            no.registros.add(registro);
            return no;
        }
        // atualiza a altura
        no.altura = 1 + Math.max(altura(no.esq), altura(no.dir));

        int balanceamento = obterBalanceamento(no);
        // rotação para esquerda, direita ou dupla conforme o caso
        if (balanceamento > 1 && registro.nome.compareToIgnoreCase(no.esq.nome) < 0) {
            return rotacaoDireita(no);
        }

        if (balanceamento < -1 && registro.nome.compareToIgnoreCase(no.dir.nome) > 0) {
            return rotacaoEsquerda(no);
        }

        if (balanceamento > 1 && registro.nome.compareToIgnoreCase(no.esq.nome) > 0) {
            no.esq = rotacaoEsquerda(no.esq);
            return rotacaoDireita(no);
        }

        if (balanceamento < -1 && registro.nome.compareToIgnoreCase(no.dir.nome) < 0) {
            no.dir = rotacaoDireita(no.dir);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    private int altura(NoAVL no) {
        return no == null ? 0 : no.altura;
    }

    private int obterBalanceamento(NoAVL no) {
        return no == null ? 0 : altura(no.esq) - altura(no.dir);
    }

    private NoAVL rotacaoDireita(NoAVL y) {
        // x vai ser a nova raiz
        NoAVL x = y.esq;
        // filho esquerdo de y
        NoAVL T2 = x.dir;
        // faz  a rotação
        x.dir = y;
        y.esq = T2;
        // atualiza a altura
        y.altura = Math.max(altura(y.esq), altura(y.dir)) + 1;
        x.altura = Math.max(altura(x.esq), altura(x.dir)) + 1;

        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        // mesma logica do outro, porem y vira  raiz de t2 fica como filho de x
        NoAVL y = x.dir;
        NoAVL T2 = y.esq;

        y.esq = x;
        x.dir = T2;

        x.altura = Math.max(altura(x.esq), altura(x.dir)) + 1;
        y.altura = Math.max(altura(y.esq), altura(y.dir)) + 1;

        return y;
    }

    public NoAVL pesquisar(String nome) {
        return pesquisar(nome, this.raiz);
    }

    private NoAVL pesquisar(String nome, NoAVL no) {
        if (no == null) {
            return null;
        }

        int comparacao = nome.compareToIgnoreCase(no.nome);

        if (comparacao == 0) {
            return no;
        } else if (comparacao < 0) {
            return pesquisar(nome, no.esq);
        } else {
            return pesquisar(nome, no.dir);
        }
    }

    private void pesquisarEGravar(String nomeSaida) throws IOException {
        File arquivoSaida = new File(nomeSaida);
        if (arquivoSaida.getParentFile() != null)
            arquivoSaida.getParentFile().mkdirs();

        String[] caminhos = {
            "src/arquivos_input/nome.txt",
            "src/arquivos_input/pesquisa.txt",
            "arquivos_input/nome.txt",
            "nome.txt"
        };

        File arquivoPesquisa = null;
        for (String c : caminhos) {
            File f = new File(c);
            if (f.exists()) {
                arquivoPesquisa = f;
                break;
            }
        }

        if (arquivoPesquisa == null) {
            System.err.println("ERRO: nome.txt não encontrado!");
            return;
        }

        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivoPesquisa));
             BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivoSaida))) {

            String nomePesquisa;
            int cont = 0;

            while ((nomePesquisa = leitor.readLine()) != null && cont < 400) {
                nomePesquisa = nomePesquisa.trim();
                if (nomePesquisa.isEmpty()) continue;

                NoAVL resultado = pesquisar(nomePesquisa);

                escritor.write("NOME " + nomePesquisa + ":\n");

                if (resultado == null || resultado.registros.isEmpty()) {
                    escritor.write("NÃO TEM RESERVA\n\n");
                } else {
                    for (Registro reg : resultado.registros) {
                        escritor.write(String.format(
                            "Reserva: %-8s Voo: %-8s Data: %-10s Assento: %-4s\n",
                            reg.reserva, reg.voo, reg.data, reg.assento
                        ));
                    }
                    escritor.write("TOTAL: " + resultado.registros.size() + " reservas\n\n");
                }

                cont++;
            }
        }
    }
}