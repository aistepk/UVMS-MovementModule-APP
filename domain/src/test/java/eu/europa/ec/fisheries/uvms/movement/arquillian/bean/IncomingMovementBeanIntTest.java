package eu.europa.ec.fisheries.uvms.movement.arquillian.bean;

import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.movement.v1.SegmentCategoryType;
import eu.europa.ec.fisheries.uvms.movement.arquillian.TransactionalTests;
import eu.europa.ec.fisheries.uvms.movement.arquillian.bean.util.TestUtil;
import eu.europa.ec.fisheries.uvms.movement.bean.IncomingMovementBean;
import eu.europa.ec.fisheries.uvms.movement.bean.MovementBatchModelBean;
import eu.europa.ec.fisheries.uvms.movement.dao.MovementDao;
import eu.europa.ec.fisheries.uvms.movement.dao.exception.MovementDaoMappingException;
import eu.europa.ec.fisheries.uvms.movement.entity.Movement;
import eu.europa.ec.fisheries.uvms.movement.entity.MovementConnect;
import eu.europa.ec.fisheries.uvms.movement.entity.area.Areatransition;
import eu.europa.ec.fisheries.uvms.movement.exception.GeometryUtilException;
import eu.europa.ec.fisheries.uvms.movement.model.exception.MovementDaoException;
import eu.europa.ec.fisheries.uvms.movement.model.exception.MovementDuplicateException;
import eu.europa.ec.fisheries.uvms.movement.model.exception.MovementModelException;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.transaction.SystemException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by andreasw on 2017-03-09.
 */
@RunWith(Arquillian.class)
public class IncomingMovementBeanIntTest extends TransactionalTests {

    final static Logger LOG = LoggerFactory.getLogger(IncomingMovementBeanIntTest.class);

    @EJB
    IncomingMovementBean incomingMovementBean;

    @EJB
    MovementBatchModelBean movementBatchModelBean;

    @EJB
    MovementDao movementDao;

    private TestUtil testUtil = new TestUtil();


    @Test
    @OperateOnDeployment("normal")
    public void testCreatingMovement() throws MovementDaoMappingException, MovementModelException, SystemException, GeometryUtilException, MovementDaoException, MovementDuplicateException {
        String uuid = UUID.randomUUID().toString();

        MovementType movementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, uuid);
        movementType = movementBatchModelBean.createMovement(movementType, "TEST");
        assertNotNull("MovementType creation was successful.", movementType.getGuid());
        em.flush();

        MovementConnect movementConnect = movementDao.getMovementConnectByConnectId(movementType.getConnectId());
        assertNotNull("MovementConnect creation was successful.", movementConnect);
        List<Movement> movementList = movementConnect.getMovementList();
        assertNotNull("List of Movement creation was successful.", movementList);
        assertTrue("The list of Movement contains exactly one Movement object.", movementList.size() == 1);
        Long id = movementList.get(0).getId();
        incomingMovementBean.processMovement(id);

        Movement movement = movementDao.getMovementById(id);
        assertNotNull("Movement object was successfully created.", movement);
        LOG.info(" [ testCreatingMovement: Movement object was successfully created. ] ");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testProcessingMovement() throws MovementDaoMappingException, MovementModelException, SystemException, GeometryUtilException, MovementDaoException, MovementDuplicateException {

        // Given: Get the id for a persisted movement entity.

        String uuid = UUID.randomUUID().toString();

        MovementType movementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, uuid);
        movementType = movementBatchModelBean.createMovement(movementType, "TEST");
        em.flush();

        MovementConnect movementConnect = movementDao.getMovementConnectByConnectId(movementType.getConnectId());
        List<Movement> movementList = movementConnect.getMovementList();
        Long id = movementList.get(0).getId();


        //When: Invoke the processMovement method
         incomingMovementBean.processMovement(id);

        //Then: Test that the Movement is processed properly.
        Movement actualMovement = movementDao.getMovementById(id);
        boolean actualProcessedValue = actualMovement.getProcessed();

