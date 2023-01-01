package pw.pureboyz.questiondiary.util;

public class GlobalVariables
{
//    private String urlApi = "http://pureboyz.pw:38180";
    private String urlApi = "http://pureboyz.pw:8080";
    public String getUrlApi() { return this.urlApi; }

    private static GlobalVariables instance = null;

    public static synchronized GlobalVariables getInstance(){
        if(null == instance){
            instance = new GlobalVariables();
        }
        return instance;
    }
}
