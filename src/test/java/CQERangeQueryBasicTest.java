import com.workday.oms.exception.DataThresholdException;
import com.workday.oms.exception.InvalidRangeException;
import com.workday.oms.impl.array.ArrayRangeContainerFactory;
import com.workday.oms.impl.bplusTree.BinaryPlusTreeRangeContainerFactory;
import com.workday.oms.impl.cqe.CQERangeContainerFactory;
import com.workday.oms.impl.treemap.TreeMapRangeContainerFactory;
import com.workday.oms.interfaces.Ids;
import com.workday.oms.interfaces.RangeContainer;
import com.workday.oms.interfaces.RangeContainerFactory;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CQERangeQueryBasicTest {

    private static final Logger log = Logger.getLogger(CQERangeQueryBasicTest.class);
    private RangeContainer rc;
    private long[] testData = {};
    RangeContainerFactory rf;

    @Before
    public void setUp() throws DataThresholdException, InvalidRangeException {
        log.debug("Test Setup");
        testData = new long[]{10, 12, 17, 21, 2, 15, 16};
        rf = new CQERangeContainerFactory();
        rc = rf.createContainer(testData);
    }

    @Test
    public void runARangeQuery() throws InvalidRangeException {
        Ids ids = rc.findIdsInRange(14, 17, true, true);
        assertEquals(2, ids.nextId());
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = rc.findIdsInRange(14, 17, true, false);
        assertEquals(5, ids.nextId());
        assertEquals(6, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
        ids = rc.findIdsInRange(20, Long.MAX_VALUE, false, true);
        assertEquals(3, ids.nextId());
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test(expected = InvalidRangeException.class)
    public void testWithInvalidRange() throws InvalidRangeException {
        Ids ids = rc.findIdsInRange(17, 14, true, true);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }

    @Test
    public void testEmptyData() throws InvalidRangeException, DataThresholdException {
        testData = new long[]{};
        rc = rf.createContainer(testData);
        Ids ids = rc.findIdsInRange(14, 17, true, true);
        assertEquals(Ids.END_OF_IDS, ids.nextId());
    }
}
