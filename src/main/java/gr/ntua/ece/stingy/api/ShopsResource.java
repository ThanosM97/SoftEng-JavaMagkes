package gr.ntua.ece.stingy.api;

import gr.ntua.ece.stingy.conf.Configuration;
import gr.ntua.ece.stingy.data.DataAccess;
import gr.ntua.ece.stingy.data.Limits;
import gr.ntua.ece.stingy.data.model.Product;
import gr.ntua.ece.stingy.data.model.Shop;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopsResource extends ServerResource {

    private final DataAccess dataAccess = Configuration.getInstance().getDataAccess();

    
    @Override
    protected Representation get() throws ResourceException {
	    /**
	     * Get parameters of the get method.
	     */
	    Form queryParams = getQuery();
	    String startString = queryParams.getFirstValue("start");
	    String countString = queryParams.getFirstValue("count");
	    String status = queryParams.getFirstValue("status");
	    String sort = queryParams.getFirstValue("sort");
	
	    Map<String, Object> map = new HashMap<>();
	    Limits limits = new Limits();
	    /**
	     * If a parameter exists and is valid put it in the map.
	     */
	    if (startString != null ) {
	    	Long start = null;
	    	try {
	    		start = Long.parseLong(startString);
	    	}
	    	catch(Exception e) {
	    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid start: " + startString);
	    	}
	    	limits.setStart(start);
	    	map.put("start", start);
	    } else {
	    	/*
	    	 * default value for start is 0.
	    	 */
	    	map.put("start", 0);
	    }
	    if (countString != null ) {
	    	Long count = null;
	    	try {
	    		count = Long.parseLong(countString);
	    	}
	    	catch(Exception e) {
	    		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid count: " + countString);
	    	}
	    	limits.setCount(count);
	    	map.put("count", count);
	    } else{
	    	/*
	    	 * Δefault value for count is 20.
	    	 */
	    	map.put("count", 20);
	    }
	    /*
	     * set default values for status and sort
	     */
	    if (status == null) {
	    	status = "ACTIVE";
	    }
	    if (sort == null) {
	    	sort = "id|DESC";
	    }
	    if (!status.equals("ALL") && !status.equals("WITHDRAWN") && !status.equals("ACTIVE")) {
	    	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid status: " + status);
	    }
	    if (!sort.equals("id|ASC") && !sort.equals("id|DESC") && !sort.equals("name|ASC") && !sort.equals("name|DESC")) {
	    	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Invalid sort: " + sort);
	    }
	    /*
	     * Get shops based on the limits.
	     */
	    List<Shop> shops = dataAccess.getShops(limits, status, sort);
	    /*
	     * Set current total products.
	     */
	    map.put("total", limits.getTotal());
	    map.put("shops", shops);
	    return new JsonMapRepresentation(map);
	}

    @Override
    protected Representation post(Representation entity) throws ResourceException {

        /*
         * Create a new restlet form
         */
        Form form = new Form(entity);
        /*
         * Read the parameters
         */
        String name = form.getFirstValue("name");
        String address = form.getFirstValue("address");
        Double lng = Double.valueOf(form.getFirstValue("lng"));
        Double lat = Double.valueOf(form.getFirstValue("lat"));
        String tagsString = form.getFirstValue("tags");
        String withdrawnString = form.getFirstValue("withdrawn");

        /*
		 *  Validate the values (in the general case)
		 */
		if (name == null || name.isEmpty()) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Name is required");
		}
		if (address == null || address.isEmpty()) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Address is required");
		}
		if (lng == null || lat == null) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Longitude and latitude are required");
		}
		if (tagsString == null || tagsString.isEmpty()) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Tags are required");
		}
		
		/*
		 * If withdrawn is not set, use default value.
		 */
		boolean withdrawn;
		if (withdrawnString == null) {
			withdrawn = false;
		}
		else {
			withdrawn = Boolean.valueOf(withdrawnString);
		}
		/*
		 * Convert tagString that represents a list of tags to a list.
		 */
		ArrayList<String> tags = new Gson().fromJson(tagsString, ArrayList.class);
		
        Shop shop = dataAccess.addShop(name, address, lng, lat, tags, withdrawn);
        /*
         * Return the json representation of the shop.
         */
        return new JsonShopRepresentation(shop);
    }
}


