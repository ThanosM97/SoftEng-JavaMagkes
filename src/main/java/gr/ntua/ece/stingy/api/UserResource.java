package gr.ntua.ece.stingy.api;

import gr.ntua.ece.stingy.conf.Configuration;
import gr.ntua.ece.stingy.data.DataAccess;
import gr.ntua.ece.stingy.data.model.User;

import org.restlet.data.Form;
import org.restlet.data.Header;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import java.util.Optional;

public class UserResource extends ServerResource {

    private final DataAccess dataAccess = Configuration.getInstance().getDataAccess();

    @Override
    protected Representation post(Representation entity) throws ResourceException {
    	/*
    	 * Get  token from headers
    	 */
    	@SuppressWarnings("unchecked")
		Series<Header> headers = (Series<Header>) getRequestAttributes().get("org.restlet.http.headers");
    	String auth = headers.getFirstValue("X-OBSERVATORY-AUTH");
    	/*
         * Create a new restlet form
         */
        Form form = new Form(entity);
        /*
         * Read the parameters
         */
        String userToken = form.getFirstValue("userToken");
        
        if (userToken == null || userToken.isEmpty()){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Missing token");
        }
        if (!dataAccess.isAdmin(auth) && !userToken.equals(auth)) {
            throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN, "You are not authorized for this information");
        }
        
        Optional<User> optional = dataAccess.getUserByToken(userToken);
        User user = optional.orElseThrow(() -> new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "User not found - token: " + userToken));
        /*
         * Return message.
         */
        return new JsonUserRepresentation(user);
    }
    
}
