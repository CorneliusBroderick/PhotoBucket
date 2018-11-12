package ie.ul.cbroderick.photobucket;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class PhotoBucketAdapter extends RecyclerView.Adapter<PhotoBucketAdapter.PhotoBucketViewHolder>{

    private List<DocumentSnapshot> mPhotoBucketsSnapshots = new ArrayList<>();

    public PhotoBucketAdapter(){
        CollectionReference photobucketsRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PATH);

        photobucketsRef.orderBy(Constants.KEY_CREATED, Query.Direction.DESCENDING).limit(50)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(Constants.TAG, "Listening failed!");
                    return;
                }
                mPhotoBucketsSnapshots = queryDocumentSnapshots.getDocuments();
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public PhotoBucketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photobucket_itemview, parent, false);
        return new PhotoBucketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoBucketViewHolder photobucketViewHolder, int i) {
        DocumentSnapshot ds = mPhotoBucketsSnapshots.get(i);
        String quote = (String)ds.get(Constants.KEY_QUOTE);
        String movie = (String)ds.get(Constants.KEY_MOVIE);

        photobucketViewHolder.mQuoteTextView.setText(quote);

        //photobucketViewHolder.mMovieTextView.setText(movie);


        //Ion.with()
        //Ion.with(mMovieImageView).load((String)documentSnapshot.get(Constants.KEY_MOVIE));
        //Ion.with(movie).load((String)documentSnapshot.get(Constants.KEY_MOVIE));
        Ion.with(photobucketViewHolder.mMovieImageView).load(movie);

    }

    @Override
    public int getItemCount() {
        return mPhotoBucketsSnapshots.size();
    }

    class PhotoBucketViewHolder extends RecyclerView.ViewHolder{
        private TextView mQuoteTextView;
        private ImageView mMovieImageView;

        public PhotoBucketViewHolder(@NonNull View itemView) {
            super(itemView);
            mQuoteTextView = itemView.findViewById(R.id.itemview_quote);
            mMovieImageView = itemView.findViewById(R.id.itemview_movie);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DocumentSnapshot ds = mPhotoBucketsSnapshots.get(getAdapterPosition());

                    Context c = view.getContext();
                    Intent intent = new Intent(c, PhotoBucketDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_DOC_ID, ds.getId());
                    c.startActivity(intent);
                }
            });
        }
    }
}
