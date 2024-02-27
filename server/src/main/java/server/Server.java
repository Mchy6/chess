package server;
import dataAccess.DataAccess;
import server.service.Service;
import spark.*;
import exception.ResponseException;

public class Server {
    private final Service service;


    public Server(DataAccess dataAccess) {
        service = new Service(dataAccess);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints here
        // Clear application endpoint
        Spark.delete("/db", (req, res) -> {
            Service.clearDB(); // not implemented yet
            return "Database cleared";
        });

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clearDB);
        Spark.exception(ResponseException.class, this::exceptionHandler);



        Spark.awaitInitialization();
        return Spark.port();
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
    }

    private Object clearDB(Request req, Response res) throws ResponseException {
        service.clearDB();
        res.status(204);
        return "";
    }
}
