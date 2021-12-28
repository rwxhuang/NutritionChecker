package com.nutrition.checker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Adapter adapter;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "DATA";
    public String total = "";
    public String tot = "";

    public Bitmap image;
    public ArrayList<Double> ingredList = new ArrayList<>();
    public ArrayList<Label> arr = new ArrayList<>();
    public ArrayList<Integer> scores = new ArrayList<>();
    public HashMap<String, Bitmap> images = new HashMap<>();

    public Button addItem, clearItem;

    public CartFragment()
    {
    }

    public CartFragment(Bitmap imageBitmap)
    {
        this.image = imageBitmap;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_cart, container, false);

        loadData();
        try {
            loadRecyclerView();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recyclerView = v.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());

        addItem = v.findViewById(R.id.addItemButton);
        clearItem = v.findViewById(R.id.clearButton);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        adapter = new Adapter(arr,getContext(), images);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        initListener();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        addItem.setOnClickListener(v1 ->
        {
            ImageFragment imageFragment = new ImageFragment();
            assert getFragmentManager() != null;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, imageFragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        clearItem.setOnClickListener(v1 ->
        {
            SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            arr.clear();
            adapter.notifyDataSetChanged();
        });
        return v;
    }

    public void loadRecyclerView() throws IOException {
        String name;
        int score = 0;
        String encoded = null;
        assert getArguments() != null;

        name = getArguments().getString("name");
        if(!name.equals("RAGHAVRODERICKARYANSOORAJ"))
        {
            //get ingredient list
            ingredList = (ArrayList<Double>) getArguments().getSerializable("ingredList");

            //get score and add it to arraylist. arraylist is for calculating average.
            score = getArguments().getInt("score");
            scores.add(score);

            //get image from bundle and store it in hashmap with the item name as key
            //image = getArguments().getParcelable("image");
            images = MyApplication.getInstance().getImages();

            //put the data into shared preferences
            total += tot + name + "/" + ingredList.get(0) + "//" + ingredList.get(1) + "///" + ingredList.get(2)
                    + "////" + ingredList.get(3) + "/////" + ingredList.get(4) + "//////" + score + "///////";

            SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(TEXT, total);
            editor.apply();

            //parse data to get relevant info
            int count = 0;
            for (int i = 0; i < total.length() - 6; i++) {
                if (total.substring(i, i + 7).equals("///////"))
                    count++;
            }

            double firstNum;
            double secNum;
            double thirdNum;
            double fourNum;
            double fiveNum;
            int finalScore;
            String name1 = "";
            try {
                for (int i = 0; i < count; i++)
                {
                    name1 = total.substring(0, total.indexOf("/"));
                    firstNum = Double.parseDouble(total.substring(name1.length() + 1, total.indexOf("//")));
                    secNum = Double.parseDouble(total.substring(name1.length() + 1 + (firstNum + "").length() + 2, total.indexOf("///")));
                    thirdNum = Double.parseDouble(total.substring(name1.length() + 1 + (firstNum + "").length() + 2 + (secNum + "").length() + 3, total.indexOf("////")));
                    fourNum = Double.parseDouble(total.substring(name1.length() + 1 + (firstNum + "").length() + 2 + (secNum + "").length() + 3 + (thirdNum + "").length() + 4, total.indexOf("/////")));
                    fiveNum = Double.parseDouble(total.substring(name1.length() + 1 + (firstNum + "").length() + 2 + (secNum + "").length() + 3 + (thirdNum + "").length() + 4 + (fourNum + "").length() + 5, total.indexOf("//////")));
                    finalScore = Integer.parseInt(total.substring(name1.length() + 1 + (firstNum + "").length() + 2 + (secNum + "").length() + 3 + (thirdNum + "").length() + 4 + (fourNum + "").length() + 5 + (fiveNum + "").length() + 6, total.indexOf("///////")));
                    total = total.substring(name1.length() + 1 + (firstNum + "").length() + 2 + (secNum + "").length() + 3 + (thirdNum + "").length() + 4 + (fourNum + "").length() + 5 + (fiveNum + "").length() + 6 + (finalScore +"").length() + 7, total.length());
                    Label label = new Label(name1, firstNum, secNum, thirdNum, fourNum, fiveNum, finalScore);
                    arr.add(label);
                }
            } catch (StringIndexOutOfBoundsException | NumberFormatException e)
            {
                ImageFragment igFragment = new ImageFragment();
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, igFragment);
                ft.commit();
                Log.println(Log.ASSERT, "ERROR", e.getMessage());
                Toast.makeText(getContext(), "Please take a new Picture", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void loadData()
    {
        SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        tot = sp.getString(TEXT, "");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ImageFragment()).commit();
        }
        return super.onOptionsItemSelected(item);
    }
    public void removeFromSharedPref(String str)
    {
        SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences(SHARED_PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String origString = sp.getString(TEXT, "");
        assert origString != null;
        int count = origString.indexOf(str);
        int secCount = origString.indexOf("///////", count);

        String subString = origString.substring(count, secCount+7);
        origString = origString.substring(0, origString.indexOf(subString)) +
                origString.substring(origString.indexOf(subString) +
                        subString.length(),origString.length());

        editor.putString(TEXT, origString);
        editor.apply();
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            String name = arr.remove(viewHolder.getAdapterPosition()).getName();
            removeFromSharedPref(name);
            adapter.notifyDataSetChanged();
        }
    };

    public void initListener()
    {
        adapter.setOnItemClickListener((view, position) -> {
            DisplayFragment dFragment = new DisplayFragment();
            assert getFragmentManager() != null;
            FragmentTransaction ft =  getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, dFragment);
            ft.commit();
        });

    }
}
