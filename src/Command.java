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
        NEG,
        GOTO,
        IF,
        LABEL,
        RETURN,
        CALL,
        FUNCTION;
    }

    public Tipos tipo;
    public List<String> args = new ArrayList<>();

    // Construtor que recebe um array de strings representando um comando
    public Command(String[] command) {

        if (command[0].equals("if-goto")) {
            tipo = Tipos.IF;
        } else {
            // O primeiro elemento do array representa o tipo do comando (ADD, SUB, PUSH,
            // POP)
            tipo = Tipos.valueOf(command[0].toUpperCase());
        }

        // Itera sobre os elementos restantes do array para obter os argumentos
        for (int i = 1; i < command.length; i++) {
            // Remove comentários e adiciona o argumento à lista
            // String arg = command[i].split("//")[0].strip();
            // args.add(arg);

            var arg = command[i];
            var pos = arg.indexOf("//");
            if (pos != -1)
                arg = arg.substring(0, pos);
            args.add(arg.strip());
        }
    }
}
