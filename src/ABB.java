import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ABB {

    private NoABB raiz;
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

    private static class NoABB {
        String nome;
        ArrayList<Registro> registros;
        NoABB esq;
        NoABB dir;

        NoABB(Registro registro) {
            this.nome = registro.nome;
            this.registros = new ArrayList<>();
            this.registros.add(registro);
        }

        NoABB(String nome, ArrayList<Registro> registros) {
            this.nome = nome;
            this.registros = new ArrayList<>(registros);
        }
        
        String getNum() {
            return this.nome;
        }
        
        NoABB getEsq() {
            return this.esq;
        }
        
        NoABB getDir() {
            return this.dir;
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
            ABB abb = new ABB();
            try {
                long mediaMs = abb.executarCincoVezes(arquivo);
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

                if (!nome.isEmpty())
                    inserir(new Registro(nome, reserva, voo, data, assento));
            }
        }

        raiz = balancear();
    }

    private void inserir(Registro registro) {
        if (raiz == null) {
            raiz = new NoABB(registro);
            quant++;
            return;
        }

        NoABB atual = raiz;
        NoABB pai = null;

        while (atual != null) {
            pai = atual;
            int cmp = registro.nome.compareToIgnoreCase(atual.nome);

            if (cmp < 0) {
                atual = atual.esq;
            } else if (cmp > 0) {
                atual = atual.dir;
            } else {
                atual.registros.add(registro);
                return;
            }
        }

        if (registro.nome.compareToIgnoreCase(pai.nome) < 0) {
            pai.esq = new NoABB(registro);
        } else {
            pai.dir = new NoABB(registro);
        }

        quant++;
    }

    public NoABB balancear() {
        NoABB aux = new NoABB(new Registro("", "", "", "", ""));
        ArrayList<NoABB> vetor = CamCentral();
        balancear(vetor, aux, 0, vetor.size() - 1);
        return aux.dir;
    }

    private void balancear(ArrayList<NoABB> vetor, NoABB aux, int inicio, int fim) {
        if (inicio <= fim) {
            int meio = (inicio + fim) / 2;
            inserir(vetor.get(meio), aux);
            balancear(vetor, aux, inicio, meio - 1);
            balancear(vetor, aux, meio + 1, fim);
        }
    }
    
    private void inserir(NoABB novoNo, NoABB aux) {
        if (aux.dir == null) {
            aux.dir = novoNo;
            novoNo.esq = null;
            novoNo.dir = null;
            return;
        }
        
        NoABB atual = aux.dir;
        NoABB pai = null;
        
        while (atual != null) {
            pai = atual;
            int cmp = novoNo.nome.compareToIgnoreCase(atual.nome);
            
            if (cmp < 0) {
                atual = atual.esq;
            } else {
                atual = atual.dir;
            }
        }
        
        if (novoNo.nome.compareToIgnoreCase(pai.nome) < 0) {
            pai.esq = novoNo;
        } else {
            pai.dir = novoNo;
        }
        
        novoNo.esq = null;
        novoNo.dir = null;
    }

    public ArrayList<NoABB> CamCentral() {
        ArrayList<NoABB> vetor = new ArrayList<>();
        if (raiz == null) return vetor;
        
        ArrayList<NoABB> auxiliar = new ArrayList<>();
        NoABB atual = raiz;
        
        while (atual != null || !auxiliar.isEmpty()) {
            while (atual != null) {
                auxiliar.add(atual);
                atual = atual.esq;
            }
            
            atual = auxiliar.remove(auxiliar.size() - 1);
            vetor.add(new NoABB(atual.nome, atual.registros));
            
            atual = atual.dir;
        }
        
        return vetor;
    }

    public NoABB pesquisar(String num) {
        return pesquisar(num, this.raiz);
    }

    private NoABB pesquisar(String num, NoABB no) {
        if (no == null) {
            return null;
        }
        if (num.equalsIgnoreCase(no.getNum())) {
            return no;
        }
        if (num.compareToIgnoreCase(no.getNum()) < 0) {
            return pesquisar(num, no.getEsq());
        }
        return pesquisar(num, no.getDir());
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

                
                NoABB resultado = pesquisar(nomePesquisa);

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