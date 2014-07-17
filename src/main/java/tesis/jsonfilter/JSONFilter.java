package tesis.jsonfilter;

import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.ArrayNode;


public class JSONFilter {
	
	public JSONFilter() {}
	
	/**
	 * Este filtrara los JSON que provienen de las aplicaciones dejando solos los campos que deseamos
	 * relevar. 
	 * 
	 * @param node		Un JSON de entrada. El que va a ser filtrado.
	 * @param schema	Un JSON el cual servira como referencia para saber cuales son los datos relevantes
	 * @param result 	Un JSON que funciona como acumulador, donde se almacena los datos relevantes
	 * @return
	 */
	public JsonNode filter(JsonNode node, JsonNode schema) {
		if (node.isObject()) {
			ObjectNode result = new ObjectNode(null) ; 
			Iterator<String> it = schema.getFieldNames() ;
			while ( it.hasNext() ) {
				String field = it.next();
				JsonNode nextNode = node.get(field) ;
				if (nextNode != null)
					result.put(field,  filter( nextNode, schema.get(field))) ;
				
			}
			return result ;
		} else if (node.isArray()) {
			ArrayNode result = new ArrayNode(null);
			Iterator<JsonNode> elements = node.getElements() ;
			while (elements.hasNext()) {
				JsonNode elem = elements.next() ;
				result.add(filter( elem, schema.get(0) ) ) ;						
			}
			return result ;
		} else {
			return node ;
		}

	}
}
