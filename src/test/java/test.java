
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static function.Functions.rootDir;

public class test {

    /*public static void main(String[] args) {
        String dir = System.getenv("APPDATA") + System.getProperty("file.separator")+"aa";
        String dir2 = System.getProperty("user.dir")+System.getProperty("file.separator")+"bb";
        String dir3 = System.getProperty("user.home")+System.getProperty("file.separator")+"cc";
        System.out.println(dir);
        System.out.println(dir2);System.out.println(dir3);
        writeFile("azeazeazeazeazeaze");
    }

    public static void writeFile(String value){

        String PATH = rootDir;
        String directoryName = PATH.concat("aze");

        File directory = new File(directoryName);
        if (!directory.exists()){
            directory.mkdir();
        }

        String fileName = "id + getTimeStamp()" + ".txt";
        File file = new File(directoryName + "/" + fileName);
        try{
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }*/

}