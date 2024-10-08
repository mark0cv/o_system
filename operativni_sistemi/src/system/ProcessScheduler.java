package system;

import memory.Process;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProcessScheduler extends Thread {
    public static List<Process> processQueue = new CopyOnWriteArrayList<>(); //thread-safe

    @Override
    public void run() {
        while (true) {
            // ako nema procesa onda ceka 1s
            if (processQueue.isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            Process nextProcess = findNextProcess();

            if (nextProcess != null) {
                nextProcess.start();
                try {
                    nextProcess.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                processQueue.remove(nextProcess);
            }
        }
    }
    private Process findNextProcess() {
        Process bestProcess = null;
        long currentTime = System.currentTimeMillis();
        double bestRatio = -1;

        for (Process p : processQueue) {
            double responseRatio = p.getResponseRatio(currentTime);
            if (responseRatio > bestRatio) {
                bestRatio = responseRatio;
                bestProcess = p;
            }
        }

        return bestProcess;
    }
}
