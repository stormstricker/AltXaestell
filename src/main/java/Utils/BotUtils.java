package Utils;

import com.google.common.collect.Lists;
import com.jakewharton.fliptables.FlipTable;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class BotUtils {
    public static void main(String[] args) {

    }


    public static void print(Map<?, ?> map) {
        for (Object key: map.keySet())  {
            System.out.println(key + ": " + map.get(key));
        }
    }

    public static void print(List<?> list)  {
        for (Object o: list)  {
            System.out.print(o + "; ");
        }

        System.out.println();
    }

    public static boolean isInFile(String inputLine, String filename)  {
        try  {
            BufferedReader br = new BufferedReader(new FileReader(Paths.get("setups", filename).toFile()));

            String line;
            while ((line = br.readLine())!=null)  {
                if (line.equalsIgnoreCase(inputLine))  {
                    return true;
                }
            }
            br.close();

            return false;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean replaceContent(String line, String filename)  {
        boolean isClean = clearFile(filename);
        if (isClean)  {
            try {
                Writer output;
                System.out.println(Paths.get("").toString());
                output = new BufferedWriter(new FileWriter(Paths.get("setups", filename).toFile(), true));
                output.append(line + System.lineSeparator());
                output.close();
                return true;
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else  {
            return false;
        }
    }

    public static boolean clearFile(String filename)  {
        try {
            Writer output;
            System.out.println(Paths.get("").toString());
            output = new BufferedWriter(new FileWriter(Paths.get("setups", filename).toFile()));

            output.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getFirstLineFromFile(String filename)  {

        try  {
            BufferedReader br = new BufferedReader(new FileReader(Paths.get("setups", filename).toFile()));
            String result = br.readLine();
            br.close();

            return result!=null ? result : "";
        }
        catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getAllLinesFromFile(String filename)  {
        try  {
            BufferedReader br = new BufferedReader(new FileReader(Paths.get("setups", filename).toFile()));
            List<String> result = new ArrayList<>();
            String line;
            while ((line = br.readLine())!=null)  {
                result.add(line);
            }
            br.close();

            return result;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addLineToFile(Long line, String filename)  {
        return addLineToFile(String.valueOf(line), filename);
    }

    public static boolean addLineToFile(String line, String filename) {
        try {
            Writer output;
            System.out.println(Paths.get("").toString());
            output = new BufferedWriter(new FileWriter(Paths.get("setups", filename).toFile(), true));
            output.append(line + System.lineSeparator());
            output.close();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeLineFromFile(String lineToRemove, String filename)  {
        try  {
            BufferedReader br=new BufferedReader(new FileReader(Paths.get("setups", filename).toFile()));

            //String buffer to store contents of the file
            StringBuffer sb=new StringBuffer("");

            //Keep track of the line number
            String line;
            while((line=br.readLine())!=null)
            {
                //Store each valid line in the string buffer
                if (!line.equalsIgnoreCase(lineToRemove))  {
                    sb.append(line + System.lineSeparator());
                }
            }
            br.close();

            FileWriter fw= new FileWriter(Paths.get("setups", filename).toFile());
            //Write entire string buffer into the file
            fw.write(sb.toString());
            fw.close();

            return true;
        }
        catch (Exception e)  {
            e.printStackTrace();
            return false;
        }
    }
}
