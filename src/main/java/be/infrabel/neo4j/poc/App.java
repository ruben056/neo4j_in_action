package be.infrabel.neo4j.poc;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import be.infrabel.neo4j.poc.model.Labels;
import be.infrabel.neo4j.poc.model.RelationShipTypes;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        App.createUsersAndMovies();
    }
    
    private static void createUsersAndMovies(){
    	 GraphDatabaseService graphDb = new GraphDatabaseFactory().newEmbeddedDatabase("/tmp/neo4j");
    	     	 
    	 try(Transaction tx = graphDb.beginTx()){
    		 Node userNode1 = graphDb.createNode();
    		 userNode1.setProperty("name", "Jim");
    		 userNode1.addLabel(Labels.USERS);
    		 System.out.println("created user node : " + userNode1.getId());
    		 
    		 Node userNode2 = graphDb.createNode();
    		 userNode2.addLabel(Labels.USERS);
    		 userNode2.setProperty("name", "Jimmy");
    		 System.out.println("created user node : " + userNode2.getId());
    		 
    		 userNode1.createRelationshipTo(userNode2, RelationShipTypes.IS_FRIEND_OF);
    		     		 
    		 for(Node curNode : GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(Labels.USERS)){
    			 System.out.println("name :" + curNode.getProperty("name"));
    		 }
    		 
    		 Node movieNode1 = graphDb.createNode();
    		 movieNode1.setProperty("name", "Fargo");
    		 movieNode1.addLabel(Labels.MOVIES);
    		 System.out.println("created movie node : " + movieNode1.getId());
    		 
    		 Node movieNode2 = graphDb.createNode();
    		 movieNode2.setProperty("name", "Pulp Fiction");
    		 movieNode2.addLabel(Labels.MOVIES);
    		 System.out.println("created movie node : " + movieNode2.getId());
    		 
    		 
    		 Relationship rel = userNode2.createRelationshipTo(movieNode1, RelationShipTypes.HAS_SEEN);
    		 rel.setProperty("stars", 4);
    		 
    		 rel = userNode2.createRelationshipTo(movieNode2, RelationShipTypes.HAS_SEEN);
    		 rel.setProperty("stars", 5);
    		 rel = userNode1.createRelationshipTo(movieNode2, RelationShipTypes.HAS_SEEN);
    		 rel.setProperty("stars", 5);
    		 
    		 userNode2 = graphDb.getNodeById(userNode2.getId());
    		 for(Relationship hasSeen : userNode2.getRelationships(Direction.OUTGOING, RelationShipTypes.HAS_SEEN)){
    			 String userName = (String)userNode2.getProperty("name");
    			 String movieName = (String)hasSeen.getEndNode().getProperty("name");
    			 int nrOfStars = (int)hasSeen.getProperty("stars");
    			 System.out.println("User " + userName + " has seen " + movieName + " and gave it " + nrOfStars + " stars.");
    			 
    		 }
    		 
    		 // movies jim's friend have seen, but jim did not see yet (recommendations...)
    		 List<String> movieNamesSeenByUser1 = new ArrayList<>();
    		 for(Relationship hasSeen : userNode1.getRelationships(Direction.OUTGOING, RelationShipTypes.HAS_SEEN)){
    			 movieNamesSeenByUser1.add((String)hasSeen.getEndNode().getProperty("name"));
    		 }
    		 List<String> moviesSeenByUser1sFriends = new ArrayList<>();
    		 for(Relationship friend : userNode1.getRelationships(Direction.OUTGOING, RelationShipTypes.IS_FRIEND_OF)){
    			 for(Relationship hasSeen : friend.getEndNode().getRelationships(Direction.OUTGOING, RelationShipTypes.HAS_SEEN)){
    				 moviesSeenByUser1sFriends.add((String)hasSeen.getEndNode().getProperty("name"));
        		 }
    		 }
    		 
    		 moviesSeenByUser1sFriends.removeAll(movieNamesSeenByUser1);
    		 System.out.println("recommended movies for " + userNode1.getProperty("name") + " : " + moviesSeenByUser1sFriends);
    		 
    		 tx.success();
    	 }
    	 
    }
}
