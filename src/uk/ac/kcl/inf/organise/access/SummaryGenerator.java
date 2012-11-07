package uk.ac.kcl.inf.organise.access;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import uk.ac.kcl.inf.organise.SettingType;
import uk.ac.kcl.inf.organise.data.Database;
import uk.ac.kcl.inf.organise.data.Priority;
import uk.ac.kcl.inf.organise.data.Task;
import uk.ac.kcl.inf.organise.data.TaskUtils;
import uk.ac.kcl.inf.settings.Settings;

public class SummaryGenerator {
    public static final String SUMMARY_FILE = "tasks.pdf";
    
    public static void generate (Database database) {
        Path summaryDirectory = Settings.get ().getFilePath (SettingType.summaryDirectory);
        Path output;
        
        if (summaryDirectory != null) {
            output = summaryDirectory.resolve (SummaryGenerator.SUMMARY_FILE);
        } else {
            output = Settings.get ().getFilePath (SettingType.databaseDirectory).resolve (SummaryGenerator.SUMMARY_FILE);
        }
        generate (output, database);        
    }
    
    public static void generate (String output, Database database) {
        generate (Paths.get (output), database);        
    }
    
    public static void generate (Path output, Database database) {
        Document document = null;

        try {
            document = new Document (PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance (document, Files.newOutputStream (output));
            document.open ();
            write (document, database);
        } catch (Throwable error) {
            error.printStackTrace ();
        } finally {
            if (document != null) {
                document.close ();
            }
        }
    }
    
    public static Paragraph normal (String text) {
        if (text == null) {
            return new Paragraph ("", FontFactory.getFont (FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
        } else {
            return new Paragraph (text, FontFactory.getFont (FontFactory.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK));
        }
    }

    public static Paragraph title (String text) {
        return new Paragraph (text, FontFactory.getFont (FontFactory.HELVETICA, 14, Font.BOLD, BaseColor.BLACK));
    }

    private static void write (Document document, Database database) throws DocumentException {
        document.add (write (database.getPriorityTasks (Priority.immediate), "Immediate"));
        document.add (write (database.getPriorityTasks (Priority.urgent), "Urgent"));
        for (String project : database.getProjects ()) {
            if (!project.equals (Database.EVENTS_PROJECT) && !project.equals (TaskUtils.DATES_PROJECT)) {
                document.add (write (database.getProjectTasks (project), project));
            }
        }
    }

    private static Chapter write (List<Task> tasks, String title) {
        Paragraph header = title (title);
        Chapter chapter = new Chapter (header, 1);

        chapter.setNumberDepth (0);
        for (Task task : tasks) {
            chapter.add (normal (task.getText ()));
        }
        
        return chapter;
    }
}