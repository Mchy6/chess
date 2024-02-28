package server;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import requests.RegisterRequest;
import requests.RegisterResponse;
import server.service.Service;
import spark.*;
import exception.ResponseException;

public class Server {
    private final Service service;


    public Server() {
        service = new Service(new MemoryDataAccess());
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        var gson = new Gson();


        // Register your endpoints here
        // Clear application endpoint
        Spark.delete("/db", (req, res) -> {

            try {
                service.clearDB();
            } catch (DataAccessException e) {
                res.status(500);
                return "{}";
            }

            return "{}";
        });
        // Register endpoint
        Spark.post("/user", (req, res) -> {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
            RegisterResponse registerResponse = service.register(registerRequest);

            try {
                service.register(registerRequest);
            } catch (BadRequestException e) { // each exception should be implemented in src/main/java/server/exceptions
                System.out.println("Bad request: " + e.getMessage());
            } catch (AlreadyTakenException e) {
                System.out.println("Username or email already taken: " + e.getMessage());
            } catch (InternalServerErrorException e) {
                System.out.println("Internal server error: " + e.getMessage());
            } catch (Exception e) {
                // Catch-all for any other exceptions not explicitly caught above
                System.out.println("An unexpected error occurred: " + e.getMessage());
            }

            return gson.toJson(registerResponse);
        });


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
