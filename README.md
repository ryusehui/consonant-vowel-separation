# consonant-vowel-separation

### 참고 소스
```
class translate {
    public static final char[] init =
            /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
        { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
            0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
            0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
    public static final char[] vowel =
            /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
        { 0x314f, 0x3150, 0x3151, 0x3152,
            0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a,
            0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
            0x3163 };
    public static final char[] fin =
            /*Xㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
        { 0x0000, 0x3131, 0x3132, 0x3133,
            0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
            0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
            0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
    
    public static final String[] initEng =
            /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
        { "G", "GG", "N", "D", "DD", "R", "M", "B", "BB", "S", "SS", "", "J", "JJ", "C",
            "K", "T", "P", "H" };
    public static final String[] vowelEng =
            /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
        { "A", "AE", "YA", "YAE", "EO", "E", "YEO", "YE", "O", "WA", "WAE", "OE", "YO",
            "U", "WEO", "WE", "WI", "YU", "EU", "YI", "I" };
    public static final String[] finEng =
            /*Xㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
        { "", "G", "GG", "GS", "N", "NJ", "NH", "D", "L", "LG", "LM", "LB", "LS", "LT", "LP",
            "LH", "M", "B", "BS", "S", "SS", "NG", "J", "C", "K", "T", "P", "H" };
 
    public static void main(String args[]) {
        String word = "1.안녕하세요[001]";        // 분리할 단어
        String result = "";                                    // 결과 저장할 변수
        String resultEng = "";                                    // 알파벳으로
        
        for (int i = 0; i < word.length(); i++) {
            
            /*  한글자씩 읽어들인다. */
            char chars = (char) (word.charAt(i) - 0xAC00);
 
            if (chars >= 0 && chars <= 11172) {
                /* A. 자음과 모음이 합쳐진 글자인경우 */
 
                /* A-1. 초/중/종성 분리 */
                int chosung = chars / (21 * 28);
                int jungsung = chars % (21 * 28) / 28;
                int jongsung = chars % (21 * 28) % 28;
 
                
                /* A-2. result에 담기 */
                result = result + init[chosung] + vowel[jungsung];
 
                
                /* 자음분리 */
                if (jongsung != 0x0000) {
                    /* A-3. 종성이 존재할경우 result에 담는다 */
                    result =  result + fin[jongsung];
                }
 
                /* 알파벳으로 */
                resultEng = resultEng + initEng[chosung] + vowelEng[jungsung];
                if (jongsung != 0x0000) {
                    /* A-3. 종성이 존재할경우 result에 담는다 */
                    resultEng =  resultEng + finEng[jongsung];
                }
 
            }
            else
                resultEng = resultEng + ((char)(chars + 0xAC00));
        }
 
        System.out.println("============ result ==========");
        System.out.println("단어       : " + word);
        System.out.println("자음분리 : " + result);
        System.out.println("알파벳    : " + resultEng);
    }
}
```
출처 [한싸이.tistory](https://hanpsy.tistory.com/2)
