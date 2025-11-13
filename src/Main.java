import com.sun.net.httpserver.HttpServer;
import controller.AlunoController;
import controller.ListarAlunosController;
import controller.CadastrarAlunoController;
import controller.ExcluirAlunoController;
import controller.AtualizarAlunoController;
import controller.BuscarAlunoController;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        try {

            HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

            AlunoController alunoController = new AlunoController();

            servidor.createContext("/api/alunos", new ListarAlunosController(alunoController));
            servidor.createContext("/api/alunos/cadastrar", new CadastrarAlunoController(alunoController));
            servidor.createContext("/api/alunos/atualizar", new AtualizarAlunoController(alunoController));
            servidor.createContext("/api/alunos/buscar", new BuscarAlunoController(alunoController));
            servidor.createContext("/api/alunos/excluir", new ExcluirAlunoController(alunoController));

            servidor.createContext("/", exchange -> {
                String caminho = exchange.getRequestURI().getPath();
                
                if (caminho.equals("/")) {
                    caminho = "/index.html";
                }

                try {
                    String diretorioBase = System.getProperty("user.dir");
                    Path arquivo = Paths.get(diretorioBase, "src", "view", caminho.substring(1));

                    if (!Files.exists(arquivo)) {
                        arquivo = Paths.get("src/view" + caminho);
                    }

                    byte[] dados = Files.readAllBytes(arquivo);
                    String tipoConteudo = "text/html";

                    if (caminho.endsWith(".css")) {
                        tipoConteudo = "text/css";
                    } else if (caminho.endsWith(".js")) {
                        tipoConteudo = "application/javascript";
                    }

                    exchange.getResponseHeaders().set("Content-Type", tipoConteudo);
                    exchange.sendResponseHeaders(200, dados.length);
                    exchange.getResponseBody().write(dados);
                    exchange.getResponseBody().close();

                } catch (IOException erro) {
                    exchange.sendResponseHeaders(404, -1);
                    exchange.close();
                }
            });

            servidor.start();
            System.out.println("Servidor iniciado em: http://localhost:8080");

        } catch (java.net.BindException e) {

            System.err.println("ERRO: A porta 8080 já está em uso!");
            System.err.println("Por favor, feche o servidor anterior ou use outra porta.");
            System.exit(1);
        }
    }
}