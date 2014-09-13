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

	/* ��ر������� */
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
		/* ����display.xml Layout */
		setContentView(R.layout.display);

		/* ȡ����Ļ�ֱ��ʴ�С */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = dm.heightPixels;

		/* ��ʼ����ر��� */
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

		/* ��С��ťonClickListener */
		mButton01.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				small();
			}
		});

		/* �Ŵ�ťonClickListener */
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
					String result = "��ǰʱ��Ϊ��" + form.format(times) + "\n";
					TextView Txt = (TextView) findViewById(R.id.promt);
					result += "����Ϊ��" + longitude + "\n";
					result += "γ��Ϊ��" + latitude + "\n";
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

	// �õ�λ����Դ
	private void getProvider() {
		// TODO Auto-generated method stub
		// ����λ�ò�ѯ����
		Criteria criteria = new Criteria();
		// ��ѯ���ȣ���
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// �Ƿ��ѯ��������
		criteria.setAltitudeRequired(false);
		// �Ƿ��ѯ��λ�� : ��
		criteria.setBearingRequired(false);
		// �Ƿ������ѣ���
		criteria.setCostAllowed(true);
		// ����Ҫ�󣺵�
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		// ��������ʵķ��������� provider ���� 2 ������Ϊ true ˵�� , ���ֻ��һ�� provider ����Ч�� , �򷵻ص�ǰ
		// provider
		provider = locationManager.getBestProvider(criteria, true);
	}

	// ����ͼƬ
	private boolean savePicture(String picName, Bitmap bm) {
		Log.e(TAG, "����ͼƬ");
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
			bm.compress(Bitmap.CompressFormat.JPEG, 10, out); // 30��ʾѹ����Ϊ�ٷ�֮70
			out.flush();
			out.close();
			Log.i(TAG, "�Ѿ�����");
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

	private double latitude = 0.0; // γ��
	private double longitude = 0.0; // ����
	private long time; // ����ʱ��

	// ���λ�ñ仯�µĻص�������
	private final LocationListener listener = new LocationListener() {

		// Provider��״̬�ڿ��á���ʱ�����ú��޷�������״ֱ̬���л�ʱ�����˺���
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		// Provider��enableʱ�����˺���������GPS����
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		// Provider��disableʱ�����˺���������GPS���ر�
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		// ������ı�ʱ�����˺��������Provider������ͬ�����꣬���Ͳ��ᱻ����
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			time = location.getTime();
			latitude = location.getLatitude();// ��ȡγ��
			longitude = location.getLongitude();// ��ȡ����
		}
	};

	private LocationManager locationManager;

	// �õ�GPS��γ�Ⱥ�ʵ����
	private void getInternetTime() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		getProvider();
		// �ж�GPS�Ƿ����
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
				latitude = location.getLatitude(); // ����
				longitude = location.getLongitude(); // γ��
			}
		}
	}

	/* ͼƬ��С��method */
	private void small() {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

		/* ����ͼƬ��С�ı��� */
		double scale = 0.8;
		/* ��������Ҫ��С�ı��� */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* ����reSize���Bitmap���� */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		// ���沢ѹ��ͼƬ
		savePicture("aaa.jpg", resizeBmp);
		if (id == 0) {
			/* ����ǵ�һ�ΰ�����ɾ��ԭ��Ĭ�ϵ�ImageView */
			layoutImage.removeView(mImageView);
		} else {
			/* ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView */
			layoutImage.removeView((ImageView) findViewById(id));
		}

		/* �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� */
		id++;
		ImageView imageView = new ImageView(this);
		imageView.setId(id);
		imageView.setImageBitmap(resizeBmp);
		layoutImage.addView(imageView);
		Log.i(TAG, "imageView.getWidth() = " + imageView.getWidth()
				+ ", imageView.getHeight() = " + imageView.getHeight());
		setContentView(layout1);
		// setContentView(layoutImage);
		/* ��ΪͼƬ�ŵ����ʱ�Ŵ�ť��disable����������Сʱ��������Ϊenable */
		mButton02.setEnabled(true);
		mButton02.setTextColor(Color.MAGENTA);
	}

	/* ͼƬ�Ŵ��method */
	private void big() {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();

		Log.i(TAG, "bmpWidth = " + bmpWidth + ", bmpHeight = " + bmpHeight);

		/* ����ͼƬ�Ŵ�ı��� */
		double scale = 1.25;
		/* �������Ҫ�Ŵ�ı��� */
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
		/* ����reSize���Bitmap���� */
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);

		if (id == 0) {
			/* ����ǵ�һ�ΰ�����ɾ��ԭ�����õ�ImageView */
			layoutImage.removeView(mImageView);
		} else {
			/* ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView */
			layoutImage.removeView((ImageView) findViewById(id));
		}

		/* �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� */
		id++;
		ImageView imageView = new ImageView(this);
		imageView.setId(id);
		imageView.setImageBitmap(resizeBmp);
		layoutImage.addView(imageView);
		setContentView(layout1);
		// setContentView(layoutImage);
		/* ����ٷŴ�ᳬ����Ļ��С���Ͱ�Button disable */
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

	// ����
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
			Toast.makeText(getApplicationContext(), "��ȷ���Ѿ�����SD��",
					Toast.LENGTH_LONG).show();
		}
	}

	// ����Uri��ȡͼƬ·��
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

	// ������Ƭ�ص�������
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
					// spath :����ͼƬȡ�����ֺ�·����������
					/*
					 * bmp = photo; small();
					 */
					savePicture("hello.jpg", photo);

					if (id == 0) {
						/* ����ǵ�һ�ΰ�����ɾ��ԭ��Ĭ�ϵ�ImageView */
						layoutImage.removeView(mImageView);
					} else {
						/* ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView */
						layoutImage.removeView((ImageView) findViewById(id));
					}

					/* �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� */
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
				String path = getPathByUri(this, uri);// ����������ϴθ���˵���Ǹ�����
				Bitmap bmps = BitmapFactory.decodeFile(path);

				bmp =bmps;
				small();
			/*	if (id == 0) {
					 ����ǵ�һ�ΰ�����ɾ��ԭ��Ĭ�ϵ�ImageView 
					layoutImage.removeView(mImageView);
				} else {
					 ������ǵ�һ�ΰ�����ɾ���ϴηŴ���С��������ImageView 
					layoutImage.removeView((ImageView) findViewById(id));
				}

				 �����µ�ImageView������reSize��Bitmap�����ٷ���Layout�� 
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
				 * ���������յ�ʱ�����ֵ����Ҫ���ñ�������Ϊ��Ա���� }
				 */
				// tempFile ����ͼƬ�����·����
			}
		}
	}
}
