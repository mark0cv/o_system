package system;

import gui.Controller;
import memory.*;
import memory.Process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ShellCommands {

    private static String currentDir = System.getProperty("user.dir");  // Trenutni direktorijum
    public static ArrayList<Process> processList = new ArrayList<>();   // Lista aktivnih procesa
    private static ProcessScheduler scheduler;
    public StringBuilder sb=new StringBuilder();

    public static String getCurrentDir() {
        return currentDir;
    }

    public String getCommand(String input) {
        String[] command = input.split(" ");

        switch (command[0].toLowerCase()) {
            case "cd":
                if (command.length < 2) {
                    return "Unesite direktorijum u koji želite da pređete.";
                }
                return cd(command);

            case "dir":  // Komanda "dir" ili "ls"
            case "ls":
                return dir();

            case "ps":  // Prikaz aktivnih procesa
                return ps();

            case "mkdir":  // Kreiranje direktorijuma
                if (command.length < 2) {
                    return "Unesite ime direktorijuma koji želite da kreirate.";
                }
                return mkdir(command[1]);

            case "run":  // Pokretanje procesa
                if (command.length < 2) {
                    return "Unesite ime fajla koji želite da pokrenete.";
                }
                return run(command[1]);

            case "mem":  // Prikaz memorije (RAM-a)
                return mem();

            case "rm":  // Brisanje fajla ili direktorijuma
                if (command.length < 2) {
                    return "Unesite ime fajla ili direktorijuma koji želite da obrišete.";
                }
                return rm(command[1]);

            case "kill":  // Prekid procesa
                if (command.length < 2) {
                    return "Unesite ime ili ID procesa za prekid.";
                }
                return kill(command);
            case "readfile":
                if (command.length < 2) {
                    return "Unesite ime fajla koji želite da pročitate.";
                }
                return readFileContent(command[1]);

            case "exit":
                exit();
                return "Sistem se gasi...";
            case "help":
                return help(command);
            case "tree":
                return listDirectoryTree(new File(currentDir), "");
            case "disk":
                return printDiskHierarchy();
            case "clear", "cls":
                Controller.clear();
                return "";
            default:
                return "'" + command[0] + "' nije prepoznata kao interna ili eksterna komanda.";
        }
    }


    // Komanda cd - promena trenutnog direktorijuma
    public static String cd(String[] command) {
        try {
            if (command[1].equals("..")) {
                // Idi na roditeljski direktorijum
                File parent = new File(new File(currentDir).getParent());
                currentDir = parent.getAbsolutePath();
                System.setProperty("user.dir", currentDir);
            } else {
                // Idi na navedeni direktorijum
                File dir = new File(currentDir, command[1]);
                if (dir.exists() && dir.isDirectory()) {
                    currentDir = dir.getAbsolutePath();
                    System.setProperty("user.dir", currentDir);
                } else {
                    return "Direktorijum ne postoji.";
                }
            }
        } catch (Exception e) {
            return "Greška pri promjeni direktorijuma.";
        }
        return "Trenutni direktorijum: " + currentDir;
    }

    // Komanda dir (ls) - prikaz sadržaja trenutnog direktorijuma
    public static String dir() {
        File dir = new File(currentDir);
        File[] files = dir.listFiles();
        StringBuilder sb = new StringBuilder();
        if (files != null) {
            for (File file : files) {
                sb.append(file.getName()).append(file.isDirectory() ? " [DIR]\n" : "\n");
            }
        } else {
            return "Nema datoteka u trenutnom direktorijumu.";
        }
        return sb.toString();
    }

    // Komanda ps - prikaz aktivnih procesa
    public static String ps() {
        StringBuilder sb = new StringBuilder("Aktivni procesi:\n");
        for (Process p : processList) {
            sb.append("Proces: ").append(p.getProcessName())
                    .append(", ID: ").append(p.getIdProces())
                    .append(", RAM zauzeće: ").append(p.getInstructions().size())
                    .append(" instrukcija\n");
        }
        return sb.toString();
    }

    // Komanda mkdir - kreiranje novog direktorijuma
    public static String mkdir(String dirName) {
        File dir = new File(currentDir + File.separator + dirName);
        if (dir.exists()) {
            return "Direktorijum već postoji.";
        }
        boolean created = dir.mkdir();
        return created ? "Direktorijum uspešno kreiran." : "Greška pri kreiranju direktorijuma.";
    }

    // Komanda run - pokretanje procesa
    public static String run(String filePath) {
        File file = new File(currentDir + File.separator + filePath);
        if (!file.exists()) {
            return "Fajl ne postoji.";
        }
        Process process = new Process(file.getPath(), processList.size());
        processList.add(process);
        ProcessScheduler.processQueue.add(process);

        if (scheduler == null) {
            scheduler = new ProcessScheduler();
            scheduler.start();
        }
        return "Proces uspješno pokrenut: " + process.getProcessName();
    }

    // Komanda mem - prikaz korišćenja RAM-a
    public static String mem() {
        StringBuilder sb = new StringBuilder("Korišćenje memorije (RAM):\n");
        for (FixedPartition partition : Ram.getInstance().getPartitions()) {
            sb.append("Particija ").append(partition.getPartitionId())
                    .append(": ").append(partition.isOccupied() ? "Zauzeta" : "Slobodna")
                    .append("\n");
        }
        return sb.toString();
    }

    // Komanda rm - brisanje fajla ili direktorijuma
    public static String rm(String fileName) {
        File file = new File(currentDir + File.separator + fileName);
        if (!file.exists()) {
            return "Fajl ili direktorijum ne postoji.";
        }
        boolean deleted = file.delete();
        return deleted ? "Uspešno obrisano." : "Greška pri brisanju.";
    }

    public static String kill(String[] command) {
        if (command.length < 2) {
            return "Unesite ime ili ID procesa za prekid.";
        }
        String target = command[1];  // Korisnik može da unese ime ili ID procesa
        Process processToKill = null;

        for (Process process : processList) {
            if (process.getProcessName().equalsIgnoreCase(target) ||
                    Integer.toString(process.getIdProces()).equals(target)) {
                processToKill = process;
                break;
            }
        }
        if (processToKill != null) {
            processToKill.interrupt();
            processList.remove(processToKill);
            return "Proces " + processToKill.getProcessName() + " je uspešno prekinut.";
        } else {
            return "Proces sa imenom ili ID-jem " + target + " nije pronađen.";
        }
    }

    private String listDirectoryTree(File dir,String indent) {
        //System.out.println(currentDir);
        File[] files = dir.listFiles();
        if (files == null) {
            sb.append(indent + dir.getName()+"\n");
            return sb.toString();
        }

        for (int i=0;i<files.length;i++) {
            String arrow="├────";
            if(i==files.length-1)
                arrow="└─────";
            if (files[i].isDirectory()) {
                sb.append(indent + arrow + files[i].getName()+"\n");
                listDirectoryTree(files[i], indent + "│    ");
            } else {
                sb.append(indent + arrow + files[i].getName()+"\n");
            }
        }

        return sb.toString();
    }
    private String readFileContent(String fileName) {
        File file = new File(currentDir + File.separator + fileName);
        if (!file.exists() || !file.isFile()) {
            return "Fajl ne postoji ili nije validan.";
        }

        StringBuilder content = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            return "Greška pri čitanju fajla.";
        }

        return content.toString();
    }

    public static String printDiskHierarchy() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hijerarhijska struktura diska:\n");

        for (FileInMemory rootFile : Disc.listOfFiles) {
            printFileHierarchy(rootFile, "", sb);
        }

        return sb.toString();
    }

    private static void printFileHierarchy(FileInMemory file, String indent, StringBuilder sb) {
        sb.append(indent).append(file.getName()).append("\n");

        for (FileInMemory child : file.getChildrenFiles()) {
            printFileHierarchy(child, indent + "    ", sb);  // Dodavanje uvlake za podfajlove
        }
    }




    public String help(String[] command) {
        StringBuilder sb = new StringBuilder();

        if (command.length == 1) {
            sb.append("Dostupne komande:\n")
                    .append("cd <direktorijum> - Promena trenutnog direktorijuma.\n")
                    .append("dir ili ls - Prikazuje sadržaj trenutnog direktorijuma.\n")
                    .append("ps - Prikazuje listu aktivnih procesa.\n")
                    .append("mkdir <ime_direktorijuma> - Kreira novi direktorijum.\n")
                    .append("run <put_do_fajla> - Pokreće proces na osnovu fajla.\n")
                    .append("mem - Prikazuje trenutno zauzeće RAM-a.\n")
                    .append("rm <ime_fajla/direktorijuma> - Briše fajl ili direktorijum.\n")
                    .append("kill <ime_procesa ili ID_procesa> - Prekida navedeni proces.\n")
                    .append("exit - Isključuje sistem.\n")
                    .append("help <komanda> - Prikazuje pomoć za određenu komandu.\n");
        } else {
            switch (command[1].toLowerCase()) {
                case "cd":
                    sb.append("cd <direktorijum> - Promena trenutnog direktorijuma.\n")
                            .append("Koristite cd .. da se vratite u nadređeni direktorijum.\n");
                    break;

                case "dir":
                    sb.append("dir - Prikazuje sadržaj trenutnog direktorijuma.\n")
                            .append("Listira sve fajlove i poddirektorijume.\n");
                    break;

                case "ps":
                    sb.append("ps - Prikazuje listu aktivnih procesa.\n")
                            .append("Sadrži informacije o imenu procesa, ID-u i zauzeću RAM-a.\n");
                    break;

                case "mkdir":
                    sb.append("mkdir <ime_direktorijuma> - Kreira novi direktorijum.\n")
                            .append("Kreirajte novi direktorijum u trenutnom direktorijumu.\n");
                    break;

                case "run":
                    sb.append("run <put_do_fajla> - Pokreće proces na osnovu fajla.\n")
                            .append("Fajl mora da sadrži assembler kod za pokretanje procesa.\n");
                    break;

                case "mem":
                    sb.append("mem - Prikazuje trenutno zauzeće RAM-a.\n")
                            .append("Prikazuje informacije o zauzetim i slobodnim particijama u RAM-u.\n");
                    break;

                case "rm":
                    sb.append("rm <ime_fajla/direktorijuma> - Briše fajl ili direktorijum.\n")
                            .append("Koristite ovu komandu za brisanje fajlova ili direktorijuma.\n");
                    break;

                case "kill":
                    sb.append("kill <ime_procesa ili ID_procesa> - Prekida navedeni proces.\n")
                            .append("Prekinite izvršavanje procesa na osnovu imena ili ID-a.\n");
                    break;

                case "exit":
                    sb.append("exit - Isključuje sistem.\n")
                            .append("Zatvara simulaciju i isključuje sistem.\n");
                    break;
                default:
                    sb.append("Nepoznata komanda: ").append(command[1]).append(".\n")
                            .append("Unesite 'help' za listu dostupnih komandi.\n");
                    break;
            }
        }

        return sb.toString();
    }


    // Komanda exit - izlaz iz simulacije
    public static void exit() {
        System.out.println("Isključivanje sistema...");
        System.exit(0);
    }
}
