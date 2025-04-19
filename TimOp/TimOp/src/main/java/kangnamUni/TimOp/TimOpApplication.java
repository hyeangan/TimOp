package kangnamUni.TimOp;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimOpApplication {
	public static void main(String[] args) {
		//EntityManagerFactory emf = Persistence.createEntityManagerFactory(); //gradle에서  persistence-unit name을 지정하려면 LocalEntityManagerFactoryBean을 새로 등록해야함
		SpringApplication.run(TimOpApplication.class, args);

	}


}
