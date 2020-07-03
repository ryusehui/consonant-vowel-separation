package xml;
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
 
class Files {
    /* 초성 중성 종성에 사용되지 않는 알파벳 */
    public static final String[] notUsed = { "n", "xx", "xxx", "v", "z" };
    
    static Set<String> s = new HashSet<>();
    static Vector<String> v = new Vector<>();
    
    static String resultEng;
    static String folderName;
    static String fileName;
 
    final String xFile = "C:\\Users\\SH\\Desktop\\symbol.xml";
    File xmlFile = new File(xFile);
 
    createSymbolXml test = new createSymbolXml();
    
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
                    String exp = "", path=file.getCanonicalPath();
                    String name = file.getName(); // 분리할 단어
                    exp = name.substring(name.lastIndexOf("."));
                    name = name.substring(0, name.lastIndexOf("."));
                    fileName = name;
                    
                    result = translate2En(name);
                    
                    String resultChk = check(result);
                    if(!resultChk.equals("1")) result+=resultChk;
                    result = result.replaceAll("__", "_");
                    
                    createXML(path, fileName, result);
                }
 
                else if (file.isDirectory()) {
                    /* 탐색할 폴더나 파일이 남아있으면 계속 */
                    if (file.getCanonicalPath() != null && file.exists())
                        FileSearch4En(file.getCanonicalPath().toString());
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
    
    public void editXML() {
        /* DOM파서 생성하는 Factory클래스 */
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setIgnoringElementContentWhitespace(true);
        Document document;
        /* XSLT변환기 */
        TransformerFactory transFac = null;
        Transformer trans = null;
        
        try {
            /* DOM파서 객체의 클래스 */
            DocumentBuilder parser = fac.newDocumentBuilder();
            document = parser.parse(xFile);
            Element eRoot = document.getDocumentElement();
            
            NodeList list = eRoot.getElementsByTagName("url");
            for(int i=0; i<list.getLength(); i++) {
                Element eUrl = (Element) list.item(i);
                Text tUrl = (Text) eUrl.getFirstChild();
                if(v.contains(tUrl.getData())) {
                    int index = v.indexOf(tUrl.getData());
                    System.out.println("url: " + tUrl.getData());
                    tUrl.setData(v.get(index)+"1");
                    System.out.println("data: " + tUrl.getData());
                    
                    /* XSLT변환기 */
                    transFac = TransformerFactory.newInstance();
                    trans = transFac.newTransformer();
                    
                    trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                    //trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "symbols.dtd");
                    trans.setOutputProperty(OutputKeys.INDENT, "yes");
                    
                    DOMSource source = new DOMSource(document);
                    StreamResult result = new StreamResult(xFile);
                    
                    trans.transform(source, result);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createXML(String path, String name, String resultEng) {
        Element eSymbols, eSymbol, eName, eLat, eLng, ePoi, eUrl;
        Text tName, tPoi, tUrl;
        
        /* DOM파서 생성하는 Factory클래스 */
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setIgnoringElementContentWhitespace(true);
        Document document;
        /* XSLT변환기 */
        TransformerFactory transFac = null;
        Transformer trans = null;
        
        String[] pathArr = path.split("\\\\");
        for(String s : pathArr)
            if(test.hm.get(s)!=null && path.contains(s))
                folderName = test.hm.get(s);
        folderName = folderName.replaceAll("[1234567890]", "");
        folderName = folderName.replaceFirst(".", "");
        
        try {
            /* xml파일이 존재하지 않으면 새로 생성 */
            if(!xmlFile.exists()) {
                /* DOM파서 객체의 클래스 */
                DocumentBuilder parser = fac.newDocumentBuilder();
                document = parser.newDocument();
                
                eSymbols = document.createElement("symbols");
                eSymbol = document.createElement("symbol");
                eName = document.createElement("name");
                tName = document.createTextNode(name);
                eLat = document.createElement("lat");
                eLng = document.createElement("lng");
                ePoi = document.createElement("poi");
                tPoi = document.createTextNode(folderName);
                eUrl = document.createElement("url");
                tUrl = document.createTextNode(resultEng);
                
                eSymbol.appendChild(eName);
                eName.appendChild(tName);
                eSymbol.appendChild(eLat);
                eSymbol.appendChild(eLng);
                eSymbol.appendChild(ePoi);
                ePoi.appendChild(tPoi);
                eSymbol.appendChild(eUrl);
                eUrl.appendChild(tUrl);
                eSymbols.appendChild(eSymbol);
                document.appendChild(eSymbols);
                
                /* XSLT변환기 */
                transFac = TransformerFactory.newInstance();
                trans = transFac.newTransformer();
                
                trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//                trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "symbols.dtd");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(xFile);
                
                trans.transform(source, result);
            }
            
            /* xml파일이 있으면 element 추가 */
            else {
                /* DOM파서 객체의 클래스 */
                DocumentBuilder parser = fac.newDocumentBuilder();
                document = parser.parse(xFile);
                
                eSymbol = document.createElement("symbol");
                eName = document.createElement("name");
                tName = document.createTextNode(name);
                eLat = document.createElement("lat");
                eLng = document.createElement("lng");
                ePoi = document.createElement("poi");
                tPoi = document.createTextNode(folderName);
                eUrl = document.createElement("url");
                tUrl = document.createTextNode(resultEng);
                
                eSymbol.appendChild(eName);
                eName.appendChild(tName);
                eSymbol.appendChild(eLat);
                eSymbol.appendChild(eLng);
                eSymbol.appendChild(ePoi);
                ePoi.appendChild(tPoi);
                eSymbol.appendChild(eUrl);
                eUrl.appendChild(tUrl);
                
                Element eRoot = document.getDocumentElement();
                eRoot.appendChild(eSymbol);
                
                /* XSLT변환기 */
                transFac = TransformerFactory.newInstance();
                trans = transFac.newTransformer();
                
                trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                //trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "symbols.dtd");
                trans.setOutputProperty(OutputKeys.INDENT, "yes");
                
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(xFile);
                
                trans.transform(source, result);
            }
        } catch(Exception e) {
            System.out.println("Log : " + e);
        }
    }
}
 
public class createSymbolXml {
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
    
    public static final String[] findFolder =
        { "1.기본기능키", "2.음식", "3.학교", "4.집", "5.놀이.여가", "6.건강", "7.교통", "8.지역사회", "9.스케쥴",
            "10.종교", "11.스포츠", "12.색", "13.동물.식물", "14.인물", "15.사람", "16.국가", "17.미분류", "18.결혼,출산,육아",
            "19.대학생활", "20.복지,지원,주민센터", "21.장애피해상황", "22.직업", "23.휠체어,휴대폰,자동차정비,AS센터" };
 
    static Map<String, String> hm = new HashMap<>();
 
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
 
    public static void main(String[] args) throws Exception {        
        for(int i=0; i<findFolder.length; i++)
            hm.put(findFolder[i], findFolder[i]);
        
        Files fileNaming = new Files();
        
        /* 파일탐색 / 탐색하려는 경로 입력 */
        fileNaming.FileSearch4En("directoryPath");
        fileNaming.editXML();
        System.out.println("-끝-");
    }
}
