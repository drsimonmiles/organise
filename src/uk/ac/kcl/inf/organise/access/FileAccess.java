package uk.ac.kcl.inf.organise.access;
import uk.ac.kcl.inf.organise.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FileAccess {
    public static String loadAsBlock (Path source) throws IOException {
        StringBuilder data = new StringBuilder ();
        List<String> lines = loadAsLines (source);
        boolean first = true;
        
        for (String line : lines) {
            if (!first) {
                data.append ("\n");
            }
            data.append (line);
            first = false;
        }

        return data.toString ();
    }

    public static List<String> loadAsLines (Path source) throws IOException {
        List<String> lines = new LinkedList <> ();
        BufferedReader in;
        String line;

        if (!Files.exists (source)) {
            return lines;
        }
        in = Files.newBufferedReader(source, StandardCharsets.UTF_8);
        line = in.readLine ();
        while (line != null) {
            lines.add (line);
            line = in.readLine ();
        }
        in.close ();

        return lines;
    }
    
    public static void saveAsBlock (Path target, String data) throws IOException {
        BufferedWriter out = Files.newBufferedWriter (target, StandardCharsets.UTF_8);

        out.write (data);
        out.close ();
    }
    
    public static void saveAsLines (Path target, Iterable<? extends Object> data) throws IOException {
        BufferedWriter out = Files.newBufferedWriter (target, StandardCharsets.UTF_8);

        for (Object item : data) {
            out.write (item.toString ());
            out.write ("\n");
        }
        out.close ();
    }
}
