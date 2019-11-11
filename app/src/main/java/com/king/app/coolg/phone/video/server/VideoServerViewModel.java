package com.king.app.coolg.phone.video.server;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.HttpConstants;
import com.king.app.coolg.model.http.bean.data.FileBean;
import com.king.app.coolg.model.http.bean.request.FolderRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/11/11 9:10
 */
public class VideoServerViewModel extends BaseViewModel {

    public ObservableInt upperVisibility = new ObservableInt(View.GONE);

    public MutableLiveData<List<FileBean>> listObserver = new MutableLiveData<>();

    private List<FileBean> mFolderList;

    private Stack<FileBean> mFolderStack;

    private String mFilterText;

    public VideoServerViewModel(@NonNull Application application) {
        super(application);
        mFolderStack = new Stack<>();
    }

    private FileBean getCurrentFolder() {
        return mFolderStack.peek();
    }

    public void goUpper() {
        FileBean curFolder = mFolderStack.pop();
        FileBean parentFolder = mFolderStack.peek();
        loadItems(false, parentFolder, true, curFolder);
    }

    public void refresh() {
        loadItems(false, getCurrentFolder(), false,null);
    }

    public void loadNewFolder(FileBean folder) {
        loadItems(true, folder, false,null);
    }

    public void loadItems(boolean isNew, FileBean folder, boolean isBack, FileBean popFolder) {
        FolderRequest request = new FolderRequest();
        request.setType(HttpConstants.FOLDER_TYPE_FOLDER);
        if (folder == null) {
            request.setFolder(HttpConstants.FOLDER_TYPE_ALL);
        }
        else {
            request.setFolder(folder.getPath());
        }
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().requestSurf(request)
                .flatMap(response -> {
                    mFolderList = response.getFileList();
                    if (mFolderList == null) {
                        mFolderList = new ArrayList<>();
                    }
                    return filterFiles();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FileBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FileBean> list) {
                        listObserver.setValue(list);
                        loadingObserver.setValue(false);

                        // 进入子目录
                        if (isNew) {
                            mFolderStack.push(folder);
                        }
                        updateUpperVisibility();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                        // 返回上层目录
                        if (isBack) {
                            // 将popFolder重新入栈
                            mFolderStack.push(popFolder);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updateUpperVisibility() {
        if (getCurrentFolder() == null) {
            upperVisibility.set(View.GONE);
        }
        else {
            upperVisibility.set(View.VISIBLE);
        }
    }

    public boolean backFolder() {
        if (getCurrentFolder() == null) {
            return false;
        }
        goUpper();
        return true;
    }

    private Observable<List<FileBean>> filterFiles() {
        return Observable.create(e -> {
            List<FileBean> result = new ArrayList<>();
            for (FileBean bean:mFolderList) {
                if (TextUtils.isEmpty(mFilterText) || mFilterText.trim().length() == 0) {
                    result.add(bean);
                }
                else {
                    if (bean.getName().toLowerCase().contains(mFilterText.toLowerCase())) {
                        result.add(bean);
                    }
                }
            }
            e.onNext(result);
        });
    }

    public void onFilterChanged(String text) {
        mFilterText = text;
        filterFiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FileBean>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<FileBean> list) {
                        listObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void clearFilter() {
        mFilterText = null;
    }
}
