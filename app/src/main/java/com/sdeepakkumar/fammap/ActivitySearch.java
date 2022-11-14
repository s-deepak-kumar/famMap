package com.sdeepakkumar.fammap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.chip.Chip;
import com.sdeepakkumar.fammap.databinding.ActivityMainBinding;
import com.sdeepakkumar.fammap.databinding.ActivitySearchBinding;
import com.sdeepakkumar.fammap.sqlitedatabase.SQLiteDB;
import com.sdeepakkumar.flowlayoutmanager.Alignment;
import com.sdeepakkumar.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class ActivitySearch extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private AdapterPlace mAdapterPlace;
    private List<Place> mListPlace;

    private static final int REQUEST_CODE_VOICE_TO_TEXT = 1;

    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private AutocompleteSessionToken token;

    private List<String> mListRecentSearches;
    private AdapterString mAdapterRecentSearch, mAdapterSearchResult;

    private FlowLayoutManager mFlowLayoutManagerRecentSearches;
    private FlowLayoutManager mFlowLayoutManagerSearchResults;

    private RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(10, 10, 10, 10);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        token = AutocompleteSessionToken.newInstance();

        mListPlace = new ArrayList<>();

        mAdapterPlace = new AdapterPlace(ActivitySearch.this, Constants.places);

        binding.placesRecyclerview.setHasFixedSize(true);
        binding.placesRecyclerview.setItemAnimator(new DefaultItemAnimator());
        binding.placesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL));
        binding.placesRecyclerview.setAdapter(mAdapterPlace);
        binding.placesRecyclerview.setNestedScrollingEnabled(false);

        int id = binding.searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView searchText = (TextView) binding.searchView.findViewById(id);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(),"capriola_regular.ttf");
        searchText.setTextSize(15);
        searchText.setTypeface(myCustomFont);
        searchText.setHintTextColor(getResources().getColor(R.color.gray));
        searchText.setTextColor(getResources().getColor(R.color.green));

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.equals("")) {

                } else {
                    searchItem(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        mListRecentSearches = new ArrayList<>();

        mFlowLayoutManagerRecentSearches = new FlowLayoutManager().setAlignment(Alignment.LEFT);
        mFlowLayoutManagerRecentSearches.setAutoMeasureEnabled(true);

        mFlowLayoutManagerSearchResults = new FlowLayoutManager().setAlignment(Alignment.LEFT);
        mFlowLayoutManagerSearchResults.setAutoMeasureEnabled(true);

        getRecentSearches();

        binding.backButton.setOnClickListener(view -> onBackPressed());

        binding.voiceButton.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

            try {
                startActivityForResult(intent, REQUEST_CODE_VOICE_TO_TEXT);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(ActivitySearch.this, "Oops! Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecentSearches(String mSearchableString){
        if (mAdapterRecentSearch != null && mListRecentSearches != null){
            if (mListRecentSearches.contains(mSearchableString)){
                mListRecentSearches.remove(mSearchableString);
            }
            if (mListRecentSearches.size() == 11){
                mListRecentSearches.remove(10);
                mListRecentSearches.add(0, mSearchableString);
            }else {
                mListRecentSearches.add(0, mSearchableString);
            }
            mAdapterRecentSearch.notifyDataSetChanged();

            if (binding.recentSearchesLayout.getVisibility() == View.GONE){
                binding.recentSearchesLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getRecentSearches(){
        mListRecentSearches = SQLiteDB.getSearchRecentBriefs(ActivitySearch.this);
        mAdapterRecentSearch = new AdapterString(ActivitySearch.this, mListRecentSearches, R.layout.item_recent_search, new AdapterString.OnItemClickListener() {
            @Override
            public void onItemClick(int positon) {
                updateRecentSearches(mListRecentSearches.get(positon));
                //searchItem(mListRecentSearches.get(positon));
                Intent mIntent = new Intent();
                mIntent.putExtra("searchedLocation", mListRecentSearches.get(positon));
                setResult(MainActivity.RESULT_OK, mIntent);
                finish();
            }
        });

        if (mListRecentSearches.size() > 0){
            binding.recentSearchesLayout.setVisibility(View.VISIBLE);
        }else {
            binding.recentSearchesLayout.setVisibility(View.GONE);
        }

        binding.recentSearchesRecyclerView.setHasFixedSize(true);
        binding.recentSearchesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recentSearchesRecyclerView.setLayoutManager(mFlowLayoutManagerRecentSearches);
        binding.recentSearchesRecyclerView.setAdapter(mAdapterRecentSearch);
        binding.recentSearchesRecyclerView.addItemDecoration(itemDecoration);
        binding.recentSearchesRecyclerView.setNestedScrollingEnabled(false);
    }

    public void searchItem(String mSearchableString) {

        if (mSearchableString.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Search Any Keyword!", Toast.LENGTH_SHORT).show();
        }else {

            FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(mSearchableString)
                    .build();
            placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                    if (task.isSuccessful()) {
                        FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                        if (predictionsResponse != null) {
                            predictionList = predictionsResponse.getAutocompletePredictions();
                            List<String> suggestionsList = new ArrayList<>();
                            for (int i = 0; i < predictionList.size(); i++) {
                                AutocompletePrediction prediction = predictionList.get(i);
                                suggestionsList.add(prediction.getFullText(null).toString());
                            }

                            mAdapterSearchResult = new AdapterString(ActivitySearch.this, suggestionsList, R.layout.item_search_results, new AdapterString.OnItemClickListener() {
                                @Override
                                public void onItemClick(int positon) {
                                    SQLiteDB.addSearchRecent(ActivitySearch.this, suggestionsList.get(positon));
                                    updateRecentSearches(suggestionsList.get(positon));
                                    Intent mIntent = new Intent();
                                    mIntent.putExtra("searchedLocation", suggestionsList.get(positon));
                                    setResult(MainActivity.RESULT_OK, mIntent);
                                    finish();
                                }
                            });

                            if (suggestionsList.size() > 0){
                                binding.searchResultsLayout.setVisibility(View.VISIBLE);
                            }else {
                                binding.searchResultsLayout.setVisibility(View.GONE);
                            }

                            binding.searchResultsRecyclerView.setHasFixedSize(true);
                            binding.searchResultsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            binding.searchResultsRecyclerView.setLayoutManager(mFlowLayoutManagerSearchResults);
                            binding.searchResultsRecyclerView.setAdapter(mAdapterSearchResult);
                            binding.searchResultsRecyclerView.addItemDecoration(itemDecoration);
                            binding.searchResultsRecyclerView.setNestedScrollingEnabled(false);
                        }
                    } else {
                        Log.i("ActivitySearch", "prediction fetching task unsuccessful");
                    }
                }
            });
            //mTabLayout.setVisibility(View.VISIBLE);
        }
    }

    /* When Mic activity close */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_VOICE_TO_TEXT: {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    String yourResult = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
                    //Toast.makeText(this, ""+yourResult, Toast.LENGTH_SHORT).show();
                    binding.searchView.setQuery(yourResult, true);
                    //searchItem(yourResult.toLowerCase());
                }
                break;
            }
        }
    }
}
