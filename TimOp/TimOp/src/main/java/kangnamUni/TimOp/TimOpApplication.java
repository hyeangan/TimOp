package kangnamUni.TimOp;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import kangnamUni.TimOp.Service.LectureService;
import kangnamUni.TimOp.domain.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import kangnamUni.TimOp.Service.WebScraper;

@SpringBootApplication
public class TimOpApplication {
	public static void main(String[] args) {
		//EntityManagerFactory emf = Persistence.createEntityManagerFactory(); //gradle에서  persistence-unit name을 지정하려면 LocalEntityManagerFactoryBean을 새로 등록해야함
		SpringApplication.run(TimOpApplication.class, args);

	}


}
