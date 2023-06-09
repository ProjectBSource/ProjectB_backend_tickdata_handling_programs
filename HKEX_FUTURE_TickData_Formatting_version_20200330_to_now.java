import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * java HKEX_FUTURE_TickData_Formatting_version_20220330_to_now [day file path] [night file path] [output file path] [F/O] [HSI/MHI]
 */

public class HKEX_FUTURE_TickData_Formatting_version_20200330_to_now {

    static File tickdata_tr = null;
    static FileReader fr_tr = null;
    static BufferedReader br_tr = null;
    static String line_tr = null;
    static String last_line_tr_before_switch = null;

    static File tickdata_tr_aht = null;
    static FileReader fr_tr_aht = null;
    static BufferedReader br_tr_aht = null;
    static String line_tr_aht = null;
    static String last_line_tr_aht = null;
    static String last_line_tr_aht_before_switch = null;

    static String outputFilepath = null;
    static String futureORoption = null;
    static String whatClassCode = null;

    public static void main(String args[]) throws Exception{
        tickdata_tr = new File (args[0]);
        fr_tr = new FileReader(tickdata_tr);
        br_tr = new BufferedReader(fr_tr);
        line_tr = null;

        tickdata_tr_aht = new File (args[1]);
        fr_tr_aht = new FileReader(tickdata_tr_aht);
        br_tr_aht = new BufferedReader(fr_tr_aht);
        line_tr_aht = null;

        outputFilepath = args[2];
        futureORoption = args[3];
        whatClassCode = args[4];

        readFile(1);
        System.out.println("finished");
    }

    private static void readFile(int aht_or_not) throws Exception{
        StringBuilder formated_content = new StringBuilder();
        if(aht_or_not==1){
            if(last_line_tr_before_switch!=null){
                //HSI   F2020062900000000.00000000 2020030309140000026282.0000000000000001020
                formated_content.append(reforamtData_version2(last_line_tr_before_switch));
                formated_content.append("\n");
                last_line_tr_before_switch = null;
            }
            Date begin_yyyyMMdd = null;
            while((line_tr = br_tr.readLine())!=null){
                if(line_tr.substring(0, 6).trim().equals(whatClassCode)){
                    if(line_tr.substring(6, 7).equals(futureORoption)){
                        if(begin_yyyyMMdd == null){
                            begin_yyyyMMdd = new SimpleDateFormat("yyyyMMdd").parse(line_tr.substring(33, 41));
                        }
                        Date curr_yyyyMMdd = new SimpleDateFormat("yyyyMMdd").parse(line_tr.substring(33, 41));
                        if(curr_yyyyMMdd.equals(begin_yyyyMMdd)==false){
                            last_line_tr_before_switch = line_tr;
                            writeFile(formated_content);
                            readFile(2);
                            break;
                        }
                        //HSI   F2020033000000000.00000000 2020030209140000025953.0000000000000001020
                        formated_content.append(reforamtData_version2(line_tr));
                        formated_content.append("\n");
                    }
                }
            }
            aht_or_not = -1;
            fr_tr.close();
            br_tr.close();
        }
        else if(aht_or_not==2){
            if(last_line_tr_aht_before_switch!=null){
                //HSI   F2020033000000000.00000000 2020030317150000026337.0000000000000001001
                formated_content.append(reforamtData_version2(last_line_tr_aht_before_switch));
                formated_content.append("\n");
                last_line_tr_aht_before_switch = null;
            }
            Date end_yyyyMmddkk = null;
            while((line_tr_aht = br_tr_aht.readLine())!=null){
                if(line_tr.substring(0, 6).trim().equals(whatClassCode)){
                    if(line_tr.substring(6, 7).equals(futureORoption)){
                        if(end_yyyyMmddkk == null){
                            end_yyyyMmddkk = new SimpleDateFormat("yyyyMMddkk").parse(line_tr_aht.substring(33, 43));
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(end_yyyyMmddkk);
                            calendar.add(Calendar.HOUR_OF_DAY, 12);
                            end_yyyyMmddkk = calendar.getTime();
                        }
                        Date curr_yyyyMMddkk = new SimpleDateFormat("yyyyMMddkk").parse(line_tr_aht.substring(33, 43));
                        if(curr_yyyyMMddkk.after(end_yyyyMmddkk)){
                            last_line_tr_aht_before_switch = line_tr_aht;
                            writeFile(formated_content);
                            readFile(1);
                            break;
                        }
                        //HSI   F2020033000000000.00000000 2020030217150000026250.0000000000000001001
                        formated_content.append(reforamtData_version2(line_tr_aht));
                        formated_content.append("\n");
                    }
                }
            }
            aht_or_not = -1;
            fr_tr_aht.close();
            br_tr_aht.close();
        }
    }

    private static void writeFile(StringBuilder content) throws Exception{
        FileWriter fw = new FileWriter(outputFilepath, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content.toString());
        bw.close();
        fw.close();
    }

    private static String reforamtData_version2(String data){
        String newData = null;
        newData = String.format("%s,%s,%s,%s,%s",
            data.substring(33,(33 + 8)).trim(),
            data.substring(41,(41 + 6)).trim(),
            data.substring(50,(50 + 5)).trim(),
            data.substring(69,(69 + 3)).trim(),
            (data.substring(7,(7 + 4)).trim())
        );
        return newData;
    }
}
