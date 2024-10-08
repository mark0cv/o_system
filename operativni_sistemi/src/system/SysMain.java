package system;

import assembler.AsmHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import memory.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SysMain extends Application {

    public static void getData(Path p, FileInMemory file) {
        try {
            if (Files.isDirectory(p)) {
                var stream = Files.newDirectoryStream(p);
                Disc.listOfFiles.add(file);
                System.out.println("dodao"+file.getName());
                for (Path p1 : stream) {
                    if (Files.isDirectory(p1)) {
                        FileInMemory f = new FileInMemory(p1.getFileName().toString(), 0, file);
                        file.getChildrenFiles().add(f);
                        getData(p1, f);
                    } else {
                        // Učitavanje sadržaja fajla
                        List<String> content = Files.readAllLines(p1);
                        FileInMemory file1 = new FileInMemory(p1.getFileName().toString(), content.size(), file);
                        file1.setContent(new ArrayList<>(content));

                        Disc.listOfFiles.add(file1);
                        file.getChildrenFiles().add(file1);

                        double dummy = content.size() * 1.0 / Block.size;
                        int numBlocks = (int) Math.ceil(dummy);
                        int index = 0;

                        for (int i = 0; i < numBlocks; i++) {
                            Block b = Disc.freeSpace.getBlock();
                            b.setFileName(file1.getName());

                            for (int y = 0; y < Block.size; y++) {
                                if (index < content.size()) {
                                    b.getContent().add(content.get(index));
                                    index++;
                                } else
                                    break;
                            }

                            b.setOcuppied(true);
                            Disc.freeSpace = Disc.freeSpace.getNextBlock();
                            if (Disc.freeSpace != null) {
                                Disc.freeSpace.setPreviousBlock(null);
                            }
                            Disc.occupiedSpace.add(b);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Priprema RAM-a i diska
        prepareRamAndDisk();

        // Učitavanje podataka sa diska
        Path p = Paths.get("Disk");
        FileInMemory rootFile = new FileInMemory(p.getFileName().toString(), 0, null);
        getData(p, rootFile);
        System.out.println(Disc.listOfFiles.toString());

        launch(args);
    }

    private static void prepareRamAndDisk() {
        // Priprema RAM-a
        for (int i = 0; i < Ram.NUM_OF_PARTITIONS; i++) {
            Ram.getInstance().getPartitions().get(i).free();
        }

        // Priprema diska
        int address = 0;
        Disc.freeSpace = new FreeSpacePointer(new Block(address));
        address++;
        FreeSpacePointer pointer = new FreeSpacePointer(new Block(address));
        Disc.freeSpace.setNextBlock(pointer);
        pointer.setPreviousBlock(Disc.freeSpace);

        for (int i = 2; i < 1024; i++) {
            address++;
            FreeSpacePointer nextPointer = new FreeSpacePointer(new Block(address));
            pointer.setNextBlock(nextPointer);
            nextPointer.setPreviousBlock(pointer);
            pointer = nextPointer;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(SysMain.class.getResource("/gui/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/gui/style.css").toExternalForm());
        stage.setTitle("gg");
        stage.setScene(scene);
        stage.show();
    }
}
