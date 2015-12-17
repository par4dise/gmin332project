package fr.um2.gmin332project.tests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import fr.um2.gmin332project.model.ISF;

/*
 * Classe permettant d'interagir avec MongoDB en Java (POJO ISF en particulier)
 * Requêtes paramétrées, ...
 * Non utilisée pour la manipulation de modèles RDF
 */
public class MongoTest {
	public static final boolean ORDER_ASC = true;
	public static final boolean ORDER_DESC = false;
	
	/* Base de données et collection */
	public static final String dbName = "gmin332project";
	public static final String collectionImpots = "impots";
	
	private MongoClient mongoClient;
	private DB db;

	private DB getMongoDb() throws UnknownHostException {
		mongoClient = new MongoClient("localhost");

		return mongoClient.getDB(dbName);
	}

	public void closeDb() {
		mongoClient.close();
	}
	
	public DBCollection getCollection() {
        if (db == null)
			try {
				db = getMongoDb();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
        
        if (db != null) 
        	return db.getCollection(collectionImpots);

        return null;
    }
	
	public void queryByCodeInsee(int code) {		  
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("code_insee", code);
		DBCursor cursor = getCollection().find(whereQuery);
		try {
			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				System.out.println("annee: " + obj.get("annee") + "\t" + 
						"code_insee: " + obj.get("code_insee") + "\t" + 
						"nbre_red: " + obj.get("nbre_redevables_isf") + "\t" + 
						"patr_moy: " + obj.get("patrimoine_moy") + "\t" + 
						"impot_moy: " + obj.get("impot_moy"));
		   }
		} finally {
		   cursor.close();
		}
	}
	
	public void queryMatchDisplay(String field, int value) {
		DBCursor cursor = getCollection().find(new BasicDBObject(field, value));
		try {
			System.out.println("Résultats requête : \"" + field + "\" est égal à " + value);
			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				System.out.println("annee: " + obj.get("annee") + "\t" + 
						"code_insee: " + obj.get("code_insee") + "\t" + 
						"nbre_red: " + obj.get("nbre_redevables_isf") + "\t" + 
						"patr_moy: " + obj.get("patrimoine_moy") + "\t" + 
						"impot_moy: " + obj.get("impot_moy"));
		   }
		} finally {
		   cursor.close();
		}
	}
	
	public List<ISF> queryMatch(String field, int value) {
		DBCursor cursor = getCollection().find(new BasicDBObject(field, value));
		List<ISF> impots = new ArrayList<ISF>();
		try {
			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				impots.add(new ISF(obj.get("code_insee").toString(),
						obj.get("nbre_redevables_isf").toString(),
						obj.get("patrimoine_moy").toString(),
						obj.get("impot_moy").toString(),
						obj.get("annee").toString()));
		   }
		} finally {
		   cursor.close();
		}
		return impots;
	}
	
	/* Requête sur la valeur d'un champ comprise entre <min> et <max> */
	public void queryByRangeDisplay(String field, int minValue, int maxValue, boolean sortAsc) {
		if (minValue > maxValue) {
			System.out.println("queryByRange : mauvais paramètres");
			return;
		}
		
		BasicDBObject rangeQuery = new BasicDBObject();
		rangeQuery.put(field, new BasicDBObject("$gt", minValue).append("$lt", maxValue));
		
		int iSort = (sortAsc) ? 1 : -1;
		DBCursor cursor = getCollection().find(rangeQuery).sort(new BasicDBObject(field, iSort));
		try {
			System.out.println("Résultats requête : \"" + field + "\" compris entre " + minValue + " et " + maxValue);
			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				System.out.println("annee: " + obj.get("annee") + "\t" + 
						"code_insee: " + obj.get("code_insee") + "\t" + 
						"nbre_red: " + obj.get("nbre_redevables_isf") + "\t" + 
						"patr_moy: " + obj.get("patrimoine_moy") + "\t" + 
						"impot_moy: " + obj.get("impot_moy"));
		   }
		} finally {
		   cursor.close();
		}
	}
	
	/* Requête sur la valeur d'un champ comprise entre <min> et <max> */
	public List<ISF> queryByRange(String field, int minValue, int maxValue, int year, String fieldSort, boolean sortAsc) {
		if (minValue > maxValue) {
			System.out.println("queryByRange : mauvais paramètres");
			return null;
		}
		
		BasicDBObject rangeQuery = new BasicDBObject();
		if (year != 0)
			rangeQuery.put("annee", year);
		rangeQuery.put(field, new BasicDBObject("$gt", minValue).append("$lt", maxValue));
		
		int iSort = (sortAsc) ? 1 : -1;
		DBCursor cursor = getCollection().find(rangeQuery).sort(new BasicDBObject(fieldSort, iSort));
		
		List<ISF> impots = new ArrayList<ISF>();
		try {
			DBObject obj;
			while (cursor.hasNext()) {
				obj = cursor.next();
				impots.add(new ISF(obj.get("code_insee").toString(),
						obj.get("nbre_redevables_isf").toString(),
						obj.get("patrimoine_moy").toString(),
						obj.get("impot_moy").toString(),
						obj.get("annee").toString()));
		   }
		} finally {
		   cursor.close();
		}
		return impots;
	}
	
	/* Listing de tout le contenu (format brut ou mis en forme) */
	public void listCollection(boolean raw) {
		DBCursor cursor = getCollection().find();
		try {
			if (raw)
				while (cursor.hasNext()) {
					System.out.println(cursor.next());
			   }
			else {
				DBObject obj;
				while (cursor.hasNext()) {
					obj = cursor.next();
			        System.out.println("annee :" + obj.get("annee") + "\t" + 
			        		"code_insee: " + obj.get("code_insee") + "\t" + 
			        		"nbre_red: " + obj.get("nbre_redevables_isf") + "\t" + 
			        		"patr_moy: " + obj.get("patrimoine_moy") + "\t" + 
			        		"impot_moy: " + obj.get("impot_moy"));
			   }
			}
		} finally {
		   cursor.close();
		}
	}
	
	/* Tests de requêtes */
	public static void main(String[] args) throws UnknownHostException {
		MongoTest mm = new MongoTest();
		try {
			mm.queryMatchDisplay("code_insee", 34172);
			System.out.println("======================");
			mm.queryByRangeDisplay("impot_moy", 4000, 4100, ORDER_ASC);
			System.out.println("======================");
			mm.queryByRangeDisplay("nbre_redevables_isf", 50, 55, ORDER_ASC);
			System.out.println("======================");
			mm.queryByRangeDisplay("patrimoine_moy", 1300000, 1310000, ORDER_DESC);
			//mm.listCollection(true);
		} finally {
			mm.closeDb();
		}
	}

}
