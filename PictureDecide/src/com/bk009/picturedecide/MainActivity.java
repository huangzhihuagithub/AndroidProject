package com.bk009.picturedecide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	/* 相关变量声明 */
	private ImageView mImageView;
	private Button mButton01;
	private Button mButton02;
	private FrameLayout layout1;
	private LinearLayout layoutImage;
	private Bitmap bmp;
	private int id = 0;
	private int displayWidth;
	private int displayHeight;
	private float scaleWidth = 1;
	private float scaleHeight = 1;
	private String TAG = "123";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/* 加载display.xml Layout */
		setContentView(R.layout.display);

		/* 取得屏幕分辨率大小 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;

		/* 初始化相关变量 */
		/*
		 * Bundle bundle = this.getIntent().getExtras(); Integer imageId =
		 * bundle.getInt("imageId"); Log.i(TAG, "onCreate, imageId = " +
		 * imageId);
		 */

		// bmp=BitmapFactory.decodeResource(getResources(), imageId);
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.koala);
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mImageView.setImageBitmap(bmp);
		// mImageView.setOnTouchListener((OnTouchListener) this);
		mImageView.setLongClickable(true);

		layout1 = (FrameLayout) findViewById(R.id.layout1);
		layoutImage = (LinearLayout) findViewById(R.id.layoutImage);
		mButton01 = (Button) findViewById(R.id.myButton1);
		mButton02 = (Button) findViewById(R.id.myButton2);

		/* 缩小按钮onClickListener */
		mButton01.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				small();
			}
		});

		/* 放大按钮onClickListener */
		mButton02.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				big();
			}
		});
		Button location = (Button) findViewById(R.id.button1);
		location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getInternetTime();
				if (longitude != 0.0) {
					Date times = new Date(time);
					SimpleDateFormat form = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String result = "当前时间为：" + form.format(times) + "\n";
					TextView Txt = (TextView) findViewById(R.id.promt);
					result += "经度为：" + longitude + "\n";
					result += "纬度为：" + latitude + "\n";
					Txt.setText(result);
				}
			}
		});

		Button photo = (Button) findViewById(R.id.button2);
		photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePhoto();
			}
		});
	}

	private String provider = "";

	// 得到位置来源
	private void getProvider() {
		// TODO Auto-generated method stub
		// 构建位置查询条件
		Criteria criteria = new Criteria();
		// 查询精度：高
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 是否查询海拨：否
		criteria.setAltitudeRequired(false);
		// 是否查询方位角 : 否
		criteria.setBearingRequired(false);
		// 是否允许付费：是
		criteria.setCostAllowed(true);
		// 电量要求：低
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// 返回最合适的符合条件的 provider ，第 2 个参数为 true 说明 , 如果只有一个 provider 是有效的 , 则返回当前
		// provider
		provider = locationManager.getBestProvider(criteria, true);
	}

	// 保存图片
	private boolean savePicture(String picName, Bitmap bm) {
		Log.e(TAG, "保存图片");
		File folder = new File("/sdcard/namecard/");
		if (!folder.exists()) {
			folder.mkdir();
		}
		File f = new File("/sdcard/namecard/", picName);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 10, out); // 30表示压缩率为百分之70
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private double latitude = 0.0; // 纬度
	private double longitude = 0.0; // 经度
	private long time; // 网络时间

	// 检测位置变化事的回调函数。
	private final LocationListener listener = new LocationListener() {

		// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		// Provider被enable时触发此函数，比如GPS被打开
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		// Provider被disable时触发此函数，比如GPS被关闭
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			time = location.getTime();
			latitude = location.getLatitude();// 获取纬度
			longitude = location.getLongitude();// 获取进度
		}
	};

	private LocationManager locationManager;

	// 得到GPS经纬度和实践。
	private void getInternetTime() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		getProvider();
		// 判断GPS是否可用
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, listener);

			/*
			 * Location location = locationManager
			 * .getLastKnownLocation(LocationManager.GPS_PROVIDER);
			 */
			Location location = locationManager.getLastKnownLocation(provider);
			while (location == null) {
				location = locationManager.getLastKnownLocation(provider);
			}
			if (location != null) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		} else {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, listener);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null) {
				latitude = location.getLatitude(); // 经度
				longitude = location.getLongitude(); // 纬度
			}
		}
	}

	/* 图片缩小的method */
	private void small() {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

		/* 设置图片缩小的比例 */
		double scale = 0.8;
		/* 计算出这次要缩小的比例 */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		// 保存并压缩图片
		savePicture("aaa.jpg", resizeBmp);
		if (id == 0) {
			/* 如果是第一次按，就删除原来默认的ImageView */
			layoutImage.removeView(mImageView);
		} else {
			/* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */
			layoutImage.removeView((ImageView) findViewById(id));
		}

		/* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */
		id++;
		ImageView imageView = new ImageView(this);
		imageView.setId(id);
		imageView.setImageBitmap(resizeBmp);
		layoutImage.addView(imageView);
		Log.i(TAG, "imageView.getWidth() = " + imageView.getWidth()
				+ ", imageView.getHeight() = " + imageView.getHeight());
		setContentView(layout1);
		// setContentView(layoutImage);
		/* 因为图片放到最大时放大按钮会disable，所以在缩小时把它重设为enable */
		mButton02.setEnabled(true);
		mButton02.setTextColor(Color.MAGENTA);
	}

	/* 图片放大的method */
	private void big() {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

		/* 设置图片放大的比例 */
		double scale = 1.25;
		/* 计算这次要放大的比例 */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* 产生reSize后的Bitmap对象 */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);

		if (id == 0) {
			/* 如果是第一次按，就删除原来设置的ImageView */
			layoutImage.removeView(mImageView);
		} else {
			/* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */
			layoutImage.removeView((ImageView) findViewById(id));
		}

		/* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */
		id++;
		ImageView imageView = new ImageView(this);
		imageView.setId(id);
		imageView.setImageBitmap(resizeBmp);
		layoutImage.addView(imageView);
		setContentView(layout1);
		// setContentView(layoutImage);
		/* 如果再放大会超过屏幕大小，就把Button disable */
		if (scaleWidth * scale * bmpWidth > bmpWidth * 3
				|| scaleHeight * scale * bmpHeight > bmpWidth * 3
				|| scaleWidth * scale * bmpWidth > displayWidth * 5
				|| scaleHeight * scale * bmpHeight > displayHeight * 5) {
			mButton02.setEnabled(false);
			mButton02.setTextColor(Color.GRAY);
		} else {
			mButton02.setEnabled(true);
			mButton02.setTextColor(Color.MAGENTA);
		}
	}

	private final int REQUEST_CODE_CAPTURE_CAMEIA = 0;

	// 拍照
	private void takePhoto() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			/*
			 * Intent getImageByCamera = new Intent(
			 * "android.media.action.IMAGE_CAPTURE");
			 */
			File tempFile = new File(Environment.getExternalStorageDirectory()
					+ "/namecard/", "nihao.jpg");
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
			/*
			 * startActivityForResult(getImageByCamera,
			 * REQUEST_CODE_CAPTURE_CAMEIA);
			 */
		} else {
			Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
					Toast.LENGTH_LONG).show();
		}
	}

	// 根据Uri获取图片路径
	public static final String getPathByUri(Context context, Uri uri) {
		if (uri == null)
			return null;

		if ("file".equals(uri.getScheme())) {
			return uri.getPath();
		}

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = MediaStore.Images.Media.query(
					context.getContentResolver(), uri, projection, null, null);
			if (cursor == null)
				return null;
			cursor.moveToNext();
			if (cursor.getCount() == 0) {
				cursor.close();
				return null;
			} else {
				String imagePath = cursor.getString(cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
				cursor.close();
				return imagePath;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return null;
	}

	// 拍完照片回调函数。
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
			Uri uri = data.getData();
			if (uri == null) {
				// use bundle to get data
				Bundle bundle = data.getExtras();
				if (bundle != null) {
					Bitmap photo = (Bitmap) bundle.get("data"); // get bitmap
					// spath :生成图片取个名字和路径包含类型
					/*
					 * bmp = photo; small();
					 */
					savePicture("hello.jpg", photo);

					if (id == 0) {
						/* 如果是第一次按，就删除原来默认的ImageView */
						layoutImage.removeView(mImageView);
					} else {
						/* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */
						layoutImage.removeView((ImageView) findViewById(id));
					}

					/* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */
					id++;
					ImageView imageView = new ImageView(this);
					imageView.setId(id);
					imageView.setImageBitmap(photo);
					layoutImage.addView(imageView);
					Log.i(TAG,
							"imageView.getWidth() = " + imageView.getWidth()
									+ ", imageView.getHeight() = "
									+ imageView.getHeight());
					setContentView(layout1);

					savePicture("hello.jpg", photo);

				} else {
					Toast.makeText(getApplicationContext(), "err****",
							Toast.LENGTH_LONG).show();
					return;
				}
			} else {
				// to do find the path of pic by uri
				String path = getPathByUri(this, uri);// 这里就是我上次跟你说的那个函数
				Bitmap bmps = BitmapFactory.decodeFile(path);

				bmp =bmps;
				small();
			/*	if (id == 0) {
					 如果是第一次按，就删除原来默认的ImageView 
					layoutImage.removeView(mImageView);
				} else {
					 如果不是第一次按，就删除上次放大缩小所产生的ImageView 
					layoutImage.removeView((ImageView) findViewById(id));
				}

				 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 
				id++;
				ImageView imageView = new ImageView(this);
				imageView.setId(id);
				imageView.setImageBitmap(bmp);
				layoutImage.addView(imageView);
				Log.i(TAG, "imageView.getWidth() = " + imageView.getWidth()
						+ ", imageView.getHeight() = " + imageView.getHeight());
				setContentView(layout1);
				savePicture("hello.jpg", bmp);*/
				/*
				 * if(path != null){ tempFile = new File(path);//tempFile
				 * 就是在拍照的时候传入的值，需要将该变量定义为成员变量 }
				 */
				// tempFile 就是图片保存的路径了
			}
		}
	}
}
