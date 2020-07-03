import java.io.*;
 
public class fileAllSearch {    
    public static void main(String[] args) {
        String oldPath = "탐색할 경로 입력";
        naming2 process = new naming2();
        process.fileAllSearch(oldPath);
    }
    
    public void fileAllSearch(String filePath) {
        try {
            File dir = new File(filePath);
            File[] list = dir.listFiles();
            System.out.println("now dir : " + filePath);
            
            for(File file : list) {
                if(file.isFile() && file.getName().contains("png"))
                    System.out.println(file.getName());
                else if(file.isDirectory()) {
                    if(file.getName().equals("png")) {
                        System.out.println(file.isDirectory() + " : " + file.getName());
                        System.out.println("절대경로 : " + file.getCanonicalPath());
                    }
                    if(file.getCanonicalPath() != null && file.exists())
                        fileAllSearch(file.getCanonicalPath().toString());
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
