import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ArrayRangeQueryBasicTest.class,
        BPlusTreeRangeQueryBasicTest.class,
        CQERangeQueryBasicTest.class,
        IgniteRangeQueryBasicTest.class,
        TreeMapRangeQueryBasicTest.class
})
public class RangeQueryTestSuite {
}