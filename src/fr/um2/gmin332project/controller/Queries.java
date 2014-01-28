package fr.um2.gmin332project.controller;

import com.hp.hpl.jena.vocabulary.RDF;

/*
 * Requêtes
 */
public class Queries {
    public static final String NL = System.getProperty("line.separator");
    
    // Nb de triples dans D2RQ
    public static String Qall = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"SELECT (COUNT(*) as ?triples) WHERE {" + NL +
    		"  ?s ?p ?o } LIMIT 100";
    
    // Test accès D2RQ : OK
    public static String Qd2rq = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"SELECT * WHERE {" + NL +
    		"  ?s rdf:type d2rq:Commune ." + NL +
    		"  ?s d2rq:nom_commune ?nom ." + NL +
    		"  ?s d2rq:code_departement ?dep ." + NL +
    		"  FILTER ((?dep > \"33\") && (?dep < \"35\")) } LIMIT 100";
    
    // Test accès Neo4J : OK
    public static String Qneo = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL +    
    		"SELECT * WHERE {" + NL +
    		"  ?s rdf:type neo:departement ." + NL +
    		"  ?s neo:name ?neo_nom_dep ." + NL +
    		"  ?s neo:pop ?y }";
    
    // Test accès TDB : OK
    public static String Qtdb = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +  
    		"SELECT * WHERE {" + NL +
    		"  ?s rdf:type tdb:Departement ." + NL +
    		"  ?s tdb:latitude ?lat ." + NL +
    		"  ?s tdb:longitude ?long }";
    
    // Test inférence entre classes Département
    public static String Qinference = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +     
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +  
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL +    
    		"SELECT * WHERE {" + NL +
    		"  ?s rdf:type d2rq:Departement ." + NL +
    		"  OPTIONAL { ?s tdb:latitude ?lat }" + NL +
    		"  OPTIONAL { ?s neo:pop ?pop } }";

    // Test interconnexion Neo4J <--> D2RQ : OK
    public static String Qd2rqneo = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX mongo: <" + Config.mongoPrefix + ">" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL +    
    		"SELECT * WHERE {" + NL +
    		"  ?s rdf:type d2rq:Departement ." + NL +
    		"  OPTIONAL { ?s d2rq:code_departement ?code_dep } ." + NL +
    		"  ?s neo:name ?neo_nom_dep } LIMIT 100";  
    
    // Test interconnexion TDB <--> D2RQ : OK
    public static String Qd2rqtdb = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +   
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +  
    		"SELECT ?nom_dept ?latitude ?longitude WHERE {" + NL +
    		"  ?s rdf:type d2rq:Departement ." + NL +
    		"  OPTIONAL { ?s tdb:latitude ?latitude } ." + NL +
    		"  OPTIONAL { ?s tdb:longitude ?longitude } ." + NL +
    		"  ?s tdb:nom ?nom_dept }";  
    
    // Test interconnexion Neo4J <--> D2RQ <--> TDB : 
    public static String Qd2rqneotdb = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX mongo: <" + Config.mongoPrefix + ">" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL + 
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +       
    		"SELECT ?nom_dept ?latitude ?longitude WHERE {" + NL +
    		"  ?s rdf:type d2rq:Departement ." + NL +     // type département sur D2RQ
    		"  ?s2 tdb:nom ?tdb_nom_dep ." + NL +
    		"  ?s neo:name ?nom_dept" + NL +
    		"  FILTER(?nom_dept = ?tdb_nom_dep) ." + NL +
    		"  OPTIONAL { ?s2 tdb:latitude ?latitude } ." + NL +
    		"  OPTIONAL { ?s2 tdb:longitude ?longitude } ." + NL +
    		" }";   		
    		
    public static String Qpatrimoine = "PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX mongo: <" + Config.mongoPrefix + ">" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL +    
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +    
    		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
    		"SELECT ?nom ?insee ?patrimoine_moyen_2002 ?patrimoine_moyen_2008 ?patrimoine_moyen_2010 WHERE {" + NL +
    		"  ?s mongo:code_insee ?insee ." + NL +
    		"  ?s2 d2rq:code_insee ?inseed ." + NL +
    		"  FILTER(?insee = ?inseed) ." + NL +
    		"  OPTIONAL { ?s mongo:patrimoine_moyen_2002 ?patrimoine_moyen_2002 } ." + NL +
    		"  OPTIONAL { ?s mongo:patrimoine_moyen_2008 ?patrimoine_moyen_2008 } ." + NL +
    		"  OPTIONAL { ?s mongo:patrimoine_moyen_2010 ?patrimoine_moyen_2010 } ." + NL +
    		"  ?s2 d2rq:code_departement ?code_dep ." + NL +
    		"  ?s2 d2rq:nom_commune ?nom " + NL +
    		"  } ORDER BY ?patrimoine_moyen_2010";
    		
    // Impot moyen 2002, 2008 et 2010 pour les communes de l'Hérault
    public static String Q1 = 
			"PREFIX rdf: <" + RDF.getURI() + ">" + NL + 
    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + NL + 
    		"PREFIX owl: <http://www.w3.org/2002/07/owl#>" + NL + 
    		"PREFIX mongo: <" + Config.mongoPrefix + ">" + NL + 
    		"PREFIX d2rq: <" + Config.d2rqPrefix + ">" + NL +    
    		"PREFIX neo: <" + Config.neoPrefix + ">" + NL +    
    		"PREFIX tdb: <" + Config.tdbPrefix + ">" + NL +    
    		"PREFIX gn: <http://www.geonames.org/ontology#>" + NL + 
    		"SELECT ?nom_commune ?insee ?impot2002 ?impot2008 ?impot2010 WHERE {" + NL +
    		"  ?s2 rdf:type mongo:Commune ." + NL +
    		"  ?s2 d2rq:nom_commune ?nom_commune ." + NL +    		
    		"  ?s2 d2rq:code_insee ?coded ." + NL +
    		"  ?s mongo:code_insee ?insee ." + NL +
    		"  FILTER ((?insee > \"33999\") && (?insee < \"35000\")) ." + NL +
    		"  OPTIONAL { ?s mongo:impot_moyen_2002 ?impot2002 } ." + NL +
    		"  OPTIONAL { ?s mongo:impot_moyen_2008 ?impot2008 } ." + NL +
    		"  OPTIONAL { ?s mongo:impot_moyen_2010 ?impot2010 } ." + NL +
    		"  FILTER (?coded = ?insee) }";
    		
}
