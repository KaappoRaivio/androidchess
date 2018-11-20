package kaappo.androidchess;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LogManager {
    private static String filename = "log";

    private FileOutputStream outputStream;

    public LogManager (Context context) {

        String name = filename + System.currentTimeMillis() + ".txt";

        try {
            File file = new File(context.getFilesDir(), name);
            file.createNewFile();
            this.outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);

        } catch (Exception e) { throw new RuntimeException(e);}

    }

    public void write (String s) {
        try {
            this.outputStream.write(s.getBytes());
            this.outputStream.write("\n".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeStream () {
        try {
            this.outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public FileOutputStream getOutputStream() {
        return outputStream;
    }
}
