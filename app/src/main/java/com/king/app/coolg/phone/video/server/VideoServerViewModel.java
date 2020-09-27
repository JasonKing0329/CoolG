package com.king.app.coolg.phone.video.server;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.HttpConstants;
import com.king.app.coolg.model.http.bean.data.FileBean;
import com.king.app.coolg.model.http.bean.request.FolderRequest;
import com.king.app.coolg.model.http.bean.request.PathRequest;
import com.king.app.coolg.model.http.bean.response.OpenFileResponse;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.player.PlayListInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
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

    private int mSortType;

    public VideoServerViewModel(@NonNull Application application) {
        super(application);
        mFolderStack = new Stack<>();
        mSortType = SettingProperty.getVideoServerSortType();
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
                .flatMap(list -> sortFiles(list))
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
        if (mFolderStack.empty() || getCurrentFolder() == null) {
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

    private ObservableSource<List<FileBean>> sortFiles(List<FileBean> list) {
        return observer -> {
            switch (mSortType) {
                case PreferenceValue.VIDEO_SERVER_SORT_DATE:
                    Collections.sort(list, new DateComparator());
                    break;
                case PreferenceValue.VIDEO_SERVER_SORT_SIZE:
                    Collections.sort(list, new SizeComparator());
                    break;
                case PreferenceValue.VIDEO_SERVER_SORT_NAME:
                default:
                    Collections.sort(list, new NameComparator());
                    break;
            }
            observer.onNext(list);
        };
    }

    public void onFilterChanged(String text) {
        mFilterText = text;
        filterFiles()
                .flatMap(list -> sortFiles(list))
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

    public void openFile(FileBean bean) {
        PathRequest request = new PathRequest();
        request.setPath(bean.getPath());
        openServerFile(request);
    }

    private void openServerFile(PathRequest request) {
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().openFileOnServer(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<OpenFileResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(OpenFileResponse response) {
                        loadingObserver.setValue(false);
                        if (response.isSuccess()) {
                            messageObserver.setValue("打开成功");
                        }
                        else {
                            messageObserver.setValue(response.getErrorMessage());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void onSortTypeChanged(int sortType) {
        mSortType = sortType;
        SettingProperty.setVideoServerSortType(sortType);
        onFilterChanged(mFilterText);
    }

    public void createPlayList(String url) {
        PlayListInstance.getInstance().addUrl(url);
    }

    private class NameComparator implements Comparator<FileBean> {

        @Override
        public int compare(FileBean o1, FileBean o2) {
            String name1 = o1.getName() == null ? "":o1.getName();
            String name2 = o2.getName() == null ? "":o2.getName();
            return name1.compareTo(name2);
        }
    }

    private class DateComparator implements Comparator<FileBean> {

        @Override
        public int compare(FileBean o1, FileBean o2) {
            long result = o2.getLastModifyTime() - o1.getLastModifyTime();
            if (result > 0) {
                return 1;
            }
            else if (result < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

    private class SizeComparator implements Comparator<FileBean> {

        @Override
        public int compare(FileBean o1, FileBean o2) {
            long result = o2.getSize() - o1.getSize();
            if (result > 0) {
                return 1;
            }
            else if (result < 0) {
                return -1;
            }
            else {
                return 0;
            }
        }
    }

}
