package net.chrotos.chrotoscloud.worker;

import net.chrotos.chrotoscloud.Cloud;

public class Main {
    public static void main(String[] args) {
        Cloud.setServiceClassLoader(Main.class.getClassLoader());
        WorkerCloud cloud = (WorkerCloud) Cloud.getInstance();

        cloud.load();
        cloud.initialize();

        try {
            new Worker(cloud).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
