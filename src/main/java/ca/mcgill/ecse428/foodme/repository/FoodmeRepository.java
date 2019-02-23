package ca.mcgill.ecse428.foodme.repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ca.mcgill.ecse428.foodme.model.*;

import ca.mcgill.ecse428.foodme.security.Password;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;


@SuppressWarnings("Duplicates")
@Repository
public class FoodmeRepository {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public AppUser testCreateUser(String username, String firstName, String lastName, String email, String password)
	{
		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);
		u.setPassword(password);
		u.setLikes(new ArrayList<String>());
		u.setDislikes(new ArrayList<String>());
		entityManager.persist(u);
		return u;
	}

	/**
	 * Method to create an new account
	 * @param username The user's chosen username
	 * @param firstName The user's first name
	 * @param lastName The user's last name
	 * @param email The user's email address
	 * @param password The user's password
	 * @return User entity that was created
	 */
	@Transactional
	public AppUser createAccount (String username, String firstName, String lastName, String email, String password) throws InvalidInputException {

		AppUser u = new AppUser();
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName(lastName);
		u.setEmail(email);

		String passwordHash;
		try {
			passwordHash = Password.getSaltedHash(password);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid password.");
		}
		u.setPassword(passwordHash);
		u.setLikes(new ArrayList<String>());
		u.setDislikes(new ArrayList<String>());
		entityManager.persist(u);

		return u;
	}

	@Transactional

	public Preference createPreference(AppUser user, String priceRange, String distanceRange, String cuisine, String rating){
		Preference preference = new Preference();
		preference.setPrice(PriceRange.valueOf(priceRange));
		preference.setDistance(DistanceRange.valueOf(distanceRange));
		preference.setCuisine(Cuisine.valueOf(cuisine));
		preference.setRating(Rating.valueOf(rating));
		preference.setUser(user);
		entityManager.persist(preference);
		user.addPreference(preference);
		entityManager.merge(user);
		return preference;
	}

	@Transactional
	public Preference editPreference(Preference editPreference, String priceRange, String distanceRange,
									 String cuisine, String rating) {
		editPreference.setPrice(PriceRange.valueOf(priceRange));
		editPreference.setDistance(DistanceRange.valueOf(distanceRange));
		editPreference.setCuisine(Cuisine.valueOf(cuisine));
		editPreference.setRating(Rating.valueOf(rating));
		entityManager.merge(editPreference);
		return editPreference;
	}

	@Transactional
	public AppUser getAppUser(String username){

		if(entityManager.find(AppUser.class, username) == null) {
			System.out.println("Cannot delete a user that does not exist");
		}
		else {
		AppUser appUser = entityManager.find(AppUser.class, username);
		return appUser;
		}
		return null;
	}

	/**
	 * gets all users in database using native SQL query statements
	 * @return list of AppUsers
	 */
	@Transactional
	public List<AppUser> getAllUsers()
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM app_user");
		@SuppressWarnings("unchecked")
		List<AppUser> users = q.getResultList();
		return users;
	}


	@Transactional
	public Preference getPreference(int pID){
		Preference preference = entityManager.find(Preference.class, pID);
		return preference;
	}
	
	/**
	 * Method to like a restaurant so its in the user list of liked restaurant
	 * @param restaurant The restaurant a user likes
	 * @return void The method returns nothing, this change will be saved in the database
	 */
	@Transactional
	public void isLiked(String username, String restaurant) {
		AppUser appUser = entityManager.find(AppUser.class, username);
		appUser.addLike(restaurant);
	}
	
	/**
	 * Method to list all the liked restaurants of a user
	 * @return The list of all the liked restaurants
	 */
	public List<String> listAllLiked(String username) {
		AppUser appUser = entityManager.find(AppUser.class, username);
		
		//TODO change the query to what is in the db
//		Query q = entityManager.createNativeQuery("SELECT liked FROM restaurants");
//		@SuppressWarnings("unchecked")
//		List<String> liked = q.getResultList();
		
		//return liked;
		return appUser.getLikes();
	}
	

	/**
	 * Method that allows users to update their account's password
	 * @param username
	 * @param newPassword
	 */


	@Transactional
	public AppUser changePassword(String username, String newPassword) {
		AppUser u = entityManager.find(AppUser.class, username);
		u.setPassword(newPassword);
		entityManager.merge(u);
		return u;
	}

	/**
	 * Method that allows users to delete their account
	 * @param username
	 */
	@Transactional
	public void deleteUser(String username)  {

		if(entityManager.find(AppUser.class, username) == null) {
			System.out.println("Cannot delete a user that does not exist");
		}
		else {
		AppUser u = entityManager.find(AppUser.class, username);
		entityManager.remove(u);
		//entityManager.detach(u);
		}
	}

	/**
	 * gets all preferences in database regardless of user
	 * @return
	 */
	@Transactional
	public List<Preference> getAllPreferences() 
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM preference");
		@SuppressWarnings("unchecked")
		List<Preference> preferences = q.getResultList();
		return preferences;
	}

	/**
	 * getting the paramaters for a specific user
	 * @param username
	 * @return list of parameters
	 */
	@Transactional
	public List<Preference> getPreferencesForUser(String username) 
	{
		Query q = entityManager.createNativeQuery("SELECT * FROM preference WHERE app_user= :user");
		q.setParameter("user", username);
		@SuppressWarnings("unchecked")
		List<Preference> preferences = q.getResultList();
		return preferences;
	}

	/**
	 * Method that checks to see if a restaurant is open at the current time 
	 * @param aRestaurant
	 * @return
	 */
	// public boolean isRestaurantOpen(Restaurant aRestaurant) {

	// 	Date date = new Date();
	// 	Date time = new Date();

	// 	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	// 	Date dateInput = sdf.parse(departureDate);
	// 	String date1 = sdf.format(dateInput);        
	// 	String date2 = sdf.format(date);

	// 	SimpleDateFormat sdf2 = new SimpleDateFormat("HHmm");
	// 	Date timeInput = sdf2.parse(departureTime);
	// 	String time1 = sdf2.format(timeInput);
	// 	String time2 = sdf2.format(time);
		
	// 	return true;
	// }

}
