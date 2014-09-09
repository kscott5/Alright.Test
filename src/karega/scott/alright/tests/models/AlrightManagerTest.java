package karega.scott.alright.tests.models;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

import karega.scott.alright.models.AlrightManager;
import karega.scott.alright.models.AlrightManager.ManagerState;
import karega.scott.alright.models.AlrightManager.ManagerStateListener;
import karega.scott.alright.models.AlrightStateType;
import android.content.Context;
import android.location.Address;
import android.test.AndroidTestCase;

public class AlrightManagerTest extends AndroidTestCase {

	private final String GRANT_PARK_ADDRESS = "337 E. Randolph St. Chicago, IL 60601";
	private final LatLng GRANT_PARK_LATLNG = new LatLng(41.884324, -87.619372);
	
	private Context context;
	
	@Override
	public void setUp() {
		context = this.getContext();
	}
	
	@Override 
	public void tearDown() {
		context = null;
	}
	
	public void testAddRemoveManagerStateListener() {
		ManagerStateListener listener = new ManagerStateListener() {
			@Override
			public void onManagerStateChanged(ManagerState state) {}
		};
		
		assertTrue(AlrightManager.addManagerStateListener(listener));
		assertTrue(AlrightManager.removeManagerStateListener(listener));
		assertFalse(AlrightManager.removeManagerStateListener(listener));
	}
	
	public void testAddressToString() {
		Address address = new Address(null);
		address.setAddressLine(0, "1234 South Alright Drive");
		address.setAddressLine(1, "Suite 101");
		address.setAddressLine(2, "City, St. 00000");
		
		assertEquals("1234 South Alright Drive Suite 101 [City, St. 00000]", AlrightManager.addressToString(address));
	}
	
	public void testComputeCompassDirectionTest() {
		String actual = AlrightManager.computeCompassDirection(0);
		assertEquals("N", actual);
		
		actual = AlrightManager.computeCompassDirection(45);
		assertEquals("NE", actual);
		
		actual = AlrightManager.computeCompassDirection(90);
		assertEquals("E", actual);
		
		actual = AlrightManager.computeCompassDirection(135);
		assertEquals("SE", actual);
		
		actual = AlrightManager.computeCompassDirection(180);
		assertEquals("S", actual);
		
		actual = AlrightManager.computeCompassDirection(225);
		assertEquals("SW", actual);
		
		actual = AlrightManager.computeCompassDirection(270);
		assertEquals("W", actual);
		
		actual = AlrightManager.computeCompassDirection(359);
		assertEquals("NW", actual);
	}
	
	public void testHandleApplicationErrorTest() {
		fail("Not tested yet");
	}
	
	public void testGetInstance() {
		AlrightManager.resetInstance(null);
		
		ManagerStateListener listener = new ManagerStateListener() {
			@Override
			public void onManagerStateChanged(ManagerState state) {
				
				assertEquals(AlrightStateType.CONNECTING, state.stateType);
			}
		};
		
		
		assertTrue(AlrightManager.addManagerStateListener(listener));		
		assertNotNull(AlrightManager.getInstance(this.context));
		assertTrue(AlrightManager.removeManagerStateListener(listener));
	}
	
	public void testGetAddress() {
		// NOTE: Why test methods that wraps Google Geocoder?
		AlrightManager manager = AlrightManager.getInstance(this.context);
		assertNotNull(manager);
		
		Address addressByName = manager.getAddress(GRANT_PARK_ADDRESS);
		assertNotNull(addressByName);
		
		Address addressByLatLng = manager.getAddress(GRANT_PARK_LATLNG.latitude, GRANT_PARK_LATLNG.longitude);
		assertNotNull(addressByLatLng);
		
		assertEquals(addressByName.getLatitude(), addressByLatLng.getLatitude());
		assertEquals(addressByName.getLongitude(), addressByLatLng.getLongitude());
		
		// TODO: How to test manager.getAddress(locationName, position);
		// TODO: How to test manager.getAddresses(locationName);
	}
	
	public void testGetSearchManager() {
		assertNotNull(AlrightManager.getInstance(this.context));
	}

