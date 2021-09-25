package application;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ThreadSafeWithSynchronization {
    static Logger LOGGER;
    static {
        try(FileInputStream ins = new FileInputStream("log.config")){
            LogManager.getLogManager().readConfiguration(ins);
            LOGGER = Logger.getLogger(ThreadSafeWithSynchronization.class.getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public final List<Integer> Randoms = new ArrayList<>();

    public void startThreadProcess() {
        Producer.start();
        Consumer.start();
    }

    private final Thread Producer = new Thread(() -> {
        synchronized (Randoms) {
            Integer i;
            while (true) {
                for (i = 0; i < new Random().nextInt(100); i++) {
                    Integer n = new Random().nextInt(100);
                    Randoms.add(n);
                    System.out.println(n);
                }
                System.out.println("wrote");
                LOGGER.log(Level.INFO, "Randoms: " + Randoms.toString());
                try {
                    Randoms.notify();
                    Randoms.wait();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private final Thread Consumer = new Thread(() -> {
        synchronized (Randoms) {
            Integer i;
            while (true) {
                for (i = 0; i < Randoms.size(); i++) {
                    System.out.println(Randoms.get(i));
                }
                Randoms.clear();
                System.out.println("read");
                try {
                    Randoms.notify();
                    Randoms.wait();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });
}