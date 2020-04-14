package syncsubtitles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 *
 * @author fariasdc
 */
public class SrtSubtitles {
    
    /**
     *
     * @param  file the {@code File} to be synced 
     * @return {@code int} time in minutes of the last subtitle in the file
     * @exception IOException
     * @exception FileNotFoundException
     */
    public static int getTotalMinutes(File file) throws IOException {
        Scanner diskScanner;
        try {
            diskScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        String timecode = "", piece;
        while (diskScanner.hasNext()) {
            piece = diskScanner.next();
            if (piece.length() == 12 && piece.charAt(2) == ':' && piece.charAt(8) == ',')
                timecode = piece;
        }
        diskScanner.close();
        if (timecode.equals(""))
            throw new IOException();
        return (int) howManySeconds(timecode.replace(',', '.')) / 60;
    }

    /**
     *
     * @param file 'srt' {@code File} to be synchronized
     * @param shift        {@code Double} how many seconds to be delayed or speeded up
     * @param startingPoint {@code Double} from here on the shift take effect
     * @return the new synced {@code File}
     */
    public static String sync(File file, double shift, double startingPoint) {
        Scanner diskScanner;
        try {
            diskScanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return null;
        }
        String newFileStr = "";
        
        while (diskScanner.hasNext()) {
            String line = diskScanner.nextLine();
            // identify timecode container
            if (line.length() == 29 && line.charAt(2) == ':' & line.charAt(15) == '>') {
                newFileStr += getTimecodeLine(line, shift, startingPoint);
            } else {
                newFileStr += line + "\n";
            }
        }

        diskScanner.close();
        try {
            overwriteFile(file, newFileStr);
        } catch (IOException e) {
            return null;
        }
        return newFileStr;
    }

    // get shifted ##:##:##,### --> ##:##:##,###
    private static String getTimecodeLine(String line, double shift, double startingPoint) {
        String[] timecodes = new String[2];
        String[] times = line.split(" --> ");
        timecodes[0] = times[0].replace(',', '.');
        timecodes[1] = times[1].replace(',', '.');

        if (howManySeconds(timecodes[0]) / 60 >= startingPoint) {
            return shiftTimecode(timecodes[0], shift).replace('.', ',') + " --> "
                + shiftTimecode(timecodes[1], shift).replace('.', ',') + "\n";
        }
        return timecodes[0].replace('.', ',') + " --> " + timecodes[1].replace('.', ',') + "\n";
    }

    private static String shiftTimecode(String timecode, double shift) {      
        int min, hr;
        double sec;
        double totalSec = howManySeconds(timecode) + shift;                                         

        if (totalSec > 0) {
            hr = (int) (totalSec / 3600);
            totalSec %= 3600;
            min = (int) (totalSec / 60);
            sec = totalSec % 60;
        } else {
            hr = 0; min = 0; sec = 0;
        }

        return String.format("%02d:%02d:%06.3f", hr, min, sec);
    }

    // converts a timecode (hr:min:sec) to seconds
    private static double howManySeconds(String timecode) {
        String[] measures = timecode.split(":");
        int hr = Integer.parseInt(measures[0]);
        int min = Integer.parseInt(measures[1]);
        double sec = Double.parseDouble(measures[2]);        
        return 3600 * hr + 60 * min + sec;
    }

    private static void overwriteFile(File oldFile, String newFileStr) throws IOException {
        try (PrintStream printOut = new PrintStream(oldFile.getAbsolutePath())) {
            printOut.print(newFileStr);
        }
    }
}
