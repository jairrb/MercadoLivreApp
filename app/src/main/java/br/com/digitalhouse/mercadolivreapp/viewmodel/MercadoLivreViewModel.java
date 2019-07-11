package br.com.digitalhouse.mercadolivreapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.digitalhouse.mercadolivreapp.data.database.DatabaseRoom;
import br.com.digitalhouse.mercadolivreapp.data.database.dao.ResultsDao;
import br.com.digitalhouse.mercadolivreapp.model.MercadoLivreResponse;
import br.com.digitalhouse.mercadolivreapp.model.Result;
import br.com.digitalhouse.mercadolivreapp.repository.MercadoLivreRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static br.com.digitalhouse.mercadolivreapp.util.AppUtil.isNetworkConnected;

public class MercadoLivreViewModel extends AndroidViewModel {

    private MutableLiveData<List<Result>> resultLiveData = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MercadoLivreRepository repository = new MercadoLivreRepository();

    public MercadoLivreViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Result>> getResultLiveData() {
        return resultLiveData;
    }

    // Ao buscar o item verificamos se estamos conectados ou não
    public void searchItem(String item, int pagina, int limite) {
        if (isNetworkConnected(getApplication())) {
            getFromNetwork(item,pagina,limite);
        } else {
            getFromLocal();
        }
    }

    private void getFromLocal() {
        // Adicionamos a chamada a um disposible para podermos eliminar o disposable da destruição do viewmodel
        disposable.add(
                repository.getLocalResults(getApplication().getApplicationContext())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(subscription -> {
                            //Setar livedata para apresentar loading
                        })
                        .doAfterTerminate(() -> {
                            //Setar livedata para esconder loading
                        })
                        .subscribe(results -> {
                            resultLiveData.setValue(results);
                        }, throwable -> {
                            //Setar livedata para error
                        })

        );
    }

    private void getFromNetwork(String item, int pagina, int limite) {

// Adicionamos a chamada a um disposible para podermos eliminar o disposable da destruição do viewmodel
        disposable.add(
                repository.searchItems(item,pagina,limite)
                        .subscribeOn(Schedulers.newThread())
                        .map(mercadoLivreResponse -> saveItems(mercadoLivreResponse))
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(subscription -> {
                            //Setar livedata para apresentar loading
                        })
                        .doAfterTerminate(() -> {
                            //Setar livedata para esconder loading
                        })
                        .subscribe(mercadoLivreResponse -> {
                            resultLiveData.setValue(mercadoLivreResponse.getResults());
                        }, throwable -> {
                            //Setar livedata para error
                        })

        );

    }

    private MercadoLivreResponse saveItems(MercadoLivreResponse mercadoLivreResponse) {
        ResultsDao resultsDao = DatabaseRoom.getDatabase(getApplication()
                .getApplicationContext())
                .resultsDAO();
        resultsDao.deleteAll();
        resultsDao.insert(mercadoLivreResponse.getResults());
        return mercadoLivreResponse;
    }

    // Limpa as chamadas que fizemos no RX
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
