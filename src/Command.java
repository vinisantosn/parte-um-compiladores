import java.util.ArrayList;
import java.util.List;

public class Command {

    public enum Tipos {
        ADD,
        SUB,
        PUSH,
        POP,
        NOT,
        EQ,
        LT,
        GT,
        AND,
        OR,
        NEG;
    }

    public Tipos tipo;
    public List<String> args = new ArrayList<>();

    // Construtor que recebe um array de strings representando um comando
    public Command(String[] command) {
        // O primeiro elemento do array representa o tipo do comando (ADD, SUB, PUSH,
        // POP)
        tipo = Tipos.valueOf(command[0].toUpperCase());

        // Itera sobre os elementos restantes do array para obter os argumentos
        for (int i = 1; i < command.length; i++) {
            // Remove comentários e adiciona o argumento à lista
            String arg = command[i].split("//")[0].strip();
            args.add(arg);
        }
    }
}
