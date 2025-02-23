package kangnamUni.TimOp.Service;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class HtmlFileCreator {
    public HtmlFileCreator(){
    }

    public void writeHtml(String htmlContent, String fileName) {
        try{
            //FileWriter fileWriter = new FileWriter("C:\\works\\intelliJ_project\\TimOp\\TimOp\\src\\main\\resources\\static/" + fileName + ".html");
            FileWriter fileWriter = new FileWriter("C:\\Users\\guddk\\OneDrive\\문서\\GitHub\\TimOp\\TimOp\\TimOp\\src\\main\\resources\\static/" + fileName + ".html");

            fileWriter.write(htmlContent);
            log.info("{} 강의계획서 저장", fileName);
            fileWriter.close();
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }
}
