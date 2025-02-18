package kangnamUni.TimOp.Service;
import kangnamUni.TimOp.domain.Lecture;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openqa.selenium.chrome.ChromeDriver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

//강남대학교 강의 스크래핑
//전공, 학년을 선택하고 조회해서 (개설 과목별 학수번호 분반 과목명 담당교수 학점 시수 강의시간) + 강의 계획서(HTML) 자동 수집

//@Service
@Slf4j
public class WebScraper {
    String url = "https://app.kangnam.ac.kr/knumis/sbr/sbr1010.jsp";

    private WebDriver driver;
    private String parentHandle;
    private final LectureService lectureService;
    int currentYear = 2024;
    String currentSemester = "1학기";
    @Autowired
    public WebScraper(LectureService lectureService) throws InterruptedException{
        this.lectureService = lectureService;
        setupDriver();
        /*
        나중에 1프레임에 다음년도 다음학기 조회 눌러서 이상없으면 scrape(다음년도 학기) 실행 -> 현재 년도 현재 학기 변수로 관리하고(이상없어서 스크랩 했으면 학기변경)
         */
        scrape(currentYear, currentSemester);
    }
    int grade = 0; //학년 데이터 수집을 위한 변수
    String major = null;
    String liberalArts = null;
    public void scrape(int year, String semester) throws InterruptedException{
        // 년도 학기 설정하는 로직 추가

        driver.switchTo().frame(1);
        driver.findElement(By.id("tab2")).click(); //학부/전공 탭 클릭
        Select dropdown = new Select(driver.findElement(By.name("dept_code1"))); //Select -> HTML select태그를 다루기위한 클래스
        //학부 전공에 옵션을 가져와서 텍스트를 추출한 후 추출한 텍스트로 도롭다운 메뉴 컨트롤
        List<WebElement> select_options = dropdown.getOptions().subList(1, dropdown.getOptions().size()); //학부/전공
        List<WebElement> radioButtons = driver.findElements(By.name("grad_area1"));
        List<WebElement> slicedGrades = radioButtons.subList(0, 4); //1~4학년 라디오 버튼
        List<WebElement> slicedAreas = radioButtons.subList(4, radioButtons.size()); //균형/자선 1영역~자선 일반 까지 라디오 버튼
        WebElement inquiryButton = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[8]/table/tbody/tr[1]/td[6]/div/div[2]")); //조회버튼

        for(WebElement select_option : select_options){
            grade = 0;
            navigateFrame(1);
            major = select_option.getText();
            log.info("학부/전공 = {}", major);
            dropdown.selectByVisibleText(select_option.getText());
            for (WebElement slicedGrade : slicedGrades) {
                grade += 1;
                log.info("학년 = {}", grade);
                navigateFrame(1);
                slicedGrade.click();
                inquiryButton.click();
                navigateFrame(2);

                //데이터 저장 로직
                if (saveLectureInfo(year, semester)) continue;
                //Thread.sleep(10000);
            }
        }

        grade = 0;
        major = null;

        HashMap<String, String> map = new HashMap<String, String>(); //크롤링에서 value 값과 자연어를 연결하기 위해서
        map.put("G31", "1영역");
        map.put("G32", "2영역");
        map.put("G333", "3영역");
        map.put("G344", "4영역");
        map.put("G355", "5영역");
        map.put("G9", "자선");
        map.put("G19", "일반");

        // 균형/자선 1~5영역, 자선, 일반
        for (WebElement slicedArea : slicedAreas) {
            navigateFrame(1);
            liberalArts = map.get(slicedArea.getAttribute("value")); //value 값을 가져와서 map에서 대응시킴
            slicedArea.click(); //1~5영역 선택
            inquiryButton.click(); //조회
            navigateFrame(2);
            // 데이터 저장
            saveLectureInfo(year, semester);
        }
    }

