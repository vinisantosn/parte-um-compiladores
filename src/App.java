import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Classe principal que contém métodos para ler e traduzir arquivos VM.
 */
public class App {

    /**
     * Função para ler o conteúdo de um arquivo.
     *
     * @param arquivo O arquivo a ser lido.
     * @return O conteúdo do arquivo como uma string, ou null se ocorrer um erro.
     */
    private static String lerArquivo(File arquivo) {

        // Verifica se o arquivo é nulo ou não existe
        if (arquivo == null || !arquivo.exists() || !arquivo.isFile()) {
            System.err.println("Arquivo inválido.");
            return null;
        }

        // Tenta ler o conteúdo do arquivo
        try {
            byte[] bytes = Files.readAllBytes(arquivo.toPath());
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Função para traduzir um arquivo de entrada VM e gerar código de saída
     * Assembly.
     *
     * @param arquivo O arquivo de entrada VM.
     * @param codigo  O objeto CodeWriter para escrever o código de saída Assembly.
     */
    private static void traduzirArquivo(File arquivo, CodeWriter codigo) {

        // Lê o conteúdo do arquivo
        String entrada = lerArquivo(arquivo);

        // Cria um objeto Parser para analisar os comandos
        Parser p = new Parser(entrada);

        // Processa os comandos enquanto houver mais comandos
        while (p.temMaisComandos()) {

            // Obtém o próximo comando do Parser
            var comando = p.proximoComando();

            // Executa a ação correspondente ao tipo de comando
            switch (comando.tipo) {
                case ADD:
                    codigo.escreverAdicao();
                    break;
                case SUB:
                    codigo.escreverSubtracao();
                    break;
                case PUSH:
                    codigo.escreverPush(comando.args.get(0), Integer.parseInt(comando.args.get(1)));
                    break;
                case POP:
                    codigo.escreverPop(comando.args.get(0), Integer.parseInt(comando.args.get(1)));
                    break;
                case NOT:
                    codigo.escreverNot();
                    break;
                case EQ:
                    codigo.escreverEq();
                    break;
                case LT:
                    codigo.escreverLt();
                    break;
                case GT:
                    codigo.escreverGt();
                    break;
                case AND:
                    codigo.escreverAnd();
                    break;
                case OR:
                    codigo.escreverOr();
                    break;
                case NEG:
                    codigo.escreverNeg();
                    break;
                case GOTO:
                    codigo.escreverGoto(comando.args.get(0));
                    break;
                
                case IF:
                        codigo.escreverIf(comando.args.get(0));
                        break;

                case LABEL:
                        codigo.escreverLabel(comando.args.get(0));
                        break;
                    
                case RETURN:
                        codigo.escreverReturn();
                        break;

                case CALL:
                        codigo.escreverCall(comando.args.get(0), Integer.parseInt(comando.args.get(1)));
                        break;

                case FUNCTION:
                        codigo.escreverFunction(comando.args.get(0), Integer.parseInt(comando.args.get(1)));
                        break;
                default:
                    System.out.println("Comando " + "\"" + comando.tipo.toString() + "\"" + " não implementado.");
            }
        }
    }

    /**
     * Método principal que recebe o caminho do arquivo VM como argumento e inicia a
     * compilação.
     *
     * @param args Os argumentos da linha de comando. Deve conter o caminho do
     *             arquivo VM.
     */
    public static void main(String[] args) {

        // Cria um objeto File a partir do caminho do arquivo fornecido como argumento
        File arquivo = new File(args[0]);

        // Verifica se o arquivo possui a extensão correta
        if (!arquivo.getName().endsWith(".vm")) {
            System.err.println("Por favor, forneça um nome de arquivo que termine com .vm");
            System.exit(1);
        } else {

            // Gera o nome do arquivo de saída com extensão .asm
            var nomeArquivoEntrada = arquivo.getAbsolutePath();
            var pos = nomeArquivoEntrada.lastIndexOf('.');
            var nomeArquivoSaida = nomeArquivoEntrada.substring(0, pos) + ".asm";

            // Cria um objeto CodeWriter para escrever o código de saída
            CodeWriter codigo = new CodeWriter(nomeArquivoSaida);

            // Exibe mensagem informando o início da compilação
            System.out.println("COMPILANDO O ARQUIVO: " + nomeArquivoEntrada);

            // Traduz o arquivo e gera o código de saída
            traduzirArquivo(arquivo, codigo);

            // Salva o código gerado no arquivo de saída
            codigo.salvar();
        }

    }
}