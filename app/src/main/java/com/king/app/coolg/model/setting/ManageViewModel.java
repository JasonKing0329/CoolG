package com.king.app.coolg.model.setting;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.view.View;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.conf.AppConfig;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.bean.CheckDownloadBean;
import com.king.app.coolg.model.bean.DownloadDialogBean;
import com.king.app.coolg.model.http.AppHttpClient;
import com.king.app.coolg.model.http.Command;
import com.king.app.coolg.model.http.HttpConstants;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.model.http.bean.request.GdbCheckNewFileBean;
import com.king.app.coolg.model.http.bean.request.GdbRequestMoveBean;
import com.king.app.coolg.model.http.bean.request.VersionRequest;
import com.king.app.coolg.model.http.bean.response.AppCheckBean;
import com.king.app.coolg.model.http.bean.response.GdbMoveResponse;
import com.king.app.coolg.model.http.bean.response.GdbRespBean;
import com.king.app.coolg.model.http.bean.response.UploadResponse;
import com.king.app.coolg.model.http.bean.response.VersionResponse;
import com.king.app.coolg.model.http.normal.BaseResponseFlatMap;
import com.king.app.coolg.model.http.upload.UploadRespGsonClient;
import com.king.app.coolg.model.repository.PropertyRepository;
import com.king.app.coolg.utils.FileUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.entity.FavorRecord;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.FavorStar;
import com.king.app.gdb.data.entity.FavorStarOrder;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.TopStar;
import com.king.app.gdb.data.entity.TopStarCategory;
import com.king.app.gdb.data.entity.VideoCoverPlayOrder;
import com.king.app.gdb.data.entity.VideoCoverStar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 10:25
 */
public class ManageViewModel extends BaseViewModel {

    public ObservableField<String> dbVersionText = new ObservableField<>();

    public MutableLiveData<DownloadDialogBean> imagesObserver = new MutableLiveData<>();
    public MutableLiveData<AppCheckBean> gdbCheckObserver = new MutableLiveData<>();
    public MutableLiveData<Long> readyToDownloadObserver = new MutableLiveData<>();

    public MutableLiveData<Boolean> warningSync = new MutableLiveData<>();
    public MutableLiveData<String> warningUpload = new MutableLiveData<>();
    private LocalData mLocalData;
    
    public ManageViewModel(@NonNull Application application) {
        super(application);
        dbVersionText.set("Local v" + new PropertyRepository().getVersion());
    }

