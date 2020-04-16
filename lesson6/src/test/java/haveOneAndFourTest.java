import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class haveOneAndFourTest {
    private static Methods methods;
    private int[] array;
    private boolean result;

    public haveOneAndFourTest(int[] array, boolean result) {
        this.array = array;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new int[]{1, 1, 1, 4, 4, 4}, true},
                {new int[]{2,3,4,5,6,7}, false},
                {new int[]{4,4,4,4}, false},
                {new int[]{1,1,1,1}, false}
        });
    }

    @Before
    public void init(){
        methods = new Methods();
    }

    @Test
    public void test(){
        Assert.assertEquals(methods.haveOneAndFour(array),result);
    }
}
