package warehouse_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class WarehouseManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseManagementApplication.class, args);
	}

}