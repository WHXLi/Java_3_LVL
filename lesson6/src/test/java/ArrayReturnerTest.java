import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class) public class ArrayReturnerTest {
    private static Methods methods;
    private int[] array;
    private int[] result;

    public ArrayReturnerTest(int[] array, int[] result){
        this.array = array;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data(){
        return Arrays.asList(new Object[][]{
                {new int[]{2,2,2,4,3,4,1}, new int[] {1}},
                {new int[]{1,4,0}, new int[] {0}},
                {new int[] {1,4,0,333,4,777,666,999}, new int[] {777,666,999}}
        });
    }

    @Before
    public void init(){
        methods = new Methods();
    }

    @Test
    public void test(){
        Assert.assertArrayEquals(result, methods.arrayReturner(array));
    }
    

}
