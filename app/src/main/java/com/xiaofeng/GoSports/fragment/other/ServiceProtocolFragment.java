

package com.xiaofeng.GoSports.fragment.other;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.xiaofeng.GoSports.core.BaseFragment;
import com.xiaofeng.GoSports.R;
import com.xiaofeng.GoSports.databinding.FragmentServiceProtocolBinding;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xutil.resource.ResUtils;
import com.xuexiang.xutil.resource.ResourceUtils;

/**
 * 服务协议【本地加载】
 *
 * @author xiaofeng
 * @since 2021/5/18 1:35 AM
 */
@Page
public class ServiceProtocolFragment extends BaseFragment<FragmentServiceProtocolBinding> {

    public static final String KEY_PROTOCOL_TITLE = "key_protocol_title";
    public static final String KEY_IS_IMMERSIVE = "key_is_immersive";
    /**
     * 用户协议asset本地保存路径
     */
    private static final String ACCOUNT_PROTOCOL_ASSET_PATH = "protocol/account_protocol.txt";

    /**
     * 隐私政策asset本地保存路径
     */
    private static final String PRIVACY_PROTOCOL_ASSET_PATH = "protocol/privacy_protocol.txt";

    @AutoWired(name = KEY_PROTOCOL_TITLE)
    String title;
    @AutoWired(name = KEY_IS_IMMERSIVE)
    boolean isImmersive;

    @Override
    protected void initArgs() {
        XRouter.getInstance().inject(this);
    }

    @NonNull
    @Override
    protected FragmentServiceProtocolBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentServiceProtocolBinding.inflate(inflater, container, false);
    }

    @Override
    protected TitleBar initTitle() {
        return super.initTitle().setTitle(title).setImmersive(isImmersive);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {
        if (title.equals(ResUtils.getString(R.string.title_user_protocol))) {
            binding.tvProtocolText.setText(getAccountProtocol());
        } else {
            binding.tvProtocolText.setText(getPrivacyProtocol());
        }
    }

    @MemoryCache("account_protocol")
    private String getAccountProtocol() {
        return ResourceUtils.readStringFromAssert(ACCOUNT_PROTOCOL_ASSET_PATH);
    }

    @MemoryCache("privacy_protocol")
    private String getPrivacyProtocol() {
        return ResourceUtils.readStringFromAssert(PRIVACY_PROTOCOL_ASSET_PATH);
    }

}
