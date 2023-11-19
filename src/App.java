import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class App {
    private static String fromFile(File file) {      
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            String textoDoArquivo = new String(bytes, "UTF-8");
            return textoDoArquivo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

    private static void translateFile (File file, CodeWriter code) {
        String input = fromFile(file);
        Parser p = new Parser(input);
        while (p.hasMoreCommands()) {
            var command = p.nextCommand();
            switch (command.type) {
                case ADD:
                    code.writeArithmeticAdd();
                    break;
                case SUB:
                    code.writeArithmeticSub();
                    break;
                case PUSH:
                    code.writePush(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;                
                case POP:
                    code.writePop(command.args.get(0), Integer.parseInt(command.args.get(1)));
                    break;
                default:
                    System.out.println(command.type.toString()+" not implemented");
            }
        } 
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide a single file path argument.");
            System.exit(1);
        }
        File file = new File(args[0]);
        if (!file.exists()) {   
            System.err.println("The file doesn't exist.");
            System.exit(1);
        }        
        if (file.isDirectory()) {
            var outputFileName = file.getAbsolutePath() +"/"+ file.getName()+".asm";
            System.out.println(outputFileName);
            CodeWriter code = new CodeWriter(outputFileName);    
            for (File f : file.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".vm")) {

                    var inputFileName = f.getAbsolutePath();
                    System.out.println("compiling " +  inputFileName);
                    translateFile(f,code);
                }
            }
            code.save();
        
        } else if (file.isFile()) {
            if (!file.getName().endsWith(".vm"))  {
                System.err.println("Please provide a file name ending with .vm");
                System.exit(1);
            } else {
                var inputFileName = file.getAbsolutePath();
                var pos = inputFileName.lastIndexOf('.');
                var outputFileName = inputFileName.substring(0, pos) + ".asm";
                CodeWriter code = new CodeWriter(outputFileName);
                System.out.println("compiling " +  inputFileName);
                translateFile(file,code); 
                code.save();               
            }
        }
    }

}
