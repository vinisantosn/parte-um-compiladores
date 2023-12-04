import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    List<String[]> comandosList;

    // Construtor recebe o input e inicializa a lista de comandos
    public Parser(String input) {
        final String eol = System.getProperty("line.separator");

        // Split do input em linhas e mapeamento para arrays de palavras
        comandosList = Arrays.stream(input.split(eol))
                .map(String::strip)
                .filter(s -> s != "" && !s.startsWith("//"))
                .map(s -> s.split("\\s+")) // Usa expressão regular para tratar múltiplos espaços
                .collect(Collectors.toList());
    }

    // Verifica se ainda existem comandos para processar
    public boolean temMaisComandos() {
        return !comandosList.isEmpty();
    }

    // Obtém e remove o próximo comando da lista
    public Command proximoComando() {
        return new Command(comandosList.remove(0));
    }

}
