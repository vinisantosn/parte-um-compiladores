import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {

    private StringBuilder output = new StringBuilder();
    private String nomeModulo = "Main";
    private String nomeArquivoSaida;

    // Construtor que recebe o nome do arquivo de saída
    public CodeWriter(String nomeArquivo) {
        nomeArquivoSaida = nomeArquivo;
    }

    // Define o nome do módulo com base no nome do arquivo
    void definirNomeArquivo(String nomeArquivo) {
        nomeModulo = nomeArquivo.substring(nomeArquivo.lastIndexOf("/") + 1);
        System.out.println(nomeModulo);
    }

    // Gera o nome do registrador com base no segmento e índice
    String nomeRegistrador(String segmento, int indice) {
        switch (segmento) {
            case "local":
                return "LCL";
            case "argument":
                return "ARG";
            case "this":
                return "THIS";
            case "that":
                return "THAT";
            case "pointer":
                return "R" + (3 + indice);
            case "temp":
                return "R" + (5 + indice);
            default:
                return nomeModulo + "." + indice;
        }
    }

    // Escreve o comando push no arquivo de saída
    void escreverPush(String segmento, int indice) {
        if ("constant".equals(segmento)) {
            escreverComentario("push " + segmento + " " + indice);
            escrever("@" + indice);
            escrever("D=A");
        } else if ("static".equals(segmento) || "temp".equals(segmento) || "pointer".equals(segmento)) {
            escreverComentario("push " + segmento + " " + indice);
            escrever("@" + nomeRegistrador(segmento, indice));
            escrever("D=M");
        } else {
            escreverComentario("push " + segmento + " " + indice);
            escrever("@" + nomeRegistrador(segmento, 0));
            escrever("D=M");
            escrever("@" + indice);
            escrever("A=D+A");
            escrever("D=M");
        }
        escrever("@SP");
        escrever("A=M");
        escrever("M=D");
        escrever("@SP");
        escrever("M=M+1");
    }

    // Escreve o comando pop no arquivo de saída
    void escreverPop(String segmento, int indice) {
        if ("static".equals(segmento) || "temp".equals(segmento) || "pointer".equals(segmento)) {
            escreverComentario("pop " + segmento + " " + indice);
            escrever("@SP");
            escrever("M=M-1");
            escrever("A=M");
            escrever("D=M");
            escrever("@" + nomeRegistrador(segmento, indice));
            escrever("M=D");
        } else {
            escreverComentario("pop " + segmento + " " + indice);
            escrever("@" + nomeRegistrador(segmento, 0));
            escrever("D=M");
            escrever("@" + indice);
            escrever("D=D+A");
            escrever("@R13");
            escrever("M=D");
            escrever("@SP");
            escrever("M=M-1");
            escrever("A=M");
            escrever("D=M");
            escrever("@R13");
            escrever("A=M");
            escrever("M=D");
        }
    }

    // Escreve o comando de adição no arquivo de saída
    void escreverAritmeticaAdicao() {
        escreverComentario("add");
        escrever("@SP");
        escrever("M=M-1");
        escrever("A=M");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=D+M");
    }

    // Escreve o comando de subtração no arquivo de saída
    void escreverAritmeticaSubtracao() {
        escreverComentario("sub");
        escrever("@SP");
        escrever("M=M-1");
        escrever("A=M");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=M-D");
    }

    // Escreve a string no arquivo de saída
    private void escrever(String s) {
        output.append(String.format("%s\n", s));
    }

    // Escreve um comentário no arquivo de saída
    private void escreverComentario(String comentario) {
        escrever(String.format("// %s", comentario));
    }

    // Retorna o código gerado como uma string
    public String codigoGerado() {
        return output.toString();
    }

    // Salva o código gerado no arquivo de saída
    public void salvar() {
        try (FileWriter escritor = new FileWriter(nomeArquivoSaida)) {
            escritor.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
