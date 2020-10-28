import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AnalizorLexical {
    public static String[] KEYWORDS={"START",
            "CIAOCACAO",
            "number",
            "bool",
            "char",
            "array",
            "READ",
            "PRINT",
            "ELSE",
            "IF",
            "WHILE"};
    public static class IFipFields{
        public static String code;
        public static String value;
    }
    public static String[] OPERATORS={"+","-","*","/","=","<","<=",">",">=","!","&","|","&&","||","==","!="};
    public static String[] SEPARATORS={"(",")","[","]","{","}",":",";"," ","","\t","\n"};
    public static ArrayList<String> FIP=new ArrayList<>() ;
    public static HashMap<String, String>TS=new HashMap<>();
    public static String ALPHABET="abcdefghijklmnopqrstuvwxyz0123456789";

    public static void main(String[] args) {

        System.out.println("Analizator is running");
        File file = new File("C:\\Users\\crm0236\\Desktop\\AnalizatorLexical\\src\\program.txt");
        int t=0; //number of line
        int i=0;
        try {
            BufferedReader reader=new BufferedReader(new FileReader(file));
            FileWriter writer = new FileWriter("C:\\Users\\crm0236\\Desktop\\AnalizatorLexical\\src\\out.txt");
            boolean hasErrors=false;
            String line;
            ArrayList<String> atoms;
            while ((line = reader.readLine()) != null){
                t++;
                atoms=detecetAtom(line);
                for (i=0;i<atoms.size();i++) {
                    if (isKeyWord(atoms.get(i)) || isOperator(atoms.get(i)) || isSeparator(atoms.get(i))) {
                        addInFip(getCodeForFip(atoms.get(i))+"||"+atoms.get(i).toLowerCase());
                    }else{
                        boolean identifier=false;
                        boolean constant=false;
                        boolean pass=false;
                        try {
                            constant = isConstant(atoms.get(i));
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                            pass=true;
                        }
                        try{
                           identifier=isIdentificator(atoms.get(i));
                        } catch (Exception e){
                            if(!constant && !pass) {
                                System.out.println(e.getMessage());
                            }
                        }

                        if (identifier||constant){
                            if(!inTS(getCode(atoms.get(i)))){
                                addInTS(getCode(atoms.get(i)),atoms.get(i));
                            }
                            addInFip(getCodeForFip(atoms.get(i))+"||"+getCode(atoms.get(i)));
                        }else{
                            hasErrors=true;
                            System.out.println("error at line " + t + " at position " + i);
                        }
                    }
                }
            }

            //write in an output file
            if (!hasErrors){
                 for (i=0;i< FIP.size();i++){
                    System.out.println(FIP.get(i));
                    drawLine(20);
                    writer.write(FIP.get(i)+"\n");
                }
                 for (i=0;i<20;i++)
                     writer.write("-");
                 writer.write("\n");
                Iterator<String> iterator1=TS.keySet().iterator();
                while ( iterator1.hasNext()){
                    String key=iterator1.next();
                    System.out.println(key+"||"+TS.get(key));
                    drawLine(20);
                    writer.write(key+"||"+TS.get(key)+"\n");
                }
            }
            writer.close();

    } catch (FileNotFoundException e) {
            System.out.println("Source code doesn't exist or other errors");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("error at reading line");
            System.out.println(e.getMessage());
        }
    }

    public static void drawLine(int length){
        String el="-";
        System.out.println();
        for (int i=0;i<length;i++){
            System.out.print(el);
        }
        System.out.println();
    }

    public static ArrayList<String> detecetAtom(String line){
        String [] atoms=line.split("((?<=( |;|:|\\[|\\]|\\(|\\)|\n|\t|\\{|\\}|\"|\\+|\\-|\\*|\\/|\\=|\\<|\\>|\\<=|\\>=|\\==|\\!|\\&))|(?=( |;|:|\\[|\\]|\\(|\\)|\n|\t|\\{|\\}|\"|\\+|\\-|\\*|\\/|\\=|\\<|\\>|\\<=|\\>=|\\==|\\!|\\&)))");
        ArrayList<String> atomsList=new ArrayList<>();
        for (int i=0;i< atoms.length;i++){
            if (atoms[i].equals("\"")) {
                String string="";
                if (i+1<atoms.length){
                while (!atoms[i+1].equals("\"")) {
                        string += atoms[i+1];
                        if (i+2<atoms.length)
                            i++;
                        else break;
                }
                i++;
                atomsList.add("\""+string+"\"");
                }
            }else {
               atomsList.add(atoms[i]);
            }
        }

        return atomsList;
    }

    public static boolean isKeyWord(String atom){
        for (int i=0;i< KEYWORDS.length;i++){
            if(KEYWORDS[i].toLowerCase().equals(atom.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean isOperator(String atom){
        for (int i=0;i< OPERATORS.length;i++){
            if(OPERATORS[i].equals(atom))
                return true;
        }
        return false;
    }
    public static boolean isSeparator(String atom){
        for (int i=0;i< SEPARATORS.length;i++){
            if(SEPARATORS[i].toLowerCase().equals(atom.toLowerCase()))
                return true;
        }
        return false;
    }

    public static void addInFip(String value){
        FIP.add(value);
    }
    public static void addInTS(String key,String value){
        TS.put(key, value);
    }

    public static boolean isIdentificator(String atom) throws Exception {
        atom=atom.toLowerCase();
        if ((atom.equals("")||atom.equals(" "))) return false;

        if (Character.isDigit(atom.charAt(0))) throw new Exception("identificator can not start with digit");

        for (int i=1;i<atom.length();i++){
            if(!ALPHABET.contains(atom.substring(i,i+1))){
                throw new Exception("symbol "+atom.substring(i,i+1)+" is not accepted!");
            }
        }
        return true;
    }
    public static boolean isConstant(String atom) throws Exception {
        int points=0;
        if (atom.length()==0)return false;
        if ((atom.charAt(0)=='"')&&(atom.charAt(atom.length()-1)=='"')) return true;
        if (atom.length()==1) {
            if (!Character.isDigit(atom.charAt(0))) return false;
        }
        if ((atom.charAt(0)!='-')&&(!Character.isDigit(atom.charAt(0)))) return false;
        if ((atom.charAt(0)=='-')&&(atom.charAt(1)=='0')) throw new Exception("-0 is not accepted");
        for (int i=1;i<atom.length();i++){
            if  (atom.charAt(i)=='.') points++;
            if (points>1) throw new Exception("more than 1 point in constant");
            if ((!Character.isDigit(atom.charAt(i)))&&(atom.charAt(i)!='.'))return false;
        }
        return true;
    }

    public static boolean inTS(String key){
        return TS.containsKey(key);
    }
    public static String getInTsPos(String value){
        return TS.get(getCode(value));
    }

    public static String getCode(String atom){
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(atom.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        /*//hashing function
        String hash="";
        for (int i=0;i<atom.length();i++){
            hash+=i+atom.substring(i,i+1);
        }
        return hash;*/
    }

    public static String getCodeForFip(String atom){
        if(isKeyWord(atom)){
            for (int i=0;i<KEYWORDS.length;i++){
                if (atom.toLowerCase().equals(KEYWORDS[i].toLowerCase())){
                    return String.valueOf(i+2);
                }
            }
        }
        if(isOperator(atom)){
            for (int i=0;i<OPERATORS.length;i++){
                if (atom.toLowerCase().equals(OPERATORS[i].toLowerCase())){
                    return String.valueOf(KEYWORDS.length+i+2);
                }
            }
        }
        if(isSeparator(atom)){
            for (int i=0;i<SEPARATORS.length;i++){
                if (atom.toLowerCase().equals(SEPARATORS[i].toLowerCase())){
                    return String.valueOf(KEYWORDS.length+OPERATORS.length+i+2);
                }
            }
        }
        try {
            if (isConstant(atom)) return "1";
            if (isIdentificator(atom)) return "0";
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return "errorcode";
    }
}
