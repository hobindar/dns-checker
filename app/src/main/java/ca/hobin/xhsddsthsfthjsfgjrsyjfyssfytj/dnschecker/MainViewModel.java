package ca.hobin.xhsddsthsfthjsfgjrsyjfyssfytj.dnschecker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private MutableLiveData<Integer> hasFailed;

    private static MainViewModel mainViewModel;

    static MainViewModel getViewModel() {
        if (mainViewModel == null) {
            mainViewModel = new MainViewModel();
        }
        return mainViewModel;
    }

    public LiveData<Integer> getFailed() {
        if (hasFailed == null) {
            hasFailed = new MutableLiveData<>();
        }
        return hasFailed;
    }

    public void setFailed(int isFailed) {
        if (hasFailed == null) {
            hasFailed = new MutableLiveData<>();
        }
        hasFailed.postValue(isFailed);
    }

}
