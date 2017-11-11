package com.example.akashb.rxpagination;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;

/**
 * Created by Akash on 11/11/17.
 */
public class MainActivity extends AppCompatActivity {

    private final int VISIBLE_THRESHOLD = 1;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    private CompositeDisposable mDisposable;
    private PublishProcessor<Integer> mPaginator;
    private RxPagingAdapter mAdapter;
    private boolean isLoading = false;
    private int pageNumber = 1;
    private int lastVisibleItem, totalItemCount;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mDisposable = new CompositeDisposable();
        mPaginator = PublishProcessor.create();
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RxPagingAdapter();
        recyclerView.setAdapter(mAdapter);

        // register scroll listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager
                        .findLastVisibleItemPosition();
                if (!isLoading
                        && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    pageNumber++;
                    mPaginator.onNext(pageNumber);
                    isLoading = true;
                }
            }
        });
        subscribeApi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mDisposable.isDisposed())
            mDisposable.dispose();
    }

    /**
     * Adding to disposable.
     */
    private void subscribeApi() {

        mDisposable.add(mPaginator
                .onBackpressureDrop()
                .concatMap(new Function<Integer, Publisher<List<String>>>() {
                    @Override
                    public Publisher<List<String>> apply(@NonNull Integer page) throws Exception {
                        isLoading = true;
                        progressBar.setVisibility(View.VISIBLE);
                        return apiResponse(page);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(@NonNull List<String> items) throws Exception {
                        mAdapter.addItems(items);
                        mAdapter.notifyDataSetChanged();
                        isLoading = false;
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }));

        mPaginator.onNext(pageNumber);

    }

    /**
     * This is just sample.
     * You can call api with retrofit interface method which returns Flowable and there you go.
     */
    private Flowable<List<String>> apiResponse(final int page) {
        return Flowable.just(true)
                .delay(3, TimeUnit.SECONDS)
                .map(new Function<Boolean, List<String>>() {
                    @Override
                    public List<String> apply(@NonNull Boolean value) throws Exception {
                        List<String> items = new ArrayList<>();
                        for (int i = 1; i <= 10; i++) {
                            items.add("Item " + (page * 10 + i));
                        }
                        return items;
                    }
                });
    }
}
