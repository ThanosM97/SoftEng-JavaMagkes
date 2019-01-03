package gr.ntua.ece.stingy.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RestfulApp extends Application {

    @Override
    public synchronized Restlet createInboundRoot() {

        Router router = new Router(getContext());

        /**
         * Attach all resources to the corresponding URIs.
         */
        //GET, POST
        router.attach("/products", ProductsResource.class);

        //GET, DELETE, PUT, PATCh
        router.attach("/products/{id}", ProductResource.class);

        //GET, POST
        router.attach("/shops", ShopsResource.class);
        return router;
    }

}
