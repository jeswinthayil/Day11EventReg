package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.services.TokenGenerator;
import in.edu.kristujayanti.utils.EmailUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;

public class EventHandler {
    public static void eventRoutes(Router router, MongoClient mongoClient) {
        router.get("/events").handler(ctx -> {
            mongoClient.find("events", new JsonObject(), res -> {
                if (res.succeeded()) {
                    ctx.response().putHeader("Content-Type", "application/json")
                            .end(res.result().toString());
                } else {
                    ctx.response().setStatusCode(500).end("Failed to fetch events.");
                }
            });
        });

        router.post("/book").handler(ctx -> {
            JsonObject body = ctx.getBodyAsJson();
            String email = body.getString("email");
            String eventId = body.getString("eventId");

            JsonObject query = new JsonObject().put("_id", eventId);
            mongoClient.findOne("events", query, null, eventRes -> {
                if (eventRes.succeeded() && eventRes.result() != null) {
                    int tokensLeft = eventRes.result().getInteger("availableTokens");

                    if (tokensLeft > 0) {
                        String token = TokenGenerator.generateToken();

                        JsonObject booking = new JsonObject()
                                .put("eventId", eventId)
                                .put("email", email)
                                .put("token", token);

                        mongoClient.insert("bookings", booking, insertRes -> {
                            if (insertRes.succeeded()) {
                                JsonObject update = new JsonObject()
                                        .put("$inc", new JsonObject().put("availableTokens", -1));

                                mongoClient.updateCollection("events", query, update, updateRes -> {
                                    if (updateRes.succeeded()) {
                                        EmailUtil.sendTokenEmail(email, token);
                                        ctx.response().end("Booking successful! Token sent.");
                                    } else {
                                        ctx.response().setStatusCode(500).end("Failed to update event.");
                                    }
                                });
                            } else {
                                ctx.response().setStatusCode(500).end("Failed to save booking.");
                            }
                        });
                    } else {
                        ctx.response().setStatusCode(400).end("No tokens left.");
                    }
                } else {
                    ctx.response().setStatusCode(404).end("Event not found.");
                }
            });
        });
    }
}