        assertThat(actualProcessedValue, is(true));
        LOG.info(" [ testProcessingMovement: Movement object was successfully processed. ] ");
    }

    @Test
    @OperateOnDeployment("normal")
    public void testProcessingMovement_NoDuplicateMovement() throws MovementDaoMappingException, MovementModelException, SystemException, GeometryUtilException, MovementDaoException, MovementDuplicateException {

        // Given: Get the id for a persisted movement entity.

        String uuid = UUID.randomUUID().toString();

        MovementType movementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, uuid);
        movementType = movementBatchModelBean.createMovement(movementType, "TEST");
        em.flush();

        MovementConnect movementConnect = movementDao.getMovementConnectByConnectId(movementType.getConnectId());
        List<Movement> movementList = movementConnect.getMovementList();
        Long id = movementList.get(0).getId();


        //When: Invoke the processMovement method on the read Movement entity.
        incomingMovementBean.processMovement(id);

        //Then: Test that the Movement is processed properly.
        Movement actualMovement = movementDao.getMovementById(id);
        boolean actualDuplicateValue = actualMovement.getDuplicate();

        assertThat(actualDuplicateValue, is(false));
        LOG.info(" [ testProcessingMovement_NoDuplicateMovement: Successful check that there are no duplicate movement entities in the database. ] ");
    }

    /*** Under construction ****/
    //@Test
    //@Ignore
    public void testProcessingMovement_DuplicateTimeStampMovement() throws MovementDaoMappingException, MovementModelException, SystemException, GeometryUtilException, MovementDaoException, MovementDuplicateException {


        //ToDo: Evaluate if this is a relevant test.
        //ToDo: If yes, figure out how to persist two Movement's with the same Date value using existing code.
        // Given: Create two movements with the same timestamps.

        // First movement

        String firstUuid = UUID.randomUUID().toString();

        MovementType firstMovementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, firstUuid);
        firstMovementType = movementBatchModelBean.createMovement(firstMovementType, "TEST");
        em.flush();

        MovementConnect firstMovementConnect = movementDao.getMovementConnectByConnectId(firstMovementType.getConnectId());

        List<Movement> firstMovementList = firstMovementConnect.getMovementList();

        Movement firstMovement = firstMovementList.get(0);
        Long firstMovementId = firstMovementList.get(0).getId();

        firstMovement.setTimestamp(new Date(1490708331790L));


        // Second movement

        String secondUuid = UUID.randomUUID().toString();

        MovementType secondMovementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, secondUuid);
        secondMovementType = movementBatchModelBean.createMovement(secondMovementType, "TEST");
        em.flush();

        MovementConnect secondMovementConnect = movementDao.getMovementConnectByConnectId(secondMovementType.getConnectId());

        List<Movement> secondMovementList = secondMovementConnect.getMovementList();

        Movement secondMovement = secondMovementList.get(0);
        Long secondMovementId = secondMovementList.get(0).getId();

        secondMovement.setTimestamp(new Date(1490708331790L));

        assertEquals(firstMovement.getTimestamp(), secondMovement.getTimestamp());

        //When: Invoke the processMovement method on the read Movement entity.
        //incomingMovementBean.processMovement(id);
    }

    /*** Still to do. ****/
    //@Test
    //@Ignore
    //@OperateOnDeployment("normal")
    public void testPopulateTransitions() {

        Movement currentMovement = new Movement();
        Movement previousMovement = new Movement();

        List<Areatransition> areatransitionList = incomingMovementBean.populateTransitions(currentMovement, previousMovement);

        assertNotNull(areatransitionList);
    }

    /*** Still to do. ****/
    //@Test
    //@Ignore
    public void testCreateAreaTransition() {

    }

    /*** Still to do. ****/
    //@Test
    //@Ignore
    public void testCreateListOfAreaTransitions() {

    }

    /**** Helper. May or may not be relevant/useful. ****/
    private Movement createMovementHelper() throws MovementDaoMappingException, MovementModelException, SystemException, GeometryUtilException, MovementDaoException, MovementDuplicateException {

        String uuid = UUID.randomUUID().toString();

        MovementType movementType = testUtil.createMovementType(0d, 1d, 0d, SegmentCategoryType.EXIT_PORT, uuid);
        movementType = movementBatchModelBean.createMovement(movementType, "TEST");
        em.flush();

        MovementConnect movementConnect = movementDao.getMovementConnectByConnectId(movementType.getConnectId());
        List<Movement> movementList = movementConnect.getMovementList();
        Long id = movementList.get(0).getId();
        incomingMovementBean.processMovement(id);

        return movementDao.getMovementById(id);
    }
}
