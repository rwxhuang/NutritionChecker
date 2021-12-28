package com.nutrition.checker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageFragment extends Fragment
{
    public ImageView selectedImage;
    public Button cameraButton, discardButton, addButton;
    public TextView ingredText;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public Bitmap imageBitmap;
    public String ingred = "";
    public EditText imageName;
    public List<Double> ingredList = new ArrayList<>();
    public double healthinessScore;
    public int finalScore;
    private String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_image, container, false);

        selectedImage = v.findViewById(R.id.imageView);
        v.findViewById(R.id.camButton).setOnClickListener(view -> {
            ingredText.setText("");
            String fileName = "photo";
            File storageDirectory = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try{
                File imageFile = File.createTempFile(fileName, ".jpg", storageDirectory);
                currentPhotoPath = imageFile.getAbsolutePath();

                Uri imageUri = FileProvider.getUriForFile(getContext(), "com.nutrition.checker.fileprovider", imageFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.addFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION );
                startActivityForResult(intent, 1);
            } catch(IOException e){
                e.printStackTrace();
            }
        });
        discardButton = v.findViewById(R.id.disButton);
        addButton = v.findViewById(R.id.addButton);
        ingredText = v.findViewById(R.id.ingredText);

        addButton.setOnClickListener((View.OnClickListener) v12 ->
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    Objects.requireNonNull(getContext()));
            View mView = getLayoutInflater().inflate(R.layout.alertdialog, null);
            alertDialogBuilder.setView(mView);

            imageName = (EditText) mView.findViewById(R.id.edit_imagename);

            alertDialogBuilder.setTitle("Save Image");
            alertDialogBuilder
                    .setMessage("Please name your food item")
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, id) -> {
                        dialog.cancel();


                        String imageNameText = imageName.getText().toString();
                        CartFragment cartFragment = new CartFragment();
                        Bundle args = new Bundle();
                        args.putSerializable("ingredList", (Serializable) ingredList);

                        MyApplication.getInstance().addImage(imageNameText, imageBitmap);
                        //args.putParcelable("image", imageBitmap);
                        args.putString("name", imageNameText);
                        args.putInt("score", finalScore);
                        cartFragment.setArguments(args);

                        assert getFragmentManager() != null;
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_container, cartFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    })
                    .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        return v;
    }

    private void detectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(firebaseVisionText -> {
            try {
                displayTextFromImage(firebaseVisionText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Error", ""+e.getMessage());
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) throws Exception
    {
        ingred = "";
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size() == 0)
        {
            Toast.makeText(getContext(), "No Text Found in Image", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(FirebaseVisionText.Block block: firebaseVisionText.getBlocks())
            {
                String text = block.getText();
                ingred+=text.trim().toUpperCase();
            }
            processText(ingred);
            ingred = "";
        }
    }

    private void processText(String str) throws Exception
    {
        str = str.trim();
        str = str.toUpperCase();
        str = str.replace("O", "0");
        str = str.replace(" ", "");
        str = str.replace("\n", "");
        str = str.replace("L", "1");
        str = str.replace("I", "1");

        double carb = 0;
        double satFat = 0;
        double transFat = 0;
        double sodium = 0;
        double addedSugar = 0;
        double cholesterol = 0;

        try
        {
            if (str.contains("CARB0HYDRATE")) {
                int i = str.indexOf("CARB0HYDRATE")+12;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                carb = Double.parseDouble(str.substring(str.indexOf("CARB0HYDRATE")+12, str.indexOf(letter, i)));
            }
            if (str.contains("SATURATEDFAT")) {
                int i = str.indexOf("SATURATEDFAT")+12;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                satFat = Double.parseDouble(str.substring(str.indexOf("SATURATEDFAT")+12, str.indexOf(letter, str.indexOf(letter, i))));
            }
            if (str.contains("TRANSFAT")) {
                int i = str.indexOf("TRANSFAT")+8;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                transFat = Double.parseDouble(str.substring(str.indexOf("TRANSFAT")+8, str.indexOf(letter, i)));
            }
            if (str.contains("S0D1UM")) {
                int i = str.indexOf("S0D1UM")+6;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                sodium = Double.parseDouble(str.substring(str.indexOf("S0D1UM")+6, str.indexOf(letter, i)));
            }
            if (str.contains("ADDEDSUGARS")) {
                int i = str.indexOf("1NC1UDES")+8;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                //System.out.println(str.substring(str.indexOf("1NC1UDES")+8, str.indexOf(letter, i)));
                addedSugar = Double.parseDouble(str.substring(str.indexOf("1NC1UDES")+8, str.indexOf(letter, i)));
            }
            if (str.contains("CH01ESTER01")) {
                int i = str.indexOf("CH01ESTER01")+11;
                while(!((int)(str.charAt(i)) >= 65 && (int)(str.charAt(i)) <= 90) && i < str.length())
                    i++;
                String letter = "" + str.charAt(i);
                cholesterol = Double.parseDouble(str.substring(str.indexOf("CH01ESTER01")+11, str.indexOf(letter, i)));
            }
        }catch (NumberFormatException e)
        {
            Toast.makeText(getContext(), "Text could not be properly read. Please take another picture.", Toast.LENGTH_SHORT).show();
        }
        ingredList.add(carb);
        ingredList.add(satFat);
        ingredList.add(transFat);
        ingredList.add(sodium);
        ingredList.add(addedSugar);
        ingredList.add(cholesterol);

        healthinessScore = (5-((sodium/188)-1)*5)/10;
        healthinessScore = (5-((cholesterol/22)-1)*5)/10;
        healthinessScore = (5-((addedSugar/30)-1)*5)/10;
        healthinessScore*=0.9;
        healthinessScore = (5-((satFat/3)-1)*5)/10;

        if(healthinessScore <= 0.1) finalScore = 1;
        else if(healthinessScore <= 0.2) finalScore = 2;
        else if(healthinessScore <= 0.3) finalScore = 3;
        else if(healthinessScore <= 0.4) finalScore = 4;
        else if(healthinessScore <= 0.5) finalScore = 5;
        else if(healthinessScore <= 0.6) finalScore = 6;
        else if(healthinessScore <= 0.7) finalScore = 7;
        else if(healthinessScore <= 0.8) finalScore = 8;
        else if(healthinessScore <= 0.9) finalScore = 9;


        File file = new File("C:\\Users\\ragha\\Documents\\AndroidStudioProjects\\NutritionChecker\\app\\src\\main\\java\\com\\nutrition\\checker\\Test.csv");
        callModel(file);
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);

            ImageView imageView = selectedImage;
            imageView.setImageBitmap(imageBitmap);
            detectTextFromImage();
        }
    }

    public void callModel(File file) throws Exception
    {
        Log.println(Log.ASSERT, "I WAS HERE", "HELLO");
        List<Integer> y = Arrays.asList(2, 3, 7, 1, 6, 4, 0, 5, 8, 9);
        MultiLayerNetwork m = ModelSerializer.restoreMultiLayerNetwork("C:\\Users\\ragha\\Documents\\AndroidStudioProjects\\NutritionChecker\\app\\model");
        m.init();

        RecordReader r = new CSVRecordReader(',');
        r.initialize(new FileSplit(file));
        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(r, 1, 0, 10);

        INDArray x = m.output(dataSetIterator);
        double [] vals = new double[10];
        for(int i = 0; i < 9; i++){
            vals[i]=x.getRow(0).getDouble(i);
        }
        int max = 0;
        for(int i = 0; i<9; i++){
            if(vals[i] >= vals[max]){
                max = i;
            }
        }
        int score = y.get(max)+1;

        Log.println(Log.ASSERT, "NUMBER", ""+score);
    }
}