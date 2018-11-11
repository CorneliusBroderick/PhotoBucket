package ie.ul.cbroderick.photobucket;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class PhotoBucketDetailActivity extends AppCompatActivity {

    private DocumentReference mDocRef;
    private DocumentSnapshot mDocSnapshot;

    private TextView mQuoteTextView;
    private TextView mMovieTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_bucket_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mQuoteTextView = findViewById(R.id.detail_quote);
        mMovieTextView = findViewById(R.id.detail_movie);

        String docId = getIntent().getStringExtra(Constants.EXTRA_DOC_ID);

        //mQuoteTextView.setText(docId);
        mDocRef = FirebaseFirestore.getInstance()
                .collection(Constants.COLLECTION_PATH).document(docId);
        mDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(Constants.TAG, "Listen failed");
                    return;
                }
                if (documentSnapshot.exists()){
                    mDocSnapshot = documentSnapshot;
                    mQuoteTextView.setText((String)documentSnapshot.get(Constants.KEY_QUOTE));
                    mMovieTextView.setText((String)documentSnapshot.get(Constants.KEY_MOVIE));
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();

            }
        });
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.photobucket_dialog, null, false );
        builder.setView(view);
        builder.setTitle("Edit this quote");
        final TextView quoteEditText = view.findViewById(R.id.dialog_quote_edittext);
        final TextView movieEditText = view.findViewById(R.id.dialog_movie_edittext);

        quoteEditText.setText((String)mDocSnapshot.get(Constants.KEY_QUOTE));
        movieEditText.setText((String)mDocSnapshot.get(Constants.KEY_MOVIE));


        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String, Object> mq = new HashMap<>();

                mq.put(Constants.KEY_QUOTE, quoteEditText.getText().toString());
                mq.put(Constants.KEY_MOVIE, movieEditText.getText().toString());
                mq.put(Constants.KEY_CREATED, new Date());
                mDocRef.update(mq);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);


        builder.create().show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_delete:
                mDocRef.delete();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
