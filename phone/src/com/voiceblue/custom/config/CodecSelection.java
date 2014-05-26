package com.voiceblue.custom.config;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.voiceblue.phone.api.SipConfigManager;

public class CodecSelection {

	private enum Codec {
	    G722,PCMA,PCMU,ISAC,GSM,ILBC,G729,SILK24,SILK8,H264,H263,VP8
	};
	
	private enum Network {
	    WIFI,MOBILE_3G
	};
	
	private static final String TAG = CodecSelection.class.getSimpleName();
		
	private List<Codec> CodecsWifi = new ArrayList<CodecSelection.Codec>();
	private List<Codec> Codecs3G = new ArrayList<CodecSelection.Codec>();
	
	public CodecSelection(String audioCodecsWifi, String audioCodecs3G,
							String videoCodecsWifi, String videoCodecs3G) throws Exception {
		
		if (audioCodecsWifi == null || audioCodecsWifi.isEmpty())
			throw new Exception("audio codecs (WIFI) is empty or null");
		
		if (audioCodecs3G == null || audioCodecs3G.isEmpty())
			throw new Exception("audio codecs (3G) is empty or null");
		
		if (videoCodecsWifi == null || videoCodecsWifi.isEmpty())
			throw new Exception("video codecs (WIFI) is empty or null");
		
		if (videoCodecs3G == null || videoCodecs3G.isEmpty())
			throw new Exception("video codecs (3G) is empty or null");
		
		parseAudioCodecs(audioCodecsWifi, Network.WIFI);
		parseAudioCodecs(audioCodecs3G, Network.MOBILE_3G);
		
		parseVideoCodecs(videoCodecsWifi, Network.WIFI);
		parseVideoCodecs(videoCodecs3G, Network.MOBILE_3G);
		
	}
	
	private void parseVideoCodecs(String videoCodecs, Network networkType) throws Exception {
		String videoArray[] = videoCodecs.split(",");		
		List<Codec> set = networkType == Network.WIFI ? CodecsWifi : Codecs3G;
		
		for(String codec:videoArray) {			
			if (codec.toLowerCase().trim().equals("h263-1998") || codec.toLowerCase().trim().equals("h263")) {
				if (!set.contains(Codec.H263))
					set.add(Codec.H263);
			}
			else if (codec.toLowerCase().trim().equals("h264"))
				set.add(Codec.H264);
			else if (codec.toLowerCase().trim().equals("vp8"))
				set.add(Codec.VP8);
			else
				throw new Exception("Unknown video codec: " + codec);
		}
	}
	
	private void parseAudioCodecs(String audioCodecs, Network networkType) throws Exception {
		
		String audioArray[] = audioCodecs.split(",");		
		List<Codec> set = networkType == Network.WIFI ? CodecsWifi : Codecs3G;
		
		for(String codec:audioArray) {
			if (codec.toLowerCase().trim().equals("g722"))
				set.add(Codec.G722);
			else if (codec.toLowerCase().trim().equals("g711a"))
				set.add(Codec.PCMA);
			else if (codec.toLowerCase().trim().equals("g711u"))
				set.add(Codec.PCMU);
			else if (codec.toLowerCase().trim().equals("isac"))
				set.add(Codec.ISAC);
			else if (codec.toLowerCase().trim().equals("gsm"))
				set.add(Codec.GSM);
			else if (codec.toLowerCase().trim().equals("ilbc"))
				set.add(Codec.ILBC);
			else if (codec.toLowerCase().trim().equals("g729"))
				set.add(Codec.G729);
			else if (codec.toLowerCase().trim().equals("silk24"))
				set.add(Codec.SILK24);
			else if (codec.toLowerCase().trim().equals("silk8"))
				set.add(Codec.SILK8);
			else
				throw new Exception("Unknown audio codec: " + codec);
		}
	}
	
	private String getID(Codec codec, boolean wideBand) throws Exception {
		
		String band = wideBand ? SipConfigManager.CODEC_WB : SipConfigManager.CODEC_NB;
		
		switch(codec) {
		case G722:
			return SipConfigManager.getCodecKey("G722/16000/1", band);
		case PCMA:
			return SipConfigManager.getCodecKey("PCMA/8000/1", band);
		case PCMU:
			return SipConfigManager.getCodecKey("PCMU/8000/1", band);
		case ISAC:
			return SipConfigManager.getCodecKey("ISAC/16000/1", band);
		case GSM:
			return SipConfigManager.getCodecKey("GSM/8000/1", band);
		case ILBC:
			return SipConfigManager.getCodecKey("iLBC/8000/1", band);
		case G729:
			return SipConfigManager.getCodecKey("G729/8000/1", band);
		case SILK24:
			return SipConfigManager.getCodecKey("SILK/24000/1", band);
		case SILK8:
			return SipConfigManager.getCodecKey("SILK/8000/1", band);
		case H264:
			return SipConfigManager.getCodecKey("H264/97", band);
		case H263:
			return SipConfigManager.getCodecKey("H263-1998/96", band);
		case VP8:
			return SipConfigManager.getCodecKey("VP8/102", band);
		}
		
		throw new Exception("Error getting codec ID");		
	}
	
	public void setupPrefences(Context ctx) {
		
		try {		
			int currentItem = CodecsWifi.size() * 2;
			
			resetCodecs(ctx);
			
			for(Codec codec:CodecsWifi) {
				String codecID = getID(codec, true);
				Log.d(TAG, "Setting up codec: " + codecID + "| weight: " + Integer.valueOf((currentItem) * 100).toString() );
				SipConfigManager.setPreferenceStringValue(ctx, codecID, Integer.valueOf((currentItem--) * 100).toString());
			}
			
			if (currentItem - Codecs3G.size() < 0)
				currentItem = Codecs3G.size();
			
			for(Codec codec:Codecs3G) {
				String codecID = getID(codec, false);
				Log.d(TAG, "Setting up codec: " + codecID + "| weight: " + Integer.valueOf((currentItem) * 100).toString() );
				SipConfigManager.setPreferenceStringValue(ctx, codecID, Integer.valueOf((currentItem--) * 100).toString());
			}
		}
		catch(Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void resetCodecs(Context ctx) {
		
		EnumSet<Codec> codecs = EnumSet.allOf(Codec.class);
		try {
			for(Codec codec:CodecsWifi) {
				String idWb = getID(codec, true);
				String idNb = getID(codec, false);
				
				SipConfigManager.setPreferenceStringValue(ctx, idWb, "0");
				SipConfigManager.setPreferenceStringValue(ctx, idNb, "0");
			}
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
	}
}
