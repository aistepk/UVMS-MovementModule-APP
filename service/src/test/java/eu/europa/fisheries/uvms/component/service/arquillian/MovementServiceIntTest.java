package eu.europa.fisheries.uvms.component.service.arquillian;

import eu.europa.ec.fisheries.schema.movement.v1.*;
import eu.europa.ec.fisheries.uvms.movement.entity.area.Area;
import eu.europa.ec.fisheries.uvms.movement.entity.area.AreaType;
import eu.europa.ec.fisheries.uvms.movement.entity.area.Areatransition;
import eu.europa.ec.fisheries.uvms.movement.model.exception.MovementDuplicateException;
import eu.europa.ec.fisheries.uvms.movement.service.MovementService;
import eu.europa.ec.fisheries.uvms.movement.service.exception.MovementServiceException;
import eu.europa.ec.fisheries.uvms.movement.util.DateUtil;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.util.Date;
import java.util.Random;

/**
 * Created by thofan on 2017-03-02.
 */

@RunWith(Arquillian.class)
public class MovementServiceIntTest   extends TransactionalTests {

    Random rnd = new Random();


    private final static String TEST_USER_NAME = "MovementServiceIntTestTestUser";

    @EJB
    MovementService movementService;


    @Deployment
    public static Archive<?> createDeployment() {
        return BuildMovementServiceTestDeployment.createDeploymentFORMovementServiceIntTest();
    }


    @Test
    public void createMovement(){

        Date now = DateUtil.nowUTC();
        Double latitude = 1.00001;
        Double longitude = 2.00001;


        MovementType  movementType = createMovementTypeHelper(now,longitude, latitude);
        Assert.assertTrue(movementService != null);


        /*

            try {
                MovementType createdMovementType = movementService.createMovement(movementType, "TEST");
            } catch (Exception e) {
                Assert.fail();
            }
        */


    }




    /******************************************************************************************************************
     *   HELPER FUNCTIONS
     ******************************************************************************************************************/

    private Areatransition getAreaTransition(String code, MovementTypeType transitionType) {
        Areatransition transition = new Areatransition();
        transition.setMovementType(transitionType);
        transition.setAreatranAreaId(getAreaHelper(code));
        return transition;
    }

    private Area getAreaHelper(String areaCode) {
        Area area = new Area();
        area.setAreaCode(areaCode);
        area.setAreaName(areaCode);
        area.setAreaType(getAraTypeHelper(areaCode));
        return area;
    }

    private AreaType getAraTypeHelper(String name) {
        AreaType areaType = new AreaType();
        areaType.setName(name);
        return areaType;
    }

    private MovementType createMovementTypeHelper(Date timeStamp, double longitude, double latitude) {
        MovementType movementType = new MovementType();
        movementType.setPositionTime(timeStamp);
        MovementPoint point = new MovementPoint();
        point.setLatitude(latitude);
        point.setLongitude(longitude);

        movementType.setPosition(point);
        movementType.setComChannelType(MovementComChannelType.MANUAL);
        //movementType.setInternalReferenceNumber( );
        movementType.setTripNumber(rnd.nextDouble());
        movementType.setMovementType(MovementTypeType.POS);

        return movementType;
    }


    public static MovementMetaData getMappedMovementHelper(int numberOfAreas) {
        MovementMetaData metaData = new MovementMetaData();
        for (int i = 0; i < numberOfAreas; i++) {
            metaData.getAreas().add(getMovementMetadataTypeHelper("AREA" + i));
        }
        return metaData;
    }

    public static MovementMetaDataAreaType getMovementMetadataTypeHelper(String areaCode) {
        MovementMetaDataAreaType area = new MovementMetaDataAreaType();
        area.setCode(areaCode);
        area.setName(areaCode);
        area.setAreaType(areaCode);
        return area;
    }


}
