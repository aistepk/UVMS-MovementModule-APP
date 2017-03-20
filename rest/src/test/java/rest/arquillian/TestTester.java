package rest.arquillian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import eu.europa.ec.fisheries.uvms.movement.rest.dto.ResponseDto;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by thofan on 2017-03-15.
 */
@RunWith(Arquillian.class)
public class TestTester  extends BuildMovementRestTestDeployment {

    final static Logger LOG = LoggerFactory.getLogger(TestTester.class);

    public static final String ENDPOINT_ROOT = "http://localhost:28080";

    ResponseDto x;



    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }





    @Test
    @RunAsClient
    public void areas() {


        webLoginTarget = client.target(ENDPOINT_ROOT).path("usm-movement").path("rest").path("areas");

        Response response = webLoginTarget
                .request(MediaType.APPLICATION_JSON)
                .get();

        ResponseDto content = response.readEntity(ResponseDto.class);
    }







}