	public void testConnected() {
		AlrightManager manager = AlrightManager.getInstance(this.context);

		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				assertEquals(AlrightStateType.CONNECTED, state.stateType);
			}
		};
		
		AlrightManager.addManagerStateListener(listener);
		manager.onConnected(null);
		AlrightManager.removeManagerStateListener(listener);
	}
	
	public void testConnectionFailed() {
		AlrightManager manager = AlrightManager.getInstance(this.context);

		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				assertEquals(AlrightStateType.ERROR, state.stateType);
			}
		};
		
		AlrightManager.addManagerStateListener(listener);
		manager.onConnectionFailed(null);
		AlrightManager.removeManagerStateListener(listener);
	}

	public void testSetMyDestination() {
		AlrightManager manager = AlrightManager.getInstance(this.context);

		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				assertEquals(AlrightStateType.DESTINATION_CHANGED, state.stateType);
				assertTrue(state.stateData instanceof Address);
				
				Address addr = (Address)state.stateData;
				assertEquals(GRANT_PARK_LATLNG.latitude, addr.getLatitude());
				assertEquals(GRANT_PARK_LATLNG.longitude, addr.getLongitude());
			}
		};
		
		AlrightManager.addManagerStateListener(listener);
		manager.setMyDestination(GRANT_PARK_ADDRESS);
		AlrightManager.removeManagerStateListener(listener);
		
		listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				fail("setMyDestination should not notify me");
			}
		};
		
		AlrightManager.addManagerStateListener(listener);
		manager.setMyDestination(GRANT_PARK_ADDRESS, 1);
		AlrightManager.removeManagerStateListener(listener);
	}

	public void testCompleteGameSetup() {
		// NOTE: This method will reverse the order of the compass direction
		// array. 
		AlrightManager manager = AlrightManager.getInstance(this.context);

		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				assertEquals(AlrightStateType.GAME_SETUP_COMPLETE, state.stateType);
				assertTrue(state.stateData instanceof ArrayList);
				
				@SuppressWarnings("unchecked")
				ArrayList<String> data = (ArrayList<String>)state.stateData;
				assertEquals(AlrightManager.COMPASS_HEADING_NORTH, data.get(0));
				assertEquals(AlrightManager.COMPASS_HEADING_NORTH_EAST, data.get(1));
			}
		};

		AlrightManager.addManagerStateListener(listener);
		manager.completeGameSetup(true);
		AlrightManager.removeManagerStateListener(listener);

		listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				assertEquals(AlrightStateType.GAME_SETUP_COMPLETE, state.stateType);
				assertTrue(state.stateData instanceof ArrayList);
				
				@SuppressWarnings("unchecked")
				ArrayList<String> data = (ArrayList<String>)state.stateData;
				assertEquals(AlrightManager.COMPASS_HEADING_NORTH, data.get(0));
				assertEquals(AlrightManager.COMPASS_HEADING_NORTH_WEST, data.get(1));
			}
		};

		AlrightManager.addManagerStateListener(listener);
		manager.completeGameSetup(false);
		AlrightManager.removeManagerStateListener(listener);
	}
	
	public void testOnLocationChanges() {
		fail("On Location Change");
	}
	
	public void testStartStopMain() {
		AlrightManager manager = AlrightManager.getInstance(this.context);
		
		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				switch(state.stateType) {
					case CONNECTED:
						break;
						
					default:
						fail(String.format("Main Start/Stop: %s", state.toString()));
				}				
			}
		};
		
		manager.startMain(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));

		manager.stopMain(listener, false);
		assertFalse(AlrightManager.addManagerStateListener(listener));
		
		// clean up
		assertTrue(AlrightManager.removeManagerStateListener(listener));
	} 

	public void testStartStopMainFinishing() {
		AlrightManager manager = AlrightManager.getInstance(this.context);
		
		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				switch(state.stateType) {
					case CONNECTED:
					case DISCONNECTED:
						break;
						
					default:
						fail(String.format("Main Start/Stop Finishing: %s", state.toString()));
				}
			}
		};
		
		manager.startMain(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));

		manager.stopMain(listener, true);
		assertFalse(AlrightManager.removeManagerStateListener(listener));
	} 
	
	public void testStartStopHelp() {
		AlrightManager manager = AlrightManager.getInstance(this.context);
		
		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				fail(String.format("Start/Stop Help should not listen to manager state changes for: %s", state.toString()));
			}
		};
		
		manager.startHelp(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));

		manager.stopHelp(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));
		
		// clean up
		assertTrue(AlrightManager.removeManagerStateListener(listener));
	}
	
	public void testStartStopGameSetup() {
		AlrightManager manager = AlrightManager.getInstance(this.context);
		
		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				switch(state.stateType) {
					case MY_LOCATION:
					case DESTINATION_CHANGED:
						break;
						
					default:
						fail(String.format("Start/Stop Game Setup should not listen to manager state changes for: %s", state.toString()));
				}
			}
		};
		
		manager.startHelp(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));

		// Simulate selection of destination
		manager.setMyDestination(GRANT_PARK_ADDRESS);
		
		manager.stopHelp(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));
		
		// clean up
		assertTrue(AlrightManager.removeManagerStateListener(listener));
	}
	
	public void testStartStopTracker() {
		AlrightManager manager = AlrightManager.getInstance(this.context);
		
		ManagerStateListener listener = new ManagerStateListener() {	
			@Override
			public void onManagerStateChanged(ManagerState state) {
				switch(state.stateType) {
					case MY_LOCATION:
					case GAME_OVER_LOSER:
					case GAME_OVER_WINNER:
					case STILL_ON_TRACK:
						break;
						
					default:
						fail(String.format("Start/Stop Tracker should not listen to manager state changes for: %s", state.toString()));
				}
			}
		};

		// Simulate selection of destination
		manager.setMyDestination(GRANT_PARK_ADDRESS);

		manager.startTracker(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));

		// Simulate location changes by setting bearing 
		// and calling onLocationChanged 
		//manager.onLocationChanged(location);
		//manager.onLocationChanged(location);
		//manager.onLocationChanged(location);
		//manager.onLocationChanged(location);
		
		manager.stopTracker(listener);
		assertFalse(AlrightManager.addManagerStateListener(listener));
		
		// clean up
		assertTrue(AlrightManager.removeManagerStateListener(listener));
		
	}
	
	public void testQuestion() {
		// NOTE: The main activity is always listening for manager state changes. The
		// question is should we always test the following scenarios:
		//
		// Main Start, Help Start, Main Stop(false), Help Stop
		// Main Start, Game Setup Start, Main Stop(false), Game Setup Stop
		// Main Start, Tracker Start, Main Stop(false), Tracker Stop
		fail("Test Question");
	}
}
