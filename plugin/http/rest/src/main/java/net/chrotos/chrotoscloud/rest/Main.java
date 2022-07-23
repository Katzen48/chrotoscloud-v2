package net.chrotos.chrotoscloud.rest;

public class Main {
    public Main() {
        RestServer rest = new RestServer();
        rest.start();
    }

    public static void main(String[] args) {
        new Main();
    }
}
