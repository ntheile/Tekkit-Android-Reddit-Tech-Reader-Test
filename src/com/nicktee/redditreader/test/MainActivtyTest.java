package com.nicktee.redditreader.test;

//import static org.fest.assertions.api.ANDROID.assertThat;
//import static org.fest.assertions.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.ViewAsserts;
import android.widget.Button;
import android.widget.ListView;
import com.nicktee.redditreader.MainActivity_;
import com.nicktee.redditreader.R;
import com.nicktee.redditreader.models.Prefs;
import com.nicktee.redditreader.models.Reddit;

public class MainActivtyTest extends
		ActivityInstrumentationTestCase2<MainActivity_> {

	// View variables
	private MainActivity_ activity;
	private ListView listViewToDo;
	private Button loadMore;
	

	// Constructor, set the Activity you are testing here
	public MainActivtyTest() {
		super("com.nicktee.redditreader", MainActivity_.class);
	}

	// jUnit setUp method that is called on test Setup
	protected void setUp() {
		try {
			super.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.setActivityInitialTouchMode(false);
		activity = this.getActivity();
		listViewToDo = (ListView) activity.findViewById(R.id.listViewToDo);
		loadMore = (Button) activity.findViewById(R.id.btnLoadMore);
		

	}

	public void testPreConditions() {
		assertNotNull(getActivity());
		assertNotNull(this.listViewToDo);
	}

	// Test is our views exist on the screen
	public void testViewsVisible() {
		ViewAsserts.assertOnScreen(listViewToDo.getRootView(), listViewToDo);
	}

	// Test you can click listview
	public void test_WHEN_listViewTodo_is_clicked_IT_SHOULD_return_true() {
		listViewToDo.performClick();
		assertTrue(true);
	}

	public void test_WHEN_a_call_is_made_to_reddit_IT_SHOULD_return_data() {
		RestTemplate restTemplate;
		restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(
				new MappingJacksonHttpMessageConverter());
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(httpHeaders);
		JsonNode json = restTemplate.exchange(
				"http://www.reddit.com/r/android/.json", HttpMethod.GET,
				requestEntity, JsonNode.class).getBody();
		assertTrue(json.size() > 0);
	}

	public void test_WHEN_you_create_a_Reddit_model_it_SHOULD_have_a_settable_url_property() {
		Reddit reddit = new Reddit();
		reddit.setUrl("http://www.google.com");
		assertEquals(reddit.getUrl(), "http://www.google.com");
	}

	@UiThreadTest
	public void test_WHEN_Load_more_is_clicked_the_list_of_reddits_should_contain_more_than_25_items()
			throws InterruptedException {
		Thread.sleep(5000); // first wait 5 seconds for first 25 reddits to load
		loadMore.performClick();
		Thread.sleep(5000); // then wait 5 seconds for the next 25 reddits to
							// load
		assertTrue(activity.reddits.size() > 25);
	}

	public void test_WHEN_a_pref_is_added_or_deleted_it_should_increment_or_decrement_the_count() {
		
		
		List<Prefs> prefs = new ArrayList<Prefs>();

		
		//
		// add iphone subreddit
		//
		Prefs p = new Prefs();
		p.setSelected(false);
		p.setSubReddits("iphone");
		
		try {
			activity.prefsTable.create(p);
			prefs = activity.prefsTable.query(activity.prefsTable.queryBuilder().where()
					.eq("subReddits", "iphone").prepare());
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// there should be one iphone subreddits in the database
		assertTrue(prefs.size() == 1);

		//
		// delete
		//
		try {
			activity.prefsTable.delete(p);

			prefs = activity.prefsTable.query(activity.prefsTable.queryBuilder().where()
					.eq("subReddits", "iphone").prepare());
		} catch (java.sql.SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// there should be one iphone subreddits in the database
		assertTrue(prefs.size() == 0);

	}

}