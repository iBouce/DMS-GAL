package properties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UUID {

    public static String MachineID = null;
    public static String uuid(){
        try {
            String command = "wmic csproduct get UUID";
            StringBuffer output = new StringBuffer();
            Process SerNumProcess = Runtime.getRuntime().exec(command);
            BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
            String line = "";
            while (true) {
                if (!((line = sNumReader.readLine()) != null)) break;
                output.append(line + "\n");
            }
            MachineID = output.toString().substring(output.indexOf("\n"), output.length()).trim();;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return MachineID;
    }
}
