package naming;
import java.io.*;
import java.util.*;
 
class Files {
    /* 초성 중성 종성에 사용되지 않는 알파벳 */
    public static final String[] notUsed = { "n", "xx", "xxx", "v", "z" };
 
    Naming test = new Naming();
    static Set<String> s = new HashSet<>();
    static Vector<String> v = new Vector<>();
 
    /* 파일 탐색하면서 한영 변환 */
    public void FileSearch4En(String filePath) {
        String result = "";
        try {
            File dir = new File(filePath);
            File[] list = dir.listFiles();
 
            /* 파일리스트 요소 하나하나 읽기 */
            for (File file : list) {
                /* 전체 탐색 */
                if (file.isFile() && /* 엑셀, jpg, png파일명만 바꾸기 */
                        (file.getName().contains(".xlsx") || file.getName().contains(".png")
                                || file.getName().contains(".jpg"))) {
                    String exp = "";
                    String name = file.getName(); // 분리할 단어
                    exp = name.substring(name.lastIndexOf("."));
                    name = name.substring(0, name.lastIndexOf("."));
                    
                    result = translate2En(name);
                    
                    String resultChk = check(result);
                    if(!resultChk.equals("1")) result+=resultChk;
                    
                    result = result.replaceAll("__", "_");
                    /* 파일 저장 */
                    fileSave(file, result+exp);
                }
 
                else if (file.isDirectory()) {
                    /* 폴더명 바꾸기 */
                    if (file.getName().equals("png"))
                        result = "png";
                    else if (file.getName().equals("cs3"))
                        result = "cs3";
                    else if (file.getName().equals("i8"))
                        result = "i8";
                    else
                        result = translate2En(file.getName());
 
                    result = result.replaceAll("__", "_");
                    File newFile = fileSave(file, result);
 
                    /* 탐색할 폴더나 파일이 남아있으면 계속 */
                    if (newFile.getCanonicalPath() != null && newFile.exists())
                        FileSearch4En(newFile.getCanonicalPath().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /* 파일 탐색하면서 영한 변환 */
    public void FileSearch4Ko(String filePath) {
        String result = "";
        try {
            File dir = new File(filePath);
            File[] list = dir.listFiles();
 
            /* 파일리스트 요소 하나하나 읽기 */
            for (File file : list) {
                /* 전체 탐색 */
                if (file.isFile() && /* 엑셀, jpg, png파일명만 바꾸기 */
                        (file.getName().contains(".xlsx") || file.getName().contains(".png")
                                || file.getName().contains(".jpg"))) {
                    /* 파일 저장 */
                    result = test.En2Ko(file.getName());
 
                    test.HWP(result);
                }
 
                else if (file.isDirectory()) {
                    /* 폴더명 바꾸기 */
                    result = test.En2Ko(file.getName());
                    test.HWP(result);
                    
 
                    /* 탐색할 폴더나 파일이 남아있으면 계속 */
                    if (file.getCanonicalPath() != null && file.exists())
                        FileSearch4Ko(file.getCanonicalPath().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /* naming계획에 맞게 이름 변경 (아직 정해지지 않아서 원래 계획대로 둠) */
    public String translate2En(String fileName) {
        String result = "";
        
        /* 기존 파일명의 대문자 --> 소문자 */
        fileName = fileName.toLowerCase();
        
        int alpha = 0;
        String addZ = "";
        for(int i=0; i<fileName.length(); i++) {
            if(alpha==0 && Character.getType(fileName.charAt(i))==2) {
                addZ += "z" + fileName.charAt(i); alpha++;
            }
            else if(alpha!=0 && (Character.getType(fileName.charAt(i))!=2) || i==fileName.length()) {
                addZ += "z" + fileName.charAt(i); alpha--;
            }
            else addZ += fileName.charAt(i);
            if(i==fileName.length()-1 && Character.getType(addZ.charAt(addZ.length()-1))==2) {addZ+="z"; break;}
        }
        fileName = addZ;
        
        result = test.Ko2En(fileName); // 한영 변환
 
        /* Naming계획에 맞게 바꾸기 */
        /* 괄호는 _로 공백(\\p{Z})은 제거 */
        result = result.replaceAll("[\\[\\]\\(\\)]", "_").replaceAll("\\p{Z}", "");
 
        /* , . -를 _로 */
        result = result.replace(",", "_").replace("-", "_").replace(".", "_");
        result = result.replaceAll("___", "_").replaceAll("__", "_");
 
        /* '00' -> 'xx' */
        if (result.matches(".*[\\D](00)[\\D].*") || result.matches("(00)[\\D].*")) {
            for (int i = 0; i < result.length(); i++) {
                if (result.charAt(i) == '0' && result.charAt(i + 1) == '0') {
                    if (Character.isDigit(result.charAt(i + 2)))
                        break;
                    else {
                        String temp = result.substring(i + 2);
                        result = result.substring(0, i);
                        result = result + notUsed[1] + temp;
                    }
                    i++;
                }
            }
        }
        if (result.matches(".*[\\D](000)[\\D].*"))
            result = result.replaceAll("000", notUsed[2]);
 
        /* _로 시작하는 파일명 앞에 'v' 붙임 */
        else if (result.charAt(0) == '_')
            result = notUsed[3] + result;
 
        /* 숫자로 시작하는 파일명은 앞에 'n' 붙임 */
        else if ('0' <= result.charAt(0) && result.charAt(0) <= '9')
            result = notUsed[0] + result;
 
        return result;
    }
    
    public String check(String name) {
        String num="1";
        
        if(s.contains(name)) {
            v.add(name);
            if(s.contains(name+"2")) {
                if(s.contains(name+"3")) {
                    num="4"; s.add(name+num);
                }
                else {num="3"; s.add(name+num);}
            }
            else {num="2"; s.add(name+num);}
        }
        else s.add(name);
        
        return num;
    }
    
    public void add1(String filePath) {
        try {
            File dir = new File(filePath);
            File[] list = dir.listFiles();
        
            /* 파일리스트 요소 하나하나 읽기 */
            for (File file : list) {
                /* 전체 탐색 */
                if (file.isFile() && /* 엑셀, jpg, png파일명만 바꾸기 */
                        (file.getName().contains(".png") || file.getName().contains(".jpg"))) {
                    String exp = "";
                    String name = file.getName(); // 분리할 단어
                    exp = name.substring(name.lastIndexOf("."));
                    name = name.substring(0, name.lastIndexOf("."));
                    
                    if(v.contains(name)) {
                        fileSave(file, name+"1"+exp);
                    }
                }
                
                else if (file.isDirectory()) {
                    /* 탐색할 폴더나 파일이 남아있으면 계속 */
                    if (file.getCanonicalPath() != null && file.exists())
                        add1(file.getCanonicalPath().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    /* 한글 폴더,파일명을 영어 폴더,파일명으로 변경 */
    public File fileSave(File file, String result) {
        File newFile = new File(file.getParent() + "\\" + result);
        file.renameTo(newFile);
 
        return newFile;
    }
}
 
public class Naming {
    /* 초성 중성 종성에 대응하는 영어표기 소문자로 선언 */
    public static final String[] initEng =
            /* ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
            { "g", "gg", "n", "d", "dd", "r", "m", "b", "bb", "s", "ss", "", "j", "jj", "c", "k", "t", "p", "h" };
 
    public static final String[] vowelEng =
            /* ㅏ ㅐ ㅑ ㅒ ㅓ ㅔ ㅕ ㅖ ㅗ ㅘ ㅙ ㅚ ㅛ ㅜ ㅝ ㅞ ㅟ ㅠ ㅡ ㅢ ㅣ */
            { "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo", "u", "weo", "we", "wi",
                    "yu", "eu", "yi", "i" };
 
    public static final String[] finEng =
            /* X ㄱ ㄲ ㄳ ㄴ ㄵ ㄶ ㄷ ㄹ ㄺ ㄻ ㄼ ㄽ ㄾ ㄿ ㅀ ㅁ ㅂ ㅄ ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ */
            { "", "g", "gg", "gs", "n", "nj", "nh", "d", "l", "lg", "lm", "lb", "ls", "lt", "lp", "lh", "m", "b", "bs",
                    "s", "ss", "ng", "j", "c", "k", "t", "p", "h" };
 
    static HashMap<String, Integer> initE = new HashMap<>();
    static HashMap<String, Integer> vowelE = new HashMap<>();
    static HashMap<String, Integer> finE = new HashMap<>();
 
    /* 한영 변환 */
    public String Ko2En(String typo) {
        String resultEng = "";
        int findZ=0;
 
        for (int i = 0; i < typo.length(); i++) {
 
            /* 한글자씩 읽어들인다 */
            char word = (char) (typo.charAt(i) - 0xAC00);
 
            /* 한글 분리 후 영어로 변환 */
            if (0 <= word && word <= 11172) {
                /* 자음과 모음이 합쳐진 글자인경우 */
 
                /* 초성 종성 중성 분리 */
                char chosung = (char) ((word / (21 * 28)) + 0x1100);
                char jungsung = (char) ((word % (21 * 28) / 28) + 0x1161);
                char jongsung = (char) ((word % (21 * 28) % 28) + 0x11A7);
 
                /* 영어로 */
                resultEng = resultEng + initEng[chosung - 0x1100] + vowelEng[jungsung - 0x1161];
                if (jongsung != 0x0000) {
                    /* A-3. 종성이 존재할경우 result에 담는다 */
                    resultEng = resultEng + finEng[jongsung - 0x11A7];
                }
            }
            else {
                if(findZ==0 && (char)(word+0xAC00)=='z') {
                    resultEng+="z"; findZ++;
                    continue;
                }
                else if(findZ==1 && (char)(word+0xAC00)=='z') {
                    resultEng+="z_"; findZ--;
                    continue;
                }
                if(Character.getType((char)(word+0xAC00))==2) {
                    resultEng = resultEng + ((char) (word + 0xAC00));
                    continue;
                }
                else {
                    resultEng = resultEng + ((char) (word + 0xAC00));
                    if(Character.isDigit(resultEng.charAt(resultEng.length()-1))
                            && (i+1!=typo.length() && Character.isDigit(typo.charAt(i+1)))) {
                        continue;
                    }
                }
            }
            if (resultEng.charAt(resultEng.length() - 1) == '_');
            else resultEng += "_";
        } // for
 
        return resultEng;
    }
 
    /* 영한 변환 */
    public String En2Ko(String typo) {
        String resultKor = "";
 
        /* xx, xxx -> 00, 000 & v_ -> _ */
        typo = typo.replaceAll("xx", "00").replaceAll("xxx", "000");
 
        /* 맨앞에 있는 숫자 앞에 붙은 n 제거 */
        int num = 0, cho, jung, jong, idxCho, idxJung;
        if (typo.charAt(0) == 'n' && Character.isDigit(typo.charAt(1)))
            typo = typo.replaceFirst("n", "");        
 
        if (typo.charAt(0) == 'v') {
            resultKor += "_";
            typo = typo.replace("v_", "");
        }
        String words[] = typo.split("_");
        if (words.length == 1)
            return resultKor = typo;
 
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            
            if(Character.isDigit(word.charAt(0))) {resultKor+=word; continue;}
            
            try {
                if(word.charAt(0)=='z') {
                    word = word.replace("z", "");
                    resultKor+=word;
                    continue;
                }
 
                if (Character.isDigit(word.charAt(0))) {
                    for (num = 0; num < word.length(); num++) {
                        if (!Character.isDigit(word.charAt(num)))
                            break;
                        resultKor += word.charAt(num);
                    }
                } else
                    num = 0;
                if (num == word.length())
                    continue;
 
                for (idxCho = num; idxCho < words[i].length(); idxCho++) {
                    if (word.charAt(idxCho) == 'a' || word.charAt(idxCho) == 'e' || word.charAt(idxCho) == 'i'
                            || word.charAt(idxCho) == 'o' || word.charAt(idxCho) == 'u' || word.charAt(idxCho) == 'w'
                            || word.charAt(idxCho) == 'y')
                        break;
                }
                cho = initE.get(word.substring(num, idxCho));
 
                for (idxJung = idxCho; idxJung < word.length(); idxJung++) {
                    if (word.charAt(idxJung) != 'a' && word.charAt(idxJung) != 'e' && word.charAt(idxJung) != 'i'
                            && word.charAt(idxJung) != 'o' && word.charAt(idxJung) != 'u' && word.charAt(idxJung) != 'w'
                            && word.charAt(idxJung) != 'y')
                        break;
                }
                jung = vowelE.get(word.substring(idxCho, idxJung));
                
                jong = finE.get(word.substring(idxJung)); 
 
                resultKor = resultKor + (char) (0xAC00 + (28 * 21 * cho) + (28 * jung) + jong);
                
            } catch (Exception e) {
                resultKor += word;
            }
        }
        
        return resultKor;
    }
 
    /* 영->한으로 변경한 것 따로 파일로 정리 */
    public void HWP(String resultKor) throws IOException {
 
        if (resultKor.equals("png") || resultKor.equals("cs3") || resultKor.equals("i8"))
            return;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream("textFile", true));
            bos.write(resultKor.getBytes());
            bos.write(System.getProperty("line.separator").getBytes());
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            bos.close();
        }
 
        return;
    }
 
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < initEng.length; i++)
            initE.put(initEng[i], i);
 
        for (int i = 0; i < vowelEng.length; i++)
            vowelE.put(vowelEng[i], i);
 
        for (int i = 0; i < finEng.length; i++)
            finE.put(finEng[i], i);
        
        Files fileNaming = new Files();
        
        /* 파일탐색 / 탐색하려는 경로 입력 */
        fileNaming.FileSearch4En("filePath");
        fileNaming.add1("filePath");
//        fileNaming.FileSearch4Ko("filePath");
        
        System.out.println("- 끝 -");
    }
}
