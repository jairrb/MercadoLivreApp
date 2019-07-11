package br.com.digitalhouse.mercadolivreapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.digitalhouse.mercadolivreapp.adapters.RecyclerViewMercadoLivreAdapter;
import br.com.digitalhouse.mercadolivreapp.model.Result;
import br.com.digitalhouse.mercadolivreapp.viewmodel.MercadoLivreViewModel;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView editTextSearch;
    private RecyclerViewMercadoLivreAdapter adapter;
    private List<Result> results = new ArrayList<>();
    private MercadoLivreViewModel viewModel;

    private int pagina = 0;
    private int limite = 10;
    private String itembusca = "Celular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        viewModel.searchItem(itembusca,pagina,limite);

        viewModel.getResultLiveData().observe(this, results1 -> adapter.update(results1));

        editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                itembusca = query;
                adapter.clear();
                viewModel.searchItem(itembusca,pagina,limite);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 3){
                    itembusca = newText;
                    adapter.clear();
                    viewModel.searchItem(itembusca,pagina,limite);
                }
                return false;
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview);
        editTextSearch = findViewById(R.id.edit_search);
        viewModel = ViewModelProviders.of(this).get(MercadoLivreViewModel.class);
        adapter = new RecyclerViewMercadoLivreAdapter(results);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setScroolListener();
    }

    private void setScroolListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalitemCount = manager.getItemCount();
                int lastVisible = manager.findLastVisibleItemPosition();

                boolean endRecycler = lastVisible + 5 >= totalitemCount;

                if (totalitemCount > 0 && endRecycler){
                    pagina++;
                    viewModel.searchItem(itembusca,pagina,limite);
                }
            }
        });
    }
}
