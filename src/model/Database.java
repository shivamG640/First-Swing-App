package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Database {
	private List<Person> people;
	private Connection con;
	
	public Database() {
		people = new LinkedList<Person>();
	}
	
	public void connect() throws Exception {
		
		if (con != null) return;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			throw new Exception("Driver not found");
		}
		
		String url = "jdbc:mysql://localhost:3306/swingdb";
		con = DriverManager.getConnection(url, "swinguser", "swingpassword");
		
		System.out.println("Connected " + con);
	}
	
	public void disconnect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("can't close connection");
			}
			
			System.out.println("Disconnected");
		}
	}
	
	public void save() throws SQLException {
		String checkSql = "select count(*) as count from person where id=?";
		PreparedStatement checkStmt = con.prepareStatement(checkSql);
		
		String insertSql = "insert into person (id, name, age, employment_status, tax_id, us_citizen, gender, occupation) values (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement insertStmt = con.prepareStatement(insertSql);
		
		String updateSql = "update person set name=?, age=?, employment_status=?, tax_id=?, us_citizen=?, gender=?, occupation=? where id=?";
		PreparedStatement updateStmt = con.prepareStatement(updateSql);
		
		for (Person person:people) {
			int id = person.getId();
			String name = person.getName();
			AgeCategory age = person.getAgeCategory();
			EmploymentCategory emp = person.getEmpCat();
			String tax = person.getTaxId();
			boolean isUs = person.isUsCitizen();
			Gender gender = person.getGender();
			String occupation = person.getOccupation();
			
			checkStmt.setInt(1, id);
			ResultSet checkResult = checkStmt.executeQuery();
			checkResult.next();
			
			int count = checkResult.getInt(1);
			
			if (count == 0) {
				System.out.println("Inserting person with ID: " + id);
				
				int col = 1;
				insertStmt.setInt(col++, id);
				insertStmt.setString(col++, name);
				insertStmt.setString(col++, age.name());
				insertStmt.setString(col++, emp.name());
				insertStmt.setString(col++, tax);
				insertStmt.setBoolean(col++, isUs);
				insertStmt.setString(col++, gender.name());
				insertStmt.setString(col++, occupation);
				
				insertStmt.executeUpdate();
			} else {
				System.out.println("Updating person with ID: " + id);
				
				int col = 1;
				updateStmt.setString(col++, name);
				updateStmt.setString(col++, age.name());
				updateStmt.setString(col++, emp.name());
				updateStmt.setString(col++, tax);
				updateStmt.setBoolean(col++, isUs);
				updateStmt.setString(col++, gender.name());
				updateStmt.setString(col++, occupation);
				updateStmt.setInt(col++, id);
				
				updateStmt.executeUpdate();
			}
		
		}
		
		updateStmt.close();
		insertStmt.close();
		checkStmt.close();
	}
	
	public void load() throws SQLException {
		people.clear();
		
		String sql = "select id, name, age, employment_status, tax_id, us_citizen, gender, occupation from person";
		Statement selectStmt = con.createStatement();
		
		ResultSet results = selectStmt.executeQuery(sql);
		
		while(results.next()) {
			int id = results.getInt("id");
			String name = results.getString("name");
			String age = results.getString("age");
			String emp = results.getString("employment_status");
			String tax = results.getString("tax_id");
			boolean isUs = results.getBoolean("us_citizen");
			String gender = results.getString("gender");
			String occ = results.getString("occupation");
			
			Person person = new Person(id, name, occ, AgeCategory.valueOf(age), EmploymentCategory.valueOf(emp), tax, isUs, Gender.valueOf(gender));
			people.add(person);
			
			System.out.println(person);
		}
		
		results.close();
		selectStmt.close();
	}
	
	public void addPerson(Person person) {
		people.add(person);
	}
	
	public List<Person> getPeople() {
		return Collections.unmodifiableList(people);
	}
	
	public void removePerson(int index) {
		people.remove(index);
	}
	
	public void saveToFile(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		
		Person[] persons = people.toArray(new Person[people.size()]);
		oos.writeObject(persons);
		
		oos.close();
	}
	
	public void loadFromFile(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		try {
			Person[] persons = (Person[])ois.readObject();
			people.clear();
			people.addAll(Arrays.asList(persons));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ois.close();
	}
}
