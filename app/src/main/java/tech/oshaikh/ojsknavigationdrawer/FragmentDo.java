package tech.oshaikh.ojsknavigationdrawer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;

import java.util.ArrayList;


public class FragmentDo extends Fragment
        implements MeetupListItemAdapter.ListItemClickListener,
                MeetupDataFetcher.QueryDataInterface {


    private ArrayList<String> meetupList;
    private ArrayList<String> urlList;
    private RecyclerView.Adapter listAdapter;
    private AutoCompleteTextView categoryText;
    private ProgressBar searchProgress;
    private String category = "";

    private Context _context;


    public FragmentDo() {
        // empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        meetupList = new ArrayList<>();
        urlList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_do, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _context = this.getContext();
        RecyclerView listView = (RecyclerView) view.findViewById(R.id.meetUpListView);

        listView.setHasFixedSize(true);
        //Sets the view vertically
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        categoryText = (AutoCompleteTextView) view.findViewById(R.id.category_text);
        categoryText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                populateMenuList();
                                            }
                                        }
        );

        searchProgress = (ProgressBar) view.findViewById(R.id.searchProgress);
        searchProgress.setVisibility(View.GONE);

        listAdapter = new MeetupListItemAdapter(meetupList, urlList,this, _context);
        listView.setAdapter(listAdapter);
        showSoftKeyboard(this.getActivity());

    }

    public void populateMenuList() {
        //Querying meet up
        String prevString = category;
        category = categoryText.getText().toString();

        //If its nothing or the same topic, dont do anything
        if (category.length() == 0 || prevString.equals(category))
            return;

        searchProgress.setVisibility(View.VISIBLE);
        meetupList.clear();
        urlList.clear();
        MeetupDataFetcher md = new MeetupDataFetcher(meetupList, urlList, this, category);
        md.execute();
    }
    @Override
    public void onItemClicked(int position) {
        Log.d("List Item Clicked", "Meetup");
        //CardView card = new CardView();
    }

    //For the interface after aync task
    @Override
    public void finishedParsingResults() {
        searchProgress.setVisibility(View.GONE);
        listAdapter.notifyDataSetChanged();
        hideSoftKeyboard(this.getActivity());
    }

    @Override
    public void updateProgressBar(int p) { searchProgress.setProgress(p); }

    //Hide the keyboard when necessary
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    //Show the keyboard when neccessary
    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
    }
}