    public void onClickStar(View view) {
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_STAR)
                .flatMap(bean -> parseCheckStarBean(bean))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckDownloadBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(CheckDownloadBean bean) {
                        loadingObserver.setValue(false);
                        if (bean.isHasNew()) {
                            DownloadDialogBean dialogBean = new DownloadDialogBean();
                            dialogBean.setDownloadList(bean.getDownloadList());
                            dialogBean.setExistedList(bean.getRepeatList());
                            dialogBean.setSavePath(bean.getTargetPath());
                            dialogBean.setShowPreview(true);
                            imagesObserver.setValue(dialogBean);
                        }
                        else {
                            messageObserver.setValue("No images found");
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

    private Observable<CheckDownloadBean> parseCheckStarBean(final GdbCheckNewFileBean bean) {
        return Observable.create(e -> {
            List<DownloadItem> repeatList = new ArrayList<>();
            List<DownloadItem> toDownloadList = pickStarToDownload(bean.getStarItems(), repeatList);
            CheckDownloadBean cdb = new CheckDownloadBean();
            cdb.setHasNew(bean.isStarExisted());
            cdb.setDownloadList(toDownloadList);
            cdb.setRepeatList(repeatList);
            cdb.setTargetPath(AppConfig.GDB_IMG_STAR);
            e.onNext(cdb);
        });
    }

    /**
     * 检查已有图片的star，将其过滤掉
     *
     * @param downloadList 服务端提供的下载列表
     * @param existedList  已存在的下载内容，不能为null
     * @return 未存在的下载内容
     */
    public List<DownloadItem> pickStarToDownload(List<DownloadItem> downloadList, List<DownloadItem> existedList) {
        List<DownloadItem> list = new ArrayList<>();
        for (DownloadItem item : downloadList) {
            // name 格式为 XXX.png
            String name = item.getName().substring(0, item.getName().lastIndexOf("."));

            String path;
            // 服务端文件处于一级目录
            if (item.getKey() == null) {
                // 检查本地一级目录是否存在
                path = AppConfig.GDB_IMG_STAR + "/" + name + ".png";
                if (!new File(path).exists()) {
                    // 检查本地二级目录是否存在
                    path = AppConfig.GDB_IMG_STAR + "/" + name + "/" + name + ".png";
                }
            }
            // 服务端文件处于二级目录
            else {
                // 只检查本地二级目录是否存在
                path = AppConfig.GDB_IMG_STAR + "/" + item.getKey() + "/" + name + ".png";
            }

            // 检查本地一级目录是否存在
            if (new File(path).exists()) {
                item.setPath(path);
                existedList.add(item);
            } else {
                list.add(item);
            }
        }
        return list;
    }

    public void onClickRecord(View view) {
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().checkNewFile(Command.TYPE_RECORD)
                .flatMap(bean -> parseCheckRecordBean(bean))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CheckDownloadBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(CheckDownloadBean bean) {
                        loadingObserver.setValue(false);
                        if (bean.isHasNew()) {
                            DownloadDialogBean dialogBean = new DownloadDialogBean();
                            dialogBean.setDownloadList(bean.getDownloadList());
                            dialogBean.setExistedList(bean.getRepeatList());
                            dialogBean.setSavePath(bean.getTargetPath());
                            dialogBean.setShowPreview(true);
                            imagesObserver.setValue(dialogBean);
                        }
                        else {
                            messageObserver.setValue("No images found");
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

    private Observable<CheckDownloadBean> parseCheckRecordBean(final GdbCheckNewFileBean bean) {
        return Observable.create(e -> {
            List<DownloadItem> repeatList = new ArrayList<>();
            List<DownloadItem> toDownloadList = pickRecordToDownload(bean.getRecordItems(), repeatList);
            CheckDownloadBean cdb = new CheckDownloadBean();
            cdb.setHasNew(bean.isRecordExisted());
            cdb.setDownloadList(toDownloadList);
            cdb.setRepeatList(repeatList);
            cdb.setTargetPath(AppConfig.GDB_IMG_RECORD);
            e.onNext(cdb);
        });
    }

    /**
     * 从服务端提供的下载列表中选出已存在的和不存在的
     * @param downloadList 服务端提供的下载列表
     * @param existedList 已存在的下载内容，不能为null
     * @return 未存在的下载内容
     */
    public List<DownloadItem> pickRecordToDownload(List<DownloadItem> downloadList, List<DownloadItem> existedList) {
        List<DownloadItem> list = new ArrayList<>();
        for (DownloadItem item:downloadList) {
            // name 格式为 XXX.png
            String name = item.getName().substring(0, item.getName().lastIndexOf("."));

            String path;
            // 服务端文件处于一级目录
            if (item.getKey() == null) {
                // 检查本地一级目录是否存在
                path = AppConfig.GDB_IMG_RECORD + "/" + name + ".png";
                if (!new File(path).exists()) {
                    // 检查本地二级目录是否存在
                    path = AppConfig.GDB_IMG_RECORD + "/" + name + "/" + name + ".png";
                }
            }
            // 服务端文件处于二级目录
            else {
                // 只检查本地二级目录是否存在
                path = AppConfig.GDB_IMG_RECORD + "/" + item.getKey() + "/" + name + ".png";
            }

            // 检查本地一级目录是否存在
            if (new File(path).exists()) {
                item.setPath(path);
                existedList.add(item);
            } else {
                list.add(item);
            }
        }
        return list;
    }

    public void moveStar() {
        requestServeMoveImages(Command.TYPE_STAR);
    }

    public void moveRecord() {
        requestServeMoveImages(Command.TYPE_RECORD);
    }

    /**
     * 通知服务器移动下载源文件
     *
     * @param type
     */
    public void requestServeMoveImages(String type) {
        loadingObserver.setValue(true);
        GdbRequestMoveBean bean = new GdbRequestMoveBean();
        bean.setType(type);

        AppHttpClient.getInstance().getAppService().requestMoveImages(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GdbMoveResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(GdbMoveResponse bean) {
                        loadingObserver.setValue(false);
                        if (bean.isSuccess()) {
                            messageObserver.setValue("Move success");
                        }
                        else {
                            messageObserver.setValue("Move failed");
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

    public void onCheckServer(View view) {
        loadingObserver.setValue(true);
        AppHttpClient.getInstance().getAppService().isServerOnline()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GdbRespBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(GdbRespBean bean) {
                        loadingObserver.setValue(false);
                        if (bean.isOnline()) {
                            messageObserver.setValue("Connect success");
                        }
                        else {
                            messageObserver.setValue("Server is not online");
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

    public void onCheckDb(View view) {
        loadingObserver.setValue(true);
        String versionName = new PropertyRepository().getVersion();
        AppHttpClient.getInstance().getAppService().checkGdbDatabaseUpdate(Command.TYPE_GDB_DATABASE, versionName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppCheckBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(AppCheckBean gdbRespBean) {
                        loadingObserver.setValue(false);
                        if (gdbRespBean.isGdbDatabaseUpdate()) {
                            gdbCheckObserver.setValue(gdbRespBean);
                        }
                        else {
                            messageObserver.setValue("Database is already updated to the latest version");
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

    public void prepareUpload() {
        loadingObserver.setValue(true);
        VersionRequest request = new VersionRequest();
        request.setType(HttpConstants.UPLOAD_TYPE_DB);
        AppHttpClient.getInstance().getAppService().getVersion(request)
                .flatMap(response -> BaseResponseFlatMap.result(response))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<VersionResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(VersionResponse response) {
                        loadingObserver.setValue(false);
                        // 没有上传过，或服务器版本与本地保存的版本号一致，直接提示将上传覆盖
                        if (response.getVersionCode() == null || response.getVersionCode().equals(SettingProperty.getUploadVersion())) {
                            warningUpload.setValue("本地database将覆盖服务端database，是否继续？");
                        }
                        // 如果服务器版本比本地版本不一致，提醒
                        else {
                            warningUpload.setValue("服务器database版本与本地上一次更新不一致，操作将执行本地database覆盖服务端database，是否继续？");
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

    public void uploadDatabase() {
        loadingObserver.setValue(true);
        //组装partMap对象
        Map<String, RequestBody> partMap = new HashMap<>();

        // gdb database
        File file = new File(AppConfig.GDB_DB_FULL_PATH);
        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        partMap.put("file\"; filename=\""+file.getName(), fileBody);

        UploadRespGsonClient.getInstance().getService().uploadDb(partMap)
                .flatMap(response -> BaseResponseFlatMap.result(response))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<UploadResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(UploadResponse uploadResponse) {
                        loadingObserver.setValue(false);
                        messageObserver.setValue("上传成功");
                        SettingProperty.setUploadVersion(uploadResponse.getTimeStamp());
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

    private Observable<List<StarRating>> getStarRatings() {
        return Observable.create(e -> e.onNext(getDaoSession().getStarRatingDao().loadAll()));
    }

    public DownloadDialogBean getDownloadDatabaseBean(long size, boolean isUploadedDb) {
        DownloadDialogBean bean = new DownloadDialogBean();
        bean.setShowPreview(false);
        bean.setSavePath(AppConfig.APP_DIR_CONF);
        DownloadItem item = new DownloadItem();
        if (isUploadedDb) {
            item.setFlag(Command.TYPE_GDB_DATABASE_UPLOAD);
        }
        else {
            item.setFlag(Command.TYPE_GDB_DATABASE);
        }
        if (size != 0) {
            item.setSize(size);
        }
        item.setName(AppConfig.DB_NAME);
        List<DownloadItem> list = new ArrayList<>();
        list.add(item);
        bean.setDownloadList(list);
        return bean;
    }

    /**
     * 下载更新数据库前将表中的本地内容暂存
     * @param bean
     */
    public void saveDataFromLocal(AppCheckBean bean) {
        loadingObserver.setValue(true);
        saveLocalData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<LocalData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(LocalData data) {
                        loadingObserver.setValue(false);
                        mLocalData = data;
                        readyToDownloadObserver.setValue(bean.getAppSize());
                    }

                    @Override
                    public void onError(Throwable e) {
                        loadingObserver.setValue(false);
                        e.printStackTrace();
                        messageObserver.setValue("Pre-save data failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * save data in memory
     */
    private Observable<LocalData> saveLocalData() {
        return Observable.create(e -> {
            // 将数据库备份至History文件夹
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
            FileUtil.copyFile(new File(AppConfig.APP_DIR_CONF + "/" + AppConfig.DB_NAME)
                    , new File(AppConfig.APP_DIR_DB_HISTORY + "/" + sdf.format(new Date()) + ".db"));

            LocalData data = new LocalData();
            // 保存star的favor字段
            data.favorMap = new HashMap<>();
            StarDao dao = getDaoSession().getStarDao();
            List<Star> list = dao.queryBuilder().build().list();
            for (Star star:list) {
                if (star.getFavor() > 0) {
                    data.favorMap.put(star.getName(), star.getFavor());
                }
            }

            // 保存favor_oder等favor相关列表
            try {
                data.favorRecordList = getDaoSession().getFavorRecordDao().loadAll();
                data.favorRecordOrderList = getDaoSession().getFavorRecordOrderDao().loadAll();
                data.favorStarList = getDaoSession().getFavorStarDao().loadAll();
                data.favorStarOrderList = getDaoSession().getFavorStarOrderDao().loadAll();

                // 保存star_rating表数据
                data.starRatingList = getDaoSession().getStarRatingDao().loadAll();

                // 保存play_xx表数据
                data.playItemList = getDaoSession().getPlayItemDao().loadAll();
                data.playOrderList = getDaoSession().getPlayOrderDao().loadAll();
                data.videoCoverPlayOrders = getDaoSession().getVideoCoverPlayOrderDao().loadAll();
                data.videoCoverStars = getDaoSession().getVideoCoverStarDao().loadAll();

                // 保存star_category, star_category_details表数据
                data.categoryList = getDaoSession().getTopStarCategoryDao().loadAll();
                data.categoryStarList = getDaoSession().getTopStarDao().loadAll();
            } catch (Exception ee) {
                ee.printStackTrace();
            }

            e.onNext(data);
        });
    }

    public void databaseDownloaded(boolean isUploadedDb) {
        loadingObserver.setValue(true);
        updateLocalData(isUploadedDb)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Object object) {
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Update successfully!");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                        messageObserver.setValue("Update failed: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 如果是更新的upload数据库，直接替换后就完成了；下载的是默认database，保存本地的其他表单
     * @param isUploadedDb
     * @return
     */
    private Observable<Object> updateLocalData(boolean isUploadedDb) {
        return Observable.create(e -> {
            new File(AppConfig.GDB_DB_JOURNAL).delete();
            CoolApplication.getInstance().reCreateGreenDao();
            if (!isUploadedDb) {
                updateStarFavorFiled();
                updateFavorTables();
                updateStarRatings();
                updatePlayList();
                updateCategory();
            }
            e.onNext(new Object());
        });
    }

    private void updateCategory() {
        if (!ListUtil.isEmpty(mLocalData.categoryList)) {
            getDaoSession().getTopStarCategoryDao().deleteAll();
            getDaoSession().getTopStarCategoryDao().insertInTx(mLocalData.categoryList);
        }
        if (!ListUtil.isEmpty(mLocalData.categoryStarList)) {
            getDaoSession().getTopStarDao().deleteAll();
            getDaoSession().getTopStarDao().insertInTx(mLocalData.categoryStarList);
        }
    }

    /**
     * update favor to database
     */
    private void updateStarFavorFiled() {
        StarDao dao = getDaoSession().getStarDao();
        List<Star> list = dao.queryBuilder().build().list();
        for (Star star:list) {
            Integer favor = mLocalData.favorMap.get(star.getName());
            if (favor != null && favor > 0) {
                star.setFavor(favor);
                dao.update(star);
            }
        }
    }

    private void updateFavorTables() {
        if (!ListUtil.isEmpty(mLocalData.favorRecordList)) {
            getDaoSession().getFavorRecordDao().deleteAll();
            getDaoSession().getFavorRecordDao().insertInTx(mLocalData.favorRecordList);
        }
        if (!ListUtil.isEmpty(mLocalData.favorRecordOrderList)) {
            getDaoSession().getFavorRecordOrderDao().deleteAll();
            getDaoSession().getFavorRecordOrderDao().insertInTx(mLocalData.favorRecordOrderList);
        }
        if (!ListUtil.isEmpty(mLocalData.favorStarList)) {
            getDaoSession().getFavorStarDao().deleteAll();
            getDaoSession().getFavorStarDao().insertInTx(mLocalData.favorStarList);
        }
        if (!ListUtil.isEmpty(mLocalData.favorStarOrderList)) {
            getDaoSession().getFavorStarOrderDao().deleteAll();
            getDaoSession().getFavorStarOrderDao().insertInTx(mLocalData.favorStarOrderList);
        }
    }

    /**
     * update star_rating
     */
    private void updateStarRatings() {
        if (!ListUtil.isEmpty(mLocalData.starRatingList)) {
            getDaoSession().getStarRatingDao().deleteAll();
            getDaoSession().getStarRatingDao().insertInTx(mLocalData.starRatingList);
        }
    }

    /**
     * update play_order, play_item
     */
    private void updatePlayList() {
        if (!ListUtil.isEmpty(mLocalData.playItemList)) {
            getDaoSession().getPlayItemDao().deleteAll();
            getDaoSession().getPlayItemDao().insertInTx(mLocalData.playItemList);
        }
        if (!ListUtil.isEmpty(mLocalData.playOrderList)) {
            // 由于onUpgrade执行了insertTempPlayOrder，所以这里要防重复
            for (int i = 0; i < mLocalData.playOrderList.size(); i ++) {
                if (mLocalData.playOrderList.get(i).getId() == AppConstants.PLAY_ORDER_TEMP_ID) {
                    mLocalData.playOrderList.remove(i);
                    break;
                }
            }
            if (!ListUtil.isEmpty(mLocalData.playOrderList)) {
                getDaoSession().getPlayOrderDao().deleteAll();
                getDaoSession().getPlayOrderDao().insertInTx(mLocalData.playOrderList);
            }
        }
        if (!ListUtil.isEmpty(mLocalData.videoCoverPlayOrders)) {
            getDaoSession().getVideoCoverPlayOrderDao().deleteAll();
            getDaoSession().getVideoCoverPlayOrderDao().insertInTx(mLocalData.videoCoverPlayOrders);
        }
        if (!ListUtil.isEmpty(mLocalData.videoCoverStars)) {
            getDaoSession().getVideoCoverStarDao().deleteAll();
            getDaoSession().getVideoCoverStarDao().insertInTx(mLocalData.videoCoverStars);
        }
    }

    private ObservableSource<Boolean> isDifferentVersion(String serverVersion) {
        return observer -> {
            boolean result = false;
            String localVersion = SettingProperty.getUploadVersion();
            if (serverVersion != null) {
                result = !localVersion.equals(serverVersion);
            }
            observer.onNext(result);
        };
    }

    public void checkSyncVersion() {
        loadingObserver.setValue(true);
        VersionRequest request = new VersionRequest();
        request.setType(HttpConstants.UPLOAD_TYPE_DB);
        AppHttpClient.getInstance().getAppService().getVersion(request)
                .flatMap(response -> BaseResponseFlatMap.result(response))
                .flatMap(response -> isDifferentVersion(response.getVersionCode()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(Boolean isHigher) {
                        loadingObserver.setValue(false);
                        if (isHigher) {
                            warningSync.setValue(true);
                        }
                        else {
                            messageObserver.setValue("The local database is already the latest version");
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

    private class LocalData {
        Map<String, Integer> favorMap = new HashMap<>();

        List<FavorRecord> favorRecordList;

        List<FavorStar> favorStarList;

        List<FavorRecordOrder> favorRecordOrderList;

        List<FavorStarOrder> favorStarOrderList;

        List<StarRating> starRatingList;

        List<PlayOrder> playOrderList;

        List<PlayItem> playItemList;

        List<VideoCoverPlayOrder> videoCoverPlayOrders;

        List<VideoCoverStar> videoCoverStars;

        List<TopStarCategory> categoryList;

        List<TopStar> categoryStarList;
    }
}
