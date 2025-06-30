package in.edu.kristujayanti.handlers;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

public class AuthHandler {
    public static void authRoutes(Router router, MongoClient mongoClient) {
        router.post("/login").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            String email = body.getString("email");
            String password = body.getString("password");

            JsonObject query = new JsonObject()
                    .put("email", email)
                    .put("password", password);

            mongoClient.findOne("users", query, null, res -> {
                if (res.succeeded() && res.result() != null) {
                    ctx.response().end("Login successful!");
                } else {
                    ctx.response().setStatusCode(401).end("Invalid credentials.");
                }
            });
        });
    }
}
