import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import play.mvc.*;
import play.libs.*;
import play.test.*;
import static play.test.Helpers.*;
import com.avaje.ebean.Ebean;
import com.google.common.collect.ImmutableMap;

public class LoginTest extends WithApplication {
    @Before
    public void setUp() {
        start(fakeApplication(inMemoryDatabase(), fakeGlobal()));
        Ebean.save((List) Yaml.load("test-data.yml"));
    }

    @Test
    public void authenticateSuccess() {
        Result result = callAction(
            controllers.routes.ref.Application.authenticate(),
            fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                "username", "s550",
                "password", "password"))
        );
        assertEquals(303, status(result));
        assertEquals("s550", session(result).get("username"));
    }
    @Test
    public void authenticateFailure() {
        Result result = callAction(
            controllers.routes.ref.Application.authenticate(),
            fakeRequest().withFormUrlEncodedBody(ImmutableMap.of(
                "username", "s550",
                "password", "badpassword"))
        );
        assertEquals(400, status(result));
        assertNull(session(result).get("username"));
    }

    @Test
    public void authenticated() {
        Result result = callAction(
            controllers.routes.ref.Application.index(),
            fakeRequest().withSession("username", "s550")
        );
        assertEquals(200, status(result));
    }

    @Test
    public void notAuthenticated() {
        Result result = callAction(
            controllers.routes.ref.Application.home(),
            fakeRequest()
        );
        assertEquals(303, status(result));
        assertEquals("/", header("Location", result));
    }

}