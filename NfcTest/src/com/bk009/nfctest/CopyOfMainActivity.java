package com.bk009.nfctest;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class CopyOfMainActivity extends Activity {
	NfcAdapter nfcAdapter;
	TextView promt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		promt = (TextView) findViewById(R.id.promt);
		// 获取默认的NFC控制器
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			promt.setText("设备不支持NFC！");
			finish();
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			promt.setText("请在系统设置中先启用NFC功能！");
			// startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));

			finish();
			return;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 得到是否检测到ACTION_TECH_DISCOVERED触发
		/*
		 * if
		 * (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
		 * // 处理该intent processIntent(getIntent()); }
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				processIntent(getIntent());
			}
		}).start();
	}

	// 字符序列转换为16进制字符串
	private String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("0x");
		if (src == null || src.length <= 0) {
			return null;
		}
		char[] buffer = new char[2];
		for (int i = 0; i < src.length; i++) {
			buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
			buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
			System.out.println(buffer);
			stringBuilder.append(buffer);
		}
		return stringBuilder.toString();
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	private void processIntent(Intent intent) {
		// 取出封装在intent中的TAG
		boolean auth = false;
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tagFromIntent != null) {
			for (String tech : tagFromIntent.getTechList()) {
				System.out.println(tech);
				auth = true;
			}
		}

		// 读取TAG
		/*
		 * MifareClassic mfc = MifareClassic.get(tagFromIntent); try { String
		 * metaInfo = ""; //Enable I/O operations to the tag from this
		 * TagTechnology object. mfc.connect(); int type =
		 * mfc.getType();//获取TAG的类型 int sectorCount =
		 * mfc.getSectorCount();//获取TAG中包含的扇区数 String typeS = ""; switch (type)
		 * { case MifareClassic.TYPE_CLASSIC: typeS = "TYPE_CLASSIC"; break;
		 * case MifareClassic.TYPE_PLUS: typeS = "TYPE_PLUS"; break; case
		 * MifareClassic.TYPE_PRO: typeS = "TYPE_PRO"; break; case
		 * MifareClassic.TYPE_UNKNOWN: typeS = "TYPE_UNKNOWN"; break; } metaInfo
		 * += "卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共" +
		 * mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize() + "B\n"; for (int
		 * j = 0; j < sectorCount; j++) { //Authenticate a sector with key A.
		 * auth = mfc.authenticateSectorWithKeyA(j, MifareClassic.KEY_DEFAULT);
		 * int bCount; int bIndex; if (auth) { metaInfo += "Sector " + j +
		 * ":验证成功\n"; // 读取扇区中的块 bCount = mfc.getBlockCountInSector(j); bIndex =
		 * mfc.sectorToBlock(j); for (int i = 0; i < bCount; i++) { byte[] data
		 * = mfc.readBlock(bIndex); metaInfo += "Block " + bIndex + " : " +
		 * bytesToHexString(data) + "\n"; bIndex++; } } else { metaInfo +=
		 * "Sector " + j + ":验证失败\n"; } } promt.setText(metaInfo);
		 */
		try {
			if (auth) {
				NfcA nfcaTag = NfcA.get((Tag) intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG));//
				if (nfcaTag != null) {
					nfcaTag.connect();// 连接卡
					String atqa = "";
					String str = "";
					for (byte tmpByte : nfcaTag.getAtqa()) {
						atqa += tmpByte;
					}
					str += "tag Atqa:" + bytesToHexString(nfcaTag.getAtqa())
							+ "\n";// 获取卡的atqa
					str += "tag SAK:" + nfcaTag.getSak() + "\n";// 获取卡的sak
					str += "max len:" + nfcaTag.getMaxTransceiveLength() + "\n";// 获取卡片能接收的最大指令长度
					byte[] cmd = null;
					// cmd=new
					// byte[]{0x41,0x54,0x4D,0x0A,0x52,0x01,0x00,0x01,(byte)
					// 0xff,(byte)0xff,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,0x0D};
					cmd = new byte[] { 0x0A, 0x00, 0x01 };// 卡请求
					str += "Card Number:" + nfcaTag.transceive(cmd);// 发送命令到卡片(找不到我们厂牌的扇区读取指令,有知道的同学告诉一声我们的卡是ISO/IEC
																	// 14443
																	// typeA标准的)
					promt.setText(str);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}