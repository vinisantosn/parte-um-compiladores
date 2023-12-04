import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {

    private StringBuilder output = new StringBuilder();
    private String nomeModulo = "Main";
    private String nomeArquivoSaida;
    private int contagemDeRotulos = 0;
    private int contagemDeChamadas = 0;

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
    void escreverAdicao() {
        escreverComentario("add");
        escrever("@SP");
        escrever("M=M-1");
        escrever("A=M");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=D+M");
    }

    // Escreve o comando de subtração no arquivo de saída
    void escreverSubtracao() {
        escreverComentario("sub");
        escrever("@SP");
        escrever("M=M-1");
        escrever("A=M");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=M-D");
    }

    // Implementação otimizada do comando and
    void escreverAnd() {
        escreverComentario("and");
        escrever("@SP");
        escrever("AM=M-1");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=D&M");
    }

    // Implementação otimizada do comando or
    void escreverOr() {
        escreverComentario("or");
        escrever("@SP");
        escrever("AM=M-1");
        escrever("D=M");
        escrever("A=A-1");
        escrever("M=D|M");
    }

    // Implementação otimizada do comando not
    void escreverNot() {
        escreverComentario("not");
        escrever("@SP");
        escrever("A=M");
        escrever("A=A-1");
        escrever("M=!M");
    }

    // Implementação otimizada do comando neg
    void escreverNeg() {
        escreverComentario("neg");
        escrever("@SP");
        escrever("A=M");
        escrever("A=A-1");
        escrever("M=-M");
    }

    // Implementação otimizada do comando eq (igual)
    void escreverEq() {
        escreverComparacao("JEQ");
    }

    // Implementação otimizada do comando gt (maior que)
    void escreverGt() {
        escreverComparacao("JGT");
    }

    // Implementação otimizada do comando lt (menor que)
    void escreverLt() {
        escreverComparacao("JLT");
    }

    // Implementação otimizada para comandos de comparação
    void escreverComparacao(String condicao) {
        String labelTrue = condicao + "_TRUE_" + nomeModulo + "_" + (contagemDeRotulos);
        String labelFalse = condicao + "_FALSE_" + nomeModulo + "_" + (contagemDeRotulos);

        escrever("@" + labelTrue);
        escrever("D;" + condicao);
        escrever("D=0");
        escrever("@" + labelFalse);
        escrever("0;JMP");
        escrever("(" + labelTrue + ")");
        escrever("D=-1");
        escrever("(" + labelFalse + ")");
        escreverPushD();

        contagemDeRotulos++;
    }

    // Implementação do comando de label
    void escreverLabel(String label) {
        escrever("(" + label + ")");
    }

    // Implementação do comando de goto
    void escreverGoto(String label) {
        escrever("@" + label);
        escrever("0;JMP");
    }

    // Implementação auxiliar para empilhar o valor de D
    private void escreverPushD() {
        escrever("@SP");
        escrever("A=M");
        escrever("M=D");
        escrever("@SP");
        escrever("M=M+1");
    }

    void escreverFramePush(String value) {
        escrever("@" + value);
        escrever("D=M");
        escreverPushD();
    }
    
    public void escreverInit() {
        // escrever("@256");
        // escrever("D=A");
        // escrever("@SP");
        // escrever("M=D");
        // escreverCall("Sys.init", 0);
    }
    
    void escreverIf(String label) {
        escrever("@SP");
        escrever("AM=M-1");
        escrever("D=M");
        escrever("M=0");
        escrever("@" + label);
        escrever("D;JNE");
    }
    
    void escreverFunction(String funcName, int nLocals) {
        String loopLabel = funcName + "_INIT_LOCALS_LOOP";
        String loopEndLabel = funcName + "_INIT_LOCALS_END";
    
        escrever("(" + funcName + ")");
        escrever("@" + nLocals);
        escrever("D=A");
        escrever("@R13");
        escrever("M=D");
        escrever("(" + loopLabel + ")");
        escrever("@" + loopEndLabel);
        escrever("D;JEQ");
        escrever("@0");
        escrever("D=A");
        escrever("@SP");
        escrever("A=M");
        escrever("M=D");
        escrever("@SP");
        escrever("M=M+1");
        escrever("@R13");
        escrever("MD=M-1");
        escrever("@" + loopLabel);
        escrever("0;JMP");
        escrever("(" + loopEndLabel + ")");
    }
    
    void escreverCall(String funcName, int numArgs) {
        String returnAddr = funcName + "_RETURN_" + contagemDeChamadas;
    
        escrever("@" + returnAddr);
        escrever("D=A");
        escrever("@SP");
        escrever("A=M");
        escrever("M=D");
        escrever("@SP");
        escrever("M=M+1");
        escreverFramePush("LCL");
        escreverFramePush("ARG");
        escreverFramePush("THIS");
        escreverFramePush("THAT");
        escrever("@" + numArgs);
        escrever("D=A");
        escrever("@5");
        escrever("D=D+A");
        escrever("@SP");
        escrever("D=M-D");
        escrever("@ARG");
        escrever("M=D");
        escrever("@SP");
        escrever("D=M");
        escrever("@LCL");
        escrever("M=D");
        escreverGoto(funcName);
        escrever("(" + returnAddr + ")");
        contagemDeChamadas++;
    }
    
    void escreverReturn() {
        escrever("@LCL");
        escrever("D=M");
        escrever("@R13");
        escrever("M=D");
        escrever("@5");
        escrever("A=D-A");
        escrever("D=M");
        escrever("@R14");
        escrever("M=D");
        escrever("@SP");
        escrever("AM=M-1");
        escrever("D=M");
        escrever("@ARG");
        escrever("A=M");
        escrever("M=D");
        escrever("D=A+1");
        escrever("@SP");
        escrever("M=D");
        escreverFramePop("THAT");
        escreverFramePop("THIS");
        escreverFramePop("ARG");
        escreverFramePop("LCL");
        escrever("@R14");
        escrever("A=M");
        escrever("0;JMP");
    }
    
    void escreverFramePop(String value) {
        escrever("@R13");
        escrever("AM=M-1");
        escrever("D=M");
        escrever("@" + value);
        escrever("M=D");
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
