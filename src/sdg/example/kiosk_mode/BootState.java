package sdg.example.kiosk_mode;

public class BootState {
     static String BootNote;
    //private StringBuffer bootMsg;

    public static void setBootNote(String bootMsg) {
        BootNote = String.valueOf(bootMsg);
    }

    public static String getBootNote(){
        return String.valueOf(BootNote);

    }

}
