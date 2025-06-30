package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.services.TokenGenerator;
import in.edu.kristujayanti.utils.EmailUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

public class RegisterHandler {
    public static void registerRoutes(Router router, MongoClient mongoClient) {
        router.post("/register").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            String email = body.getString("email");
            String name = body.getString("name");

            String password = TokenGenerator.generatePassword(8);

            JsonObject user = new JsonObject()
                    .put("email", email)
                    .put("name", name)
                    .put("password", password);

            mongoClient.insert("users", user, res -> {
                if (res.succeeded()) {
                    EmailUtil.sendPasswordEmail(email, password);
                    ctx.response().setStatusCode(201).end("Registered successfully!");
                } else {
                    ctx.response().setStatusCode(500).end("Registration failed.");
                }
            });
        });
    }
}
