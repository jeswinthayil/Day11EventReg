package in.edu.kristujayanti;

import in.edu.kristujayanti.handlers.AuthHandler;
import in.edu.kristujayanti.handlers.EventHandler;
import in.edu.kristujayanti.handlers.RegisterHandler;
import in.edu.kristujayanti.utils.EmailUtil;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        MongoClient mongoClient = MongoClient.createShared(vertx, new JsonObject()
                .put("connection_string", "mongodb://localhost:27017")
                .put("db_name", "eventSystem"));

        EmailUtil.setupMailer(vertx); // Setup mail client

        Router router = Router.router(vertx);


        router.route().handler(BodyHandler.create());


        RegisterHandler.registerRoutes(router, mongoClient);
        AuthHandler.authRoutes(router, mongoClient);
        EventHandler.eventRoutes(router, mongoClient);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888, http -> {
                    if (http.succeeded()) {
                        System.out.println("Server running at http://localhost:8888");
                    } else {
                        http.cause().printStackTrace();
                    }
                });
    }
}