    //강의 정보 저장
    private boolean saveLectureInfo(int year, String semester) throws InterruptedException {
        try {
            String classCounts = driver.findElement(By.name("data_rowcount")).getAttribute("value"); //개설교과목 수
            int classCount = Integer.parseInt(classCounts);
            //클래스 수만큼 돌면서 정보 가져오기
            for (int i = 1; i < classCount + 1; i++) {
                String row = "row" + Integer.toString(i);
                log.info(driver.findElement(By.id(row)).getText());
                String lectureInfos = driver.findElement(By.id(row)).getText();
                List<WebElement> td = driver.findElement(By.id(row)).findElements(By.tagName("td"));
                List<String> lectureInfo = new ArrayList<>();
                for (WebElement webElement : td) {
                    lectureInfo.add(webElement.getText());
                }
                log.info("새로운 lectureInfo = {}", lectureInfo);
                //String[] lectureInfo = lectureInfos.split(" "); //학수번호 분반 과목명 담당교수 학점 시수 강의시간
                //현재창 저장 후 강의 계획서 팝업 열었다 닫기
                String syllabusName = saveSyllabus(row, lectureInfo);//해당 열에 있는 강의계획서 다운
                log.info("강의 정보 저장 = {}", lectureInfos); // 강의 정보 저장 = ND01603 11 컴퓨터프로그래밍 심정연 3 3 (주)목1ab2ab3ab
                //강의 정보 저장 = ND01612 09 Academic English R&W(Intro) KIM HYUN JUNG 2 2 (주)화7ab8ab 해결 해야함 + 학부/학년, 전공 정보 추가
                Lecture lecture = new Lecture();
                lecture.setTitle(lectureInfo.get(2));
                lecture.setNum(lectureInfo.get(0));
                lecture.setCredit(Integer.parseInt(lectureInfo.get(4)));
                lecture.setTime(lectureInfo.get(6));
                lecture.setProfessor(lectureInfo.get(3));
                lecture.setDivision_class(lectureInfo.get(1));
                lecture.setProgress_time(lectureInfo.get(5));
                lecture.setSyllabus(syllabusName);
                lecture.setMajor(major);
                lecture.setGrade(grade);
                lecture.setLiberalArts(liberalArts);
                lecture.setYear(year);
                lecture.setSemester(semester);
                lectureService.lectureToDatabase(lecture);

                driver.switchTo().window(parentHandle);
                navigateFrame(2);
            }
        } catch (NoSuchElementException e) {
            return true;
        }
        return false;
    }
    //강의계획서 저장
    //강의계획서 조회 버튼 클릭 후 강의계획서 html을 저장하고 원래 탭으로 돌아오기
    private String saveSyllabus(String row, List<String> lectureInfo) throws InterruptedException {
        String syllabusName = "";
        driver.findElement(By.id(row)).findElement(By.className("btn1_center")).click(); //강의계획서 버튼 클릭
        Set<String> windowHandles = driver.getWindowHandles(); //브라주저 각 탭에 대한 고유 식별자 리턴
        for (String windowHandle : windowHandles) {
            if(parentHandle.equals(windowHandle)){
                continue;
            }
            driver.switchTo().window(windowHandle); //강의계획서 창으로 이동
            //Thread.sleep(500);
            HtmlFileCreator htmlFileCreator = new HtmlFileCreator();
            syllabusName = "syllabus-" + lectureInfo.get(0).toLowerCase() + "-" + lectureInfo.get(1); //학수번호 + 분반
            htmlFileCreator.writeHtml(driver.getPageSource(), syllabusName);
            driver.close(); //강의계획서 창 닫기
        }
        return syllabusName;
    }

    //여러 프레임으로 되어있는 페이지 프레임 이동
    private void navigateFrame(int index) {
        driver.switchTo().defaultContent(); //가장 상위 프레임으로 이동
        driver.switchTo().frame(index); //프레임 번호로 이동
    }

    private void setupDriver() throws InterruptedException{
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); //윈도우 창 크기 최대
        //options.addArguments("--headless=new"); // 화면 없이
        this.driver = new ChromeDriver(options);
        this.driver.get(url); //웹 페이지 읽기
        //Thread.sleep(5000);
        parentHandle = driver.getWindowHandle(); //처음 화면 식별자 저장
    }

    public void close(){
        if(driver != null){
            driver.quit();
        }
    }
}

