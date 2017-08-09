package be.infrabel.neo4j.poc.model;

import org.neo4j.graphdb.RelationshipType;

public enum RelationShipTypes implements RelationshipType {
		IS_FRIEND_OF,
		HAS_SEEN;
}
