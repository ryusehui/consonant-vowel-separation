import java.io.*;
import java.util.*;
 
public class naming2 {
    /* 16진수의 초성 중성 종성 선언 */
    public static final char[] init =
            /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
        { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142,
            0x3143, 0x3145, 0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b,
            0x314c, 0x314d, 0x314e };
    public static final char[] vowel =
            /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
        { 0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156,
            0x3157, 0x3158, 0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e,
            0x315f, 0x3160, 0x3161, 0x3162, 0x3163 };
    public static final char[] fin =
            /*Xㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
        { 0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137,
            0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140,
            0x3141, 0x3142, 0x3144, 0x3145, 0x3146, 0x3147, 0x3148, 0x314a,
            0x314b, 0x314c, 0x314d, 0x314e };
 
    /* 초성 중성 종성에 대응하는 영어표기 소문자로 선언 */
    public static final String[] initEng =
            /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
        { "g", "gg", "n", "d", "dd", "r", "m", "b", "bb", "s", "ss", "", "j", "jj", "c",
            "k", "t", "p", "h" };
    public static final String[] vowelEng =
            /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
        { "a", "ae", "ya", "yae", "eo", "e", "yeo", "ye", "o", "wa", "wae", "oe", "yo",
            "u", "weo", "we", "wi", "yu", "eu", "yi", "i" };
    public static final String[] finEng =
            /*Xㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
        { "", "g", "gg", "gs", "n", "nj", "nh", "d", "l", "lg", "lm", "lb", "ls", "lt", "lp",
            "lh", "m", "b", "bs", "s", "ss", "ng", "j", "c", "k", "t", "p", "h" };
    
    Map<String, String> hm = new HashMap<>();
    List <List<String>> l = new ArrayList<>();
    static String resultKor;
    static String resultEng;
    static String newPath;
    
    public static void main(String[] args) {
        /* 폴더명과 사진 파일명 변경 */
        naming2 search = new naming2();
        search.fileAllSearch("탐색을 시작할 경로");
    }
    
    /* 디렉토리 및 파일 재귀 탐색 */
    public void fileAllSearch(String filePath) {
        try {
            File dir = new File(filePath);
            File[] list = dir.listFiles();
            
            /* 리스트 요소 하나하나 읽으면서 디렉토리 및 파일 이름 바꿔주기 */
            for(File file : list) {
                String name = file.getName();        // 분리할 단어
                resultKor = "";                        // 분리 결과 저장할 변수
                resultEng = "";                        // 분리한 자음,모음 알파벳으로
                for (int i = 0; i < name.length(); i++) {
                    
                    /*  한글자씩 읽어들인다. */
                    char chars = (char) (name.charAt(i) - 0xAC00);
                    
                    /* 특문처리 */
                    if (chars >= 0 && chars <= 11172) {
                        /* A. 자음과 모음이 합쳐진 글자인경우 */
        
                        /* A-1. 초성 종성 중성 분리 */
                        int chosung = chars / (21 * 28);
                        int jungsung = chars % (21 * 28) / 28;
                        int jongsung = chars % (21 * 28) % 28;
        
                        
                        /* A-2. result에 담기 */
                        resultKor = resultKor + init[chosung] + vowel[jungsung];
                        
                        /* 자음분리 */
                        if (jongsung != 0x0000) {
                            /* A-3. 종성이 존재할경우 result에 담는다 */
                            resultKor =  resultKor + fin[jongsung];
                        }
        
                        /* 알파벳으로 */
                        resultEng = resultEng + initEng[chosung] + vowelEng[jungsung];
                        if (jongsung != 0x0000) {
                            /* A-3. 종성이 존재할경우 result에 담는다 */
                            resultEng =  resultEng + finEng[jongsung];
                        }
                    }
                    else
                        /* 특수문자, 숫자 등 그대로 출력 */
                        resultEng = resultEng + ((char)(chars + 0xAC00));
                } //for
                
                /* 기존 파일명의 대문자 --> 소문자 */
                resultEng = resultEng.toLowerCase();
                
            /* 특수문자 제거, 변경 */                
                /* 괄호는 _로 공백(\\p{Z})은 제거 */
                resultEng = resultEng.replaceAll("[\\[\\]\\(\\)]", "_").replaceAll("\\p{Z}", "");
                
                /* , . __를 _로 치환 */
                resultEng = resultEng.replace(",", "_").replace("-", "_");
                resultEng = resultEng.replace(".", "_");
                resultEng = resultEng.replace("__", "_");
                
                /* .을 _로 바꾸면서 확장자의 .도 _로 바뀜 --> _확장자를 .확장자로 치환 */
                resultEng = resultEng.replaceAll("_xlsx", ".xlsx");
                resultEng = resultEng.replaceAll("_png", ".png");
                resultEng = resultEng.replaceAll("_jpg", ".jpg");
                
                /* 전체 탐색 */
                if(file.isFile() &&
                    (file.getName().contains(".xlsx") || file.getName().contains(".png") || file.getName().contains(".jpg"))) {
                    
                    /* 00으로 시작하는 파일명에서 00제거 */
                    if(resultEng.charAt(0)=='0' && resultEng.charAt(1)=='0')
                        resultEng = resultEng.replaceFirst("00", "");
                    
                    /* _로 시작하는 파일명 _제거 */
                    if(resultEng.charAt(0)=='_')
                        resultEng = resultEng.replaceFirst("_", "");
                    
                    /* 숫자로 시작하는 파일명은 앞에 num을 붙여줌 */
                    if(!file.getName().contains(".xlsx") && '0'<=resultEng.charAt(0) && resultEng.charAt(0)<='9')
                        resultEng = "num" + resultEng;
                    
                    /* hashMap에 특수문자가 포함된 string 넣기위한 처리 */
                    name = name.replaceAll("[\\[\\]\\(\\)]", "[\\\\[\\\\]\\\\(\\\\)]");
                    hm.put(name, resultEng);
                    newPath = file.getCanonicalPath();
                    String[] fileArr = newPath.split("\\\\");
                    /* 분리한 문자열이 경로에 있으면 문자열에 해당하는 영문으로 바꿔주기 */
                    for(String s : fileArr) {
                        if(hm.get(s)!=null && newPath.contains(s))
                            newPath = newPath.replaceAll(s, hm.get(s));
                    }
                    newPath = newPath.replaceAll("temp", "translate").replaceAll(name, resultEng);
                    File newFile = new File(newPath);
                    file.renameTo(newFile);
                }
                
                else if(file.isDirectory()) {
                    hm.put(name, resultEng);
                    newPath = file.getCanonicalPath();
                    String[] folderArr = newPath.split("\\\\");
                    for(String s : folderArr) {
                        if(hm.get(s)!=null && newPath.contains(s))
                            newPath = newPath.replaceAll(s, hm.get(s));
                    }
                    newPath = newPath.replaceAll("temp", "translate").replaceAll(name, resultEng);
                    File newFile = new File(newPath);
                    newFile.mkdir();
                    /* 탐색할 폴더나 파일이 남아있으면 재귀 호출 */    
                    if(file.getCanonicalPath() != null && file.exists())
                        fileAllSearch(file.getCanonicalPath().toString());
                }
            } //for
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
