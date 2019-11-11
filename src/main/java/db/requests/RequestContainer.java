package db.requests;

import com.google.gson.Gson;
import db.DBMain;
import db.dataclas.GsonContainer;

import java.util.HashMap;
import java.util.Map;

public class RequestContainer {
    private Map<String, RequestParent> container = new HashMap();
    private Gson gson = new Gson();
    public RequestContainer(DBMain db) {
        container.put(RequestAccounts.IDENTIFIER, new RequestAccounts(db));
    }

    public String getAnswer(String request) {
        GsonContainer gsonContainer = gson.fromJson(request, GsonContainer.class);
        if (container.containsKey(gsonContainer.type)) {
            return container.get(gsonContainer.type).answer(gsonContainer);
        }
        return "Has not request class";
    }
}
