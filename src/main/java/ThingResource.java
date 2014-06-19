import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class ThingResource {

    @GET
    public String thing() {
        return "THING!";
    }
}
