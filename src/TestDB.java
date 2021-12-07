import java.sql.SQLException;

import model.AgeCategory;
import model.Database;
import model.EmploymentCategory;
import model.Gender;
import model.Person;

public class TestDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Running DB");
		Database db = new Database();
		try {
			db.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.addPerson(new Person("Shivam Gupta", "SE", AgeCategory.adult, EmploymentCategory.employed, null, false, Gender.male));
		db.addPerson(new Person("Heiko Braun", "SM", AgeCategory.senior, EmploymentCategory.selfEmployed, "777", true, Gender.male));
		
		try {
			db.save();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			db.load();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		db.disconnect();
	}

}
