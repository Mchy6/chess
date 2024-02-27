package server.service;

import dataAccess.DataAccess;

public class Service {

    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public static void clearDB() {
        System.out.println("not implemented yet");
    }
}
