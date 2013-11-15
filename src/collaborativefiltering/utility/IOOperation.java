package collaborativefiltering.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author xji
 */
public class IOOperation {
    public static BufferedWriter getBufferedWriter(String filename) throws IOException {
        return getBufferedWriter(filename, false);
    }

    public static BufferedWriter getBufferedWriter(String filename, boolean append) throws IOException {
        File file = new File(filename);
        FileWriter fw;
        BufferedWriter bw;
        file.createNewFile();
        if(append == true)
            fw = new FileWriter(file.getAbsoluteFile(),true);
        else
            fw = new FileWriter(file.getAbsoluteFile());
        bw = new BufferedWriter(fw);
        return bw;
    }

    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}

