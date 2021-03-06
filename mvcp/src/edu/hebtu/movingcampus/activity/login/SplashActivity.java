package edu.hebtu.movingcampus.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import edu.hebtu.movingcampus.AppInfo;
import edu.hebtu.movingcampus.R;
import edu.hebtu.movingcampus.activity.MainActivity;
import edu.hebtu.movingcampus.activity.base.BaseActivity;
import edu.hebtu.movingcampus.activity.wrapper.IPreference;
import edu.hebtu.movingcampus.config.Configs;
import edu.hebtu.movingcampus.config.Constants;
import edu.hebtu.movingcampus.entity.User;
import edu.hebtu.movingcampus.utils.LogUtil;
import edu.hebtu.movingcampus.utils.NetWorkHelper;
import edu.hebtu.movingcampus.utils.TimeUtil;

public class SplashActivity extends BaseActivity {

	private String str1; // 用户名
	private String str2; // 密码
	private Handler mHandler = new Handler();
	private TimeUtil timer;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		timer = new TimeUtil(this, Constants.REMEMBER_PASSWORD_TIME);

		View view = View.inflate(this, R.layout.activity_preview, null);
		setContentView(view);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		view.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				LogUtil.i("msg", "动画结束了");
				SharedPreferences localSharedPreferences = SplashActivity.this
						.getSharedPreferences(Constants.PREFER_FILE, 0);
				str1 = localSharedPreferences.getString(Constants.USERNAME, "");
				str2 = localSharedPreferences.getString(Constants.PASSWORD, "");
				// 没有记住密码或者第一次登录
				if (str1 == null || str1.equals("") || str2 == null
						|| str2.equals("")) {
					try {
						if (!NetWorkHelper
								.isMobileDataEnable(SplashActivity.this)
								&& !NetWorkHelper
										.isWifiDataEnable(SplashActivity.this)) {
							Toast.makeText(SplashActivity.this,
									"您暂时没有可用的网络,请检查网络", 0).show();
							openActivity(LoginActivity.class);
							// startActivity(new
							// Intent("android.settings.WIRELESS_SETTINGS"));
						} else {
							LogUtil.i("msg", "启动LoginActivity");
							openActivity(LoginActivity.class);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 或者记住密码超时
					if ((System.currentTimeMillis() - timer.getUpdateTime()) > Configs.Remember_Password_Time)
						try {
							Intent i = new Intent(SplashActivity.this,
									LoginActivity.class);
							i.putExtra("remember_password_time_passed", true);
							startActivity(i);
						} catch (Exception e) {
							e.printStackTrace();
						}
					else {
						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								goHome();
							}
						}, 2000);
					}
				}
				try {
					StrictMode
							.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
									.detectDiskReads().detectDiskWrites()
									.detectNetwork().penaltyLog().build());
					StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
							.detectLeakedSqlLiteObjects().penaltyLog()
							.penaltyDeath().build());
				} catch (Exception e) {
				}
			}
		});
	}

	protected void onResume() {
		super.onResume();
	}

	private void goHome() {
		LogUtil.i("msg", "启动PreviewActivity");
		/**
		 * 以下为改动代码
		 */
		User res = IPreference.getInstance(SplashActivity.this).getProfile();
		// new UserDao(SplashActivity.this).mapperJson(str1, str2);
		try {
			if (res == null) {
				LogUtil.i("msg", "返回值为空");
				Toast localToast2 = Toast.makeText(SplashActivity.this,
						"请重新输入用户名和密码", 0);
				localToast2.setGravity(17, 0, 0);
				localToast2.show();
				// 跳转到登陆界面
				openActivity(LoginActivity.class);
				return;
			}
		} catch (Exception localException) {
			Toast localToast1 = Toast.makeText(SplashActivity.this,
					"用户名信息异常，请注意", 0);
			localToast1.setGravity(17, 0, 0);
			localToast1.show();
			return;
		}
		if (res != null) {
			LogUtil.i("msg", "返回值为正确");
			AppInfo.setUser(res);
			SharedPreferences.Editor localEditor = getSharedPreferences(
					Constants.PREFER_FILE, 0).edit();
			localEditor
					.putString(LoginActivity.KEY, AppInfo.getUser().getJid());
			localEditor.commit();
			SplashActivity.this.startActivity(new Intent(SplashActivity.this,
					MainActivity.class));
		}
		defaultFinish();
	}

	@Override
	protected void bindButton() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy(){
		LogUtil.w("save data:", "IPreferrence");
		try {
			IPreference.save(this);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
}
