import java.util.ArrayList;
import java.util.List;

public class Command {

    public enum Type {
        ADD, 
        SUB,
        PUSH,
        POP;
    }

    public Command.Type type;
    public List<String> args = new ArrayList<>();

    public Command (String[] command) {

        type = Command.Type.valueOf(command[0].toUpperCase());

        for (int i=1;i<command.length;i++){
            var arg = command[i];
            var pos = arg.indexOf("//");
            if (pos != -1) arg = arg.substring(0, pos);
            args.add(arg.strip());
        } 
    }
    
}