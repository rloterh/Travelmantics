package com.loterh.robert.travelmantics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase rFirebaseDatabase;
    private DatabaseReference rDatabaseReference;
private static final int PICTURE_RESULT = 42;
    EditText txtTitle;
    EditText txtDescription;
    EditText txtPrice;
    TravelDeal deal;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        //FirebaseUtil.openFbReference("traveldeals", this);
        rFirebaseDatabase = FirebaseUtil.rFirebaseDatabase;
        rDatabaseReference = FirebaseUtil.rDatabaseReference;
        txtTitle = (EditText)findViewById(R.id.txt_title);
        txtDescription = (EditText)findViewById(R.id.txt_description);
        txtPrice = (EditText) findViewById(R.id.txt_price);
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal==null){
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String[] mimeTypes = {"image/jpeg","image/png", "image/jpg"};
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICTURE_RESULT && requestCode == RESULT_OK){
            Uri imageUri = data.getData();
            StorageReference ref = FirebaseUtil.rStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri);
        }
    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if(deal.getId()==null){
            rDatabaseReference.push().setValue(deal);
        }
        else {
            rDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }
    private void deleteDeal(){
        if(deal==null){
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        rDatabaseReference.child(deal.getId()).removeValue();
    }
    private void backToList() {
        Intent intent =new Intent(this, ListActivity.class);
        startActivity(intent);
    }
    private  void clean(){
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }
    private void enableEditTexts(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditTexts(false);
        }

        return true;
    }
}
