package com.bisol.busfinder.tests;

import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.TextView;

import com.bisol.busfinder.BusFinder;
import com.bisol.busfinder.R;

public class BusFinderTest extends ActivityUnitTestCase<BusFinder> {

    public BusFinderTest() {
        super(BusFinder.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Log.d("BusFinderTest", "test setup");
        Intent busFinderLaunchIntent = new Intent(getInstrumentation().getTargetContext(), BusFinder.class);
        startActivity(busFinderLaunchIntent, null, null);
    }

    /**
     * Verify display of address from geolocation
     */
    @UiThreadTest
    public void testMapActivityResponse() {
        String expectedText = "Mauro Ramos";
        String address = "Av. " + expectedText + ", 108 - Centro";


        BusFinder.BusFinderResultReceiver receiver = getActivity().new BusFinderResultReceiver(null);
        Bundle bundle= new Bundle();
        bundle.putCharSequence(BusFinder.INTENT_RESULT, address);
        receiver.send(BusFinder.RESULT_OK, bundle);

        TextView streetNameText = (TextView) getActivity().findViewById(R.id.bus_finder_street_name);
        CharSequence actualText = streetNameText.getText().toString();

        assertNotNull(actualText);
        assertEquals(expectedText, actualText.toString());
    }
}
