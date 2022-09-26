import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TesterTest {

    @Test
    void testerMath() {
        ArrayList<Integer> mList = mock(ArrayList.class);
        when(mList.size()).thenReturn(1); //stub

        assertEquals(1, mList.size());
    }
}